package com.hot.sentefinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hot.sentefinder.R;
import com.hot.sentefinder.fragments.BorrowMoneyFSPFragment;
import com.hot.sentefinder.fragments.SaveMoneyFSPFragment;
import com.hot.sentefinder.fragments.SendMoneyFSPFragment;
import com.hot.sentefinder.fragments.WithdrawMoneyFSPFragment;
import com.hot.sentefinder.services.FragmentService;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import java.io.File;
import java.util.List;

public class FinancialServiceProviderListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG_FRAGMENT = "FRAGMENT";
    private static final String TAG_BORROW_MONEY = "BORROW_MONEY";
    private static final String TAG_SAVE_MONEY = "SAVE_MONEY";
    private static final String TAG_SEND_MONEY = "SEND_MONEY";
    private static final String TAG_WITHDRAW_MONEY = "WITHDRAW_MONEY";
    private static final String TAG_TITLE = "TAG_TITLE";
    public static int navigationItemIndex = 0;
    private static String CURRENT_TAG;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private MapView mapView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout relativeLayout;
    private FragmentService fragmentService;
    private MenuItem menuListItem;
    private MenuItem menuMapItem;
    private MenuItem menuSearchItem;
    private String[] fragmentTitles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_service_provider_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //set the fragment titles array
        fragmentTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        //set navigation view listener
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //initiate the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //set up the cache directories for the map tiles
        Configuration.getInstance().setOsmdroidBasePath(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "osmdroidDir"));
        Configuration.getInstance().setOsmdroidTileCache(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "osmdroidDir/tiles"));

        //load selected fragment from main menu or previously active fragment from search
        loadFragment(fragmentTransaction);
        fragmentService = new FragmentService(getApplicationContext(), this);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.financial_service_provider_list_menu, menu);

        menuListItem = menu.findItem(R.id.action_list_toggle);
        menuMapItem = menu.findItem(R.id.action_map_toggle);
        menuSearchItem = menu.findItem(R.id.action_search);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putString(TAG_FRAGMENT, CURRENT_TAG);
        bundle.putString(TAG_TITLE, fragmentTitles[navigationItemIndex]);

        FragmentManager fragmentManager = FinancialServiceProviderListActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_map_toggle:
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null && fragment.isVisible()) {
                            item.setVisible(false);
                            menuSearchItem.setVisible(false);
                            menuListItem.setVisible(true);

                            //always set the device location as center of the map by default
                            mapView = (MapView) fragment.getView().findViewById(R.id.map);
                            mapView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                            relativeLayout = (RelativeLayout) fragment.getView().findViewById(R.id.fsp_map_and_detail_view);
                            relativeLayout.setVisibility(View.VISIBLE);

                            swipeRefreshLayout = (SwipeRefreshLayout)fragment.getView().findViewById(R.id.swipe_refresh_layout);
                            swipeRefreshLayout.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                //this returns false so that is handled by the fragment
                return false;
            case R.id.action_list_toggle:
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null && fragment.isVisible()) {
                            item.setVisible(false);
                            menuSearchItem.setVisible(true);
                            menuMapItem.setVisible(true);

                            relativeLayout = (RelativeLayout)fragment.getView().findViewById(R.id.fsp_map_and_detail_view);
                            relativeLayout.setVisibility(View.INVISIBLE);

                            swipeRefreshLayout = (SwipeRefreshLayout)fragment.getView().findViewById(R.id.swipe_refresh_layout);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }

                return true;
            case R.id.action_search:
                Intent intent = new Intent(this, FinancialServiceProviderSearchActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = null;

        switch(id){
            case R.id.nav_home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.nav_borrow:
                navigationItemIndex = 1;
                CURRENT_TAG = TAG_BORROW_MONEY;
                fragmentClass = BorrowMoneyFSPFragment.class;
                break;
            case R.id.nav_save:
                navigationItemIndex = 2;
                CURRENT_TAG = TAG_SAVE_MONEY;
                fragmentClass = SaveMoneyFSPFragment.class;
                break;
            case R.id.nav_send:
                navigationItemIndex = 3;
                CURRENT_TAG = TAG_SEND_MONEY;
                fragmentClass = SendMoneyFSPFragment.class;
                break;
            case R.id.nav_withdraw:
                navigationItemIndex = 4;
                CURRENT_TAG = TAG_WITHDRAW_MONEY;
                fragmentClass = WithdrawMoneyFSPFragment.class;
                break;
            case R.id.nav_about_us:
                Intent intent1 = new Intent(this, AboutUsActivity.class);
                startActivity(intent1);
                return true;
        }

        try {
            assert fragmentClass != null;
            fragment = (Fragment)fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setFragmentTitle();
        changeAppBarMenuItems();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragmentTitle() {
        getSupportActionBar().setTitle(fragmentTitles[navigationItemIndex]);
    }

    private void changeAppBarMenuItems() {
        if(menuListItem.isVisible()){
            menuListItem.setVisible(false);
            menuSearchItem.setVisible(true);
            menuMapItem.setVisible(true);
        }
    }

    private void loadFragment(FragmentTransaction fragmentTransaction) {
        //get string from main activity through bundle object
        Bundle bundle = getIntent().getExtras();
        String tag = bundle.getString(TAG_FRAGMENT);

        if(tag != null){
            switch (tag){
                case TAG_BORROW_MONEY:
                    navigationItemIndex = 1;
                    navigationView.getMenu().getItem(navigationItemIndex).setChecked(true);

                    BorrowMoneyFSPFragment borrowMoneyFSPFragment = new BorrowMoneyFSPFragment();
                    CURRENT_TAG = TAG_BORROW_MONEY;
                    fragmentTransaction.replace(R.id.main_content, borrowMoneyFSPFragment).commit();
                    break;
                case TAG_SAVE_MONEY:
                    navigationItemIndex = 2;
                    navigationView.getMenu().getItem(navigationItemIndex).setChecked(true);

                    SaveMoneyFSPFragment saveMoneyFSPFragment = new SaveMoneyFSPFragment();
                    CURRENT_TAG = TAG_SAVE_MONEY;
                    fragmentTransaction.replace(R.id.main_content, saveMoneyFSPFragment).commit();
                    break;
                case TAG_SEND_MONEY:
                    navigationItemIndex = 3;
                    navigationView.getMenu().getItem(navigationItemIndex).setChecked(true);

                    SendMoneyFSPFragment sendMoneyFSPFragment = new SendMoneyFSPFragment();
                    CURRENT_TAG = TAG_SEND_MONEY;
                    fragmentTransaction.replace(R.id.main_content, sendMoneyFSPFragment).commit();
                    break;
                case TAG_WITHDRAW_MONEY:
                    navigationItemIndex = 4;
                    navigationView.getMenu().getItem(navigationItemIndex).setChecked(true);

                    WithdrawMoneyFSPFragment withdrawMoneyFSPFragment = new WithdrawMoneyFSPFragment();
                    CURRENT_TAG = TAG_WITHDRAW_MONEY;
                    fragmentTransaction.replace(R.id.main_content, withdrawMoneyFSPFragment).commit();
                    break;
                default:
                    break;
            }
            setFragmentTitle();
        }
    }

}
