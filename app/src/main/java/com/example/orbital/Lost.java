package com.example.orbital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Lost extends AppCompatActivity {

    private RecyclerView lostRecItem;
    private String username;
    private String phone;
    TextView greeting;
    Button postLostBtn, detailsBtn;
    LostDB db;
    DBhelper userDB;

    private LostItemAdapter.RecyclerViewOnClickListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        phone = intent.getStringExtra("phone");
        greeting = (TextView) findViewById(R.id.greetingMSG);
        postLostBtn = findViewById(R.id.postLostBtn);

        db = new LostDB(this);
        userDB = new DBhelper(this);
        if (username == null) {
            greeting.setText("Hi " + "my friend");
        } else {
            greeting.setText("Hi " + username);
        }

        postLostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PostItem.class);
                startActivity(i);
            }
        });

        lostRecItem = findViewById(R.id.lostItemView);

        ArrayList<Item> lostItems= db.selectAllLostItem();

        listener = new LostItemAdapter.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View v, int position) {
                Bitmap imageToStore;
                ByteArrayOutputStream byteArrayOutputStream;
                byte[] imageInByte;
                imageToStore = lostItems.get(position).getImage();
                byteArrayOutputStream= new ByteArrayOutputStream();
                imageToStore.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                imageInByte = byteArrayOutputStream.toByteArray();
                Intent i = new Intent(getApplicationContext(), LostItemDetail.class);
                i.putExtra("name", lostItems.get(position).getName());
                i.putExtra("location", lostItems.get(position).getLocation());
                i.putExtra("time", lostItems.get(position).getTime());
                i.putExtra("description", lostItems.get(position).getDescription());
                i.putExtra("phone", lostItems.get(position).getPhone());
                i.putExtra("username", lostItems.get(position).getUsername());
                i.putExtra("image", imageInByte);
                startActivity(i);
            }
        };

        LostItemAdapter adapter = new LostItemAdapter(this, listener);
        adapter.setLostItems(lostItems);

        lostRecItem.setAdapter(adapter);
        lostRecItem.setLayoutManager(new GridLayoutManager(this, 2));

    }
}