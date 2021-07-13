package com.example.wefound;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

//public class MessageAdapter extends ArrayAdapter<Message>  {
//
//    private AppCompatActivity context;
//
//    private List<Message> messageList;
//
//    private TextView textViewUser, textViewMessage, textViewTime;
//
//    public static  final int MESSAGE_RIGHT = 0; // FOR USER LAYOUT
//    public static final int MESSAGE_LEFT = 1;
//
//
//    public MessageAdapter(AppCompatActivity context, List<Message> messageList){
//        super(context, R.layout.chatitemleft,messageList);
//        this.context = context;
//        this.messageList = messageList;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        LayoutInflater inflater = context.getLayoutInflater();
//        View messageView = inflater.inflate(R.layout.card_message, null, true);
//
//        // Initialize
//        //textViewUser = (TextView) messageView.findViewById(R.id.textViewUser);
//        textViewMessage = (TextView) messageView.findViewById(R.id.textViewMessage);
//        textViewTime = messageView.findViewById(R.id.textViewTime);
//
//        textViewMessage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//
//        Message message = messageList.get(position);
//
//        // Set message to textview
//        //textViewUser.setText(message.getUser());
//        textViewMessage.setText(message.getText());
//        textViewTime.setText(message.getTime());
//
//        return messageView;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//
//        Message message = messageList.get(position);
//
//        if (message.getUser().equals(firebaseAuth.getCurrentUser().getUid())) {
//
//
//            return MESSAGE_RIGHT;
//
//
//
//        } else {
//
//
//            return MESSAGE_LEFT;
//
//
//        }
//
//    }
//}
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyMessageHolder> {


    List<Message> messageModelList;

    public static  final int MESSAGE_RIGHT = 0; // FOR USER LAYOUT
    public static final int MESSAGE_LEFT = 1; // FOR FRIEND LAYOUT

    public void setMessageModelList(List<Message> messageModelList){

        this.messageModelList = messageModelList;


    }

    @NonNull
    @Override
    public MyMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MESSAGE_RIGHT) {


            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitemright, parent, false);
            return new MyMessageHolder(view);

        } else {


            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitemleft, parent, false);
            return new MyMessageHolder(view);

        }



    }

    @Override
    public void onBindViewHolder(@NonNull MyMessageHolder holder, int position) {


        holder.showMessage.setVisibility(View.VISIBLE);
        holder.time.setVisibility(View.VISIBLE);
        holder.showMessage.setText(messageModelList.get(position).getText());
        holder.time.setText(messageModelList.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        if (messageModelList == null) {
            return 0;
        } else {
            return  messageModelList.size();
        }
    }

    class MyMessageHolder extends RecyclerView.ViewHolder {

        TextView showMessage, time;

        public MyMessageHolder(@NonNull View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.show_message);
            time = itemView.findViewById(R.id.displaytime);

        }
    }


    @Override
    public int getItemViewType(int position) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        Message messageModel = messageModelList.get(position);

        Log.d("ll",messageModel.getUser() +  firebaseAuth.getCurrentUser().getUid());


        if (messageModel.getUid().equals(firebaseAuth.getCurrentUser().getUid())) {
            return MESSAGE_RIGHT;

        } else {
            return MESSAGE_LEFT;
        }

    }
}
