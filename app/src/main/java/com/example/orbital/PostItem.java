package com.example.orbital;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class PostItem extends AppCompatActivity {

    Button postBtn;
    ImageView image;
    EditText name, location, description, time, phone, username;
    private Uri imageFilePath;
    private Bitmap imageToStore;
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] imageInByte;
    private static final int PICK_IMAGE_REQUEST = 100;

    LostDB db;
    FirebaseDatabase root;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);
        postBtn = findViewById(R.id.PostBtn);
        name = findViewById(R.id.postLostItemName);
        location = findViewById(R.id.postLostItemLocation);
        description = findViewById(R.id.postLostItemDescription);
        time = findViewById(R.id.postLostItemTime);
        phone = findViewById(R.id.postLostItemPhone);
        username = findViewById(R.id.postLostItemUser);
        image = findViewById(R.id.postItemImage);
        db = new LostDB(this);

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = String.valueOf(name.getText());
                String itemLocation = String.valueOf(location.getText());
                String itemDescription = String.valueOf(description.getText());
                String itemTime = String.valueOf(time.getText());
                String itemPhone = String.valueOf(phone.getText());
                String itemUsername = String.valueOf(username.getText());

                Log.d("Post Lost Item", itemName + itemLocation + itemTime + itemDescription + itemPhone);
                Boolean insert = db.insertLostItem(itemName, itemLocation, itemDescription, itemTime, itemPhone, itemUsername, imageInByte);
                if (insert) {
                    Toast.makeText(getApplicationContext(), "Post successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), Lost.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to post", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void chooseImage(View v) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageFilePath = data.getData();
            try {
                imageToStore = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.setImageBitmap(imageToStore);
            byteArrayOutputStream= new ByteArrayOutputStream();
            imageToStore.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            imageInByte = byteArrayOutputStream.toByteArray();
        }
    }
}