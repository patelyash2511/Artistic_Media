package com.example.shailesh.artisticmedia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Client_detail extends AppCompatActivity {

    TextView arnamee,categoryy,ardescri,aremail;
    ImageView aarprofile;
    Button hirebtn;
    String artist_name,artist_title;
    String namee,cate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);

        arnamee=findViewById(R.id.name);
        categoryy=findViewById(R.id.titlee);
        ardescri=findViewById (R.id.descripiion);
        aarprofile=findViewById(R.id.arprofile);
        //aremail=findViewById(R.id.artist_email);
        hirebtn=findViewById(R.id.hireart);

        byte[] bytes=getIntent().getByteArrayExtra("image");
        Bitmap bmp= BitmapFactory.decodeByteArray(bytes,0,bytes.length);

        namee=getIntent().getStringExtra("name");
        cate=getIntent().getStringExtra("category");
        String description=getIntent().getStringExtra("descriptionn");
        Log.d("aaaaaa", "onCreate: "+description);

        //String mail=getIntent().getStringExtra("email");
        //Log.d("aaaaaa","onCreate: "+mail);

        arnamee.setText(namee);
        categoryy.setText(cate);
        ardescri.setText(description);
        //aremail.setText(mail);
        aarprofile.setImageBitmap(bmp);
        artist_name=arnamee.toString();
        artist_title=categoryy.toString();

        hirebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent email = new Intent(Intent.ACTION_SENDTO);
                email.setType("message/rfc822");
                email.setData(Uri.parse("mailto:artisticmedia054@gmail.com"));
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"artisticmedia054@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Hire");

                email.putExtra(Intent.EXTRA_TEXT,"Artist Name:- "+namee+"\nArtist Title:- "+cate +"\n\n Describe Your Project Idea Here:");

                startActivity(Intent.createChooser(email, "Choose an Email client:"));
            }
        });


    }
}
