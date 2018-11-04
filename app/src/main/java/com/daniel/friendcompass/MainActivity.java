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
import android.widget.Toast;

import com.daniel.friendcompass.heading.HeadingListener;
import com.daniel.friendcompass.heading.HeadingSensor;
import com.daniel.friendcompass.location.LocationListener;
import com.daniel.friendcompass.location.LocationService;
import com.daniel.friendcompass.userstore.UserStore;
import com.daniel.friendcompass.userstore.UserStoreCallback;
import com.daniel.friendcompass.util.BearingRollingAverage;
import com.daniel.friendcompass.util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements HeadingListener, LocationListener, UserStoreCallback {
    private static final String TAG = MainActivity.class.getSimpleName();

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
        if (locationService != null) locationService.stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationService != null) locationService.startLocationUpdates();
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
//        if (this.location != null &&
//                this.location.getLatitude() != location.getLatitude() &&
//                this.location.getLongitude() != location.getLongitude()) {
////            UserStore.getInstance().updateUserLocation("neU8OvMGuJYMCZIjArQM", location);
//            UserStore.getInstance().getUser("neU8OvMGuJYMCZIjArQM", this);
//
//        }
//        UserStore.getInstance().getUser("neU8OvMGuJYMCZIjArQM", this);

        this.location = location;

        locationTextView.setText(getString(R.string.location_placeholder, location.getLatitude(), location.getLongitude()));
        distanceTextView.setText(getString(R.string.distance_placeholder, Util.distanceBetweenTwoCoordinates(location, targetLocation)));
    }

    @Override
    public void userDataReceived(Map<String, Object> data) {
        String locationString = String.valueOf(data.get("location"));
        List<String> locationStringTokens = Arrays.asList(locationString.split(","));

        Location location = new Location("");
        location.setLatitude(Double.valueOf(locationStringTokens.get(0)));
        location.setLongitude(Double.valueOf(locationStringTokens.get(1)));

        targetLocation = location;
        targetLocationTextView.setText("Target " + getString(R.string.location_placeholder, targetLocation.getLatitude(), targetLocation.getLongitude()));

//        Toast.makeText(this, "Location Updated!", Toast.LENGTH_SHORT).show();
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
