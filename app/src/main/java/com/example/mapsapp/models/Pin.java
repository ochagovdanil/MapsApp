package com.example.mapsapp.models;

public class Pin {

    private String mTitle;
    private double mLat;
    private double mLon;

    public Pin(String title, double lat, double lon) {
        mTitle = title;
        mLat = lat;
        mLon = lon;
    }

    public String getTitle() {
        return mTitle;
    }

    public double getLat() {
        return mLat;
    }

    public double getLon() {
        return mLon;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setPosition(double lat, double lon) {
        mLat = lat;
        mLon = lon;
    }

}
