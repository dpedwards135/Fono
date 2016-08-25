package com.davidparkeredwards.fono;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidparkeredwards.fono.data.EventDbManager;
import com.davidparkeredwards.fono.sync.FonoSyncAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity  {

    ImageView radarSelector;
    ImageView preferenceSelector;
    ImageView customSearchSelector;
    int defaultBackgroundColor = 0xFFCCCCCC;
    int viewSelected; // 1 = Radar, 2 = Pref, 3 = Custom

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radarSelector = (ImageView) findViewById(R.id.radarViewSelector);
        preferenceSelector = (ImageView) findViewById(R.id.preferenceViewSelector);
        customSearchSelector = (ImageView) findViewById(R.id.customSearchViewSelector);

        if (savedInstanceState == null) {
            Bundle radarBundle = new Bundle();
            radarBundle.putString("Requester", EventDbManager.RADAR_SEARCH_REQUEST);
            Fragment fragment = new CustomSearchFragment();
            fragment.setArguments(radarBundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityContainer,
                    fragment).commit();
            viewSelected = 1;
            changeSelectedFragment();
        } else {
            String savedView = savedInstanceState.getString("View");
            Log.i("Saved View", savedView);
        }
        FonoSyncAdapter fonoSyncAdapter = new FonoSyncAdapter(this, true);
        fonoSyncAdapter.initializeSyncAdapter(this);
        fonoSyncAdapter.syncImmediately(this);



    }


    public void launchCustomSearch(View view) {
        Bundle radarBundle = new Bundle();
        radarBundle.putString("Requester", EventDbManager.CUSTOM_SEARCH_REQUEST);
        Fragment fragment = new CustomSearchFragment();
        fragment.setArguments(radarBundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityContainer,
                fragment).commit();
        viewSelected = 3;
        changeSelectedFragment();
    }

    public void launchEventfulResults(View view) {
        Bundle radarBundle = new Bundle();
        radarBundle.putString("Requester", EventDbManager.RADAR_SEARCH_REQUEST);
        Fragment fragment = new CustomSearchFragment();
        fragment.setArguments(radarBundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityContainer,
                fragment).commit();
        viewSelected = 1;
        changeSelectedFragment();
        Log.i("RadarLaunch", "launchEventfulResults: Launching Radar");
    }

    public void launchPreferences(View view) {

        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityContainer, new PreferencesFragment()).commit();
        viewSelected = 2;
        changeSelectedFragment();
    }

    public void changeSelectedFragment() {
        radarSelector.setBackgroundResource(0);
        preferenceSelector.setBackgroundResource(0);
        customSearchSelector.setBackgroundResource(0);
        switch (viewSelected) {
            case 1:
                radarSelector.setBackgroundResource(R.drawable.menu_selected);
                break;
            case 2:
                preferenceSelector.setBackgroundResource(R.drawable.menu_selected);
                break;
            case 3:
                customSearchSelector.setBackgroundResource(R.drawable.menu_selected);
                break;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //I need to add a variable that indicates which fragment is currently loaded
        //so that it can be reloaded as if the button was pressed again.

        outState.putString("View", Integer.toString(viewSelected));
        Log.i("onSaveInstanceState", "selected view: " + Integer.toString(viewSelected));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        int savedView = Integer.valueOf(savedInstanceState.getString("View"));
        Log.i("Saved View", Integer.toString(savedView));
        viewSelected = savedView;
        restoreView();
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void restoreView() {
        switch (viewSelected) {
            case(1):
                Bundle radarBundle = new Bundle();
                radarBundle.putString("Requester", EventDbManager.RADAR_SEARCH_REQUEST);
                Fragment fragment = new CustomSearchFragment();
                fragment.setArguments(radarBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityContainer,
                        fragment).commit();
                viewSelected = 1;
                changeSelectedFragment();
                break;
            case(2):
                getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityContainer, new PreferencesFragment()).commit();
                viewSelected = 2;
                changeSelectedFragment();
                break;
            case(3):
                Bundle searchBundle = new Bundle();
                searchBundle.putString("Requester", EventDbManager.CUSTOM_SEARCH_REQUEST);
                Fragment customfragment = new CustomSearchFragment();
                customfragment.setArguments(searchBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityContainer,
                        customfragment).commit();
                viewSelected = 3;
                changeSelectedFragment();

        }
    }
}

