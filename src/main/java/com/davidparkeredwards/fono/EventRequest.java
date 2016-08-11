package com.davidparkeredwards.fono;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.davidparkeredwards.fono.data.EventDbHelper;
import com.davidparkeredwards.fono.data.EventDbManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * I need to change this class so that all it does is update the database
 * and then do all the event loading on the pertinent activity/fragment.
 */
public class EventRequest extends AsyncTask<Void, Void, Void> {



    //1. Get Center Location, 2. Pull events, 3. Update content provider

    private Context context;
    String coordinates;



    public EventRequest(Context context) {
        this.context = context;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
       // try {
            Log.i("Check JSON String", "onPostExecute: ");

    }



    @Override
    protected Void doInBackground(Void... params) {

        LocationIdentifier locationIdentifier = new LocationIdentifier();
        locationIdentifier.openConnection();
        int ms = 0;
        while (coordinates == null) {
            ms += 1;
        }
        Log.i("Do In Background", "MS to return location: " + ms);
        Log.i("Background", "doInBackground: " + coordinates);
        String jsonString = getJsonString(coordinates);
        Log.i("Check JSON", "doInBackground: " + jsonString);
        try {
            List<FonoEvent> eventsList = parseJsonString(jsonString);
            EventDbManager dbManager = new EventDbManager(context);
            dbManager.createDbTable(eventsList, coordinates);


        } catch(JSONException e) {
            Log.i("OnPostExecute", "onPostExecute: Unable to parse JSON string");
        }

        return null;
    }

    public String getJsonString(String coordinates) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String eventsJsonStr = null;

        try {
            URL url = new URL("http://api.eventful.com/json/events/search?...&where="
                    + coordinates + "&within=25&date=Today&app_key=w732ztLVhvrG9DN8&include=categories"
                    + "&page_size=100");
            Log.i("URL", "doInBackground: " + url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            eventsJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            return null;
        } finally {
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
        //Log.i("Background", "doInBackground: " + eventsJsonStr);
        return eventsJsonStr;
    }


        public List parseJsonString(String jsonString) throws JSONException {
            List eventsList = new ArrayList();
            JSONObject eventfulString = new JSONObject(jsonString);

            JSONObject eventString = eventfulString.getJSONObject("events");
            JSONArray events = eventString.getJSONArray("event");
            JSONObject checkEvent = events.getJSONObject(0);
            for (int i = 0; i < events.length(); i++) {
                JSONObject jsonEvent = events.getJSONObject(i);

                String name = jsonEvent.getString("title");
                String date = jsonEvent.getString("start_time");
                String venueName = jsonEvent.getString("venue_name");
                String address = jsonEvent.getString("venue_address") + ", " + jsonEvent.getString("city_name") + ", " + jsonEvent.getString("region_name");
                String description = jsonEvent.getString("description");
                String category = "";

                ///Parse Category
                JSONObject categoriesObject = jsonEvent.getJSONObject("categories");
                JSONArray categoryArray = categoriesObject.getJSONArray("category");
                for (int l = 0; l<categoryArray.length(); l++) {
                    JSONObject categoryObject = categoryArray.getJSONObject(l);
               category = category + categoryObject.getString("name");
                }

                String linkToOrigin = jsonEvent.getString("url");
                int id = 0;

                FonoEvent newFonoEvent = new FonoEvent(name, date, venueName, address, description, category, linkToOrigin, id);
                eventsList.add(newFonoEvent);

            }
            return eventsList;

        }

             protected class LocationIdentifier implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


                 GoogleApiClient mGoogleApiClient;
                 PackageManager packageManager;
                 int hasPermission;
                 Location mLastLocation;


                 public Void openConnection() {

                     packageManager = context.getPackageManager();
                     if (mGoogleApiClient == null) {
                         Log.i("Get centerLocation", "onCreate: Launching builder");
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
                             coordinates = newCenterLocation;
                         }
                         Log.i("Location Info", "Current location: " + newCenterLocation);

                         mGoogleApiClient.disconnect();
                     }

                 }

                 @Override
                 public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                 }

             }

}

