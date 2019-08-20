package com.example.run2thebeat;
import java.io.Serializable;
import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;

public class PlaylistItem implements Serializable {
    public ArrayList<Song> mPlayList;

    public String name;
    public String mDate;

    public PlaylistItem(){

    }
    public PlaylistItem(ArrayList<Song> playList, String date, String playlistName){
        name = playlistName;
        mPlayList = playList;
        mDate = date;
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

}
