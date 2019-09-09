package com.example.run2thebeat;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class SavedRunItem {

    private int mImageResource;
    private String dateDescription; // description of the day, e.g. Sunday night run
    private Date date;
    private String distance;
    private String avgPace;
    private Context context;
    private SharedPreferences myPrefs;


    public SavedRunItem() {
    }

    public SavedRunItem(int imageResource, String text1, Date text2, String kilometers, String avg_time) {
        mImageResource = imageResource;
        setmImageResource(R.drawable.medal);
        dateDescription = text1;
        date = text2;
        distance = kilometers;
        avgPace = avg_time;
    }

    public int getImageResource() {
        myPrefs = context.getSharedPreferences("GOAL_PREF", Context.MODE_PRIVATE);
        String current_goal = myPrefs.getString("GOAL", "zero");
        if(current_goal.equals("zero")){
            return R.drawable.medal;
        }
        else if (Double.parseDouble(distance) >= Double.parseDouble(current_goal)) {
            return R.drawable.trophy;
        } else {
            return R.drawable.medal;
        }
    }

    public String getDateDescription() {
        return dateDescription;
    }

    public Date getDate() {
        return date;
    }

    public void setmImageResource(int mImageResource) {
        this.mImageResource = mImageResource;
    }

    public String getDistance() {
        return distance + " km";
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAvgPace() {
        return avgPace;
    }

    public Context getContext() {
        return context;
    }

    public void setAvgPace(String avgPace) {
        this.avgPace = avgPace;
    }


    public void setContext(Context c) {
        context = c;
    }
}
