package com.daniel.friendcompass;

import android.location.Location;

public class Util {
    public static float getRelativeBearing(Location src, Location dest, float azimuth) {
        return (src.bearingTo(dest) - azimuth + 360) % 360;
    }

    public static double distanceBetweenTwoCoordinates(Location src, Location dest) {
        double radius = 6371000;

        double dLat = Math.toRadians(dest.getLatitude() - src.getLatitude());
        double dLon = Math.toRadians(dest.getLongitude() - src.getLongitude());

        double lat1 = Math.toRadians(src.getLatitude());
        double lat2 = Math.toRadians(dest.getLatitude());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return radius * c;
    }
}
