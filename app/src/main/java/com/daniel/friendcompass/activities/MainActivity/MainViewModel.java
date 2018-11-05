package com.daniel.friendcompass.activities.MainActivity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;

import com.daniel.friendcompass.azimuth.AzimuthListener;
import com.daniel.friendcompass.location.LocationListener;
import com.daniel.friendcompass.util.Util;

public class MainViewModel extends ViewModel implements AzimuthListener, LocationListener {
    private MutableLiveData<Double> azimuth;
    private MutableLiveData<Location> location;
    private MutableLiveData<Integer> sensorAccuracy;

    public MutableLiveData<Double> getAzimuth() {
        if (azimuth == null) azimuth = new MutableLiveData<>();
        return azimuth;
    }

    public MutableLiveData<Location> getLocation() {
        if (location == null) location = new MutableLiveData<>();
        return location;
    }

    public MutableLiveData<Integer> getSensorAccuracy() {
        if (sensorAccuracy == null) sensorAccuracy = new MutableLiveData<>();
        return sensorAccuracy;
    }

    @Override
    public void bearingReceived(double azimuth) {
        azimuth = Util.normalise(azimuth);

        if (location == null || location.getValue() == null) {
            this.azimuth.setValue(azimuth);
            return;
        }

        azimuth = Util.getBearingWithDeclination(azimuth, location.getValue());
        this.azimuth.setValue(azimuth);
    }

    @Override
    public void sensorAccuracyChanged(int sensorAccuracy) {
        this.sensorAccuracy.setValue(sensorAccuracy);
    }

    @Override
    public void locationReceived(Location location) {
        this.location.setValue(location);
    }
}
