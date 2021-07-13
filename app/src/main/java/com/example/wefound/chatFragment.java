package com.example.wefound;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link chatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class chatFragment extends Fragment {


    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private FirebaseFirestore firestore;

    private ListView listView;

    private SearchView searchView;

    private List<String> messageUsers;
    private List<String> messageIds;

    private String userId;

    private String token;

    private String useridfortoken;



    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public chatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment chatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static chatFragment newInstance(String param1, String param2) {
        chatFragment fragment = new chatFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();

        // If user not login in, return to login activity
        if (firebaseAuth.getCurrentUser() == null){

            startActivity(new Intent(getContext(), Login.class));
        }

        listView = (ListView) getView().findViewById(R.id.listView2);

        searchView = (SearchView) getView().findViewById(R.id.searchView);

        userId = firebaseAuth.getCurrentUser().getUid();

        messageUsers = new ArrayList<>();
        messageIds = new ArrayList<>();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //refreshList(newText);
                return false;
            }
        });

        // Populate all message history
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId + "/CHAT/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageUsers.clear();
                messageIds.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    messageUsers.add(postSnapshot.getKey());
                    messageIds.add(postSnapshot.getValue(String.class));
                }
                MessageViewAdapter messageViewAdapter = new MessageViewAdapter((AppCompatActivity) getActivity(),messageUsers);
                listView.setAdapter(messageViewAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                token = instanceIdResult.getToken();
                GenerateToken(token);

            }
        });


        // Go to a particular message history to message with that user
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String messageUser = messageUsers.get(i);
                Intent intent = new Intent(getContext(), MessageActivity.class);
                intent.putExtra("id", messageUser);



                databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + messageUser);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child("INFO").getValue(User.class);


                        intent.putExtra("name", user.getName());

                        startActivity(intent);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





            }
        });


    }

    private void GenerateToken(String token) {

        useridfortoken = firebaseAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + useridfortoken);
        databaseReference.child("TOKEN").setValue(token);





    }


}