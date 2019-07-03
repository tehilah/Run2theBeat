package com.example.run2thebeat;


import java.util.ArrayList;


public class Route {

    private String mTimestamp;
    private ArrayList<Point> mPoints;
    private String mDateDescription; // e.g. tuesday night, sunday morning...

    public Route() {
    }

    public Route(String timestamp, ArrayList<Point> points, String dateDescription) {
        mTimestamp = timestamp;
        mPoints = points;
        mDateDescription = dateDescription;
    }

    public String getDate() {
        return mTimestamp;
    }

    public void setDate(String mDate) {
        this.mTimestamp = mDate;
    }

    public ArrayList<Point> getPoints() {
        return mPoints;
    }

    public void setPoints(ArrayList<Point> mPoints) {
        this.mPoints = mPoints;
    }

    public String getDateDescription() {
        return mDateDescription;
    }

    public void setDateDescription(String mDateDescription) {
        this.mDateDescription = mDateDescription;
    }

}
