package com.daniel.friendcompass.util;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.daniel.friendcompass.BaseApplication;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodeUtil {
    private static final String TAG = GeocodeUtil.class.getSimpleName();

    public static String reverseGeocode(Location location) {
        Geocoder myLocation = new Geocoder(BaseApplication.getInstance(), Locale.getDefault());
        try {
            List<Address> list = myLocation.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (list.isEmpty()) return "";
            return list.get(0).getAddressLine(0);
        } catch (IOException e) {
            Log.e(TAG, "Service not available. ", e);
            return "";
        }
    }
}
