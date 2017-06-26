//package com.hot.sentefinder.utilities;
//
//import android.app.Activity;
//import android.app.SearchManager;
//import android.content.ContentProvider;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.MatrixCursor;
//import android.net.Uri;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.hot.sentefinder.models.FinancialServiceProvider;
//import com.hot.sentefinder.network.APIClientInterface;
//import com.hot.sentefinder.network.RetrofitClient;
//import com.hot.sentefinder.services.AppManager;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//
//public class FSPSuggestionProvider extends ContentProvider {
//    private static final String[] COLUMNS = {"_id", SearchManager.SUGGEST_COLUMN_TEXT_1};
//    public MatrixCursor cursor = new MatrixCursor(COLUMNS);
//    private Context context;
//    private Activity activity;
//    private List<FinancialServiceProvider> financialServiceProviderList = new ArrayList<>();
//
//    public FSPSuggestionProvider(Context context, Activity activity) {
//        this.context = context;
//        this.activity = activity;
//        getFinancialServiceProviders();
//    }
//
//    @Override
//    public boolean onCreate() {
//        return false;
//    }
//
//    @Override
//    public Cursor query(Uri uri, String[] projection, String query, String[] selectionArgs, String sortOrder) {
//        for (FinancialServiceProvider fsp : financialServiceProviderList) {
//            FSPSuggestionProvider.this.cursor.addRow(new Object[]{fsp.getId(), fsp.getName()});
//        }
//
//        MatrixCursor returnMatrix = cursor;
//        cursor = new MatrixCursor(COLUMNS);
//        return returnMatrix;
//    }
//
//    private void getFinancialServiceProviders() {
//        financialServiceProviderList.clear();
//        APIClientInterface retrofitClient = RetrofitClient.getClient().create(APIClientInterface.class);
//        String searchParam = AppManager.createSearchParameter(context, activity);
//        Call<List<FinancialServiceProvider>> call = retrofitClient.getAllFinancialServiceProviders(searchParam);
//
//        call.enqueue(new Callback<List<FinancialServiceProvider>>() {
//            @Override
//            public void onResponse(Call<List<FinancialServiceProvider>> call, Response<List<FinancialServiceProvider>> response) {
//                if (response.isSuccessful()) {
//
//                    for (FinancialServiceProvider fsp : response.body()) {
//                        financialServiceProviderList.add(fsp);
//                    }
//                    AppManager.AllFinancialServiceProviders(financialServiceProviderList);
//
//                } else {
//                    Toast.makeText(context, "Unable to fetch data, try again later", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<FinancialServiceProvider>> call, Throwable t) {
//                Toast.makeText(context, "Unable to connect, try again later", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    @Override
//    public String getType(Uri uri) {
//        return null;
//    }
//
//    @Override
//    public Uri insert(Uri uri, ContentValues values) {
//        return null;
//    }
//
//    @Override
//    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        return 0;
//    }
//
//    @Override
//    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        return 0;
//    }
//
//    public FinancialServiceProvider getFinancialServiceProvider(int position) {
//        return financialServiceProviderList.get(position);
//    }
//
//    public Serializable getFinancialServiceProviderById(long id) {
//        return FinancialServiceProviderHashMap().get(String.valueOf(id));
//    }
//
//    private HashMap<String, FinancialServiceProvider> FinancialServiceProviderHashMap() {
//        final HashMap<String, FinancialServiceProvider> hashMap = new HashMap<>();
//        for (FinancialServiceProvider fsp : financialServiceProviderList) {
//            hashMap.put(String.valueOf(fsp.getId()), fsp);
//        }
//        return hashMap;
//    }
//
//}
