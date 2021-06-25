package com.example.wefound;

import android.content.Context;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder_Record> {

    public ArrayList<Item> lostItems;
    private Context context;
    Button detailsBtn;
    ViewHolder_Record holder;
    boolean isSelected = false;
    public RecordAdapter.RecyclerViewOnClickListener listener;
    public RecordAdapter.RecyclerViewOnClickListener deletelistener;
    public RecordAdapter.RecyclerViewOnClickListener deleteimageListener;

    public RecordAdapter(Context context, ArrayList<Item> lostItems,  RecordAdapter.RecyclerViewOnClickListener listener, RecordAdapter.RecyclerViewOnClickListener deletelistener, RecordAdapter.RecyclerViewOnClickListener deleteimageListener) {
        this.context = context;
        this.listener = listener;
        this.lostItems = lostItems;
        this.deletelistener = deletelistener;
        this.deleteimageListener = deleteimageListener;
    }

    public RecordAdapter(Context context, ArrayList<Item> lostItems) {
        this.context = context;
        this.lostItems = lostItems;
    }

//    @Override
//    public boolean onPrepareActionMode(ActionMode actionMode) {
//        clickItem(holder);
//        return false;
//    }

    @NonNull
    @Override
    public ViewHolder_Record onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item_list, parent, false);
        holder = new ViewHolder_Record(view);
        return holder;
    }


    public String shortenString(String s) {
        if (s.length() > 10) {
            String mys = s.substring(0, 10);
            mys += "...";
            return mys;
        } else {
            return s;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_Record holder, int position) {
        holder.lostitemName.setText(shortenString(lostItems.get(position).getName()));
        holder.lostitemLocation.setText(shortenString(lostItems.get(position).getLocation()));
        holder.lostitemTime.setText(shortenString(lostItems.get(position).getTime()));
        Picasso.get().load(lostItems.get(position).getImageurl()).fit().centerCrop().into(holder.lostItemImage);
//        String itemID = lostItems.get(position).getmyItemID();
//        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                DatabaseReference lostItemReference = FirebaseDatabase.getInstance().getReference("/LOST/" ).child(itemID);
//                lostItemReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        lostItems.remove(position);
//                        //Toast.makeText(, "Delete successfully", Toast.LENGTH_SHORT).show();
//                        Log.d("delete", "success-------");
//                    }
//                });
////                DatabaseReference lostItemReference = FirebaseDatabase.getInstance().getReference("/LOST/" );
////                Query myquery = lostItemReference.orderByKey().equalTo(itemID);
////                myquery.addListenerForSingleValueEvent(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(@NonNull DataSnapshot snapshot) {
////                        for (DataSnapshot sp : snapshot.getChildren()) {
////
////                        }
////                    }
////
////                    @Override
////                    public void onCancelled(@NonNull DatabaseError error) {
////
////                    }
////                });
//            }
//        });


    }



    @Override
    public int getItemCount() {
        return lostItems.size();
    }

    public void setLostItems(ArrayList<Item> lostItems) {
        this.lostItems = lostItems;
        notifyDataSetChanged();
    }

    public class ViewHolder_Record extends RecyclerView.ViewHolder{

        private TextView lostitemName, lostitemLocation, lostitemTime;
        private ImageView lostItemImage;
        private ImageView deleteImage;



        private CardView parent;
        public ViewHolder_Record(@NonNull View itemView) {
            super(itemView);
            lostitemName = itemView.findViewById(R.id.recorditemName);
            lostitemLocation = itemView.findViewById(R.id.recorditemLocation);
            lostitemTime = itemView.findViewById(R.id.recorditemTime);
            parent = itemView.findViewById(R.id.recorditemparent);
            detailsBtn = itemView.findViewById(R.id.recordItemDetailsBtn);
            lostItemImage = itemView.findViewById(R.id.recorditemimage);
            deleteImage = itemView.findViewById(R.id.deleteimage);


            detailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v, getAdapterPosition());
                }
            });
            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletelistener.onClick(v, getAdapterPosition());
                }
            });



        }


    }

    public interface RecyclerViewOnClickListener {
        void onClick(View v, int position);
    }
}
