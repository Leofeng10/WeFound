package com.example.wefound;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageViewAdapter2 extends RecyclerView.Adapter<MessageViewAdapter2.ViewHolder>{

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private AppCompatActivity context;

    private List<String> messageIds, messageUsers;

    private TextView textViewUser, textViewMessage, textViewTime;

    private String messageId;

    public MessageViewAdapter2.RecyclerViewOnClickListener listener;
    // Pass in message list
    public MessageViewAdapter2(AppCompatActivity context, List<String> messageUsers, MessageViewAdapter2.RecyclerViewOnClickListener listener){

        this.context = context;
        this.messageUsers = messageUsers;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_message, parent, false);
        MessageViewAdapter2.ViewHolder holder = new MessageViewAdapter2.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String messageUser = messageUsers.get(position);



        firebaseAuth = FirebaseAuth.getInstance();

        // Get the user name who sent the message
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + messageUser);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child("INFO").getValue(User.class);
                //Log.d("position", position+"-----"+user.getName() +"---"+textViewUser.getText());
                holder.textViewUser.setText(user.getName());
                Log.d("position", position+"-----"+user.getName() +"---"+holder.textViewUser.getText());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Get the message
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + messageUser + "/CHAT/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userId = firebaseAuth.getCurrentUser().getUid();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String findUser = postSnapshot.getKey();
                    if (userId.equals(findUser)){
                        messageId = postSnapshot.getValue(String.class);
                    }
                    databaseReference = FirebaseDatabase.getInstance().getReference("/MESSAGES/" + messageId);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                Message message = postSnapshot.getValue(Message.class);
                                holder.textViewMessage.setText(message.getText());
                                //textViewTime.setText(message.getTime());
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return messageUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textViewUser, textViewMessage;


        private CardView parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUser = itemView.findViewById(R.id.textViewUser);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);


        }


        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewOnClickListener {
        void onClick(View v, int position);
    }
}
