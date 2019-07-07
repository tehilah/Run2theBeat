package com.example.run2thebeat;


import java.util.ArrayList;


public class Route {

    private String mTimestamp;
    private ArrayList<Point> mPoints;
    private String mDuration;
    private String mDistance;
    private String mDateDescription; // e.g. tuesday night, sunday morning...
    private int avgBPM = 0;
    private String avgPace;

    public Route() {
    }

    public Route(String timestamp, ArrayList<Point> points, String dateDescription, String duration
            , String distance, int averageBPM, String averagePace) {
        mTimestamp = timestamp;
        mPoints = points;
        mDateDescription = dateDescription;
        mDuration = duration;
        mDistance = distance;
        avgBPM = averageBPM;
        avgPace = averagePace;
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

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        this.mDuration = duration;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        this.mDistance = distance;
    }

    public int getAvgBPM() {
        return avgBPM;
    }

    public void setAvgBPM(int avgBPM) {
        this.avgBPM = avgBPM;
    }

    public String getAvgPace() {
        return avgPace;
    }

    public void setAvgPace(String avgPace) {
        this.avgPace = avgPace;
    }
}
