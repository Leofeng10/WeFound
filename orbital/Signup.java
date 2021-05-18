package com.example.orbital;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class Signup extends AppCompatActivity {

    EditText fullName, Email, userName, Password, confirmPassword, phone;
    Button buttonSignup, registerGmail;
    DBhelper db;

    FirebaseDatabase root;
    DatabaseReference reference;
    FirebaseAuth auth;

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
        phone = findViewById(R.id.Phone);
        db = new DBhelper(this);
        registerGmail = findViewById(R.id.registerGmail);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname, username, password, email, comfirmpassword, phoneNumber;
                fullname = String.valueOf(fullName.getText());
                username = String.valueOf(userName.getText());
                password = String.valueOf(Password.getText());
                email = String.valueOf(Email.getText());
                phoneNumber = String.valueOf(phone.getText());
                comfirmpassword = String.valueOf(confirmPassword.getText());

//                root = FirebaseDatabase.getInstance();
//                reference = root.getReference("users");
//                auth = FirebaseAuth.getInstance();
//                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(Signup.this, "Registered successfully", Toast.LENGTH_LONG).show();
//                        } else {
//                            Toast.makeText(Signup.this, "Failed", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
                //UserHelperClass userHelperClass = new UserHelperClass(fullname, username, email, password, comfirmpassword);
//                reference.setValue(userHelperClass).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(Signup.this, "Registered successfully", Toast.LENGTH_LONG).show();
//                        } else {
//                            Toast.makeText(Signup.this, "Failed", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//                reference.setValue("leo1111");

               //Log.d("userhelper", userHelperClass.toString());

                if (username.equals("") || password.equals("") || comfirmpassword.equals("")) {
                    Toast.makeText(Signup.this, "All fields are needed", Toast.LENGTH_LONG).show();
                } else {
                    if (password.equals(comfirmpassword)) {
                        Boolean checkuser = db.checkusername(username);
                        if (checkuser == false) {
                            Boolean insert = db.insertData(username, password, email, fullname, phoneNumber);
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

        registerGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });


    }



    public void jumpToLogin(View v) {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

}