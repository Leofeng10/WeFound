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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class PostFoundItem extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference, databaseUser;
    private StorageTask uploadTask;
    private StorageReference storageRef;

    private ImageView imageView;

    private Uri imageUri;

    private ProgressBar progressBar;

    private ProgressDialog progressDialog;

    Button postBtn, buttonCamera, buttonChooseImage;
    ImageView image;
    EditText name, location, description, time, phone, username;
    private Uri imageFilePath;
    private Bitmap imageToStore;
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] imageInByte;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    FirebaseDatabase root;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_found_item);
        postBtn = findViewById(R.id.PostFoundBtn);
        name = findViewById(R.id.postFoundItemName);
        location = findViewById(R.id.postFoundItemLocation);
        description = findViewById(R.id.postFoundItemDescription);
        time = findViewById(R.id.postFoundItemTime);
        phone = findViewById(R.id.postFoundItemPhone);
        username = findViewById(R.id.postFoundItemUser);
        image = findViewById(R.id.postFoundItemImage);
        buttonCamera = findViewById(R.id.found_buttonCamera);
        buttonChooseImage = findViewById(R.id.button_found_choose_image);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        imageView = (ImageView) findViewById(R.id.postFoundItemImage);

        progressBar = findViewById(R.id.progress_bar);

        // Initialize firestore
        storageRef = FirebaseStorage.getInstance().getReference("FOUND");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        postBtn.setOnClickListener(this);
        buttonCamera.setOnClickListener(this);
        buttonChooseImage.setOnClickListener(this);
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
                    databaseReference = FirebaseDatabase.getInstance().getReference("/FOUND/");
                    databaseReference.child(id).setValue(myItem);
                    Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    Intent i = new Intent(getApplicationContext(), Lost.class);

                    Log.d("post", "end");
                    startActivity(i);
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
                                databaseReference = FirebaseDatabase.getInstance().getReference("/FOUND/");
                                databaseReference.child(id).setValue(myItem);
                                Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                Intent i = new Intent(getApplicationContext(), Lost.class);

                                Log.d("post", "end");
                                startActivity(i);

                            }



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "0000000000000000" + e.getMessage(), Toast.LENGTH_LONG).show();
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
                ActivityCompat.requestPermissions(PostFoundItem.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
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