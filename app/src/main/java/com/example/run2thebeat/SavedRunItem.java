package com.example.run2thebeat;

public class SavedRunItem {

    private int mImageResource;
    private String dateDescription; // description of the day, e.g. Sunday night run
    private String date; // how many km, e.g. 5.5km

    public SavedRunItem(){}

    public SavedRunItem(int imageResource, String text1, String text2){
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

    public String getDate() {
        return date;
    }
}
