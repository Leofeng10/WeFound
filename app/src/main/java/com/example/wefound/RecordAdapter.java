package com.example.wefound;

import android.content.Context;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder_Record> {

    public ArrayList<Item> lostItems;
    private Context context;
    Button detailsBtn;
    public LostItemAdapter.RecyclerViewOnClickListener listener;

    public RecordAdapter(Context context, ArrayList<Item> lostItems,  LostItemAdapter.RecyclerViewOnClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.lostItems = lostItems;
    }

    public RecordAdapter(Context context, ArrayList<Item> lostItems) {
        this.context = context;
        this.lostItems = lostItems;
    }

    @NonNull
    @Override
    public ViewHolder_Record onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item_list, parent, false);
        ViewHolder_Record holder = new ViewHolder_Record(view);
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
    }



    @Override
    public int getItemCount() {
        return lostItems.size();
    }

    public void setLostItems(ArrayList<Item> lostItems) {
        this.lostItems = lostItems;
        notifyDataSetChanged();
    }

    public class ViewHolder_Record extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView lostitemName, lostitemLocation, lostitemTime;
        private ImageView lostItemImage;

        private CardView parent;
        public ViewHolder_Record(@NonNull View itemView) {
            super(itemView);
            lostitemName = itemView.findViewById(R.id.recorditemName);
            lostitemLocation = itemView.findViewById(R.id.recorditemLocation);
            lostitemTime = itemView.findViewById(R.id.recorditemTime);
            parent = itemView.findViewById(R.id.recorditemparent);
            detailsBtn = itemView.findViewById(R.id.recordItemDetailsBtn);
            lostItemImage = itemView.findViewById(R.id.recorditemimage);

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
