package com.mintic.marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mintic.marketplace.utils.Constants;
import com.mintic.marketplace.utils.Keyboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    CheckBox termAndConditionsCheckBox;
    TextView termAndConditionsTextView;
    Button registerButton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.register_title);

        firstNameEditText = findViewById(R.id.first_name_edittext);
        lastNameEditText = findViewById(R.id.last_name_edittext);
        emailEditText = findViewById(R.id.email_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        termAndConditionsCheckBox = findViewById(R.id.term_and_conditions_checkbox);
        termAndConditionsTextView = findViewById(R.id.term_and_conditions_textview);
        registerButton = findViewById(R.id.register_button);

        termAndConditionsCheckBox.setOnClickListener(this);
        termAndConditionsTextView.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.term_and_conditions_checkbox:
                registerButton.setEnabled(termAndConditionsCheckBox.isChecked());
                break;
            case R.id.term_and_conditions_textview:
                startActivity(new Intent(this, TermsAndConditionsActivity.class));
                break;
            case R.id.register_button:
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (firstName.isEmpty()) {
                    firstNameEditText.requestFocus();
                    Toast.makeText(this, R.string.register_error_first_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lastName.isEmpty()) {
                    lastNameEditText.requestFocus();
                    Toast.makeText(this, R.string.register_error_last_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.isEmpty()) {
                    emailEditText.requestFocus();
                    Toast.makeText(this, R.string.register_error_email, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    passwordEditText.requestFocus();
                    Toast.makeText(this, R.string.register_error_password, Toast.LENGTH_SHORT).show();
                    return;
                }
                Keyboard.hideSoft(this);
                ProgressDialog progress = ProgressDialog.show(this, this.getResources().getString(R.string.progress_dialog_title), this.getResources().getString(R.string.progress_dialog_message_registering));
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = auth.getCurrentUser();
                        if (currentUser == null) {
                            progress.dismiss();
                            Toast.makeText(RegisterActivity.this, R.string.register_error_authentication_failed, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> user = new HashMap<>();
                        user.put(Constants.firstName, firstName);
                        user.put(Constants.lastName, lastName);
                        user.put(Constants.email, email);
                        db.collection(Constants.users)
                                .document(currentUser.getUid())
                                .set(user)
                                .addOnSuccessListener(unused -> {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this)
                                            .setMessage(R.string.alert_dialog_message_register_confirm)
                                            .setPositiveButton(R.string.alert_dialog_positive_button_register_confirm, (dialogInterface, i) -> finish());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                        builder.setOnDismissListener(dialogInterface -> finish());
                                    }
                                    builder.show();
                                    progress.dismiss();
                                })
                                .addOnFailureListener(e -> {
                                    progress.dismiss();
                                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        progress.dismiss();
                        Toast.makeText(RegisterActivity.this, R.string.register_error_authentication_failed, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                break;
        }
    }
}