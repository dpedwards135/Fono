package com.davidparkeredwards.fono;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    TextView mLatitudeText;
    TextView mLongitudeText;
    int hasPermission;
    PackageManager packageManager;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    String locCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //EventRequest newEventRequest = new EventRequest(this);
        //newEventRequest.execute();
        FonoSyncAdapter fonoSyncAdapter = new FonoSyncAdapter(this, true);
        fonoSyncAdapter.initializeSyncAdapter(this);
        fonoSyncAdapter.syncImmediately(this);


    }


    public void launchEventfulResults(View view) {
        Intent launchEventfulResults = new Intent(this, EventfulResults.class);
        startActivity(launchEventfulResults);
    }

}

