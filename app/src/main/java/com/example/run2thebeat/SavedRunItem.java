package com.example.run2thebeat;

import java.util.Date;

public class SavedRunItem {

    private int mImageResource;
    private String dateDescription; // description of the day, e.g. Sunday night run
    private Date date; // how many km, e.g. 5.5km

    public SavedRunItem(){}

    public SavedRunItem(int imageResource, String text1, Date text2){
        mImageResource = R.drawable.ic_running;
        dateDescription = text1;
        date = text2;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getDateDescription() {
        return dateDescription;
    }

    public Date getDate() {
        return date;
    }

}
