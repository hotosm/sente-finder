package com.hot.sentefinder.activities;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hot.sentefinder.R;
import com.hot.sentefinder.services.AppManager;

import org.osmdroid.api.IMapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

public class FinancialServiceProviderMapToggleActivity extends AppCompatActivity {

    private static final String TAG_FRAGMENT = "FRAGMENT";
    private static final String TAG_TITLE = "TAG_TITLE";
    String activeFragmentString;
    String activeFragmentTitle;
    private Toolbar toolbar;
    private MapView mapView;
    private MapController mapController;
    private Drawable userMarker, fspMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_service_provider_map_toggle);

        //get string from list activity through bundle object
        Bundle bundle = getIntent().getExtras();
        activeFragmentString = bundle.getString(TAG_FRAGMENT);
        activeFragmentTitle = bundle.getString(TAG_TITLE);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(activeFragmentTitle);

        mapView = (MapView)findViewById(R.id.map);
        mapController = (MapController) mapView.getController();
        mapController.setZoom(18);

        //drawable for user location
        userMarker = ContextCompat.getDrawable(this, R.drawable.ic_user_location);
        userMarker.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        //drawable for the fsps
        fspMarker = ContextCompat.getDrawable(this, R.drawable.ic_action_location);
        fspMarker.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

        //my current location ...you will need to get device location
        //set user location as center of map
        GeoPoint deviceGeoPoint = AppManager.getDeviceGeoPoint();
        mapController.setCenter(deviceGeoPoint);

        addMarkerOnMap(deviceGeoPoint, userMarker);

        addMarkerOnMap(new GeoPoint(0.329061, 32.578644), fspMarker);
        addMarkerOnMap(new GeoPoint(0.330944, 32.577538), fspMarker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.financial_service_provider_map_toggle_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }else if (id == R.id.action_list_toggle) {
            Bundle bundle = new Bundle();
            bundle.putString(TAG_FRAGMENT, activeFragmentString);

            Intent intent = new Intent(getApplicationContext(), FinancialServiceProviderListActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addMarkerOnMap(GeoPoint geoPoint, Drawable drawable) {
        int markerWidth = drawable.getIntrinsicWidth();
        int markerHeight = drawable.getIntrinsicHeight();
        drawable.setBounds(0, markerHeight, markerWidth, 0);

        mapView.getOverlays().add(new MapLocations(drawable, geoPoint.getLatitude(), geoPoint.getLongitude()));
    }

    private void addFinancialServiceProviderMarkersOnMap(Drawable drawable) {
        int markerWidth = drawable.getIntrinsicWidth();
        int markerHeight = drawable.getIntrinsicHeight();
        drawable.setBounds(0, markerHeight, markerWidth, 0);

    }

    private class MapLocations extends ItemizedOverlay<OverlayItem> {

        private List<OverlayItem> locations = new ArrayList<>();
        private Drawable marker;
        private OverlayItem overlayItem;


        private MapLocations(Drawable marker, double Latitude, double Longitude) {
            super(marker);
            this.marker = marker;
            GeoPoint geoPoint = new GeoPoint(Latitude,Longitude);
            overlayItem = new OverlayItem("", "", geoPoint);
            locations.add(overlayItem);

            populate();
        }

        @Override
        protected OverlayItem createItem(int i) {
            return locations.get(i);
        }

        @Override
        public int size() {
            return locations.size();
        }

        @Override
        public boolean onSnapToItem(int x, int y, Point snapPoint, IMapView mapView) {
            return false;
        }


    }

}
