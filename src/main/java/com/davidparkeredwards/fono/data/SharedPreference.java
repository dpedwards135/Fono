package com.davidparkeredwards.fono.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.davidparkeredwards.fono.FONO;
import com.davidparkeredwards.fono.R;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class SharedPreference {

    public static final String PREFS_NAME = FONO.getContext().getString(R.string.prefs_name);
    public static final String PREFS_LOCATION_KEY = FONO.getContext().getString(R.string.prefs_location_key);
    public static final String PREFS_SYNC_DATE_KEY = FONO.getContext().getString(R.string.prefs_sync_date_key);
    public static final String PREFS_CATEGORIES_KEY = "FONO_PREFERRED_CATEGORIES_List";

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
    public void saveCategories(Context context, Set<String> categoriesList) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putStringSet(PREFS_CATEGORIES_KEY, categoriesList);
        editor.commit();
    }
    public String getValue(Context context, String value) {
        SharedPreferences settings;
        String text;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(value, null);
        return text;
    }

    public Set<String> getCategoriesList(Context context) {
        SharedPreferences settings;
        Set<String> categoriesList;


        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        categoriesList = settings.getStringSet(PREFS_CATEGORIES_KEY, null);
        if (categoriesList == null) {
            categoriesList = new HashSet<String>(Arrays.asList("No Categories Selected"));
        }
        return categoriesList;
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