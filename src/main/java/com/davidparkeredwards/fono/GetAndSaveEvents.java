package com.davidparkeredwards.fono;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.davidparkeredwards.fono.data.EventDbManager;
import com.google.android.gms.drive.events.ProgressEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 8/20/2016.
 */
public class GetAndSaveEvents extends AsyncTask<Void, Void, Void> {

    double pageNumber;
    URL baseJsonUrl;
    String requester;
    String locationRequestSubmitted;
    Context context;
    double totalEvents;
    
    public GetAndSaveEvents(Context context, double totalEvents, double pageNumber, URL baseJsonUrl,
                            String locationRequestSubmitted, String requester) {
        this.context = context;
        this.pageNumber = pageNumber;
        this.baseJsonUrl = baseJsonUrl;
        this.requester = requester;
        this.locationRequestSubmitted = locationRequestSubmitted;
        this.totalEvents = totalEvents;
        
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(requester == EventDbManager.CUSTOM_SEARCH_REQUEST) {

        }
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        if(requester==EventDbManager.CUSTOM_SEARCH_REQUEST) {
            Toast toast = Toast.makeText(FONO.getContext(), "Searching", Toast.LENGTH_SHORT);
            toast.show();
        }
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {


        if(requester == EventDbManager.RADAR_SEARCH_REQUEST) {
            Toast toast = Toast.makeText(context, "Radar loading " + totalEvents + " new events", Toast.LENGTH_SHORT);
            toast.show();
        }
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.i("GetandSave", "doInBackground: Running GetandSaveEvents");


        publishProgress();
        if(requester==EventDbManager.CUSTOM_SEARCH_REQUEST) {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND + Process.THREAD_PRIORITY_MORE_FAVORABLE);
        } else {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_LESS_FAVORABLE);
        }

        List<FonoEvent> eventsList = new ArrayList<>();

        String jsonString = getJsonString();
        try{
            eventsList.addAll(parseJsonString(jsonString));
        } catch(JSONException e) {
            Log.i("GetAndSaveEvents", "doInBackground: JSON Exception");
        }
        publishProgress();
        //Save Events to contentProvider
        EventDbManager dbManager = new EventDbManager(context);
        try {

            dbManager.bulkInsert(eventsList);
        }
        catch (SQLiteException e) {
            Log.i("Event Request", "Unable to get DB to delete and save new events" );
        }
        
        return null;
    }

    public String getJsonString() {
        
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String eventsJsonStr = null;


        try {
            URL url = new URL(baseJsonUrl +
                    "&page_size=100" +
                    "&page_number=" + pageNumber);
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

    public List parseJsonString(String jsonString) throws JSONException {
        List eventsList = new ArrayList();
        JSONObject eventfulString = new JSONObject(jsonString);

        JSONObject eventString = eventfulString.getJSONObject("events");
        JSONArray events = eventString.getJSONArray("event");
        for (int i = 0; i < events.length(); i++) {
            JSONObject jsonEvent = events.getJSONObject(i);

            String name = jsonEvent.getString("title");
            String date = jsonEvent.getString("start_time");

            ////Format date
            SimpleDateFormat inputDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputDate = new SimpleDateFormat("EEE MMM dd yyyy ' - 'hh:mm a");
            try {
                java.util.Date dateDate = inputDate.parse(date);
                date = outputDate.format(dateDate);

            } catch (java.text.ParseException e) {
                Log.i("parseJsonString", "Unable to parse date");
            }

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

            //category_1-3 parse
            if(categoryArray.length()>0) {
                category_1 = categoryArray.getJSONObject(0)
                        .getString("name")
                        .replaceAll("&amp;", "and");
            } else {
                category_1 = "Uncategorized";
            }
            if(categoryArray.length()>1) {
                category_2 = categoryArray.getJSONObject(1)
                        .getString("name")
                        .replaceAll("&amp;", "and");
            }
            if(categoryArray.length()>2) {
                category_3 = categoryArray.getJSONObject(2).
                        getString("name").
                        replaceAll("&amp;", "and");
            }

            String linkToOrigin = jsonEvent.getString("url");
            String locationCoordinates = jsonEvent.getString("latitude") + "," +
                    jsonEvent.getString("longitude");
            String requestCoordinates = locationRequestSubmitted;

            int id = 0;
            String eventRequester = requester;

            FonoEvent newFonoEvent = new FonoEvent(name, date, venueName, address, description,
                    category_1, category_2, category_3, linkToOrigin, id, locationCoordinates, requestCoordinates,
                    eventRequester);

            eventsList.add(newFonoEvent);

        }
        return eventsList;

    }

}
