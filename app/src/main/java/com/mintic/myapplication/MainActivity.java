package com.mintic.myapplication;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_password;
    private Button btn_signup, btn_login;

    private Activity mySelf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySelf = this;
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_signup = findViewById(R.id.btn_signup);
        btn_login = findViewById(R.id.btn_login);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: cambiar SignUp por la actividad de registro (Milena)
                Intent signup = new Intent(mySelf, SignUp.class);
                startActivity(signup);
            }

        });
        //TODO: este botón debe dirigir a la actividad del Navigation Drawer
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = et_username.getText().toString();
                String password = et_password.getText().toString();

                if (username.equals("admin") && password.equals("123")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mySelf);
                    builder.setTitle(R.string.txt_builder);
                    builder.setMessage(R.string.txt_builder_success);
                    builder.setPositiveButton(R.string.txt_accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent navigationDrawer = new Intent(mySelf, NavigationDrawer.class);

                            navigationDrawer.putExtra("username", username);
                            navigationDrawer.putExtra("password", password);

                            startActivity(navigationDrawer);

                        }
                    });
                    builder.setNegativeButton(R.string.txt_cancel, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mySelf);
                    builder.setTitle(R.string.txt_builder);
                    builder.setMessage(R.string.txt_error_login);
                    builder.setPositiveButton(R.string.txt_accept, null);
                    builder.setCancelable(false);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });


    }
}