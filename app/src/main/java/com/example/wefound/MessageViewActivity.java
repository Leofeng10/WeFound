package com.example.wefound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageViewActivity extends AppCompatActivity {

    // Declare variables

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private ListView listView;
    private RecyclerView recyclerView;

    private SearchView searchView;

    private List<String> messageUsers;
    private List<String> messageIds;

    private MessageViewAdapter2.RecyclerViewOnClickListener listener;

    private String userId;

    public static final String POST_USER_ID = "com.example.lostfound.postuserid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        setContentView(R.layout.activity_message_view);

        firebaseAuth = FirebaseAuth.getInstance();

        // If user not login in, return to login activity
        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, Login.class));
        }

        //listView = (ListView) findViewById(R.id.listView);

        recyclerView = findViewById(R.id.listView);

//        searchView = (SearchView) findViewById(R.id.searchView);

        userId = firebaseAuth.getCurrentUser().getUid();

        messageUsers = new ArrayList<>();
        messageIds = new ArrayList<>();

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                //refreshList(newText);
//                return false;
//            }
//        });

//        listener = new MessageViewAdapter2.RecyclerViewOnClickListener() {
//            @Override
//            public void onClick(View v, int position) {
//                String messageUser = messageUsers.get(position);
//                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
//                intent.putExtra("id", messageUser);
//                intent.putExtra("username", user.getName());
//                startActivity(intent);
//            }
//        };

        listener = new MessageViewAdapter2.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View v, int position) {
                String messageUser = messageUsers.get(position);
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra("id", messageUser);
                Log.d("message", messageUser);

                databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + messageUser);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child("INFO").getValue(User.class);


                        intent.putExtra("username", user.getName());

                        startActivity(intent);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };


        // Populate all message history
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/CHAT/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageUsers.clear();
                messageIds.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    messageUsers.add(postSnapshot.getKey());
                    messageIds.add(postSnapshot.getValue(String.class));
                }
                MessageViewAdapter2 messageViewAdapter = new MessageViewAdapter2(MessageViewActivity.this,messageUsers, listener);
                recyclerView.setAdapter(messageViewAdapter);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Go to a particular message history to message with that user
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String messageUser = messageUsers.get(i);
//                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
//                intent.putExtra("id", messageUser);
//                startActivity(intent);
//            }
//        });






    }
}