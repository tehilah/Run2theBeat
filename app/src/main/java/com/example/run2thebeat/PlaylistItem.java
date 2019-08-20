package com.example.run2thebeat;
import java.io.Serializable;
import java.util.ArrayList;

public class PlaylistItem implements Serializable {
    public ArrayList<Song> mPlayList;
    public String name;
    public String km;

    public PlaylistItem(){

    }
    public PlaylistItem(ArrayList<Song> playList, String date, String playlistName, String KM){
        km = KM;
        name = playlistName;
        mPlayList = playList;
    }

    public void setSongsList(ArrayList<Song> list){
        mPlayList = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }


}
