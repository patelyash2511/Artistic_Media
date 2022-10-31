package com.example.shailesh.artisticmedia;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.ContentValues.TAG;

public class Show_Artist_List extends ArrayAdapter<Artist> {

    private Activity context;
    private List<Artist> Artistlist;


    public Show_Artist_List(@NonNull Activity context, @NonNull List<Artist> Artistlist) {
        super(context, R.layout.activity_show__artist__list, Artistlist);
        this.context=context;
        this.Artistlist=Artistlist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=context.getLayoutInflater();
        View ListViewItem= layoutInflater.inflate(R.layout.activity_show__artist__list,null,true);

        TextView artistname=(TextView) ListViewItem.findViewById(R.id.showartistname);
        TextView artistcategory=(TextView) ListViewItem.findViewById(R.id.showArtistCategory);
        ImageView artistprofilr=(ImageView)ListViewItem.findViewById(R.id.imgArticle);
        TextView artistdescription=(TextView) ListViewItem.findViewById(R.id.showArtistdescription);
        TextView artistemailid=(TextView) ListViewItem.findViewById(R.id.showartistmail);


        Artist artist=Artistlist.get(position);

        artistname.setText(""+artist.getArtistname());
        artistcategory.setText(""+artist.getArtistTitle());
        artistdescription.setText(""+artist.getArtistDescription());
        artistemailid.setText(""+artist.getArtistMail());

        String a=artist.getProfileimage();
        Log.d("urlllll", "getView: "+a);
        Picasso.get().load(a).into(artistprofilr);
        return ListViewItem;
    }

}
