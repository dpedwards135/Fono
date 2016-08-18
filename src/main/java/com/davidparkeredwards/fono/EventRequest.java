package com.davidparkeredwards.fono;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.davidparkeredwards.fono.data.EventDbHelper;
import com.davidparkeredwards.fono.data.EventDbManager;
import com.davidparkeredwards.fono.data.EventScorer;
import com.davidparkeredwards.fono.data.SharedPreference;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.query.internal.LogicalFilter;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.prefs.PreferenceChangeListener;

/**
 * I need to change this class so that all it does is update the database
 * and then do all the event loading on the pertinent activity/fragment.
 */
public class EventRequest extends AsyncTask<Void, Void, Void> {

    //1. Get Center Location, 2. Pull events, 3. Update content provider
    private Context context;
    String date;
    String keywords = "";
    String coordinates;
    String requester;
    String customLocation = "";
    String todayDate;
    String oldCoordinates;
    String oldDate;

    public EventRequest(Context context, String date, String keywords, String requester, String customLocation) {
        this.context = context;
        this.date = date;
        this.keywords = keywords;
        this.requester = requester;
        this.customLocation = customLocation;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //EventDbManager eventDbManager = new EventDbManager(context);
        //eventDbManager.scoreEvents();

        Log.i("Event Request", "Event Request Background thread complete");

    }



    @Override
    protected Void doInBackground(Void... params) {
        //Get location and date variables
        getArguments();
        //Check if new Events Request necessary, end if not needed
        if (!checkSyncNeed()) {
            return null;
        }
        //If sync is needed:
        // Create JSON String, query API, parse FonoEvents, save to list
        List<FonoEvent> eventsList = new ArrayList<>();

        try {
            double totalPages = 1;
            double parsedPages = 0;

            while (parsedPages < totalPages) {
                String jsonString = getJsonString(coordinates, parsedPages + 1);
                Log.i("JSON Parse Loop", "jsonString readout: " + jsonString);
                totalPages = getTotalPages(jsonString);
                eventsList.addAll(parseJsonString(jsonString));
                parsedPages += 1;
                Log.i("JSON Parse Loop", "Cycle: " + parsedPages + ", " + totalPages);
                Log.i("JSON Parse Loop", "Events in eventsList: " + eventsList.size());
            }
        } catch (JSONException e) {
                Log.i("JSONString to List", "Unable to parse JSON string");
                return null;

        }

        //Save Events to contentProvider
        EventDbManager dbManager = new EventDbManager(context);
        //dbManager.deleteAndInsertEvents(requester, eventsList);
        try {
            dbManager.deleteEventRecords(requester);
            dbManager.bulkInsert(eventsList);

        }
        catch (SQLiteException e) {
            Log.i("Event Request", "Unable to get DB to delete and save new events" );
        }

            //if Radar Request and did not return empty, save the sync date to avoid unnecessary syncs
        if(requester.equals(EventDbManager.RADAR_SEARCH_REQUEST) || eventsList.size() > 0) {

            SharedPreference sharedPreference = new SharedPreference();
            sharedPreference.save(context, coordinates, SharedPreference.PREFS_LOCATION_KEY);
            sharedPreference.save(context, date, SharedPreference.PREFS_SYNC_DATE_KEY);
        }


        return null;

    }


    public boolean checkSyncNeed() {
        //Check if sync is needed: Has date or location changed since last sync?
        switch (requester) {
            //Run sync for every Custom Search
            case EventDbManager.CUSTOM_SEARCH_REQUEST:
                Log.i("Check Sync Need", requester + " sync needed");
                return true;
            //Only run sync if something has changed for Radar Search Sync
            case EventDbManager.RADAR_SEARCH_REQUEST:
                if (date.equals(oldDate) && coordinates.equals(oldCoordinates)) {
                    Log.i("Check Sync Need",
                            "No Sync - Requester: " + requester + ", " +
                                    oldDate + date + ", " +
                                    oldCoordinates + coordinates);
                    return false;
                } else {
                    Log.i("Check Sync Need",
                            "Perform Sync - Requester: " + requester + ", " +
                                    oldDate + date + ", " +
                                    oldCoordinates + coordinates);
                    return true;
                }

        }
        Log.i("Check Sync Need", "Reached end of Sync need without case reached - Perform Sync");
        return true;
    }

    public void getArguments() {

        //Pulls remaining arguments: dates and request locations
        SharedPreference sharedPreference = new SharedPreference();
        Log.i("SPCheck", "doInBackground: " + sharedPreference.getValue(context, SharedPreference.PREFS_LOCATION_KEY));
        oldCoordinates = sharedPreference.getValue(context, SharedPreference.PREFS_LOCATION_KEY);
        oldDate = sharedPreference.getValue(context, SharedPreference.PREFS_SYNC_DATE_KEY);
        Date today = new Date();
        todayDate =
                Integer.toString(today.getMonth()) +
                        Integer.toString(today.getDate()) +
                        Integer.toString(today.getHours());
        Log.i("Change Preferences", "doInBackground: " + oldCoordinates + ", " + date + ", " + oldDate);
        LocationIdentifier locationIdentifier = new LocationIdentifier();
        locationIdentifier.openConnection();
        int ms = 0;
        while (coordinates == null) {
            ms += 1;
        }
        Log.i("Do In Background", "MS to return location: " + ms);
        Log.i("Background", "doInBackground: " + coordinates);
    }

    public String getRequestVariables() {

        String radiusString = "&within=25"; //Radius in which to search for events, presently hard-coded
        String locationString = "";
        String keywordString = "";
        String dateString = "";
        //Format customLocation to replace spaces with +
        String customLocationFormatted = customLocation.replaceAll(" ", "+");
        String keywordsFormatted = keywords.replaceAll(" ", "+");
        Log.i("Format Location", "newCustomLocation: " + customLocationFormatted);
        switch (requester) {
                case EventDbManager.RADAR_SEARCH_REQUEST :
                    locationString = "&where=" + coordinates;
                    keywordString = "";
                    dateString = "&date=Today";
                    break;
                case EventDbManager.CUSTOM_SEARCH_REQUEST:
                    if(customLocation == "") {
                        locationString = "&where=" + coordinates;
                    } else {
                        locationString = "&location=" + customLocationFormatted;
                    }
                    if(date == "") {
                        dateString = "&date=Today";
                    } else {
                        dateString = "&date=" + date;
                    }
                    keywordString = "&keywords=" + "music"; //+keywordsFormatted

        }
        return locationString + radiusString + dateString + keywordString;
    }

    public double getTotalPages(String jsonString)
        throws JSONException {

        double totalPages;
        JSONObject eventfulString = new JSONObject(jsonString);
        double totalItems = Integer.valueOf(eventfulString.getString("total_items"));
        double pageSize = Integer.valueOf(eventfulString.getString("page_size"));
        totalPages = Math.ceil(totalItems/pageSize);
        Log.i("Check getTotal Pages", totalItems + ", " + pageSize);


            return totalPages;
    }


    public String getJsonString(String coordinates, double pageNumber) {

        String requestVariables = getRequestVariables();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String eventsJsonStr = null;
        String pageNumberString = Double.toString(pageNumber);

        try {
            URL url = new URL("http://api.eventful.com/json/events/search?..." +
                    requestVariables +
                    "&app_key=w732ztLVhvrG9DN8" +
                    "&include=categories" +
                    "&page_size=100" +
                    "&page_number=" + pageNumberString);
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
            Log.e("Event Request", "Error ", e);
            return null;
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

        return eventsJsonStr;
    }

        //////This method parses JSON string and adds distance and event scores to create
        //////final eventsList to save
        public List parseJsonString(String jsonString) throws JSONException {
            List eventsList = new ArrayList();
            JSONObject eventfulString = new JSONObject(jsonString);

            JSONObject eventString = eventfulString.getJSONObject("events");
            JSONArray events = eventString.getJSONArray("event");
            for (int i = 0; i < events.length(); i++) {
                JSONObject jsonEvent = events.getJSONObject(i);

                String name = jsonEvent.getString("title");
                String date = jsonEvent.getString("start_time");
                String venueName = jsonEvent.getString("venue_name");
                String address = jsonEvent.getString("venue_address") + ", " + jsonEvent.getString("city_name") + ", " + jsonEvent.getString("region_name");
                String description = jsonEvent.getString("description");
                String category_1 = "none";
                String category_2 = "none";
                String category_3 = "none";
                double distance = 0;

                ///Parse Category - currently only allows for max of 3 categories per event
                JSONObject categoriesObject = jsonEvent.getJSONObject("categories");
                JSONArray categoryArray = categoriesObject.getJSONArray("category");

                //Log.i("parseJSON", "JSON Array: " + categoryArray.toString());
                //category_1-3 parse
                if(categoryArray.length()>0) {
                    category_1 = categoryArray.getJSONObject(0)
                            .getString("name")
                            .replaceAll("&amp;", "and");
                //    Log.i("parseJSON", "Category 1: " + category_1);
                } else {
                    category_1 = "Uncategorized";
                }
                if(categoryArray.length()>1) {
                    category_2 = categoryArray.getJSONObject(1)
                            .getString("name")
                            .replaceAll("&amp;", "and");
                  //  Log.i("parseJSON", "Category 2: " + category_2);
                }
                if(categoryArray.length()>2) {
                    category_3 = categoryArray.getJSONObject(2).
                            getString("name").
                            replaceAll("&amp;", "and");
                    //Log.i("parseJSON", "Category 3: " + category_3);
                }

                //Log.i("parseJSON", "Completed Parsing Categories");
                String linkToOrigin = jsonEvent.getString("url");
                String locationCoordinates = jsonEvent.getString("latitude") + "," +
                        jsonEvent.getString("longitude");
                String requestCoordinates = coordinates;
                EventScorer eventScorer = new EventScorer();
                distance = eventScorer.calculateDistance(locationCoordinates, requestCoordinates);
                //Log.i("Calculate Distance", "Distance = " + distance);
                double eventScore = eventScorer.scoreEvents(context, distance, category_1, category_2,
                        category_3, description);
                int id = 0;
                String eventRequester = requester;

                FonoEvent newFonoEvent = new FonoEvent(name, date, venueName, address, description,
                        category_1, category_2, category_3, linkToOrigin, id, locationCoordinates, requestCoordinates,
                        distance, eventScore, eventRequester);

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

