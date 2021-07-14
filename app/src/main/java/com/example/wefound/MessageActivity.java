package com.example.wefound;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    // Declare variables

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private RecyclerView listViewMessage;
    private EditText edittext_chatbox;
    private Button button_chatbox_send;

    TextView textToUser;

    private String userId, postUserEmail, messageUserId, messageId;

    private List<Message> messageList;

    // Send email to notify user someone messaged them
//    void addNotification(final String email){
//        // Create new thread to send email
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    GMailSender sender = new GMailSender("lostfoundee32f@gmail.com","A123456789d@");
//                    //sender.addAttachment(Environment.getExternalStorageDirectory().getPath()+"/image.jpg");
//                    sender.sendMail("New message", "You have received a new message from someone.","lostfoundee32f@gmail.com",email);
//                }
//                catch (Exception e) {
//                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
//                }
//            }
//        }).start();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        setContentView(R.layout.activity_message);

        firebaseAuth = FirebaseAuth.getInstance();

        // If user not login in, return to login activity
        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, Login.class));
        }

        Intent intent = getIntent();
        String toname = intent.getStringExtra("username");

        userId = firebaseAuth.getCurrentUser().getUid();

        textToUser = findViewById(R.id.textToUser);



        textToUser.setText(toname);


        // Get intent passed data
        messageUserId = intent.getStringExtra("id");
        //postUserEmail = intent.getStringExtra(PostViewActivity.POST_USER_EMAIL);

        messageList = new ArrayList<>();

        listViewMessage = findViewById(R.id.listViewMessage);
        listViewMessage.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Locate the message history betwee nthe two user
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/CHAT/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getKey().equals(messageUserId)) {
                        messageId = dataSnapshot.child(messageUserId).getValue(String.class);
                    }
                }
                // If no message history found, create one
                if (messageId == null){
                    //addNotification(postUserEmail);
                    String key = databaseReference.push().getKey();
                    databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/CHAT/");
                    databaseReference.child(messageUserId).setValue(key);
                    databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + messageUserId + "/CHAT/");
                    databaseReference.child(userId).setValue(key);
                    databaseReference = FirebaseDatabase.getInstance().getReference("/MESSAGES/");
                    messageId = key;
                }

                // Populate the message history
                databaseReference = FirebaseDatabase.getInstance().getReference("/MESSAGES/" + messageId);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        messageList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Message message = postSnapshot.getValue(Message.class);
                            messageList.add(message);
                        }
                        MessageAdapter messageAdapter = new MessageAdapter();
                        messageAdapter.setMessageModelList(messageList);
                        listViewMessage.setAdapter(messageAdapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Initialize
        edittext_chatbox = (EditText) findViewById(R.id.edittext_chatbox);
        button_chatbox_send = (Button) findViewById(R.id.button_chatbox_send);


        // Set listener
        button_chatbox_send.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        // Send message
        if (view == button_chatbox_send){
//            String sms = "You received a message";
//            CharSequence name = "WeFound";
//            int importance = NotificationManager.IMPORTANCE_LOW;
//            NotificationChannel notificationChannel = new NotificationChannel("my_channel",name, importance );
//
//            notificationChannel.setDescription(sms);
//            notificationChannel.enableVibration(true);
//            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.createNotificationChannel(notificationChannel);
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(MessageActivity.this)
//                    .setSmallIcon(R.drawable.ic_baseline_chat_24)
//                    .setContentTitle("WeFound")
//                    .setContentText(sms)
//                    .setOngoing(true)
//                    .setChannelId("my_channel")
//                    .setAutoCancel(true);
//
//            Intent intent = new Intent(getApplicationContext(), Lost.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra("message", sms);
//            PendingIntent pendingIntent = PendingIntent.getActivity(MessageActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.setContentIntent(pendingIntent);
//
//            notificationManager.notify(0, builder.build());


            databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/INFO/");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Push the message into message history
                    String content = edittext_chatbox.getText().toString();
                    if (!content.isEmpty()){
                        edittext_chatbox.setText("");
                        User user = dataSnapshot.getValue(User.class);
                        Message message = new Message(content,user.getName(), firebaseAuth.getCurrentUser().getUid());
                        databaseReference = FirebaseDatabase.getInstance().getReference("/MESSAGES/" + messageId);
                        String postUID = databaseReference.push().getKey();
                        databaseReference.child(postUID).setValue(message);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}