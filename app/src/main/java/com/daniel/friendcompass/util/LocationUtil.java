package com.daniel.friendcompass.util;

import android.location.Location;

public class LocationUtil {
    public static final double MIN_ACCURACY = 40;

    public static Location createLocation(double latitude, double longitude) {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    public static boolean isSameLocation(Location oldLocation, Location newLocation) {
        return oldLocation.getLatitude() == newLocation.getLatitude() &&
                oldLocation.getLongitude() == newLocation.getLongitude();
    }

    public static boolean shouldSendLocation(Location oldLocation, Location newLocation) {
        return !isSameLocation(oldLocation, newLocation) &&
                newLocation.getAccuracy() < MIN_ACCURACY;

    }
}
