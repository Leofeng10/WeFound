package com.example.orbital;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LostItemAdapter extends RecyclerView.Adapter<LostItemAdapter.ViewHolder> {

    public ArrayList<Item> lostItems = new ArrayList<>();
    private Context context;
    Button detailsBtn;
    public RecyclerViewOnClickListener listener;

    public LostItemAdapter(Context context, RecyclerViewOnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lost_item_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.lostitemName.setText(lostItems.get(position).getName());
        holder.lostitemLocation.setText(lostItems.get(position).getLocation());
        //holder.lostitemDescription.setText(lostItems.get(position).getDescription());
        holder.lostitemTime.setText(lostItems.get(position).getTime());
        holder.lostItemImage.setImageBitmap(lostItems.get(position).getImage());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"click on  " + lostItems.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lostItems.size();
    }

    public void setLostItems(ArrayList<Item> lostItems) {
        this.lostItems = lostItems;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView lostitemName, lostitemLocation, lostitemDescription, lostitemTime;
        private ImageView lostItemImage;

        private CardView parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lostitemName = itemView.findViewById(R.id.lostitemName);
            lostitemLocation = itemView.findViewById(R.id.lostitemLocation);
            //lostitemDescription = itemView.findViewById(R.id.lostitemDescription);
            lostitemTime = itemView.findViewById(R.id.lostitemTime);
            parent = itemView.findViewById(R.id.lostitemparent);
            detailsBtn = itemView.findViewById(R.id.LostItemDetailsBtn);
            lostItemImage = itemView.findViewById(R.id.lostitemimage);

            detailsBtn.setOnClickListener(this);

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
