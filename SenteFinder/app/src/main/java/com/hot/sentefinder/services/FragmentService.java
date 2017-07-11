package com.hot.sentefinder.services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hot.sentefinder.R;
import com.hot.sentefinder.models.FinancialServiceProvider;
import com.hot.sentefinder.utilities.URLSpanEdit;
import com.hot.sentefinder.viewmodels.FinancialServiceProviderViewModel;

import org.osmdroid.api.IMapView;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Jamie on 5/4/2017.
 */

public class FragmentService {
    private static long FSP_ID = 0;
    private static final int FULL_VIEW_MAP_ZOOM = 15;
    private static final int HALF_VIEW_MAP_ZOOM = 16;
    private Context context;
    private Activity activity;
    private List<OverlayItem> overlayItems = new ArrayList<>();
    private RelativeLayout locationLayout, openingHoursLayout, phoneLayout, additionalInfoLayout, reviewLayout;
    private MapView mapView;
    private MapController mapController;
    private TextView fspNameView, fspDistanceFromUserView, fspTimeFromUserView, fspAddressView, fspOpeningHoursView, fspPhoneView, fspAdditionalInfoView;
    private Drawable fspMarker;
    private GeoPoint deviceGeoPoint;

    public FragmentService(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setUpMap(List<FinancialServiceProvider> financialServiceProviderList) {
        setUpMap(financialServiceProviderList, true);
    }

    public void setUpMap(List<FinancialServiceProvider> financialServiceProviderList, Boolean halfScreen) {
        setUpViews();
        getDeviceLocation();
        mapController.setZoom(halfScreen ? HALF_VIEW_MAP_ZOOM : FULL_VIEW_MAP_ZOOM);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        //drawable for user location
        Drawable userMarker = ContextCompat.getDrawable(context, R.drawable.ic_user_location);
        userMarker.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        //drawable for the fsps
        fspMarker = ContextCompat.getDrawable(context, R.drawable.ic_action_location);
        fspMarker.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

        //set device location as center of map
        mapController.setCenter(deviceGeoPoint);

        addDeviceLocationMarkerOnMap(deviceGeoPoint, userMarker);

        //add fsp markers to the map
        for (FinancialServiceProvider provider : financialServiceProviderList) {
            List<Double> coordinates = provider.getCoordinates();
            double fspLatitude = coordinates.get(1);
            double fspLongitude = coordinates.get(0);
            addFSPMarkerOnMap(new GeoPoint(fspLatitude, fspLongitude), fspMarker, provider.getId());
        }
    }

    private void setUpViews() {
        mapView = (MapView) ((Activity) context).findViewById(R.id.map);
        mapController = (MapController) mapView.getController();

        //initialize the text views
        fspNameView = (TextView) ((Activity) context).findViewById(R.id.fsp_name_text);
        fspDistanceFromUserView = (TextView) ((Activity) context).findViewById(R.id.fsp_dist_from_user);
        fspTimeFromUserView = (TextView) ((Activity) context).findViewById(R.id.fsp_time_from_user);
        fspAddressView = (TextView) ((Activity) context).findViewById(R.id.fsp_location_text);
        fspOpeningHoursView = (TextView) ((Activity) context).findViewById(R.id.fsp_opening_hours_text);
        fspPhoneView = (TextView) ((Activity) context).findViewById(R.id.fsp_phone_number);
        fspAdditionalInfoView = (TextView) ((Activity) context).findViewById(R.id.fsp_additional_information_text);

        //initialize the relative layouts
        locationLayout = (RelativeLayout) ((Activity) context).findViewById(R.id.fsp_location);
        openingHoursLayout = (RelativeLayout) ((Activity) context).findViewById(R.id.fsp_opening_hours);
        phoneLayout = (RelativeLayout) ((Activity) context).findViewById(R.id.fsp_telephone);
        additionalInfoLayout = (RelativeLayout) ((Activity) context).findViewById(R.id.fsp_additional_information);
        reviewLayout = (RelativeLayout) ((Activity) context).findViewById(R.id.fsp_review);
    }

    private void getDeviceLocation() {
        deviceGeoPoint = AppManager.getDeviceGeoPoint();
    }

    private void addDeviceLocationMarkerOnMap(GeoPoint geoPoint, Drawable drawable) {
        int markerWidth = drawable.getIntrinsicWidth();
        int markerHeight = drawable.getIntrinsicHeight();
        drawable.setBounds(0, markerHeight, markerWidth, 0);

        DeviceLocationOverlayItem item = new DeviceLocationOverlayItem(drawable, geoPoint.getLatitude(), geoPoint.getLongitude());
        mapView.getOverlays().add(item);
    }

    private void addFSPMarkerOnMap(GeoPoint geoPoint, final Drawable drawable, long fspId) {
        int markerWidth = drawable.getIntrinsicWidth();
        int markerHeight = drawable.getIntrinsicHeight();
        drawable.setBounds(0, markerHeight, markerWidth, 0);

        MapOverlayItem mapOverlayItem = new MapOverlayItem(drawable, geoPoint.getLatitude(), geoPoint.getLongitude(), fspId);
        mapView.getOverlays().add(mapOverlayItem);

    }


    public void inflateFspMarker(FinancialServiceProvider fsp) {
        OverlayItem item = getOverlayItem(fsp);

        fspMarker = ContextCompat.getDrawable(context, R.drawable.ic_action_location);
        fspMarker.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

        Bitmap bitmap = ((BitmapDrawable) fspMarker).getBitmap();
        Drawable scaledDrawable = new BitmapDrawable(activity.getResources(), Bitmap.createScaledBitmap(bitmap, 200, 200, true));
        scaledDrawable.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        item.setMarker(scaledDrawable);
    }

    private OverlayItem getOverlayItem(FinancialServiceProvider fsp) {
        OverlayItem item = new OverlayItem("", "", new GeoPoint(fsp.getCoordinates().get(1), fsp.getCoordinates().get(0)));
        for (OverlayItem overlayItem : overlayItems) {
            if (overlayItem.getSnippet().equals(String.valueOf(fsp.getId()))) {
                item = overlayItem;
                break;
            }
        }
        return item;
    }

    public void toggleMapHeight(FinancialServiceProvider fsp) {
        setUpViews();
        RelativeLayout.LayoutParams mapParams = (RelativeLayout.LayoutParams) mapView.getLayoutParams();
        //get height of display screen
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        int displayHeight = size.y;
        mapParams.height = displayHeight / 3;
        mapView.setLayoutParams(mapParams);
        GeoPoint point = new GeoPoint(fsp.getCoordinates().get(1), fsp.getCoordinates().get(0));
        mapController.setCenter(point);
        mapController.animateTo(point);
    }

    public void updateTextViewsWithFSPDetails(FinancialServiceProvider fsp) {
        setUpViews();
        double distance = AppManager.getDistanceBetweenUserAndFSP(fsp);
        double time = AppManager.getTimeDurationBetweenUserAndFSP(fsp);
        fspNameView.setText(fsp.getName());
        String d = AppManager.convertDistanceToString(distance);
        fspDistanceFromUserView.setText(AppManager.convertDistanceToString(distance));
        fspTimeFromUserView.setText(AppManager.convertTimeToString(time));

        if (fsp.getAddrStreet() == null || fsp.getAddrCity() == null) {
            locationLayout.setVisibility(View.INVISIBLE);
        } else {
            locationLayout.setVisibility(View.VISIBLE);
        }
        if (fsp.getOpeningHours() == null) {
            openingHoursLayout.setVisibility(View.INVISIBLE);
        } else {
            openingHoursLayout.setVisibility(View.VISIBLE);
        }
        if (fsp.getPhone() == null) {
            phoneLayout.setVisibility(View.INVISIBLE);
        } else {
            phoneLayout.setVisibility(View.VISIBLE);
        }
        if (fsp.getDescription() == null) {
            additionalInfoLayout.setVisibility(View.INVISIBLE);
        } else {
            additionalInfoLayout.setVisibility(View.VISIBLE);
        }

        fspAddressView.setText(String.format("%s, %s", fsp.getAddrStreet(), fsp.getAddrCity()));
        fspOpeningHoursView.setText(fsp.getOpeningHours());
        fspPhoneView.setText(fsp.getPhone());
        removeUnderlines(fspPhoneView);
        fspAdditionalInfoView.setText(fsp.getDescription());

        RelativeLayout.LayoutParams reviewBtnParams = (RelativeLayout.LayoutParams) reviewLayout.getLayoutParams();
        RelativeLayout.LayoutParams infoParams = (RelativeLayout.LayoutParams) additionalInfoLayout.getLayoutParams();
        RelativeLayout.LayoutParams phoneParams = (RelativeLayout.LayoutParams) phoneLayout.getLayoutParams();
        RelativeLayout.LayoutParams openingHrsParams = (RelativeLayout.LayoutParams) openingHoursLayout.getLayoutParams();

        //change position of opening hours layout accordingly
        if (locationLayout.getVisibility() == View.INVISIBLE) {
            openingHrsParams.addRule(RelativeLayout.BELOW, R.id.fsp_distance_and_time);
        } else {
            openingHrsParams.addRule(RelativeLayout.BELOW, R.id.fsp_location);
        }

        //change position of phone layout accordingly
        if (openingHoursLayout.getVisibility() == View.INVISIBLE) {
            if (locationLayout.getVisibility() == View.INVISIBLE) {
                phoneParams.addRule(RelativeLayout.BELOW, R.id.fsp_distance_and_time);
            } else {
                phoneParams.addRule(RelativeLayout.BELOW, R.id.fsp_location);
            }
        } else {
            phoneParams.addRule(RelativeLayout.BELOW, R.id.fsp_opening_hours);
        }

        //change position of additional info layout accordingly
        if (phoneLayout.getVisibility() == View.INVISIBLE) {
            if (openingHoursLayout.getVisibility() == View.INVISIBLE) {
                if (locationLayout.getVisibility() == View.INVISIBLE) {
                    infoParams.addRule(RelativeLayout.BELOW, R.id.fsp_distance_and_time);
                } else {
                    infoParams.addRule(RelativeLayout.BELOW, R.id.fsp_location);
                }
            } else {
                infoParams.addRule(RelativeLayout.BELOW, R.id.fsp_opening_hours);
            }
        } else {
            infoParams.addRule(RelativeLayout.BELOW, R.id.fsp_telephone);
        }

        //change position of review button accordingly
        if (additionalInfoLayout.getVisibility() == View.INVISIBLE) {
            if (phoneLayout.getVisibility() == View.INVISIBLE) {
                if (openingHoursLayout.getVisibility() == View.INVISIBLE) {
                    if (locationLayout.getVisibility() == View.INVISIBLE) {
                        reviewBtnParams.addRule(RelativeLayout.BELOW, R.id.fsp_distance_and_time);
                    } else {
                        reviewBtnParams.addRule(RelativeLayout.BELOW, R.id.fsp_location);
                    }
                } else {
                    reviewBtnParams.addRule(RelativeLayout.BELOW, R.id.fsp_opening_hours);
                }
            } else {
                reviewBtnParams.addRule(RelativeLayout.BELOW, R.id.fsp_telephone);
            }
        } else {
            reviewBtnParams.addRule(RelativeLayout.BELOW, R.id.fsp_additional_information);
        }
    }

    public void ascendingOrderList(List<FinancialServiceProviderViewModel> FSPList) {
        Collections.sort(FSPList, new Comparator<FinancialServiceProviderViewModel>() {
            @Override
            public int compare(FinancialServiceProviderViewModel provider1, FinancialServiceProviderViewModel provider2) {
                return provider1.getDistance() < provider2.getDistance() ? -1 : (provider1.getDistance() > provider2.getDistance()) ? 1 : 0;
            }
        });
    }

    public void deflateAllFspMarkers() {
        for (OverlayItem overlayItem : overlayItems) {
            if (!overlayItem.getSnippet().isEmpty()) {
                Bitmap bitmap = ((BitmapDrawable) fspMarker).getBitmap();
                Drawable scaledDrawable = new BitmapDrawable(activity.getResources(), Bitmap.createScaledBitmap(bitmap, 90, 90, true));
                scaledDrawable.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                overlayItem.setMarker(scaledDrawable);
            }
        }
    }

    private void removeUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanEdit(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    public List<FinancialServiceProvider> getFinancialServiceProvidersFromCache(String fragmentTag) {
        List<FinancialServiceProvider> cachedFSPs = new ArrayList<>();
        try {
            cachedFSPs = (List<FinancialServiceProvider>) InternalStorageService.readCacheFromFile(context, fragmentTag);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cachedFSPs;
    }

    private class MapOverlayItem extends ItemizedOverlay<OverlayItem> {
        private OverlayItem overlayItem;

        MapOverlayItem(Drawable drawable, double latitude, double longitude, long fspId) {
            super(drawable);

            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
            overlayItem = new OverlayItem("", String.valueOf(fspId), geoPoint);
            overlayItems.add(overlayItem);

            populate();
        }

        @Override
        protected OverlayItem createItem(int i) {
            return overlayItems.get(i);
        }

        @Override
        public int size() {
            return overlayItems.size();
        }

        @Override
        public boolean onSnapToItem(int x, int y, Point snapPoint, IMapView mapView) {
            return false;
        }

        protected boolean onTap(int index) {
            setUpViews();
            OverlayItem item = overlayItems.get(index);
            Bitmap bitmap = ((BitmapDrawable) fspMarker).getBitmap();

            //check if fsp marker is clicked or device location marker
            if (!item.getSnippet().isEmpty() && item.getSnippet() != null) {
                FSP_ID = Long.parseLong(item.getSnippet());
                FinancialServiceProvider fsp = AppManager.getFinancialServiceProviderById(FSP_ID);
                toggleMapHeight(fsp);
                updateTextViewsWithFSPDetails(fsp);
                deflateAllFspMarkers();
                inflateFspMarker(fsp);
                GeoPoint geoPoint = new GeoPoint(fsp.getCoordinates().get(1), fsp.getCoordinates().get(0));
                mapController.setCenter(geoPoint);
                mapController.setZoom(HALF_VIEW_MAP_ZOOM);
                mapController.animateTo(geoPoint);
            }
            return true;
        }

        @Override
        public boolean onLongPress(MotionEvent e, MapView mapView) {
            RelativeLayout.LayoutParams mapParams = (RelativeLayout.LayoutParams) mapView.getLayoutParams();
            int height = mapParams.height;
            if (height != ViewGroup.LayoutParams.MATCH_PARENT) {
                mapView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                GeoPoint geoPoint = AppManager.getDeviceGeoPoint();
                mapController.setCenter(geoPoint);
                mapController.setZoom(FULL_VIEW_MAP_ZOOM);
                mapController.animateTo(geoPoint);
                //make sure all fsp markers have the default size
                deflateAllFspMarkers();

            }
            return true;
        }
    }

    private class DeviceLocationOverlayItem extends ItemizedOverlay<OverlayItem> {
        private List<OverlayItem> overlayItemList = new ArrayList<>();
        private Drawable marker;
        private OverlayItem overlayItem;

        DeviceLocationOverlayItem(Drawable drawable, double latitude, double longitude) {
            super(drawable);

            marker = drawable;
            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
            overlayItem = new OverlayItem("", "", geoPoint);
            overlayItemList.add(overlayItem);

            populate();
        }

        @Override
        protected OverlayItem createItem(int i) {
            return overlayItemList.get(i);
        }

        @Override
        public int size() {
            return overlayItemList.size();
        }

        @Override
        public boolean onSnapToItem(int x, int y, Point snapPoint, IMapView mapView) {
            return false;
        }
    }
}
