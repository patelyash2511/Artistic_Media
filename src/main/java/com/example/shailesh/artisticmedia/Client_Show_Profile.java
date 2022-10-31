package com.example.shailesh.artisticmedia;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Client_Show_Profile extends AppCompatActivity {

    TextView name,emailid;
    CircleImageView circleImageView;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    String currentuserid;
    Button editclientprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__show__profile);

        name=findViewById(R.id.profilename);
        emailid=findViewById(R.id.mailid);
        circleImageView=(CircleImageView) findViewById(R.id.profile_picture);
        editclientprofile=findViewById(R.id.cliedit);

        auth=FirebaseAuth.getInstance();
        currentuserid=auth.getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Client_View");

        editclientprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Client_Show_Profile.this,Client_SecondEdit_profile.class);
                startActivity(intent);

            }
        });


        databaseReference.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String proname=dataSnapshot.child("artistname").getValue().toString();
                    String promail=dataSnapshot.child("artistMail").getValue().toString();
                    String image=dataSnapshot.child("clientprofileimage").getValue().toString();

                    name.setText(proname);
                    emailid.setText(promail);
                    Picasso.get().load(image).into(circleImageView);
                }
                else{

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Client_Show_Profile.this,ClientActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
