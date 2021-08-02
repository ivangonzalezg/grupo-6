package com.mintic.marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mintic.marketplace.utils.Constants;
import com.mintic.marketplace.utils.Keyboard;
import com.mintic.marketplace.utils.SharedPref;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    EditText emailEditText;
    EditText passwordEditText;
    Button signInButton;
    Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        signInButton = findViewById(R.id.sign_in_button);
        registerButton = findViewById(R.id.register_button);

        signInButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(this, R.string.login_error_email, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(this, R.string.login_error_password, Toast.LENGTH_SHORT).show();
                    return;
                }
                Keyboard.hideSoft(this);
                ProgressDialog progress = ProgressDialog.show(this, this.getResources().getString(R.string.progress_dialog_title), this.getResources().getString(R.string.progress_dialog_message_logging_in));
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = auth.getCurrentUser();
                        if (currentUser == null) {
                            progress.dismiss();
                            Toast.makeText(LoginActivity.this, R.string.login_error_authentication_failed, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection(Constants.users).document(currentUser.getUid()).get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot document = task1.getResult();
                                        if (document == null || document.getData() == null) {
                                            progress.dismiss();
                                            Toast.makeText(LoginActivity.this, R.string.login_error_user_not_found, Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        if (document.exists()) {
                                            SharedPref.save(LoginActivity.this, Constants.userId, currentUser.getUid());
                                            SharedPref.save(LoginActivity.this, Constants.firstName, (String) document.getData().get(Constants.firstName));
                                            SharedPref.save(LoginActivity.this, Constants.lastName, (String) document.getData().get(Constants.lastName));
                                            SharedPref.save(LoginActivity.this, Constants.email, (String) document.getData().get(Constants.email));
                                            SharedPref.save(LoginActivity.this, Constants.isUserLoggedIn, true);
                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        } else {
                                            progress.dismiss();
                                            // TODO: Show error to user when data doesn't exist
                                        }
                                    } else {
                                        progress.dismiss();
                                        // TODO: Show error to user when task fail
                                    }
                                });
                    } else {
                        progress.dismiss();
                        Toast.makeText(LoginActivity.this, R.string.login_error_authentication_failed, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.register_button:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            default:
                break;
        }
    }
}