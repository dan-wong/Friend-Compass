package com.daniel.friendcompass;

import android.Manifest;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.daniel.friendcompass.heading.HeadingListener;
import com.daniel.friendcompass.heading.HeadingSensor;
import com.daniel.friendcompass.location.LocationListener;
import com.daniel.friendcompass.location.LocationService;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements HeadingListener, LocationListener {
    @BindView(R.id.azimuthTextView) TextView azimuthTextView;
    @BindView(R.id.locationTextView) TextView locationTextView;
    @BindView(R.id.targetLocationTextView) TextView targetLocationTextView;
    @BindView(R.id.bearingToTextView) TextView bearingToTextView;
    @BindView(R.id.distanceTextView) TextView distanceTextView;

    @BindView(R.id.compassImageView) ImageView compassImageView;

    private LocationService locationService;
    private Location targetLocation;
    private Location location;

    private BearingRollingAverage rollingAverage = new BearingRollingAverage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        targetLocation = new Location(LocationManager.GPS_PROVIDER);
        targetLocation.setLatitude(-36.950370);
        targetLocation.setLongitude(174.623885);

        targetLocationTextView.setText("Target " + getString(R.string.location_placeholder, targetLocation.getLatitude(), targetLocation.getLongitude()));

        MainActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);

        HeadingSensor headingSensor = new HeadingSensor(this);
        headingSensor.registerListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationService.stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationService.startLocationUpdates();
    }

    @Override
    public void headingReceived(int azimuth) {
        azimuthTextView.setText(getString(R.string.azimuth_placeholder, azimuth));

        if (location == null) return;
        float bearing = Util.getRelativeBearing(location, targetLocation, azimuth);

        double avgBearing = rollingAverage.getAverageBearing(bearing);

        bearingToTextView.setText(getString(R.string.bearing_placeholder, avgBearing));
        compassImageView.setRotation((int) avgBearing);
    }

    @Override
    public void locationReceived(Location location) {
        this.location = location;
        locationTextView.setText(getString(R.string.location_placeholder, location.getLatitude(), location.getLongitude()));
        distanceTextView.setText(getString(R.string.distance_placeholder, Util.distanceBetweenTwoCoordinates(location, targetLocation)));
    }

    /**
     * Permission things below
     */

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void startLocationUpdates() {
        locationService = new LocationService(this, this);
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("The location permission is required for the app to function correctly!")
                .setTitle("Location Permission");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAskForLocation() {
        showDeniedForLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher
                .onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
