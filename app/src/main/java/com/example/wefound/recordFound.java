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
 * Use the {@link recordFound#newInstance} factory method to
 * create an instance of this fragment.
 */
public class recordFound extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private FirebaseAuth firebaseAuth;
    private RecyclerView foundRecItem;
    private DatabaseReference foundItemReference;
    private ProgressDialog progressDialog;

    private RecordAdapter.RecyclerViewOnClickListener listener;
    private RecordAdapter.RecyclerViewOnClickListener deleteListener;
    private RecordAdapter.RecyclerViewOnClickListener deleteimageListener;

    public recordFound() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment recordFound.
     */
    // TODO: Rename and change types and number of parameters
    public static recordFound newInstance(String param1, String param2) {
        recordFound fragment = new recordFound();
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
        return inflater.inflate(R.layout.fragment_record_found, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userId = user.getUid();

        foundRecItem = getView().findViewById(R.id.recordfoundlistView);



        ArrayList<Item> foundItems = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();









        foundItemReference = FirebaseDatabase.getInstance().getReference("/FOUND/" );
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Retrieving data...");
        progressDialog.show();

        foundItemReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Item mItem = postSnapshot.getValue(Item.class);
                    if (mItem.getUserID().equals(userId))
                        foundItems.add(mItem);

                    RecordAdapter adapter = new RecordAdapter(getContext(), foundItems, listener, deleteListener, deleteimageListener);
                    foundRecItem.setAdapter(adapter);
                    foundRecItem.setLayoutManager(new GridLayoutManager(getContext(), 1));
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
                i.putExtra("name", foundItems.get(position).getName());
                i.putExtra("location", foundItems.get(position).getLocation());
                i.putExtra("time", foundItems.get(position).getTime());
                i.putExtra("description", foundItems.get(position).getDescription());
                i.putExtra("phone", foundItems.get(position).getPhone());
                i.putExtra("username", foundItems.get(position).getUsername());
                i.putExtra("image", foundItems.get(position).getImageurl());
                i.putExtra("id", foundItems.get(position).getUserID());
                startActivity(i);
            }
        };

        deleteListener = new RecordAdapter.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View v, int position) {

                String itemID = foundItems.get(position).getmyItemID();

                DatabaseReference lostItemReference = FirebaseDatabase.getInstance().getReference("/FOUND/" ).child(itemID);

                lostItemReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getFragmentManager().beginTransaction().replace(R.id.record_container, new recordFound()).commit();
                        Log.d("delete", "success-------");
                    }
                });
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