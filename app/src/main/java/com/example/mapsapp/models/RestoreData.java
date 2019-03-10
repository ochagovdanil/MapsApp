package com.example.mapsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class RestoreData implements Parcelable {

    private float mZoom;
    private double mLat;
    private double mLon;
    private boolean mTraffic;
    private List<Pin> mListPins;

    private RestoreData(Parcel in) {
        mZoom = in.readFloat();
        mLat = in.readDouble();
        mLon = in.readDouble();
        mTraffic = in.readByte() != 0;
        mListPins = in.readArrayList(mListPins.getClass().getClassLoader());
    }

    public RestoreData(float zoom, double lat, double lon, boolean traffic, List<Pin> listPins) {
        mZoom = zoom;
        mLat = lat;
        mLon = lon;
        mTraffic = traffic;
        mListPins = listPins;
    }

    public float getZoom() {
        return mZoom;
    }

    public void setZoom(float zoom) {
        mZoom = zoom;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public List<Pin> getListPins() {
        return mListPins;
    }

    public boolean getTraffic() {
        return mTraffic;
    }

    public void setTraffic(boolean value) {
        mTraffic = value;
    }

    public void setListPins(List<Pin> listPins) {
        mListPins = listPins;
    }

    public static final Creator<RestoreData> CREATOR = new Creator<RestoreData>() {
        @Override
        public RestoreData createFromParcel(Parcel in) {
            return new RestoreData(in);
        }

        @Override
        public RestoreData[] newArray(int size) {
            return new RestoreData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mZoom);
        dest.writeDouble(mLat);
        dest.writeDouble(mLon);
        dest.writeByte((byte) (mTraffic ? 1 : 0));
        // when user set a pin and go to the official app he gets en error in this line
        try {
            dest.writeList(mListPins);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    
}
