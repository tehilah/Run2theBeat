package com.example.run2thebeat;

import java.io.Serializable;

public class Song implements Serializable {

    private long id;
    private String title;
    private String artist;
    private String genre;
    private String fullName;

    public Song(long songID, String songTitle, String songArtist, String songGenre, String songfullName) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        genre = songGenre;
        fullName = songfullName;

    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getGenre(){return genre;}
    public String getFullName(){return fullName;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
