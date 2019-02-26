package com.example.mapsapp.models;

public class MapType {

    private int mType;
    private int mSrcImage;
    private String mTypeName;

    public MapType(int type, int srcImage, String typeName) {
        mType = type;
        mSrcImage = srcImage;
        mTypeName = typeName;
    }

    public int getSrcImage() {
        return mSrcImage;
    }

    public String getTypeName() {
        return mTypeName;
    }

    public int getType() {
        return mType;
    }

}
