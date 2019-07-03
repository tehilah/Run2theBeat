package com.example.run2thebeat;

public class SavedRunItem {

    private int mImageResource;
    private String mText1; // description of the day, e.g. Sunday night run
    private String mText2; // how many km, e.g. 5.5km

    public SavedRunItem(int imageResource, String text1, String text2){
        mImageResource = imageResource;
        mText1 = text1;
        mText2 = text2;
    }

    public int getmImageResource() {
        return mImageResource;
    }

    public String getmText1() {
        return mText1;
    }

    public String getmText2() {
        return mText2;
    }
}
