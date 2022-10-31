package com.example.shailesh.artisticmedia;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ClientActivity extends AppCompatActivity {
    ListView listViewArtist;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<Artist> Artistlist;
    SearchView searchView;
    String userId;
    Context context;
    Show_Artist_List adpter;
    Query queryy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        context=this;
        Artistlist=new ArrayList<>();
        listViewArtist=findViewById(R.id.listartist);
        searchView = findViewById(R.id.searchartist);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser!=null;
        userId=firebaseUser.getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference("Artistss");



        /*SearchView searchView;
        searchView = (SearchView) menu.findItem(R.id.listartist).getActionView();
        searchView.setIconified(false);
        searchView.clearFocus();*/



        listViewArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ClientActivity.this,Client_detail.class);

                TextView mname=view.findViewById(R.id.showartistname);
                TextView mcategory=view.findViewById(R.id.showArtistCategory);
                TextView mdescription=view.findViewById(R.id.showArtistdescription);
                TextView memail=view.findViewById(R.id.showartistmail);
                ImageView mimage=view.findViewById(R.id.imgArticle);

                String aname=mname.getText().toString();
                String acategory=mcategory.getText().toString();
                String adescription=mdescription.getText().toString();
                String aemail=memail.getText().toString();

                Drawable mDrawable=mimage.getDrawable();
                Bitmap mBitmap=((BitmapDrawable)mDrawable).getBitmap();

                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                byte[] bytes=stream.toByteArray();
                intent.putExtra("image",bytes);
                intent.putExtra("name",aname);
                intent.putExtra("category",acategory);
                intent.putExtra("descriptionn",adescription);
                intent.putExtra("email",aemail);
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_item,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.show_profilee:
                showpro();
                return true;

            case R.id.logoutt:
                FirebaseAuth.getInstance().signOut();
                logout();
                return true;
            case R.id.contactus:
                hire();
                return true;



        }
        return super.onOptionsItemSelected(item);
    }

    private void showpro() {

        Intent intent=new Intent(ClientActivity.this,Client_Show_Profile.class);
        startActivity(intent);
    }

    private void hire() {

        Intent email = new Intent(Intent.ACTION_SENDTO);
        email.setType("message/rfc822");
        email.setData(Uri.parse("mailto:artisticmedia054@gmail.com"));
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"artisticmedia054@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Hire");

        startActivity(Intent.createChooser(email, "Choose an Email client:"));
    }



    private void logout() {
        Intent intent=new Intent(ClientActivity.this,MainActivity.class);
        startActivity(intent);
    }




    @Override
    protected void onStart() {
        super.onStart();
        final ProgressDialog progressDialog=new ProgressDialog(ClientActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Artistlist.clear();
                if (dataSnapshot.exists()){

                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                        Artist artist=snapshot.getValue(Artist.class);
                        Artistlist.remove(userId);
                        Artistlist.add(artist);
                    }
                }
                else {
                    Toast.makeText(context, "Data Not Available", Toast.LENGTH_SHORT).show();
                }

                adpter=new Show_Artist_List(ClientActivity.this,Artistlist);
                listViewArtist.setAdapter(adpter);

                progressDialog.dismiss();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                firebaseSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String ss) {
                firebaseSearch(ss);
                return true;
            }
        });


    }

    private void firebaseSearch(String s) {

        queryy=databaseReference.orderByChild("artistTitle").startAt(s).endAt(s + "\uf8ff");
        queryy.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Artistlist.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                        Artist artist=snapshot.getValue(Artist.class);
                        Artistlist.remove(userId);
                        Artistlist.add(artist);
                    }
                }
                adpter=new Show_Artist_List(ClientActivity.this,Artistlist);
                listViewArtist.setAdapter(adpter);
                adpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

   /* @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ClientActivity  .this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }*/
}
