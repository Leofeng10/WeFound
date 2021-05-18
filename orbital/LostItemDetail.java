package com.example.orbital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LostItemDetail extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    TextView Detailsname, DetailsLocation, DetailsTime, DetailsDescription, DetailsUserName, DetailsPhone;
    ImageView imageView;
    DBhelper db;
    Button buttonCallBtn, buttonSMSBtn, buttonSMSSend;
    String phone, name;
    EditText message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lost_item_detail);


        db = new DBhelper(this);
        Detailsname = findViewById(R.id.detailsName);
        DetailsLocation = findViewById(R.id.detailsLocation);
        DetailsTime = findViewById(R.id.detailsTime);
        DetailsDescription = findViewById(R.id.detailsDescription);
        DetailsUserName = findViewById(R.id.detailsUserName);
        DetailsPhone = findViewById(R.id.detailsPhone);
        imageView = findViewById(R.id.detailsImage);
        buttonCallBtn = findViewById(R.id.buttonCall);
        buttonSMSBtn = findViewById(R.id.buttonMessage);
        message = findViewById(R.id.messageContent);
        buttonSMSSend = findViewById(R.id.buttonSend);
        buttonSMSSend.setEnabled(false);

        Intent i = getIntent();
        name = i.getStringExtra("name");
        Detailsname.setText(i.getStringExtra("name"));
        DetailsLocation.setText(i.getStringExtra("location"));
        DetailsTime.setText(i.getStringExtra("time"));
        DetailsDescription.setText(i.getStringExtra("description"));
        DetailsPhone.setText(i.getStringExtra("phone"));
        phone = i.getStringExtra("phone");
        DetailsUserName.setText(i.getStringExtra("username"));
        byte[] imageInByte = i.getByteArrayExtra("image");
        Bitmap imagebyte = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
        imageView.setImageBitmap(imagebyte);


        buttonSMSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setVisibility(View.VISIBLE);
                DetailsDescription.setVisibility(View.INVISIBLE);
                buttonCallBtn.setEnabled(false);
                buttonSMSBtn.setEnabled(false);
                buttonSMSSend.setEnabled(true);
            }
        });

        buttonCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        buttonSMSSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS(v);
                buttonCallBtn.setEnabled(true);
                buttonSMSBtn.setEnabled(true);
                buttonSMSSend.setEnabled(false);
                message.setVisibility(View.INVISIBLE);
                DetailsDescription.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Message Sent Successfully", Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(getApplicationContext(), "Permission DENIED", Toast.LENGTH_LONG);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permission DENIED", Toast.LENGTH_LONG);
        }
    }

    public void makePhoneCall() {
        if (phone.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(LostItemDetail.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LostItemDetail.this, new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel: " + phone;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(getApplicationContext(), "Phone Number not available", Toast.LENGTH_LONG);
        }
    }


    public void sendSMS(View view) {
        ActivityCompat.requestPermissions(LostItemDetail.this, new String[] {Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
        String SMSContent = message.getText().toString();
        if (!SMSContent.equals("")) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, SMSContent, null, null);
            Toast.makeText(LostItemDetail.this, "Message Sent Successfully", Toast.LENGTH_LONG);
        } else {
            Toast.makeText(getApplicationContext(), "Message is empty", Toast.LENGTH_LONG);
        }
        Toast.makeText(LostItemDetail.this, "Message Sent Successfully", Toast.LENGTH_LONG);

    }
}