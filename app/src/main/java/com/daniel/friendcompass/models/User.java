package com.daniel.friendcompass.models;

import android.support.annotation.NonNull;

import com.daniel.friendcompass.util.GeocodeUtil;
import com.daniel.friendcompass.util.LocationUtil;

public class User implements Comparable<User> {
    private final String uid;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final long timestamp;
    private final String address;

    public User(String uid, String name) {
        this(uid, name, 0.0, 0.0, 0);
    }

    public User(String uid, String name, double latitude, double longitude, long timestamp) {
        this.uid = uid;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;

        String address = GeocodeUtil.reverseGeocode(LocationUtil.createLocation(latitude, longitude));
        this.address = address.isEmpty() ? String.format("%s, %s", latitude, longitude) : address;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int compareTo(@NonNull User user) {
        return name.compareTo(user.name);
    }
}
