package com.example.covid;

import android.location.Location;

public class Common {
    public static String getLocationText(Location mLocation) {
        return mLocation == null ? "Unknown Location" : new StringBuilder()
                .append(mLocation.getLatitude())
                .append("/")
                .append(mLocation.getLongitude())
                .toString();
    }

    public static CharSequence getLocationTitle(BackgroundService backgroundService) {
        return null;
    }
}
