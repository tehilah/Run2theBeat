package com.example.run2thebeat;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PlaylistItem {
    public ArrayList<Song> mPlayList;

    public String mDate;
    public PlaylistItem(){

    }
    public PlaylistItem(ArrayList<Song> playList, String date){
        mPlayList = playList;
        mDate = date;
    }
}
