package com.hot.sentefinder.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.hot.sentefinder.R;

import java.util.List;

/**
 * Created by Jamie on 4/25/2017.
 */

public class DeviceLocationListener implements LocationListener {

    private final Context context;
    private Activity activity;

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    boolean canGetLocation = false;


    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private static final long MIN_TIME_BTWN_UPDATES = 1; // 1 minute

    protected LocationManager locationManager;
    public Location location;

    public DeviceLocationListener(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

//    public void checkIfNetworkOrGPSAvailable() {
//        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//        if (!isGPSEnabled && !isNetworkEnabled) {
//            this.canGetLocation = false;
//            showSettingsAlert();
//        } else {
//            this.canGetLocation = true;
//            if (isNetworkEnabled) {
//
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                }
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BTWN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                if (locationManager != null) {
//                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                }
//            }
//            // if GPS Enabled get lat/long using GPS Services
//            if (isGPSEnabled) {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BTWN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                if (locationManager != null) {
//                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                }
//            }
//        }
//
//    }
//
//        public Location getLocation(){
//        return location;
//    }

    public Location getLocation() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            locationManager.requestLocationUpdates(provider, MIN_TIME_BTWN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (location == null || l.getAccuracy() < location.getAccuracy()) {
                // Found best last known location: %s", l);
                location = l;
            }
        }
        return location;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * launch Settings Options
     * */

//    public void showSettingsAlert() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
//
//        // Setting Dialog Title
//        alertDialog.setTitle("GPS settings");
//
//        // Setting Dialog Message
//        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
//
//        // On pressing Settings button
//        alertDialog.setPositiveButton("Settings",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        context.startActivity(intent);
//                    }
//                });
//
//        // on pressing cancel button
//        alertDialog.setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//        // Showing Alert Message
//        alertDialog.show();
//    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public boolean canGetLocation() {
        return canGetLocation;
    }
}
