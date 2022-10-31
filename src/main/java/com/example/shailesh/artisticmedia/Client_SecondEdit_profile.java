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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

public class Client_SecondEdit_profile extends AppCompatActivity {

    EditText name,emailid;
    Button savebio;
    ImageView profile_picture;

    private static final int PICK_IMAGE_REQUEST = 234;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    String currentuserid;
    StorageReference mstorag;

    private Uri filepath;
    public StorageReference storageReference;
    UploadTask uploadTask;
    String proname,promail,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__second_edit_profile);

        name=findViewById(R.id.profilename);
        emailid=findViewById(R.id.mailid);
        savebio=findViewById(R.id.save);
        profile_picture=findViewById(R.id.profile_picture);


        auth=FirebaseAuth.getInstance();
        currentuserid=auth.getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Client_View").child(currentuserid);
        mstorag= FirebaseStorage.getInstance().getReference();

        storageReference=FirebaseStorage.getInstance().getReference();

        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filechooser();
            }
        });

        savebio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                proname= name.getText().toString();
                promail= emailid.getText().toString();

                final ProgressDialog progressDialog = new ProgressDialog(Client_SecondEdit_profile.this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                HashMap usermap=new HashMap();
                usermap.put("artistname",proname);
                usermap.put("artistMail",promail);

                databaseReference.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                        }
                        else {
                            Toast.makeText(Client_SecondEdit_profile.this, "error occured", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                if(filepath != null) {

                    final ProgressDialog progressDialog1 = new ProgressDialog(Client_SecondEdit_profile.this);
                    progressDialog1.setTitle("Uploading...");
                    progressDialog1.show();


                    final StorageReference riversRef = mstorag.child(currentuserid+"."+getFileExtension(filepath));

                    uploadTask = riversRef.putFile(filepath);

                    Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                            if (task.isSuccessful()){
                                progressDialog1.dismiss();
                                Uri downloadUri = task.getResult();
                                String profileImageUrl = downloadUri.toString();
                                databaseReference.child("clientprofileimage").setValue(profileImageUrl);
                                Toast.makeText(getApplicationContext(),"File Uploaded", Toast.LENGTH_LONG).show();

                                Intent ii=new Intent(Client_SecondEdit_profile.this,ClientActivity.class);
                                startActivity(ii);

                            }
                        }
                    });

                }
                else {

                    Toast.makeText(Client_SecondEdit_profile.this, "No File Selected", Toast.LENGTH_SHORT).show();
                }

            }
        });
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
    }
}
