package com.example.wefound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

//    Button btn;
//    private DatabaseReference databaseReference;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("/Lost");
//
//        btn = findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "uplaod", Toast.LENGTH_SHORT).show();
//                String uploadId = databaseReference.push().getKey();
//                databaseReference.child(uploadId).setValue("hello world1");
//            }
//        });
//    }

    String username, phone;
    Button lostBtn, fountBtn;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth firebaseAuth;
    private TextView navEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        setContentView(R.layout.activity_main);





        lostBtn = findViewById(R.id.lostBtn);
        fountBtn = findViewById(R.id.foundBtn);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        phone = intent.getStringExtra("phone");




        navigationView = (NavigationView)findViewById(R.id.navigation);



        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();



        lostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Lost.class);
                startActivity(i);
            }
        });

        fountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Found.class);
                i.putExtra("username", username);
                startActivity(i);
            }
        });



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id) {
                    case R.id.navigation_item_1:
                        startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
                        return true;
                    case R.id.navigation_item_2:
                        startActivity(new Intent(getApplicationContext(), MessageViewActivity.class));
                        return true;
                    case R.id.navigation_item_3:
                        // Log out

                        finish();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }
}