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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
 * Use the {@link recordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class recordFragment extends Fragment {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference lostItemReference;

    private User currUser;
    private RecyclerView lostRecItem;
    TextView greeting;
    Button postLostBtn;

    private ProgressDialog progressDialog;


    private LostItemAdapter.RecyclerViewOnClickListener listener;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public recordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment recordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static recordFragment newInstance(String param1, String param2) {
        recordFragment fragment = new recordFragment();
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
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {



        greeting = (TextView) getView().findViewById(R.id.recordgreetingMSG);
        postLostBtn = getView().findViewById(R.id.editBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userId = user.getUid();

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("USERS").child(userId).child("INFO");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Retrieving data...");
        progressDialog.show();

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String school = snapshot.child("school").getValue(String.class);
                    String id = snapshot.child("id").getValue(String.class);
                    String phoneNum = snapshot.child("phoneNum").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    currUser = new User(name, school, id, email, phoneNum);
                    greeting.setText("Hi " + currUser.getName());
                } else  {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




        postLostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PostItem.class);
                startActivity(i);
            }
        });

        lostRecItem = getView().findViewById(R.id.recordlistView);

        ArrayList<String> referenceList = new ArrayList<>();

        ArrayList<Item> lostItems = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currUser = firebaseAuth.getCurrentUser();

        lostItemReference = FirebaseDatabase.getInstance().getReference("/LOST/" );



        lostItemReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Item mItem = postSnapshot.getValue(Item.class);
                    if (mItem.getUserID().equals(userId))
                        lostItems.add(mItem);

                    RecordAdapter adapter = new RecordAdapter(getContext(), lostItems, listener);
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

        listener = new LostItemAdapter.RecyclerViewOnClickListener() {
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


    }

}