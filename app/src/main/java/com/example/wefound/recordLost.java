package com.example.wefound;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link recordLost#newInstance} factory method to
 * create an instance of this fragment.
 */
public class recordLost extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private FirebaseAuth firebaseAuth;
    private RecyclerView lostRecItem;
    private DatabaseReference lostItemReference;
    private ProgressDialog progressDialog;

    private RecordAdapter.RecyclerViewOnClickListener listener;
    private RecordAdapter.RecyclerViewOnClickListener deleteListener;
    private RecordAdapter.RecyclerViewOnClickListener deleteimageListener;

    public recordLost() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment recordLost.
     */
    // TODO: Rename and change types and number of parameters
    public static recordLost newInstance(String param1, String param2) {
        recordLost fragment = new recordLost();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record_lost, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userId = user.getUid();

        lostRecItem = getView().findViewById(R.id.recordlostlistView);



        ArrayList<Item> lostItems = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();









        lostItemReference = FirebaseDatabase.getInstance().getReference("/LOST/" );
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Retrieving data...");
        progressDialog.show();

        lostItemReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lostItems.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Item mItem = postSnapshot.getValue(Item.class);
                    if (mItem.getUserID().equals(userId))
                        lostItems.add(mItem);

                    RecordAdapter adapter = new RecordAdapter(getContext(), lostItems, listener, deleteListener, deleteimageListener);
                    lostRecItem.setAdapter(adapter);
                    lostRecItem.setLayoutManager(new GridLayoutManager(getContext(), 1));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Fail to retrieve lost items " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        listener = new RecordAdapter.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent i = new Intent(getContext(), LostItemDetail.class);
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

        deleteListener = new RecordAdapter.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View v, int position) {

                String itemID = lostItems.get(position).getmyItemID();

                DatabaseReference lostItemReference = FirebaseDatabase.getInstance().getReference("/LOST/" ).child(itemID);
                lostItemReference.removeValue();

//
            }
        };


        deleteimageListener = new RecordAdapter.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View v, int position) {
                ImageView imageView = v.findViewById(R.id.deleteimage);
                imageView.setVisibility(View.INVISIBLE);

            }
        };

    }
}