package com.example.wefound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;


    private DatabaseReference databaseReference, databaseUser;
    private StorageTask uploadTask;
    private StorageReference storageRef;

    private ImageView imageView, imageViewBack;
    private TextInputEditText editTextName, editTextSchool, editTextId, editTextPhoneNum;
    private Button buttonSave, buttonChooseImage, buttonCamera;

    private ProgressBar progressBar;

    private ProgressDialog progressDialog;
    private Uri imageUri;

    private String userId;
    private String imageUrl;
    private String imageName;

    private String UserName, UserFaculty, UserSchoolID, UserPhoneNumber, UserInfo;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        setContentView(R.layout.activity_user_info);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        String email = firebaseAuth.getCurrentUser().getEmail();
        // If user not login in, return to login activity
        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, Login.class));
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize
        imageView = (ImageView) findViewById(R.id.imageView);
        imageViewBack = findViewById(R.id.imageViewBack);

        editTextName = (TextInputEditText) findViewById(R.id.editTextName);
        editTextSchool = (TextInputEditText) findViewById(R.id.editTextSchool);
        editTextId = (TextInputEditText) findViewById(R.id.editTextId);
        editTextPhoneNum = (TextInputEditText) findViewById(R.id.editTextPhoneNum);

        UserName = editTextName.getText().toString();
        UserFaculty = editTextSchool.getText().toString();
        UserSchoolID = editTextId.getText().toString();
        UserPhoneNumber = editTextPhoneNum.getText().toString();
        UserInfo = UserName + "/" + UserFaculty + "/" + UserSchoolID + "/" + UserPhoneNumber;

        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonCamera = (Button) this.findViewById(R.id.buttonCamera);
        buttonChooseImage = (Button) findViewById(R.id.button_choose_image);

        progressBar = findViewById(R.id.progress_bar);



        FirebaseUser user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        Log.d("UserID", userId+"   " + user.getEmail());

        // Set listeners
        buttonSave.setOnClickListener(this);
        buttonCamera.setOnClickListener(this);
        buttonChooseImage.setOnClickListener(this);

        // Initialize firestore
        storageRef = FirebaseStorage.getInstance().getReference("/Profile");

        // Populate user information if it is there
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId);
        databaseUser = FirebaseDatabase.getInstance().getReference();

        Log.d("-------------",databaseUser.toString());


        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        loadUserProfile();
    }




    private void loadUserProfile() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                String photoUrl = user.getPhotoUrl().toString();
                Glide.with(this).load(photoUrl).into(imageView);
            }
            if (user.getDisplayName() != null) {
                String[] info = user.getDisplayName().split("/");

                editTextName.setText(info[0]);
                editTextSchool.setText(info[1]);
                editTextId.setText(info[2]);
                editTextPhoneNum.setText(info[3]);
            }
        }
    }

    // Open photo gallery
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Get file extension
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // Uploading image to firebase and store it in firestore through camera
    private void cameraUpload(final String path) {
        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.child(System.currentTimeMillis() + "" ).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(UserInfoActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                Upload upload = new Upload(downloadUrl.toString());

                UserName = editTextName.getText().toString();
                UserFaculty = editTextSchool.getText().toString();
                UserSchoolID = editTextId.getText().toString();
                UserPhoneNumber = editTextPhoneNum.getText().toString();
                UserInfo = UserName + "/" + UserFaculty + "/" + UserSchoolID + "/" + UserPhoneNumber;

                if (UserName.equals("") || UserFaculty.equals("") || UserSchoolID.equals("") || UserPhoneNumber.equals("")){
                    Toast.makeText(getApplicationContext(), "You Need to Fill in ALl Information", Toast.LENGTH_SHORT).show();
                } else {

                    UserProfileChangeRequest profile =
                            new UserProfileChangeRequest.Builder().setDisplayName(UserInfo).setPhotoUri(downloadUrl).build();

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Profile Updates", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
                databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + path);
                databaseReference.child("IMAGE").setValue(upload);
            }
        });
    }

    // Uploading image to firebase and store it in firestore through image gallery
    private void uploadFile(final String path) {
        if (imageUri != null) {
            StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);


                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            Upload upload = new Upload(downloadUrl.toString());

                            UserName = editTextName.getText().toString();
                            UserFaculty = editTextSchool.getText().toString();
                            UserSchoolID = editTextId.getText().toString();
                            UserPhoneNumber = editTextPhoneNum.getText().toString();
                            UserInfo = UserName + "/" + UserFaculty + "/" + UserSchoolID + "/" + UserPhoneNumber;

                            if (UserName.equals("") || UserFaculty.equals("") || UserSchoolID.equals("") || UserPhoneNumber.equals("")){
                                Toast.makeText(getApplicationContext(), "You Need to Fill in ALl Information", Toast.LENGTH_SHORT).show();
                            } else {
                                UserProfileChangeRequest profile =
                                        new UserProfileChangeRequest.Builder().setDisplayName(UserInfo).setPhotoUri(downloadUrl).build();

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Profile Updates", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }



                            databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + path);
                            databaseReference.child("IMAGE").setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        }
        else {
            cameraUpload(path);
        }
    }

    // Save user profile picture to firebase
    private void saveProfilePic(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userId = user.getUid();
        uploadFile(userId);
    }

    // Save user information to firebase
    private void addUser() {
        FirebaseUser currUser = firebaseAuth.getCurrentUser();
        String name = editTextName.getText().toString().trim();
        String school = editTextSchool.getText().toString().trim();
        String id = editTextId.getText().toString().trim();
        String phoneNum = editTextPhoneNum.getText().toString().trim();

        User user = new User(name,school,id,currUser.getEmail(),phoneNum);
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + currUser.getUid());

        databaseReference.child("INFO").setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Fail to Update", Toast.LENGTH_SHORT).show();
            }
        });

    }

//    private void saveUser(){
//        FirebaseUser currUser = firebaseAuth.getCurrentUser();
//
//        String name = editTextName.getText().toString().trim();
//        String school = editTextSchool.getText().toString().trim();
//        String id = editTextId.getText().toString().trim();
//        String phoneNum = editTextPhoneNum.getText().toString().trim();
//
//        User user = new User(name,school,id,currUser.getEmail(),phoneNum);
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/");
//        databaseReference.child(currUser.getUid().toString()).child("INFO").setValue(user);
//        Toast.makeText(this, "Information Saved...", Toast.LENGTH_LONG).show();
//    }

    // Request user permission for camera
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_SHORT).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Request user permission for camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).resize(300,300).into(imageView);
        }
        else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSave){
            // Save user information
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            saveProfilePic();
            //saveUser();
            addUser();

        }
        else if (view == buttonCamera){
            // Open camera to take picture
            if (ContextCompat.checkSelfPermission(UserInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UserInfoActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }
            else {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
        else if (view == buttonChooseImage){
            // Open photo gallery to choose image
            openFileChooser();
        }
    }
}