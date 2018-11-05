package com.daniel.friendcompass.models;

import android.support.annotation.NonNull;

public class User implements Comparable<User> {
    private final String name;
    private final double latitude;
    private final double longitude;
    private final long timestamp;

    public User(String name) {
        this(name, 0.0, 0.0, 0);
    }

    public User(String name, double latitude, double longitude, long timestamp) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
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

    @Override
    public int compareTo(@NonNull User user) {
        return name.compareTo(user.name);
    }
}
