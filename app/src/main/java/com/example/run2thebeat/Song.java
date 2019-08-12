package com.example.run2thebeat;

import java.io.Serializable;

public class Song implements Serializable ,Comparable<Song>{

    private long id;
    private String title;
    private String artist;
    private String genre;
    private String fullName;
    private int songBPM;
    public Song(){}

    public Song(long songID, String songTitle, String songArtist, String songGenre, String songfullName, int BPM) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        genre = songGenre;
        fullName = songfullName;
        songBPM = BPM;

    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getGenre(){return genre;}
    public String getFullName(){return fullName;}
    public int getSongBPM(){return songBPM;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


    @Override
    public int compareTo(Song o) {
        return (songBPM -o.songBPM);
    }
}
