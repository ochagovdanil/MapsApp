package com.example.mapsapp.models;

public enum EMaps {

    NORMAL_LIGHT(0), NORMAL_SILVER(1), NORMAL_RETRO(2), NORMAL_AUBERGINE(3),
    NORMAL_DARK(4), SATELLITE(5), HYBRID(6), TERRAIN(7);

    private int mType;

    EMaps(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

}
