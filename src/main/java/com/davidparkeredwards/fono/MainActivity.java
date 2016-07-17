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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
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


        // Get last location:
        if (mGoogleApiClient == null) {
            Log.i(LOG_TAG, "onCreate: Launching builder");
            mGoogleApiClient = new GoogleApiClient.Builder(this)

                    .addConnectionCallbacks(this)

                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        Log.i(LOG_TAG, "onCreate: " + mGoogleApiClient);
        mLatitudeText = (TextView) findViewById(R.id.locDisplay);

/**        //++++++++++++++++++++++

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        int holder = 0;

        // Will contain the raw JSON response as a string.
        String eventsJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("http://api.eventful.com/rest/events/search?...&keywords=books&location=S");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                holder = 0;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                holder = 0;
            }
            eventsJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            holder = 0;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        holder = 1;
        TextView jsonView = (TextView) findViewById(R.id.jsonView);
        jsonView.setText(eventsJsonStr);
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++
*/

    }

    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(LOG_TAG, "onConnected: Connecting ");
        packageManager = getPackageManager();
        hasPermission = packageManager.checkPermission("android.permission.ACCESS_COARSE_LOCATION","com.davidparkeredwards.fono");
        Log.i(LOG_TAG, "onConnected: " + hasPermission);
        if(hasPermission == packageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.i(LOG_TAG, "onConnected: Permission passed");
            if (mLastLocation != null) {
                locCoordinates = (String.valueOf(mLastLocation.getLatitude()) +"," + String.valueOf(mLastLocation.getLongitude()));
                mLatitudeText.setText(locCoordinates);
               // mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    //Start and Stop methods for mGoogleApiClient
    @Override
    protected void onStart() {
        Log.i(LOG_TAG, "onStart: App started");
            mGoogleApiClient.connect();
            super.onStart();
        }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        Log.i(LOG_TAG, "onStop: Activity Stopped");
        super.onStop();
    }

    public void launchEventfulResults(View view) {
        Intent launchEventfulResults = new Intent(this, EventfulResults.class);
        launchEventfulResults.putExtra("loccoordinates", locCoordinates);
        startActivity(launchEventfulResults);
    }

}

