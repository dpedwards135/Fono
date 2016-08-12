package com.davidparkeredwards.fono.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.davidparkeredwards.fono.FONO;
import com.davidparkeredwards.fono.R;

import java.util.Date;


public class SharedPreference {

    public static final String PREFS_NAME = FONO.getContext().getString(R.string.prefs_name);
    public static final String PREFS_LOCATION_KEY = FONO.getContext().getString(R.string.prefs_location_key);
    public static final String PREFS_SYNC_DATE_KEY = FONO.getContext().getString(R.string.prefs_sync_date_key);

    public SharedPreference() {
        super();
    }

    public void save(Context context, String text, String value) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putString(value, text); //3

        editor.commit(); //4
    }

    public String getValue(Context context, String value) {
        SharedPreferences settings;
        String text;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(value, null);
        return text;
    }

    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

    public void removeValue(Context context, String value) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.remove(value);
        editor.commit();
    }
}