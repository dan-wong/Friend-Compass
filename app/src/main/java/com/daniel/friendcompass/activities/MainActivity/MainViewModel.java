package com.daniel.friendcompass.activities.MainActivity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;

import com.daniel.friendcompass.azimuth.AzimuthListener;
import com.daniel.friendcompass.location.LocationListener;
import com.daniel.friendcompass.userrepository.UserRepository;
import com.daniel.friendcompass.util.BearingUtil;

public class MainViewModel extends ViewModel implements AzimuthListener, LocationListener {
    private MutableLiveData<Double> azimuth;
    private MutableLiveData<Location> location;

    public MutableLiveData<Double> getAzimuth() {
        if (azimuth == null) azimuth = new MutableLiveData<>();
        return azimuth;
    }

    public MutableLiveData<Location> getLocation() {
        if (location == null) location = new MutableLiveData<>();
        return location;
    }

    @Override
    public void bearingReceived(double azimuth) {
        azimuth = BearingUtil.normalise(azimuth);

        if (location == null || location.getValue() == null) {
            this.azimuth.setValue(azimuth);
            return;
        }

        azimuth = BearingUtil.getBearingWithDeclination(azimuth, location.getValue());
        this.azimuth.setValue(azimuth);
    }

    @Override
    public void locationReceived(Location newLocation) {
//        if (location.getValue() != null && LocationUtil.isSameLocation(location.getValue(), newLocation)) {
//            UserRepository.getInstance().updateUserLocation(newLocation);
//        }
        UserRepository.getInstance().updateUserLocation(newLocation);
        this.location.setValue(newLocation);
    }
}
