package com.hot.sentefinder.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.hot.sentefinder.R;
import com.hot.sentefinder.models.FinancialServiceProvider;
import com.hot.sentefinder.services.AppManager;
import com.hot.sentefinder.services.InternalStorageService;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String PREFERENCES_KEY = "com.hot.sentefinder.PRIVATE_PREFS";
    private static final String LOC_LONGITUDE = "LONGITUDE";
    private static final String LOC_LATITUDE = "LATITUDE";
    private static final String TAG_BORROW_MONEY = "BORROW_MONEY";
    private static final String TAG_SAVE_MONEY = "SAVE_MONEY";
    private static final String TAG_SEND_MONEY = "SEND_MONEY";
    private static final String TAG_WITHDRAW_MONEY = "WITHDRAW_MONEY";
    private static final String TAG_FRAGMENT = "FRAGMENT";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 100;
    private static final int CHECK_LOCATION_SETTINGS_REQUEST = 200;
    private static final int LOCATION_PERMISSION_REQUEST = 300;
    private static final int READ_WRITE_STORAGE_REQUEST = 400;

    private static final int UPDATE_INTERVAL = 60000; //1 minute
    private static final int FASTEST_INTERVAL = 5000; //5 seconds
    private static final int DISPLACEMENT = 10; //10 metres
    private static final String TAG = "MainActivity";

    Intent intent;
    Bundle bundle;
    private Toolbar toolbar;

    private Location lastLocation;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private GeoPoint deviceGeoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
            checkLocationSettingsStatus();
        } else {
            Toast.makeText(this, "This device does not support google play services.", Toast.LENGTH_LONG).show();
        }
        intent = new Intent(context, FinancialServiceProviderListActivity.class);
        bundle = new Bundle();

    }

    public void loadBorrowFragment(View view) {
        if (AppManager.isOnline(getApplicationContext())) {
            GeoPoint deviceGeoPoint = AppManager.getDeviceGeoPoint();
            if (deviceGeoPoint != null) {
                bundle.putString(TAG_FRAGMENT, TAG_BORROW_MONEY);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                checkLocationSettingsStatus();
                Toast.makeText(getApplicationContext(), "Failed to acquire device Location", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadSaveFragment(View view) {
        if (AppManager.isOnline(getApplicationContext())) {
            GeoPoint deviceGeoPoint = AppManager.getDeviceGeoPoint();
            if (deviceGeoPoint != null) {
                bundle.putString(TAG_FRAGMENT, TAG_SAVE_MONEY);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                checkLocationSettingsStatus();
                Toast.makeText(getApplicationContext(), "Failed to acquire device Location", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadSendFragment(View view) {
        if (AppManager.isOnline(getApplicationContext())) {
            GeoPoint deviceGeoPoint = AppManager.getDeviceGeoPoint();
            if (deviceGeoPoint != null) {
                bundle.putString(TAG_FRAGMENT, TAG_SEND_MONEY);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                checkLocationSettingsStatus();
                Toast.makeText(getApplicationContext(), "Failed to acquire device Location", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadWithdrawFragment(View view) {
        if (AppManager.isOnline(getApplicationContext())) {
            GeoPoint deviceGeoPoint = AppManager.getDeviceGeoPoint();
            if (deviceGeoPoint != null) {
                bundle.putString(TAG_FRAGMENT, TAG_WITHDRAW_MONEY);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                checkLocationSettingsStatus();
                Toast.makeText(getApplicationContext(), "Failed to acquire device Location", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPlayServices() {
        Log.i(TAG, "verifying google play services.");
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int resultCode = googleApi.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS ) {
            if (googleApi.isUserResolvableError(resultCode)) {
                googleApi.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support google play services.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        Log.i(TAG, "google play services, Okie.");
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void checkLocationSettingsStatus() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        getDeviceLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, CHECK_LOCATION_SETTINGS_REQUEST);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECK_LOCATION_SETTINGS_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    protected void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        getDeviceLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null)
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        GeoPoint oldGeoPoint = AppManager.getDeviceGeoPoint();
        getDeviceLocation();
        lastLocation = location;
        Log.d(TAG, "Geo-Location Changed: Old " + oldGeoPoint + " New: " + location);
        if (lastLocation != null && oldGeoPoint != null) {
            GeoPoint newGeoPoint = new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
            double distanceBtnGeoPoints = newGeoPoint.distanceTo(oldGeoPoint);
            if (distanceBtnGeoPoints >= 500) {
                emptyCache();
                //Toast.makeText(this, "Device location changed", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Geo-Location Updated");
            }
        }
    }

    private void emptyCache() {
        List<FinancialServiceProvider> emptyBorrowFSPList = new ArrayList<>();
        List<FinancialServiceProvider> emptySendFSPList = new ArrayList<>();
        List<FinancialServiceProvider> emptySaveFSPList = new ArrayList<>();
        List<FinancialServiceProvider> emptyWithdrawFSPList = new ArrayList<>();
        try {
            InternalStorageService.emptyObject(this, TAG_BORROW_MONEY, emptyBorrowFSPList);
            InternalStorageService.emptyObject(this, TAG_SAVE_MONEY, emptySaveFSPList);
            InternalStorageService.emptyObject(this, TAG_SEND_MONEY, emptySendFSPList);
            InternalStorageService.emptyObject(this, TAG_WITHDRAW_MONEY, emptyWithdrawFSPList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        }

        saveDeviceGeoPoint();

    }

    private void saveDeviceGeoPoint() {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        if (lastLocation != null) {
            double latitude = lastLocation.getLatitude();
            double longitude = lastLocation.getLongitude();
            deviceGeoPoint = new GeoPoint(latitude, longitude);
            AppManager.saveGeoPoint(deviceGeoPoint);
            final double oLatitude = Double.parseDouble(sharedPref.getString(LOC_LATITUDE, "0"));
            final double oLongitude = Double.parseDouble(sharedPref.getString(LOC_LONGITUDE, "0"));
            GeoPoint oldGeoPoint = new GeoPoint(oLatitude, oLongitude);
            System.out.println("oldGeoPoint = " + oldGeoPoint);
            double distanceBtnGeoPoints = deviceGeoPoint.distanceTo(oldGeoPoint);
            if (distanceBtnGeoPoints > 500) {
                emptyCache();
                //Toast.makeText(this, "Device location changed", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Geo-Location Updated");
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(LOC_LATITUDE, latitude + "");
                editor.putString(LOC_LONGITUDE, longitude + "");
                editor.apply();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[0];
                int grantResult = grantResults[0];

                if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        getDeviceLocation();

                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
                    }
                }
            }
        }
    }

}
