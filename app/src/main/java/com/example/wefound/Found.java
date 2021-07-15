package com.example.wefound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Found extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private  DatabaseReference lostItemReference;

    private User currUser;
    private RecyclerView lostRecItem;
    TextView greeting;
    Button postLostBtn;

    private ProgressDialog progressDialog;


    private LostItemAdapter.RecyclerViewOnClickListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);
        greeting = (TextView) findViewById(R.id.greetingMSG1);
        postLostBtn = findViewById(R.id.postFoundBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userId = user.getUid();

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("USERS").child(userId).child("INFO");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieving data...");
        progressDialog.show();

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String school = snapshot.child("school").getValue(String.class);
                    String id = snapshot.child("id").getValue(String.class);
                    String phoneNum = snapshot.child("phoneNum").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    currUser = new User(name, school, id, email, phoneNum);
                    greeting.setText("Hi " + currUser.getName());
                } else  {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




        postLostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PostFoundItem.class);
                startActivity(i);
            }
        });

        lostRecItem = findViewById(R.id.foundItemView);

        ArrayList<String> referenceList = new ArrayList<>();

        ArrayList<Item> lostItems = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currUser = firebaseAuth.getCurrentUser();

        lostItemReference = FirebaseDatabase.getInstance().getReference("/FOUND/" );



        lostItemReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Item mItem = postSnapshot.getValue(Item.class);
                    lostItems.add(mItem);

                    LostItemAdapter adapter = new LostItemAdapter(getApplicationContext(), lostItems, listener);
                    lostRecItem.setAdapter(adapter);
                    lostRecItem.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Fail to retrieve found items " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        listener = new LostItemAdapter.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent i = new Intent(getApplicationContext(), LostItemDetail.class);
                i.putExtra("name", lostItems.get(position).getName());
                i.putExtra("location", lostItems.get(position).getLocation());
                i.putExtra("time", lostItems.get(position).getTime());
                i.putExtra("description", lostItems.get(position).getDescription());
                i.putExtra("phone", lostItems.get(position).getPhone());
                i.putExtra("username", lostItems.get(position).getUsername());
                i.putExtra("image", lostItems.get(position).getImageurl());
                i.putExtra("id", lostItems.get(position).getUserID());
                startActivity(i);
            }
        };


    }
}