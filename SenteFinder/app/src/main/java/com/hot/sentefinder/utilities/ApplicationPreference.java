package com.hot.sentefinder.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.hot.sentefinder.R;

/**
 * Created by Jamie on 5/29/2017.
 */

public class ApplicationPreference {
    private SharedPreferences sharedPreferences;
    private Context context;
    private String fragmentTag;

    public ApplicationPreference(Context context, String fragmentTag) {
        this.context = context;
        this.fragmentTag = fragmentTag;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.application_preference),Context.MODE_PRIVATE);
    }

    public void writeFirstLoadPreference(String fragmentTag, boolean isFirstLoad){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(fragmentTag, isFirstLoad);
        editor.apply();
    }

    public  boolean readFirstLoadPreference(){
        return sharedPreferences.getBoolean(fragmentTag, true);
    }
}
