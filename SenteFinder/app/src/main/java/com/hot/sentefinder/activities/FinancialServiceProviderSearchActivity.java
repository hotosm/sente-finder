package com.hot.sentefinder.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.hot.sentefinder.R;
import com.hot.sentefinder.adapters.PlacesAutoCompleteAdapter;
import com.hot.sentefinder.models.FinancialServiceProvider;
import com.hot.sentefinder.network.APIClientInterface;
import com.hot.sentefinder.network.RetrofitClient;
import com.hot.sentefinder.services.AppManager;
import com.hot.sentefinder.services.FragmentService;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinancialServiceProviderSearchActivity extends AppCompatActivity {
    private AutoCompleteTextView fspView, cityView;
    private ImageButton searchBtn;
    private ProgressBar progressBar;
    private List<FinancialServiceProvider> fspResultList = new ArrayList<>();
    Context context;
    private final String SEARCH_RESULTS = "SEARCH_RESULTS";
    private final String SEARCH_COORDINATES = "SEARCH_COORDINATES";
    private Bundle searchBundle;

    private static final String TAG_FRAGMENT = "FRAGMENT";
    private String activeFragmentString;
    FragmentService fragmentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_service_provider_search);

        context = getApplicationContext();

        fragmentService = new FragmentService(context, getParent());
        searchBundle = new Bundle();
        //get string from main activity through bundle object
        final Bundle fragmentBundle = getIntent().getExtras();
        activeFragmentString = fragmentBundle.getString(TAG_FRAGMENT);

        //initialize the views
        searchBtn = (ImageButton)findViewById(R.id.search_btn);
        fspView = (AutoCompleteTextView)findViewById(R.id.fsp_search);
        cityView = (AutoCompleteTextView)findViewById(R.id.city_search);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        cityView.setDropDownBackgroundResource(R.color.colorWhite);

        setDefaultCity();

        cityView.setAdapter(new PlacesAutoCompleteAdapter(getApplicationContext(), R.layout.search_list_item));

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBundle.putString(TAG_FRAGMENT, activeFragmentString);

                String selectedFSP = getSelectedFSP();
                String selectedCity = getSelectedCity();
                if(!selectedFSP.isEmpty() && !selectedCity.isEmpty()){
                    progressBar.setVisibility(View.VISIBLE);
                    if(selectedCity.equals("Nearby")){
                        localSearchForFinancialServiceProviders(selectedFSP, activeFragmentString);
                    }else{
                        searchForFinancialServiceProviders(selectedFSP, activeFragmentString);
                    }

                }
                else if(!selectedFSP.isEmpty() && selectedCity.isEmpty()){
                    progressBar.setVisibility(View.VISIBLE);
                    localSearchForFinancialServiceProviders(selectedFSP, activeFragmentString);
                }
                else if(selectedFSP.isEmpty() && !selectedCity.isEmpty()){
                    progressBar.setVisibility(View.VISIBLE);
                    if(selectedCity.equals("Nearby")){
                        localSearchForFinancialServiceProviders(selectedFSP, activeFragmentString);
                    }else{
                        searchForFinancialServiceProviders(selectedFSP, activeFragmentString);
                    }
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putString(TAG_FRAGMENT, activeFragmentString);

        Intent intent = new Intent(context, FinancialServiceProviderListActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        super.onBackPressed();
    }

    private void setDefaultCity() {
        cityView.setText(R.string.default_city);
    }

    private String getSelectedCity(){
        String selected = cityView.getText().toString().trim();
        List<String> selectedStrings = Arrays.asList(selected.split(","));

        return selectedStrings.get(0);
    }

    private String getSelectedFSP(){
        return fspView.getText().toString().trim();
    }

    private GeoPoint getSelectedCityGeoPoint(){
        GeoPoint geoPoint = null;
        String selectedCity = cityView.getText().toString().trim();
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(selectedCity, 1);
            geoPoint = new GeoPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return geoPoint;
    }

    public void searchForFinancialServiceProviders(String queryString, String fragment) {
        GeoPoint geoPoint = getSelectedCityGeoPoint() == null? AppManager.getDeviceGeoPoint(): getSelectedCityGeoPoint();
        String searchParam = AppManager.createSearchParameter(queryString, geoPoint);
        APIClientInterface retrofitClient = RetrofitClient.getClient().create(APIClientInterface.class);

        Call<List<FinancialServiceProvider>> call = null;

        switch(fragment){
            case "BORROW_MONEY":
                call = retrofitClient.getBorrowFinancialServiceProviders(searchParam);
                break;
            case "SAVE_MONEY":
                call = retrofitClient.getSaveFinancialServiceProviders(searchParam);
                break;
            case "SEND_MONEY":
                call = retrofitClient.getSendFinancialServiceProviders(searchParam);
                break;
            case "WITHDRAW_MONEY":
                call = retrofitClient.getWithdrawFinancialServiceProviders(searchParam);
                break;
        }

        assert call != null;
        call.enqueue(new Callback<List<FinancialServiceProvider>>() {
            @Override
            public void onResponse(Call<List<FinancialServiceProvider>> call, Response<List<FinancialServiceProvider>> response) {
                fspResultList.clear();
                AppManager.searchResults.clear();
                if (response.body() != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    fspResultList.addAll(response.body());
                    AppManager.searchResults.addAll(response.body());
                    searchBundle.putSerializable(SEARCH_RESULTS, (Serializable) fspResultList);
                    searchBundle.putSerializable(SEARCH_COORDINATES, getSelectedCityGeoPoint());
                    Log.d("SUCCESS:", ""+response.body());
                    returnToPreviousActivity(searchBundle);

                } else {
                    Toast.makeText(getApplicationContext(), "No financial service providers found", Toast.LENGTH_LONG).show();
                    returnToPreviousActivity(searchBundle);
                }

            }

            @Override
            public void onFailure(Call<List<FinancialServiceProvider>> call, Throwable t) {
                Log.d("ERROR: ", t.getMessage());
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Unable to fetch data, try again later ", Toast.LENGTH_LONG).show();
                returnToPreviousActivity(searchBundle);
            }
        });
    }

    private void returnToPreviousActivity(Bundle bundle) {
        Intent intent = new Intent(getApplicationContext(), FinancialServiceProviderListActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void localSearchForFinancialServiceProviders(String queryString, String fragment) {
        List<FinancialServiceProvider> list = new ArrayList<>();
        switch(fragment){
            case "BORROW_MONEY":
                list = AppManager.borrowFinancialServiceProviderList;
                break;
            case "SAVE_MONEY":
                list = AppManager.saveFinancialServiceProviderList;
                break;
            case "SEND_MONEY":
                list = AppManager.sendFinancialServiceProviderList;
                break;
            case "WITHDRAW_MONEY":
                list = AppManager.withdrawFinancialServiceProviderList;
                break;
        }

        fspResultList.clear();
        if (list != null) {
            search(queryString, list);
            searchBundle.putSerializable(SEARCH_RESULTS, (Serializable) fspResultList);
            returnToPreviousActivity(searchBundle);
        } else {
            Toast.makeText(getApplicationContext(), "No financial service providers found", Toast.LENGTH_LONG).show();
            returnToPreviousActivity(searchBundle);
        }

    }

    // TODO use hamming distance search for better results
    private void search(String queryString, List<FinancialServiceProvider> searchList){
        queryString = queryString.toLowerCase(Locale.getDefault());
        fspResultList.clear();
        if(queryString.length() != 0){
            for(FinancialServiceProvider fsp: searchList){
                if(fsp.getName().toLowerCase(Locale.getDefault()).contains(queryString)){
                    fspResultList.add(fsp);
                }
            }
        }
    }
}
