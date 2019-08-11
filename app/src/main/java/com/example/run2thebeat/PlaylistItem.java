package com.example.run2thebeat;
import java.io.Serializable;
import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;

public class PlaylistItem implements Serializable {
    public ArrayList<Song> mPlayList;

    public String mDate;

    public PlaylistItem(){

    }
    public PlaylistItem(ArrayList<Song> playList, String date){
        mPlayList = playList;
        mDate = date;
    }

    public void setSongsList(ArrayList<Song> list){
        mPlayList = list;
    }
}
