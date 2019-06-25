package com.example.run2thebeat;

public class SavedRunItem {

    private int mImageResource;
    private String mText1;
    private String mText2;

    public SavedRunItem(int imageRecource, String text1, String text2){
        mImageResource = imageRecource;
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
