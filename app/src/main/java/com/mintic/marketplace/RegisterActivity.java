package com.mintic.marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    CheckBox termAndConditionsCheckBox;
    TextView termAndConditionsTextView;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.term_and_conditions_checkbox:
                if (termAndConditionsCheckBox.isChecked()) {
                    registerButton.setEnabled(true);
                } else {
                    registerButton.setEnabled(false);
                }
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
                // TODO: Register the user in firebase
                break;
            default:
                break;
        }
    }
}