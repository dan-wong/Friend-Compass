package com.daniel.friendcompass.util;

import android.hardware.GeomagneticField;
import android.location.Location;

public class BearingUtil {
    public static double getRelativeBearing(Location src, Location dest, double bearing) {
        return normalise(getBearingWithDeclination(src.bearingTo(dest), src) - bearing);
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

    public static double normalise(double bearing) {
        return (bearing + 360) % 360;
    }

    public static double getBearingWithDeclination(double bearing, Location location) {
        GeomagneticField geomagneticField = new GeomagneticField(
                Double.valueOf(location.getLatitude()).floatValue(),
                Double.valueOf(location.getLongitude()).floatValue(),
                Double.valueOf(location.getAltitude()).floatValue(),
                System.currentTimeMillis()
        );

        return bearing + (geomagneticField.getDeclination() * -1);
    }
}
