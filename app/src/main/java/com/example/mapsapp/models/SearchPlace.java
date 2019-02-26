package com.example.mapsapp.models;

public class SearchPlace {

    // coordinates
    private double mL1;
    private double mL2;

    private String mName;
    private String mSubName;
    private String mSubArea;
    private String mCountryCode;
    private String mCountryName;

    public SearchPlace(double l1, double l2, String name, String subName, String sunArea, String countryCode, String countryName) {
        mL1 = l1;
        mL2 = l2;
        mName = name;
        mSubName = subName;
        mSubArea = sunArea;
        mCountryCode = countryCode;
        mCountryName = countryName;
    }

    public double getLat() {
        return mL1;
    }

    public double getLong() {
        return mL2;
    }

    public String getName() {
        return mName;
    }

    public String getSubName() {
        return mSubName;
    }

    public String getSubArea() {
        return mSubArea;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public String getCountryName() {
        return mCountryName;
    }

}
