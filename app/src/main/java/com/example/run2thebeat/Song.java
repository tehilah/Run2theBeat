package com.example.run2thebeat;

public class Song {

    private long id;
    private String title;
    private String artist;
    private String genre;

    public Song(long songID, String songTitle, String songArtist, String songGenre) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        genre = songGenre;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getGenre(){return genre;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
