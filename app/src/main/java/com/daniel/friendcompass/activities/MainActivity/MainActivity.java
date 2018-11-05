package com.daniel.friendcompass.activities.MainActivity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daniel.friendcompass.Constants;
import com.daniel.friendcompass.R;
import com.daniel.friendcompass.activities.UserActivity.UserActivity;
import com.daniel.friendcompass.azimuth.AzimuthSensor;
import com.daniel.friendcompass.location.LocationService;
import com.daniel.friendcompass.userstore.UserStoreCallback;
import com.daniel.friendcompass.util.BearingRollingAverage;
import com.daniel.friendcompass.util.FetchAddressIntentService;
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
public class MainActivity extends AppCompatActivity implements UserStoreCallback {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.locationTextView) TextView locationTextView;
    @BindView(R.id.distanceTextView) TextView distanceTextView;
    @BindView(R.id.compassImageView) ImageView compassImageView;
    @BindView(R.id.accuracyImageView)
    ImageView accuracyImageView;
    @BindView(R.id.friendsBtn)
    Button friendsBtn;
    @BindView(R.id.mapBtn)
    Button mapbtn;

    private LocationService locationService;
    private Location location;
    private Location targetLocation;

    private BearingRollingAverage rollingAverage = new BearingRollingAverage();

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        targetLocation = new Location(LocationManager.GPS_PROVIDER);
        targetLocation.setLatitude(-36.950141);
        targetLocation.setLongitude(174.623906);

        MainActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);

        AzimuthSensor azimuthSensor = new AzimuthSensor(this);
        azimuthSensor.registerListener(this.viewModel);

        friendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(intent);
            }
        });

        mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location == null) return;
                Uri gmmIntentUri = Uri.parse(getString(R.string.maps_uri, location.getLatitude(), location.getLongitude(), "Daniel Wong"));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        final Observer<Double> azimuthObserver = new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double azimuth) {
                if (azimuth == null || location == null) return;
                double relativeBearing = Util.getRelativeBearing(location, targetLocation, azimuth);
                relativeBearing = Util.normalise(rollingAverage.getAverageBearing(relativeBearing));

                int roundedBearing = (int) Math.round(relativeBearing);
                compassImageView.setRotation(roundedBearing);
            }
        };

        final Observer<Location> locationObserver = new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location newLocation) {
                if (newLocation == null) return;
                location = newLocation;
//                locationTextView.setText(getString(R.string.location_placeholder, location.getLatitude(), location.getLongitude()));
                distanceTextView.setText(getString(R.string.distance_placeholder,
                        Math.round(Util.distanceBetweenTwoCoordinates(location, targetLocation))));

                getLocationAddress(location, new AddressResultReceiver(new Handler(Looper.getMainLooper())));
            }
        };

        final Observer<Integer> sensorAccuracyObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer accuracy) {
                if (accuracy == null) return;
                switch (accuracy) {
                    case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                        accuracyImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.accuracy_full));
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                        accuracyImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.accuracy_medium));
                        break;
                    default:
                        accuracyImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.accuracy_low));
                        break;
                }
            }
        };

        viewModel.getAzimuth().observe(this, azimuthObserver);
        viewModel.getLocation().observe(this, locationObserver);
        viewModel.getSensorAccuracy().observe(this, sensorAccuracyObserver);
    }

    private void getLocationAddress(Location location, ResultReceiver resultReceiver) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
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
    public void userDataReceived(Map<String, Object> data) {
        String locationString = String.valueOf(data.get("location"));
        List<String> locationStringTokens = Arrays.asList(locationString.split(","));

        Location location = new Location("");
        location.setLatitude(Double.valueOf(locationStringTokens.get(0)));
        location.setLongitude(Double.valueOf(locationStringTokens.get(1)));

        targetLocation = location;

//        Toast.makeText(this, "Location Updated!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Permission things below
     */

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void startLocationUpdates() {
        locationService = new LocationService(this, this.viewModel);
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

    class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) return;
            String address = resultData.getString(Constants.RESULT_DATA_KEY);
            if (resultCode == Constants.SUCCESS_RESULT && address != null) {
                locationTextView.setText(getString(R.string.location_placeholder, address));
            }
        }
    }
}
