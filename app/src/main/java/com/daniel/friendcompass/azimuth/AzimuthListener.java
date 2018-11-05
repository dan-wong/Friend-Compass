package com.daniel.friendcompass.azimuth;

public interface AzimuthListener {
    void bearingReceived(double azimuth);

    void sensorAccuracyChanged(int sensorAccuracy);
}
