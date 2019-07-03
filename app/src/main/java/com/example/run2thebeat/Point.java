package com.example.run2thebeat;

public class Point {

    private double longitude;
    private double latitude;

    public Point(){}

    public Point(double latitude, double longitude){

        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }


}
