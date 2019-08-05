package com.example.run2thebeat;
import java.util.ArrayList;
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
