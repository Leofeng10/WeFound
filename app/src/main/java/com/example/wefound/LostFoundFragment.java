package com.example.wefound;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LostFoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LostFoundFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    Button lostBtn, fountBtn;

    public LostFoundFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LostFoundFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LostFoundFragment newInstance(String param1, String param2) {
        LostFoundFragment fragment = new LostFoundFragment();
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


        return inflater.inflate(R.layout.fragment_lost_found, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        lostBtn = getView().findViewById(R.id.lostBtn);
        fountBtn = getView().findViewById(R.id.foundBtn);

        FirebaseAuth firebaseAuth;

        DatabaseReference databaseReference;

        firebaseAuth = FirebaseAuth.getInstance();

        String userId = firebaseAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId);








        lostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child("INFO").getValue(User.class);
                        if (user == null) {
                            Toast.makeText(getContext(), "Please finish your profile", Toast.LENGTH_SHORT);

                            Intent i = new Intent(getContext(), UserInfoActivity.class);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(getContext(), Lost.class);
                            startActivity(i);
                        }




                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        fountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child("INFO").getValue(User.class);
                        if (user == null) {
                            Toast.makeText(getContext(), "Please finish your profile", Toast.LENGTH_SHORT);
                            Intent i = new Intent(getContext(), UserInfoActivity.class);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(getContext(), Found.class);
                            startActivity(i);
                        }




                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}