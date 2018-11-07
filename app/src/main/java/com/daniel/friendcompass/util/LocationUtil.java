package com.daniel.friendcompass.util;

import android.location.Location;

public class LocationUtil {
    public static Location createLocation(double latitude, double longitude) {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    public static boolean isSameLocation(Location one, Location two) {
        return one.getLatitude() == two.getLatitude() &&
                one.getLongitude() == two.getLongitude();
    }
}
