package com.hot.sentefinder.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.hot.sentefinder.R;
import com.hot.sentefinder.models.FinancialServiceProvider;
import com.hot.sentefinder.network.APIClientInterface;
import com.hot.sentefinder.network.RetrofitClient;
import com.hot.sentefinder.services.AppManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinancialServiceProviderSearchActivity extends AppCompatActivity {
    private SearchManager searchManager;
    private ListView fspListView;
    private ProgressBar progressBar;
    private List<String> fsproviders = new ArrayList<>();
    private List<FinancialServiceProvider> fspResultList = new ArrayList<>();
    private String selectedItem;
    Context context;
    private final String SEARCH_RESULTS = "SEARCH_RESULTS";
    private Bundle searchBundle;

    private static final String TAG_FRAGMENT = "FRAGMENT";
    private String activeFragmentString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_service_provider_search);

        context = getApplicationContext();

        searchBundle = new Bundle();
        //get string from main activity through bundle object
        Bundle fragmentBundle = getIntent().getExtras();
        activeFragmentString = fragmentBundle.getString(TAG_FRAGMENT);

        //initialize the views
        fspListView = (ListView)findViewById(R.id.fsp_category_list);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        //set up the search views
        final SearchView fspSearchView = (SearchView)findViewById(R.id.fsp_search);
        setupSearchView(fspSearchView);

        //called when the focus is on the fsp search view
        focusOnFSPSearchView(fspSearchView);

        fspListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               selectedItem = fsproviders.get(position);
                fspSearchView.setQuery(selectedItem, true);
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

    private void setupSearchView(SearchView searchView) {
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setFocusable(true);
        searchView.requestFocus();
    }

    private void focusOnFSPSearchView(final SearchView fspSearchView) {
        fspSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    fsproviders.clear();
                }
            }
        });

        fspSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchBundle.putString(TAG_FRAGMENT, activeFragmentString);
                searchForFSPs(s, activeFragmentString);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
//                filterList(queryText, fsproviders, fspCategoryArray, fspListView);
                return false;
            }
        });
    }

//    private void filterList(String queryString, List<String> list, String[] string_array, ListView listView){
//        queryString = queryString.toLowerCase(Locale.getDefault());
//
//        list.clear();
//        if(queryString.length() != 0){
//            for(String item: string_array){
//                if(item.toLowerCase(Locale.getDefault()).contains(queryString)){
//                    list.add(item);
//                    listView.setAdapter(adapter);
//                }
//                adapter.notifyDataSetChanged();
//            }
//        }else {
//            Collections.addAll(list, string_array);
//            listView.setAdapter(adapter);
//        }
//
//    }

    public void searchBorrowFinancialServiceProviders(String queryString) {
        progressBar.setVisibility(View.VISIBLE);
        String searchParam = AppManager.createSearchParameter(queryString);
        APIClientInterface retrofitClient = RetrofitClient.getClient().create(APIClientInterface.class);

        Call<List<FinancialServiceProvider>> call = retrofitClient.getBorrowFinancialServiceProviders(searchParam);
        call.enqueue(new Callback<List<FinancialServiceProvider>>() {
            @Override
            public void onResponse(Call<List<FinancialServiceProvider>> call, Response<List<FinancialServiceProvider>> response) {
                fspResultList.clear();
                if (response.body() != null) {
                    fspResultList.addAll(response.body());
                    for(FinancialServiceProvider fsp: response.body()){
                        fsproviders.add(fsp.getName());
                    }
//                    loadFSPCategories();
                    progressBar.setVisibility(View.INVISIBLE);
                    searchBundle.putSerializable(SEARCH_RESULTS, (Serializable) fspResultList);
                    Log.d("SUCCESS:", ""+response.body());
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
//                    returnToPreviousActivity(searchBundle);

                } else {
                    Toast.makeText(getApplicationContext(), "Unable to fetch data, try again later", Toast.LENGTH_LONG).show();
                    returnToPreviousActivity(searchBundle);
                }

            }

            @Override
            public void onFailure(Call<List<FinancialServiceProvider>> call, Throwable t) {
                Log.d("ERROR: ", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                returnToPreviousActivity(searchBundle);
            }
        });
    }

    public void searchForFSPs(String queryString, String fragment) {
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
            Toast.makeText(getApplicationContext(), "Unable to fetch data, try again later", Toast.LENGTH_LONG).show();
            returnToPreviousActivity(searchBundle);
        }

    }

    private void returnToPreviousActivity(Bundle bundle) {
        Intent intent = new Intent(getApplicationContext(), FinancialServiceProviderListActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
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
        progressBar.setVisibility(View.INVISIBLE);
    }
}
