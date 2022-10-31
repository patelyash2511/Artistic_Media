package com.example.shailesh.artisticmedia;

public class Artist {

    private String artistname;
    private String artistDescription;
    private String artistMail;
    private String artistTitle;
    private String profileimage;


    public Artist(){

    }

    public Artist( String artistname, String artistDescription,String artistMail,String artistTitle,String profileimage) {

        this.artistname = artistname;
        this.artistDescription = artistDescription;
        this.artistMail = artistMail;
        this.artistTitle = artistTitle;
        this.profileimage=profileimage;
    }

    public String getArtistMail() {
        return artistMail;
    }

    public String getArtistTitle() {
        return artistTitle;
    }

    public String getArtistname() {
        return artistname;
    }

    public String getArtistDescription() {
        return artistDescription;
    }

    public String getProfileimage() {
        return profileimage;
    }
}