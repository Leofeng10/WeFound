package com.example.orbital;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class Signup extends AppCompatActivity {

    EditText fullName, Email, userName, Password, confirmPassword;
    Button buttonSignup;
    DBhelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        fullName = findViewById(R.id.FullName);
        userName = findViewById(R.id.userName);
        Password = findViewById(R.id.SignupPassword);
        confirmPassword = findViewById(R.id.SignupComfirmPassword);
        Email = findViewById(R.id.EmailAddress);
        buttonSignup = findViewById(R.id.SignUPbutton);
        db = new DBhelper(this);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname, username, password, email, comfirmpassword;
                fullname = String.valueOf(fullName.getText());
                username = String.valueOf(userName.getText());
                password = String.valueOf(Password.getText());
                email = String.valueOf(Email.getText());
                comfirmpassword = String.valueOf(confirmPassword.getText());

                if (username.equals("") || password.equals("") || comfirmpassword.equals("")) {
                    Toast.makeText(Signup.this, "All fields are needed", Toast.LENGTH_LONG).show();
                } else {
                    if (password.equals(comfirmpassword)) {
                        Boolean checkuser = db.checkusername(username);
                        if (checkuser == false) {
                            Boolean insert = db.insertData(username, password, email, fullname);
                            if (insert) {
                                Toast.makeText(Signup.this, "Registered successfully", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getApplicationContext(), Login.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(Signup.this, "Registration failed", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(Signup.this, "User name already exist", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Signup.this, "Two passwords do not match", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });



    }



    public void jumpToLogin(View v) {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

}