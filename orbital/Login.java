package com.example.orbital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    EditText userName, Password;
    Button button;
    DBhelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.editTextTextPersonName);
        Password = findViewById(R.id.editTextTextPassword);
        button = findViewById(R.id.Loginbutton);
        db = new DBhelper(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password;
                username = String.valueOf(userName.getText());
                password = String.valueOf(Password.getText());

                if (db.checkuserpassword(username, password)) {
                    Toast.makeText(Login.this, "Log in successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);

                } else {
                    Toast.makeText(Login.this, "Failed to log in", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void jumpToSignup(View v) {
        Intent i = new Intent(this, Signup.class);
        startActivity(i);
    }
}