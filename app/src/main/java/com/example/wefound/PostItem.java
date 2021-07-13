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
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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


public class PostItem extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference, databaseUser;
    private StorageTask uploadTask;
    private StorageReference storageRef;

    private ImageView imageView;

    private ProgressDialog progressDialog;

    private Uri imageUri;

    private ProgressBar progressBar;

    Button postBtn, buttonCamera, buttonChooseImage;
    ImageView image, pickLocation;
    EditText name, location, description, time, phone, username;
    private Uri imageFilePath;
    private Bitmap imageToStore;
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] imageInByte;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_LOCATION_REQUEST = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private FusedLocationProviderClient client;

    FirebaseDatabase root;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);
        postBtn = findViewById(R.id.PostBtn);
        name = findViewById(R.id.postLostItemName);
        location = findViewById(R.id.postLostItemLocation);
        description = findViewById(R.id.postLostItemDescription);
        time = findViewById(R.id.postLostItemTime);
        phone = findViewById(R.id.postLostItemPhone);
        username = findViewById(R.id.postLostItemUser);
        image = findViewById(R.id.postItemImage);
        buttonCamera = findViewById(R.id.lost_buttonCamera);
        buttonChooseImage = findViewById(R.id.button_lost_choose_image);
        pickLocation = findViewById(R.id.pickLocationBtn);

        firebaseAuth = FirebaseAuth.getInstance();


        progressDialog = new ProgressDialog(this);
        imageView = (ImageView) findViewById(R.id.postItemImage);

        progressBar = findViewById(R.id.progress_bar);

        // Initialize firestore
        storageRef = FirebaseStorage.getInstance().getReference("Lost");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        postBtn.setOnClickListener(this);
        buttonCamera.setOnClickListener(this);
        buttonChooseImage.setOnClickListener(this);

        client = LocationServices.getFusedLocationProviderClient(PostItem.this);


        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
                Log.d("select", "select location");
            }


        });
    }


    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("select", "no");
            ActivityCompat.requestPermissions(PostItem.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PICK_LOCATION_REQUEST);


        } else {
            Intent i = new Intent(getApplicationContext(), SelectLocation.class);
            startActivity(i);
        }


    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void cameraUpload(final String path, String id) {
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
                Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                Upload upload = new Upload(downloadUrl.toString());

                String myname = name.getText().toString();
                String mylocation = location.getText().toString();
                String mydescription = description.getText().toString();
                String mytime = time.getText().toString();
                String myusername = username.getText().toString();
                String myphone = phone.getText().toString();

                if (myname.equals("") || mylocation.equals("") || mytime.equals("") || myusername.equals("")){
                    Toast.makeText(getApplicationContext(), "You Need to Fill in ALl Information", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseUser currUser = firebaseAuth.getCurrentUser();

                    Item myItem = new Item(myname, mylocation, mydescription, mytime, myphone, myusername, downloadUrl.toString(), currUser.getUid(), id);
                    databaseReference = FirebaseDatabase.getInstance().getReference("/LOST/");
                    databaseReference.child(id).setValue(myItem);
                    Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    Log.d("post", "end");
                }
            }
        });
    }


    private void uploadFile(final String path, String id) {
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

                            //Toast.makeText(UserInfoActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            Upload upload = new Upload(downloadUrl.toString());

                            String myname = name.getText().toString();
                            String mylocation = location.getText().toString();
                            String mydescription = description.getText().toString();
                            String mytime = time.getText().toString();
                            String myusername = username.getText().toString();
                            String myphone = phone.getText().toString();


                            if (myname.equals("") || mylocation.equals("") || mytime.equals("") || myusername.equals("")){
                                Toast.makeText(getApplicationContext(), "You Need to Fill in ALl Information", Toast.LENGTH_SHORT).show();
                            } else {
                                FirebaseUser currUser = firebaseAuth.getCurrentUser();

                                Item myItem = new Item(myname, mylocation, mydescription, mytime, myphone, myusername, downloadUrl.toString(), currUser.getUid(), id);
                                databaseReference = FirebaseDatabase.getInstance().getReference("/LOST/");
                                databaseReference.child(id).setValue(myItem);
                                Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                Log.d("post", "end");
                            }



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "0000000000000000" + e.getMessage(), Toast.LENGTH_LONG).show();
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
            cameraUpload(path, id);
        }
    }


    private void saveProfilePic(String id){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userId = user.getUid();
        uploadFile(userId, id);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).resize(200,200).into(imageView);
        }
        else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageUri = data.getData();
            imageView.setImageBitmap(photo);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == postBtn){
            String uploadid =  databaseReference.push().getKey();
            progressDialog.setMessage("Uploading");
            progressDialog.show();
            Log.d("post", "start");

            saveProfilePic(uploadid);

        } else if (view == buttonCamera){
            // Open camera to take picture
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PostItem.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
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