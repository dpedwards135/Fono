package com.davidparkeredwards.fono;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.davidparkeredwards.fono.data.EventDbManager;
import com.davidparkeredwards.fono.data.EventScorer;
import com.davidparkeredwards.fono.data.SharedPreference;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by User on 8/20/2016.
 */
public class EventRequest extends AsyncTask<Void, Void, Void>{

    //Variables needed to run Eventful API request:
    Context context;
    String location; //Coordinates or city name - If this is blank, automatically supply current location
    String keywords; //Blank is okay
    String date; // If this is blank, automatically supply today's date
    String requester; //Required

    //Variables generated and used within class
    String todayDate;
    String locationRequestSubmitted;
    URL baseJsonUrl;
    double totalItems;
    boolean internetConnected;

    //JSON Request String Elements


    public EventRequest(Context context, String location, String keywords, String date, String requester) {
        this.context = context;
        this.location = location;
        this.keywords = keywords;
        this.date = date;
        this.requester = requester;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(internetConnected == false) {
            Toast toast = Toast.makeText(context, "Unable to get events: No internet connection", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (totalItems == 0 && requester == EventDbManager.CUSTOM_SEARCH_REQUEST) {
            Toast toast = Toast.makeText(context, "No Events Found", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        for(int i = 1; i <= Math.ceil(totalItems/100); i++) {
            GetAndSaveEvents getAndSaveEvents = new GetAndSaveEvents(context, totalItems, i, baseJsonUrl, locationRequestSubmitted, requester);
            getAndSaveEvents.executeOnExecutor(THREAD_POOL_EXECUTOR);
        }

        if (requester == EventDbManager.RADAR_SEARCH_REQUEST) {
            saveSyncDateAndLocation();
        }

        Log.i("OnPostExecute", "Event Request Task Finished");
        return;
    }

    @Override
    protected Void doInBackground(Void... params) {
        /////Check internet connection and cancel if unavailable

        internetConnected = isInternetAvailable();
        if(internetConnected == false) {
            return null;
        }
        /////Give priority to Custom Search Requests
        if(requester==EventDbManager.CUSTOM_SEARCH_REQUEST) {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND + Process.THREAD_PRIORITY_MORE_FAVORABLE);
        } else {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_LESS_FAVORABLE);
        }

        getLocationRequestSubmitted();

        if (requester == EventDbManager.RADAR_SEARCH_REQUEST && !requiresSync()) {
            //End Sync Process if not required
            return null;
        }
        try {
            baseJsonUrl = getBaseJsonUrl();
        } catch (MalformedURLException e) {
            Log.i("doInBackground", "Malformed URL");
        }
        getTotalItemQuantity(baseJsonUrl);
        if(totalItems == 0) {
            return null;
        }
        Log.i("EventRequest", "Total Items = " + totalItems);

        EventDbManager dbManager = new EventDbManager(context);
        dbManager.deleteEventRecords(requester);
        return null;
    }

    protected void getLocationRequestSubmitted() {
        LocationIdentifier locationIdentifier = new LocationIdentifier();
        locationIdentifier.openConnection();
        int ms = 0;
        while (locationRequestSubmitted == null) {
            ms += 1;
        }
    }

    protected boolean requiresSync() {
        //Get Today's Date:
        Date today = new Date();
        todayDate = Integer.toString(today.getMonth()) +
                        Integer.toString(today.getDate()) +
                        Integer.toString(today.getHours());

        //Get last sync location and date
        SharedPreference sharedPreference = new SharedPreference();
        String lastSyncLocation = sharedPreference.getLastSyncLocation(context);
        String lastSyncDate = sharedPreference.getLastSyncDate(context);
        EventScorer eventScorer = new EventScorer();
        double milesBetweenSyncs = eventScorer.calculateDistance(lastSyncLocation, locationRequestSubmitted);

        if(lastSyncDate.equals(todayDate) && milesBetweenSyncs < 3) {
            Log.i("Sync?", "Does not need Sync. Now: " + todayDate + " LastSync: " + lastSyncDate +
                    "\nHere: " + locationRequestSubmitted + " LastSyncLocation: " + lastSyncLocation
                    +"\nMilesBetweenSyncs: " + milesBetweenSyncs
            );
            return false;
        } else
            Log.i("Sync?", "Needs Sync. Now: " + todayDate + " LastSync: " + lastSyncDate +
                    "\nHere: " + locationRequestSubmitted + " LastSyncLocation: " + lastSyncLocation
                    +"\nMilesBetweenSyncs: " + milesBetweenSyncs
            );
            return true;
        }

    protected URL getBaseJsonUrl() throws MalformedURLException{


        String variableString = "Variable String Broken";
        String radiusString = "&within=25"; //Radius in which to search for events, presently hard-coded
        String locationString = "";
        String keywordString = "";
        String dateString = "";
        //Format location and keywords to replace spaces with +
        String locationFormatted = location.replaceAll(" ", "+");
        String keywordsFormatted = keywords.replaceAll(" ", "+");

        switch (requester) {
            case EventDbManager.RADAR_SEARCH_REQUEST :
                locationString = "&where=" + locationRequestSubmitted;
                keywordString = "";
                dateString = "&date=Today";
                variableString = locationString + radiusString + dateString + keywordString;
                break;
            case EventDbManager.CUSTOM_SEARCH_REQUEST:
                //Assign locationRequestSubmitted to location if none supplied by CustomSearch
                if(location.isEmpty()) {
                    locationString = "&where=" + locationRequestSubmitted;
                } else {
                    locationString = "&location=" + locationFormatted;
                }
                //Assign today's date if none supplied by customSearch
                if(date.isEmpty()) {
                    dateString = "&date=Today";
                } else {
                    dateString = "&date=" + date;
                }
                keywordString = "&keywords=" + keywordsFormatted;
                variableString = locationString + radiusString + dateString + keywordString;
                break;
            default:
                Log.i("Switch", "Went to default");
                break;
        }
        URL baseJsonUrl = new URL("http://api.eventful.com/json/events/search?..." +
                variableString +
                "&app_key=w732ztLVhvrG9DN8" +
                "&include=categories");
        return baseJsonUrl;
    }

    protected void getTotalItemQuantity(URL baseJsonUrl) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String eventsJsonStr = null;

        try {
            URL url = new URL(
                    baseJsonUrl +
                            "&page_size=1");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            eventsJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("Event Request", "Error ", e);
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Event Request", "Error closing stream", e);
                }
            }
        }

        totalItems = 0;
        try {
            JSONObject eventfulString = new JSONObject(eventsJsonStr);
            totalItems = Integer.valueOf(eventfulString.getString("total_items"));
        } catch (JSONException e) {
            Log.i("Get Page Numbers", "JSON Exception");


         return;
        }
        return;
    }

    public void saveSyncDateAndLocation() {
        SharedPreference sharedPreference = new SharedPreference();
        sharedPreference.save(context, locationRequestSubmitted, SharedPreference.PREFS_LOCATION_KEY);
        sharedPreference.save(context, todayDate, SharedPreference.PREFS_SYNC_DATE_KEY);
    }


    protected class LocationIdentifier implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


        GoogleApiClient mGoogleApiClient;
        PackageManager packageManager;
        int hasPermission;
        Location mLastLocation;


        public Void openConnection() {

            packageManager = context.getPackageManager();
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(context)

                        .addConnectionCallbacks(this)

                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            mGoogleApiClient.connect();

            return null;
        }

        @Override
        public void onConnectionSuspended(int i) {

        }


        @Override
        public void onConnected(Bundle connectionHint) {
            String newCenterLocation = "";
            Log.i("onConnected", "onConnected: Connecting ");

            hasPermission = packageManager.checkPermission("android.permission.ACCESS_FINE_LOCATION", "com.davidparkeredwards.fono");
            Log.i("onConnected", "onConnected: " + hasPermission);
            if (hasPermission == packageManager.PERMISSION_GRANTED) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                Log.i("onConnected", "onConnected: Permission passed");
                if (mLastLocation != null) {
                    Log.i("Last Location", String.valueOf(mLastLocation.getLatitude()) + "," + String.valueOf(mLastLocation.getLongitude()));
                    newCenterLocation = String.valueOf(mLastLocation.getLatitude()) + "," + String.valueOf(mLastLocation.getLongitude());
                    locationRequestSubmitted = newCenterLocation;
                }
                Log.i("Location Info", "Current location: " + newCenterLocation);

                mGoogleApiClient.disconnect();
            }

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }

}
