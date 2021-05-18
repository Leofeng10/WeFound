package com.example.orbital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SendMessage extends AppCompatActivity {

    Button sendSMS;
    TextView phone, name;
    EditText message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        message = findViewById(R.id.messageText);
        sendSMS = findViewById(R.id.sendMessageBtn);
        phone = findViewById(R.id.messagePhone);
        name = findViewById(R.id.messageName);

        Intent i = getIntent();
        name.setText(i.getStringExtra("name"));
        phone.setText(i.getStringExtra("phone"));
    }
}