package com.daniel.friendcompass.models;

public class User {
    private final String name;
    private double latitude;
    private double longitude;
    private long timestamp;

    public User(String name) {
        this.name = name;
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
}
