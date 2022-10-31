package com.example.shailesh.artisticmedia;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class Show_Artist_Profile extends AppCompatActivity {
    TextView name,desc,emailid,spintext;
    CircleImageView circleImageView;
    Button logout,editdetail;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    String currentuserid;
    StorageReference mstorag;

    String proname;
    String prodesk;
    String promail;
    String prospintext;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__artist__profile);

        name=findViewById(R.id.profilename);
        desc=findViewById(R.id.Description);
        emailid=findViewById(R.id.mailid);
        spintext=findViewById(R.id.category);
        circleImageView=(CircleImageView) findViewById(R.id.profile_picture);
        logout = (Button) findViewById(R.id.Logout);
        editdetail = findViewById(R.id.save);

        auth=FirebaseAuth.getInstance();
        currentuserid=auth.getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Artistss");

        databaseReference.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    proname=dataSnapshot.child("artistname").getValue().toString();
                    prodesk=dataSnapshot.child("artistDescription").getValue().toString();
                    promail=dataSnapshot.child("artistMail").getValue().toString();
                    prospintext=dataSnapshot.child("artistTitle").getValue().toString();
                    image=dataSnapshot.child("profileimage").getValue().toString();

                    name.setText(proname);
                    desc.setText(Html.fromHtml(prodesk));
                    desc.setMovementMethod(LinkMovementMethod.getInstance());
                    emailid.setText(promail);
                    spintext.setText(prospintext);
                    Log.d("urlllll", "getView: "+image);
                    Picasso.get().load(image).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Show_Artist_Profile.this,MainActivity.class);
                startActivity(intent);
            }
        });

        editdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Show_Artist_Profile.this,ArtistActivity.class);
                startActivity(intent);
            }
        });

    }

}
