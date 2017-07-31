package com.hot.sentefinder.services;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.hot.sentefinder.adapters.FinancialServiceProviderAdapter;
import com.hot.sentefinder.models.FinancialServiceProvider;
import com.hot.sentefinder.utilities.ApplicationPreference;
import com.hot.sentefinder.viewmodels.FinancialServiceProviderViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jamie on 5/29/2017.
 */

public class GetProvidersTask extends AsyncTask<Void, Void, List<FinancialServiceProvider>> {
    private  String fragmentTag;
    private Context context;
    private Activity activity;
    private List<FinancialServiceProvider> financialServiceProviderList;
    private List<FinancialServiceProviderViewModel> financialServiceProviderArrayList;
    private FinancialServiceProviderAdapter financialServiceProviderAdapter = new FinancialServiceProviderAdapter(context, financialServiceProviderArrayList);

    public GetProvidersTask(String fragmentTag, Context context, Activity activity, List<FinancialServiceProvider> financialServiceProviderList, List<FinancialServiceProviderViewModel> financialServiceProviderArrayList, FinancialServiceProviderAdapter financialServiceProviderAdapter) {
        this.fragmentTag = fragmentTag;
        this.context = context;
        this.activity = activity;
        this.financialServiceProviderList = financialServiceProviderList;
        this.financialServiceProviderArrayList = financialServiceProviderArrayList;
        this.financialServiceProviderAdapter = financialServiceProviderAdapter;
    }

    @Override
    protected void onPreExecute() {
        ApplicationPreference applicationPreference = new ApplicationPreference(context, fragmentTag);
        applicationPreference.writeFirstLoadPreference(fragmentTag, false);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<FinancialServiceProvider> FSPs) {
        super.onPostExecute(FSPs);
        FragmentService fragmentService = new FragmentService(context, activity);

        financialServiceProviderArrayList.clear();
        FinancialServiceProviderViewModel fspViewModel;
        for (FinancialServiceProvider fsp : FSPs) {
            if (fsp.getAmenity().equals("mobile_money_agent")) {
                fspViewModel = new FinancialServiceProviderViewModel(fsp.getId(), fsp.getName(), fsp.getNetwork(), AppManager.getDistanceBetweenUserAndFSP(fsp), fsp.getAmenity());
            } else {
                fspViewModel = new FinancialServiceProviderViewModel(fsp.getId(), fsp.getName(), fsp.getOperator(), AppManager.getDistanceBetweenUserAndFSP(fsp), fsp.getAmenity());
            }
            financialServiceProviderList.add(fsp);
            financialServiceProviderArrayList.add(fspViewModel);

            fragmentService.ascendingOrderList(financialServiceProviderArrayList);

            financialServiceProviderAdapter.notifyDataSetChanged();
        }
        AppManager.createFinancialServiceProvidersList(fragmentTag, FSPs);
        fragmentService.setUpMap(financialServiceProviderList, AppManager.getDeviceGeoPoint());

    }

    @Override
    protected List<FinancialServiceProvider> doInBackground(Void... params) {

        List<FinancialServiceProvider> cachedFSPs = new ArrayList<>();
        try {
            cachedFSPs = (List<FinancialServiceProvider>) InternalStorageService.readCacheFromFile(context, fragmentTag);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cachedFSPs;
    }

}
