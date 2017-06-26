package com.hot.sentefinder.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.hot.sentefinder.models.FinancialServiceProvider;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jamie on 4/25/2017.
 */

public class AppManager {
    private static final String TAG_BORROW_MONEY = "BORROW_MONEY";
    private static final String TAG_SAVE_MONEY = "SAVE_MONEY";
    private static final String TAG_SEND_MONEY = "SEND_MONEY";
    private static final String TAG_WITHDRAW_MONEY = "WITHDRAW_MONEY";

    private static List<FinancialServiceProvider> borrowFinancialServiceProviderList = new ArrayList<>();
    private static List<FinancialServiceProvider> saveFinancialServiceProviderList = new ArrayList<>();
    private static List<FinancialServiceProvider> sendFinancialServiceProviderList = new ArrayList<>();
    private static List<FinancialServiceProvider> withdrawFinancialServiceProviderList = new ArrayList<>();
    public static List<FinancialServiceProvider> allFinancialServiceProviderList = new ArrayList<>();
    private static HashMap<String, GeoPoint> geoPointHashMap = new HashMap<>();
    public static final String DEVICE_GEO_POINT = "DeviceGeoPoint";

    public static HashMap<String, GeoPoint> saveGeoPoint(GeoPoint deviceGeoPoint){
        geoPointHashMap.put(DEVICE_GEO_POINT, deviceGeoPoint);
        return geoPointHashMap;
    }

    public static GeoPoint getDeviceGeoPoint(){
        GeoPoint deviceGeoPoint = geoPointHashMap.get(DEVICE_GEO_POINT);
        return deviceGeoPoint;
    }

    public static String createSearchParameter() {
        GeoPoint deviceGeoPoint = getDeviceGeoPoint();
        double deviceLatitude = 0;
        double deviceLongitude = 0;
        if(deviceGeoPoint != null){
            deviceLatitude = deviceGeoPoint.getLatitude();
            deviceLongitude = deviceGeoPoint.getLongitude();
        }

        return String.format("{ \"search\": \"\", \"lat\":\"%s\", \"long\":\"%s\", \"radius\":\"2000\" }", deviceLatitude, deviceLongitude);
    }

    public static String createSearchParameter(String query) {
        GeoPoint deviceGeoPoint = getDeviceGeoPoint();
        double deviceLatitude = deviceGeoPoint.getLatitude();
        double deviceLongitude = deviceGeoPoint.getLongitude();

        return String.format("{ \"search\": \"%s\", \"lat\":\"%s\", \"long\":\"%s\", \"radius\":\"2000\" }", query, deviceLatitude, deviceLongitude);
    }

    public static void createFinancialServiceProvidersList(String fragmentTag, List<FinancialServiceProvider> fspList) {
        if(!fspList.isEmpty()){
            switch (fragmentTag){
                case TAG_BORROW_MONEY:
                    borrowFinancialServiceProviderList.clear();
                    borrowFinancialServiceProviderList.addAll(fspList);
                    allFinancialServiceProviderList.addAll(borrowFinancialServiceProviderList);
                    break;
                case TAG_SAVE_MONEY:
                    saveFinancialServiceProviderList.clear();
                    saveFinancialServiceProviderList.addAll(fspList);
                    allFinancialServiceProviderList.addAll(saveFinancialServiceProviderList);
                    break;
                case TAG_SEND_MONEY:
                    sendFinancialServiceProviderList.clear();
                    sendFinancialServiceProviderList.addAll(fspList);
                    allFinancialServiceProviderList.addAll(sendFinancialServiceProviderList);
                    break;
                case TAG_WITHDRAW_MONEY:
                    withdrawFinancialServiceProviderList.clear();
                    withdrawFinancialServiceProviderList.addAll(fspList);
                    allFinancialServiceProviderList.addAll(withdrawFinancialServiceProviderList);
                    break;
            }
        }
    }
    public static List<FinancialServiceProvider> getAllFinancialServiceProviderList(){
        return allFinancialServiceProviderList;
    }

    public static FinancialServiceProvider getFSPById(String fragmentTag, long id) {
        FinancialServiceProvider fsp = new FinancialServiceProvider();
        switch(fragmentTag){
            case TAG_BORROW_MONEY:
                fsp = borrowFSPHashMap().get(String.valueOf(id));
                break;
            case TAG_SAVE_MONEY:
                fsp = saveFSPHashMap().get(String.valueOf(id));
                break;
            case TAG_SEND_MONEY:
                fsp = sendFSPHashMap().get(String.valueOf(id));
                break;
            case TAG_WITHDRAW_MONEY:
                fsp =  withdrawFSPHashMap().get(String.valueOf(id));
                break;

        }
        return fsp;
    }

    private static HashMap<String, FinancialServiceProvider> borrowFSPHashMap() {
        final HashMap<String, FinancialServiceProvider> hashMap = new HashMap<>();
        for (FinancialServiceProvider fsp: borrowFinancialServiceProviderList){
            hashMap.put(String.valueOf(fsp.getId()), fsp);
        }
        return hashMap;
    }

    private static HashMap<String, FinancialServiceProvider> saveFSPHashMap() {
        final HashMap<String, FinancialServiceProvider> hashMap = new HashMap<>();
        for (FinancialServiceProvider fsp: saveFinancialServiceProviderList){
            hashMap.put(String.valueOf(fsp.getId()), fsp);
        }
        return hashMap;
    }

    private static HashMap<String, FinancialServiceProvider> sendFSPHashMap() {
        final HashMap<String, FinancialServiceProvider> hashMap = new HashMap<>();
        for (FinancialServiceProvider fsp: sendFinancialServiceProviderList){
            hashMap.put(String.valueOf(fsp.getId()), fsp);
        }
        return hashMap;
    }

    private static HashMap<String, FinancialServiceProvider> withdrawFSPHashMap() {
        final HashMap<String, FinancialServiceProvider> hashMap = new HashMap<>();
        for (FinancialServiceProvider fsp: withdrawFinancialServiceProviderList){
            hashMap.put(String.valueOf(fsp.getId()), fsp);
        }
        return hashMap;
    }

    public static FinancialServiceProvider getFinancialServiceProviderById(long id) {
        HashMap<String, FinancialServiceProvider> hash = FinancialServiceProviderHashMap();
        return hash.get(String.valueOf(id));

    }

    private static HashMap<String, FinancialServiceProvider> FinancialServiceProviderHashMap() {
        final HashMap<String, FinancialServiceProvider> hashMap = new HashMap<>();
        for (FinancialServiceProvider fsp: allFinancialServiceProviderList){
            hashMap.put(String.valueOf(fsp.getId()), fsp);
        }
        return hashMap;
    }

    public static double getTimeDurationBetweenUserAndFSP(FinancialServiceProvider financialServiceProvider) {
        double distance = getDistanceBetweenUserAndFSP(financialServiceProvider);
        double distanceInKm = distance / 1000;
        double speed = 3.3;
        double time = distanceInKm / speed;
        return roundOff(time, 1);

    }

    public static double getDistanceBetweenUserAndFSP(FinancialServiceProvider financialServiceProvider) {
        GeoPoint deviceLocation = AppManager.getDeviceGeoPoint();
        List<Double> coordinates = financialServiceProvider.getCoordinates();
        double fspLatitude = coordinates.get(1);
        double fspLongitude = coordinates.get(0);

        GeoPoint fspLocation = new GeoPoint(fspLatitude, fspLongitude);

        return deviceLocation.distanceTo(fspLocation);

    }

    private static double roundOff(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String convertTimeToString(double time){
        String timeString = "";
        int minutes = (int) (time * 60);
        if(time >= 1.00){
            timeString = "about " + time + "hrs away";

        }else {
            if(minutes <= 0){
                timeString = "about 1min away";
            }else{
                timeString = "about " + minutes + "mins away";
            }
        }
        return timeString;
    }

    public static String convertDistanceToString(double distance) {
        String stringDistance = "";
        if (distance >= 1000) {
            double distanceInKiloMetres = roundOff((distance / 1000), 1);
            stringDistance = Double.toString(distanceInKiloMetres) + " km";
        } else {
            stringDistance = Double.toString(roundOff(distance, 1)) + " m";
        }
        return stringDistance;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

}
