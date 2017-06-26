//package com.hot.sentefinder.activities;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Point;
//import android.graphics.PorterDuff;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.support.design.widget.AppBarLayout;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.hot.sentefinder.R;
//import com.hot.sentefinder.models.FinancialServiceProvider;
//import com.hot.sentefinder.services.AppManager;
//import com.hot.sentefinder.services.FragmentService;
//
//import org.osmdroid.api.IMapView;
//import org.osmdroid.config.Configuration;
//import org.osmdroid.util.GeoPoint;
//import org.osmdroid.views.MapController;
//import org.osmdroid.views.MapView;
//import org.osmdroid.views.overlay.ItemizedOverlay;
//import org.osmdroid.views.overlay.OverlayItem;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//
//public class FinancialServiceProviderDetailActivity extends AppCompatActivity {
//
//
//    private static final String TAG_FSP_ID = "TAG_FSP_ID";
//    private static final String TAG_FSP = "TAG_FSP";
//    FragmentService fragmentService;
//    long financialServiceProviderId;
//    private Toolbar toolbar;
//    private RelativeLayout locationLayout, openingHoursLayout, phoneLayout, additionalInfoLayout, reviewLayout;
//    private MapView mapView;
//    private MapController mapController;
//    private TextView fspNameView, fspDistanceFromUserView, fspTimeFromUserView, fspAddressView, fspOpeningHoursView, fspPhoneView, fspAdditionalInfoView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
//
//        setContentView(R.layout.activity_financial_service_provider_detail);
//        fragmentService = new FragmentService(getApplicationContext(), this);
//
//        //get string from fragment through bundle object
//        Bundle bundle = getIntent().getExtras();
//        financialServiceProviderId = bundle.getLong(TAG_FSP_ID);
//        Serializable serializable = bundle.getSerializable(TAG_FSP);
//        FinancialServiceProvider fsp = (FinancialServiceProvider)serializable;
//
//        toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        // displaying the up button in the toolbar and removing title
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//
//        //initializing and setting up the map view
//        mapView =(MapView)findViewById(R.id.map);
//        mapController = (MapController)mapView.getController();
//        mapController.setZoom(18);
//
//        //initialize the text views
//        fspNameView = (TextView)findViewById(R.id.fsp_name_text);
//        fspDistanceFromUserView = (TextView)findViewById(R.id.fsp_dist_from_user);
//        fspTimeFromUserView = (TextView)findViewById(R.id.fsp_time_from_user);
//        fspAddressView = (TextView)findViewById(R.id.fsp_location_text);
//        fspOpeningHoursView = (TextView)findViewById(R.id.fsp_opening_hours_text);
//        fspPhoneView = (TextView)findViewById(R.id.fsp_phone_number);
//        fspAdditionalInfoView = (TextView)findViewById(R.id.fsp_additional_information_text);
//
//        //initialize the relative layouts
//        locationLayout = (RelativeLayout)findViewById(R.id.fsp_location);
//        openingHoursLayout = (RelativeLayout)findViewById(R.id.fsp_opening_hours);
//        phoneLayout = (RelativeLayout)findViewById(R.id.fsp_telephone);
//        additionalInfoLayout = (RelativeLayout)findViewById(R.id.fsp_additional_information);
//        reviewLayout = (RelativeLayout)findViewById(R.id.fsp_review);
//
//        updateTextViewsWithFSPDetails(fsp);
//        fspSetCenterOfMap(fsp);
//
//        //get location of fsp and make it centre of the map
//        GeoPoint deviceGeoPoint = AppManager.getDeviceGeoPoint(getApplicationContext(), this);
//        double fspLatitude = deviceGeoPoint.getLatitude();
//        double fspLongitude = deviceGeoPoint.getLongitude();
//
//        Drawable marker = ContextCompat.getDrawable(this, R.drawable.ic_action_location);
//        marker.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
//
//        int markerWidth = marker.getIntrinsicWidth();
//        int markerHeight = marker.getIntrinsicHeight();
//        marker.setBounds(0, markerHeight, markerWidth, 0);
//
//        mapView.getOverlays().add(new MapLocations(marker, fspLatitude, fspLongitude));
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        //go back to the last fragment in previous activity
//        if(id == android.R.id.home){
//            onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    public void loadReviewForm(View view) {
//        Intent intent = new Intent(this, ReviewActivity.class);
//        startActivity(intent);
//    }
//
//
//    private class MapLocations extends ItemizedOverlay<OverlayItem> {
//
//        private List<OverlayItem> locations = new ArrayList<>();
//        private Drawable marker;
//        private OverlayItem overlayItem;
//
//
//        private MapLocations(Drawable marker, double Latitude, double Longitude) {
//            super(marker);
//            this.marker = marker;
//            GeoPoint geoPoint = new GeoPoint(Latitude,Longitude);
//            overlayItem = new OverlayItem("", "", geoPoint);
//            locations.add(overlayItem);
//
//            populate();
//        }
//
//        @Override
//        protected OverlayItem createItem(int i) {
//            return locations.get(i);
//        }
//
//        @Override
//        public int size() {
//            return locations.size();
//        }
//
//        @Override
//        public boolean onSnapToItem(int x, int y, Point snapPoint, IMapView mapView) {
//            return false;
//        }
//
//    }
//
//    public void updateTextViewsWithFSPDetails(FinancialServiceProvider fsp) {
//
//        double distance = AppManager.getDistanceBetweenUserAndFSP(getApplicationContext(),this,  fsp);
//        double time = AppManager.getTimeDurationBetweenUserAndFSP(getApplicationContext(),this,  fsp);
//        fspNameView.setText(fsp.getName());
//        fspDistanceFromUserView.setText(AppManager.convertDistanceToString(distance));
//        fspTimeFromUserView.setText(AppManager.convertTimeToString(time));
//
//        if (fsp.getAddrStreet() == null || fsp.getAddrCity() == null) {
//            locationLayout.setVisibility(View.INVISIBLE);
//        } else {
//            locationLayout.setVisibility(View.VISIBLE);
//        }
//        if (fsp.getOpeningHours() == null) {
//            openingHoursLayout.setVisibility(View.INVISIBLE);
//        } else {
//            openingHoursLayout.setVisibility(View.VISIBLE);
//        }
//        if (fsp.getPhone() == null) {
//            phoneLayout.setVisibility(View.INVISIBLE);
//        } else {
//            phoneLayout.setVisibility(View.VISIBLE);
//        }
//        if (fsp.getDescription() == null) {
//            additionalInfoLayout.setVisibility(View.INVISIBLE);
//        } else {
//            additionalInfoLayout.setVisibility(View.VISIBLE);
//        }
//
//        fspAddressView.setText(String.format("%s, %s", fsp.getAddrStreet(), fsp.getAddrCity()));
//        fspOpeningHoursView.setText(fsp.getOpeningHours());
//        fspPhoneView.setText(fsp.getPhone());
//        fspAdditionalInfoView.setText(fsp.getDescription());
//
//        RelativeLayout.LayoutParams reviewBtnParams = (RelativeLayout.LayoutParams) reviewLayout.getLayoutParams();
//        RelativeLayout.LayoutParams infoParams = (RelativeLayout.LayoutParams) additionalInfoLayout.getLayoutParams();
//        RelativeLayout.LayoutParams phoneParams = (RelativeLayout.LayoutParams) phoneLayout.getLayoutParams();
//        RelativeLayout.LayoutParams openingHrsParams = (RelativeLayout.LayoutParams) openingHoursLayout.getLayoutParams();
//
//        //change position of opening hours layout accordingly
//        if (locationLayout.getVisibility() == View.INVISIBLE) {
//            openingHrsParams.addRule(RelativeLayout.BELOW, R.id.fsp_distance_and_time);
//        } else {
//            openingHrsParams.addRule(RelativeLayout.BELOW, R.id.fsp_location);
//        }
//
//        //change position of phone layout accordingly
//        if (openingHoursLayout.getVisibility() == View.INVISIBLE) {
//            if (locationLayout.getVisibility() == View.INVISIBLE) {
//                phoneParams.addRule(RelativeLayout.BELOW, R.id.fsp_distance_and_time);
//            } else {
//                phoneParams.addRule(RelativeLayout.BELOW, R.id.fsp_location);
//            }
//        } else {
//            phoneParams.addRule(RelativeLayout.BELOW, R.id.fsp_opening_hours);
//        }
//
//        //change position of additional info layout accordingly
//        if (phoneLayout.getVisibility() == View.INVISIBLE) {
//            if (openingHoursLayout.getVisibility() == View.INVISIBLE) {
//                if (locationLayout.getVisibility() == View.INVISIBLE) {
//                    infoParams.addRule(RelativeLayout.BELOW, R.id.fsp_distance_and_time);
//                } else {
//                    infoParams.addRule(RelativeLayout.BELOW, R.id.fsp_location);
//                }
//            } else {
//                infoParams.addRule(RelativeLayout.BELOW, R.id.fsp_opening_hours);
//            }
//        } else {
//            infoParams.addRule(RelativeLayout.BELOW, R.id.fsp_telephone);
//        }
//
//        //change position of review button accordingly
//        if (additionalInfoLayout.getVisibility() == View.INVISIBLE) {
//            if (phoneLayout.getVisibility() == View.INVISIBLE) {
//                if (openingHoursLayout.getVisibility() == View.INVISIBLE) {
//                    if (locationLayout.getVisibility() == View.INVISIBLE) {
//                        reviewBtnParams.addRule(RelativeLayout.BELOW, R.id.fsp_distance_and_time);
//                    } else {
//                        reviewBtnParams.addRule(RelativeLayout.BELOW, R.id.fsp_location);
//                    }
//                } else {
//                    reviewBtnParams.addRule(RelativeLayout.BELOW, R.id.fsp_opening_hours);
//                }
//            } else {
//                reviewBtnParams.addRule(RelativeLayout.BELOW, R.id.fsp_telephone);
//            }
//        } else {
//            reviewBtnParams.addRule(RelativeLayout.BELOW, R.id.fsp_additional_information);
//        }
//    }
//
//    public void fspSetCenterOfMap(FinancialServiceProvider fsp) {
//        List<Double> coordinates = fsp.getCoordinates();
//        double fspLatitude = coordinates.get(1);
//        double fspLongitude = coordinates.get(0);
//        mapController.setCenter(new GeoPoint(fspLatitude, fspLongitude));
//    }
//}
