package com.hot.sentefinder.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hot.sentefinder.R;
import com.hot.sentefinder.activities.MainActivity;
import com.hot.sentefinder.activities.ReviewActivity;
import com.hot.sentefinder.adapters.FinancialServiceProviderAdapter;
import com.hot.sentefinder.models.FinancialServiceProvider;
import com.hot.sentefinder.network.APIClientInterface;
import com.hot.sentefinder.network.RetrofitClient;
import com.hot.sentefinder.services.AppManager;
import com.hot.sentefinder.services.FragmentService;
import com.hot.sentefinder.services.GetProvidersTask;
import com.hot.sentefinder.services.InternalStorageService;
import com.hot.sentefinder.utilities.ApplicationPreference;
import com.hot.sentefinder.utilities.TouchListener;
import com.hot.sentefinder.viewmodels.FinancialServiceProviderViewModel;

import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SendMoneyFSPFragment extends Fragment {
    private static final String TAG_SEND_MONEY = "SEND_MONEY";
    private static final String TAG_FSP_ID = "TAG_FSP_ID";
    private final String SEARCH_RESULTS = "SEARCH_RESULTS";
    private static final String TAG_FRAGMENT = "FRAGMENT";
    private static long FSP_ID = 0;
    String searchParam;
    private List<FinancialServiceProviderViewModel> FSPList = new ArrayList<>();
    private List<FinancialServiceProvider> financialServiceProviderList = new ArrayList<>();
    private List<FinancialServiceProvider> searchResultsList = new ArrayList<>();
    private MenuItem mapMenuItem, listMenuItem, searchMenuItem;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout relativeLayout;
    private MapView mapView;
    private RecyclerView recyclerView;
    private Button fspReviewButton;
    private FinancialServiceProviderAdapter financialServiceProviderAdapter;
    private FragmentService fragmentService;
    private OnFragmentInteractionListener mListener;
    private ApplicationPreference applicationPreference;

    public SendMoneyFSPFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationPreference = new ApplicationPreference(getContext(), TAG_SEND_MONEY);

        fragmentService = new FragmentService(getContext(), getActivity());
        searchParam = AppManager.createSearchParameter();

        //set options menu to true to access the appbar menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_borrow_money_fsp, container, false);

        //initialize the views on the layout
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.fsp_map_and_detail_view);
        mapView = (MapView) rootView.findViewById(R.id.map);

        //initialize the relative review button
        fspReviewButton = (Button) rootView.findViewById(R.id.fsp_review_button);

        financialServiceProviderAdapter = new FinancialServiceProviderAdapter(getActivity().getApplicationContext(), FSPList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(financialServiceProviderAdapter);

        recyclerView.addOnItemTouchListener(new TouchListener(getActivity().getApplicationContext(), recyclerView, new TouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                FinancialServiceProviderViewModel fspViewModel = financialServiceProviderAdapter.getFinancialServiceProvider(position);

                FSP_ID = fspViewModel.getId();
                FinancialServiceProvider fsp = AppManager.getFSPById(TAG_SEND_MONEY, FSP_ID);
                fragmentService.setUpMap(financialServiceProviderList);
                //if fsp is null get the fsp from allfsplist in appmanager

                searchMenuItem.setVisible(false);
                mapMenuItem.setVisible(false);
                listMenuItem.setVisible(true);

                relativeLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.INVISIBLE);

                fragmentService.updateTextViewsWithFSPDetails(fsp);
                fragmentService.deflateAllFspMarkers();
                fragmentService.inflateFspMarker(fsp);
                fragmentService.toggleMapHeight(fsp);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSendFinancialServiceProviders();
            }
        });

        if (applicationPreference.readFirstLoadPreference()) {
            getSendFinancialServiceProviders();
        } else {
            List<FinancialServiceProvider> cachedFSPs = fragmentService.getFinancialServiceProvidersFromCache(TAG_SEND_MONEY);

            if (cachedFSPs.isEmpty()) {
                getSendFinancialServiceProviders();
            } else {
                //get bundle data from search activity
                Bundle bundle = getActivity().getIntent().getExtras();
                searchResultsList = (List<FinancialServiceProvider>) bundle.getSerializable(SEARCH_RESULTS);
                String fragment = bundle.getString(TAG_FRAGMENT);
                if (searchResultsList != null && (fragment != null && fragment.equals(TAG_SEND_MONEY))) {
                    getActivity().getIntent().removeExtra(TAG_FRAGMENT);
                    if (searchResultsList.size() > 0) {
                        loadSearchResults();
                    } else {
                        Toast.makeText(getContext(), "nothing found", Toast.LENGTH_SHORT).show();
                        new GetProvidersTask(TAG_SEND_MONEY, getContext(), getActivity(), financialServiceProviderList, FSPList, financialServiceProviderAdapter).execute();

                    }
                } else {
                    new GetProvidersTask(TAG_SEND_MONEY, getContext(), getActivity(), financialServiceProviderList, FSPList, financialServiceProviderAdapter).execute();

                }

            }
        }

        fspReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong(TAG_FSP_ID, FSP_ID);

                Intent intent = new Intent(getContext(), ReviewActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void loadSearchResults() {
        FSPList.clear();
        FinancialServiceProviderViewModel fspViewModel;
        for (FinancialServiceProvider fsp : searchResultsList) {
            if (fsp.getAmenity().equals("mobile_money_agent")) {
                fspViewModel = new FinancialServiceProviderViewModel(fsp.getId(), fsp.getName(), fsp.getNetwork(), AppManager.getDistanceBetweenUserAndFSP(fsp), fsp.getAmenity());
            } else {
                fspViewModel = new FinancialServiceProviderViewModel(fsp.getId(), fsp.getName(), fsp.getOperator(), AppManager.getDistanceBetweenUserAndFSP(fsp), fsp.getAmenity());
            }
            financialServiceProviderList.add(fsp);
            FSPList.add(fspViewModel);

            fragmentService.ascendingOrderList(FSPList);

        }
        financialServiceProviderAdapter.notifyDataSetChanged();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        searchMenuItem = menu.findItem(R.id.action_search);
        mapMenuItem = menu.findItem(R.id.action_map_toggle);
        listMenuItem = menu.findItem(R.id.action_list_toggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_map_toggle) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mapView.getLayoutParams();
            if (params.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                fragmentService.setUpMap(financialServiceProviderList, false);
                fragmentService.deflateAllFspMarkers();
            }
            return true;
        } else {

            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void getSendFinancialServiceProviders() {
        swipeRefreshLayout.setRefreshing(true);

        APIClientInterface retrofitClient = RetrofitClient.getClient().create(APIClientInterface.class);

        Call<List<FinancialServiceProvider>> call = retrofitClient.getSendFinancialServiceProviders(searchParam);
        call.enqueue(new Callback<List<FinancialServiceProvider>>() {
            @Override
            public void onResponse(Call<List<FinancialServiceProvider>> call, Response<List<FinancialServiceProvider>> response) {
                Context context = getContext();
                if(context == null)
                    //App was closed or sth close to that
                    return;
                FSPList.clear();

                FinancialServiceProviderViewModel fspViewModel;
                if (response.body() != null) {
                    try {
                        InternalStorageService.writeCacheToFile(getContext(), TAG_SEND_MONEY, response.body());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("ERROR: ", response.errorBody().toString());
                    Toast.makeText(getContext(), "Unable to fetch data, try again later", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }

                new GetProvidersTask(TAG_SEND_MONEY, getContext(), getActivity(), financialServiceProviderList, FSPList, financialServiceProviderAdapter).execute();

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<FinancialServiceProvider>> call, Throwable t) {
                Log.d("ERROR: ", t.getMessage());
                Context context = getContext();
                if(context == null)
                    //App was closed or sth close to that
                    return;
                Toast.makeText(getContext(), "Unable to fetch data, try again later", Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
