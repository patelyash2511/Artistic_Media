package com.example.shailesh.artisticmedia;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ArtistActivity extends AppCompatActivity  {

    private static final int PICK_IMAGE_REQUEST = 234;
    TextView getArtist;
    //1
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    String currentuserid;
    StorageReference mstorag;

    Spinner spinner;
    EditText name,desc,emailid;
    Button savebio,logoutuser,profile;
    String spintext;
    String userId;



    ImageView profile_picture;
    private Uri filepath;
    public StorageReference storageReference;
    UploadTask uploadTask;

    //
    String proname,prodesc,promail,id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);


        name=findViewById(R.id.profilename);
        desc=findViewById(R.id.Description);
        emailid=findViewById(R.id.mailid);
        savebio=findViewById(R.id.save);
        logoutuser=findViewById(R.id.logout);
        profile=findViewById(R.id.Show_profle);
        spinner=findViewById(R.id.spinner);

        profile_picture=findViewById(R.id.profile_picture);

        //1
        auth=FirebaseAuth.getInstance();
        currentuserid=auth.getCurrentUser().getUid();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Artistss").child(currentuserid);
        mstorag=FirebaseStorage.getInstance().getReference();


//         mstorag = storageReference.child("uploads"+System.currentTimeMillis()+"."+getFileExtension(filepath));

        storageReference=FirebaseStorage.getInstance().getReference();

        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filechooser();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ArtistActivity.this,Show_Artist_Profile.class);
                startActivity(intent);
            }
        });

        savebio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proname= name.getText().toString();
                prodesc= desc.getText().toString();
                promail= emailid.getText().toString();

                spintext = spinner.getSelectedItem().toString();


                if(proname.isEmpty())
                {
                    name.setError("Enter Name");
                    name.requestFocus();
                    return;
                }
                else if(prodesc.isEmpty())
                {
                    desc.setError("Enter Description");
                    desc.requestFocus();
                    return;

                }
                else if(promail.isEmpty())
                {
                    emailid.setError("Enter Email");
                    emailid .requestFocus();
                    return;
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(promail).matches())
                {
                    emailid.setError("Enter a valid Email");
                    emailid .requestFocus();
                    return;
                }
                else {
                    final ProgressDialog progressDialog = new ProgressDialog(ArtistActivity.this);
                    progressDialog.setTitle("Uploading...");

                    HashMap usermap = new HashMap();
                    usermap.put("artistname", proname);
                    usermap.put("artistMail", promail);
                    usermap.put("artistDescription", prodesc);
                    usermap.put("artistTitle", spintext);
                    progressDialog.show();

                    databaseReference.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(ArtistActivity.this, "error occured", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                    if (filepath != null) {

                        final ProgressDialog progressDialog1 = new ProgressDialog(ArtistActivity.this);
                        progressDialog1.setTitle("Uploading...");


                        final StorageReference riversRef = mstorag.child(currentuserid + "." + getFileExtension(filepath));

                        uploadTask = riversRef.putFile(filepath);

                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return riversRef.getDownloadUrl();

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                progressDialog1.show();

                                if (task.isSuccessful()) {
                                    progressDialog1.dismiss();
                                    Uri downloadUri = task.getResult();
                                    String profileImageUrl = downloadUri.toString();
                                    databaseReference.child("profileimage").setValue(profileImageUrl);
                                    Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_LONG).show();

                                }
                            }
                        });

                    } else {

                        Toast.makeText(ArtistActivity.this, "No File Selected", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        logoutuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                logout();
            }
        });
    }

    public void logout()
    {
        Intent intent=new Intent(ArtistActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void filechooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Choose an image"),PICK_IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filepath = data.getData();

            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                profile_picture.setImageBitmap(bitmap);



            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this,"Select Image",Toast.LENGTH_LONG).show();
        }
    }
}