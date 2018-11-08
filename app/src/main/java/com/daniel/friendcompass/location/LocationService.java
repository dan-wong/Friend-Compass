package com.daniel.friendcompass.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.daniel.friendcompass.BaseApplication;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationService {
    private static final int DEFAULT_REQUEST_INTERVAL = 10000;
    private static final int FASTEST_REQUEST_INTERVAL = 5000;

    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final LocationCallback locationCallback;

    private final Context context;
    private final LocationListener listener;

    public LocationService(LocationListener listener) {
        this.context = BaseApplication.getInstance();
        this.listener = listener;

        checkLocationPermissionGranted();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        locationCallback = createLocationCallback();

        getLastKnownLocation();
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null) return;
                listener.locationReceived(location);
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(createLocationRequest(),
                locationCallback, null);
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private LocationRequest createLocationRequest() {
        return new LocationRequest()
                .setInterval(DEFAULT_REQUEST_INTERVAL)
                .setFastestInterval(FASTEST_REQUEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private LocationCallback createLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                listener.locationReceived(locationResult.getLastLocation());
            }
        };
    }

    private void checkLocationPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("LocationService created without Location permission being granted");
        }
    }
}
