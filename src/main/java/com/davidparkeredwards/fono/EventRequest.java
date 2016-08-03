package com.davidparkeredwards.fono;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.davidparkeredwards.fono.data.EventDbHelper;
import com.davidparkeredwards.fono.data.EventDbManager;

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
 * Created by User on 7/31/2016.
 *
 * Ultimate objective of EventRequest is to return a list of events, so that we can use it
 * for both home (auto) and search. Though with search it has to be saved to DB anyway for persistence,
 * so I guess it can go both ways, but let's create a separate DB creation/update anyway.
 */
public class EventRequest extends AsyncTask<String, Void, String> {

    String centerLocation;

    ArrayAdapter<Object> eventsListAdapter;
    private Context context;
    ListView listView;


    public EventRequest(Context context, String loccoordinates, ListView listView) {
        this.centerLocation = loccoordinates;
        this.context = context;
        this.listView = listView;
    }
    //Steps: 1. get a JSON String from API; 2. transform JSON String into List

    @Override
    protected void onPostExecute(String jsonString) {
        super.onPostExecute(jsonString);
        try {
            List<FonoEvent> eventsList = parseJsonString(jsonString);
            EventDbManager dbManager = new EventDbManager(context);
            dbManager.createDbTable(eventsList, centerLocation);
            //Steps: create DB if not created, this should just happen once.
            //     : delete all the records from previous query
            //     : save all records from current query
            //     : access records to populate fields

            updateListView(eventsList);

        } catch(JSONException e) {
            Log.i("OnPostExecute", "onPostExecute: Unable to parse JSON string");
        }

    }



    @Override
    protected String doInBackground(String... params) {
        Log.i("Event Request", "doInBackground: Starting Do In Background");
        String coordinates = params[0];
        Log.i("Event Request", "doInBackground: Coordinates" + coordinates);
        String jsonString = getJsonString(coordinates);
        Log.i("Event Request", "doInBackground: jsonString = " + jsonString);
        return jsonString;
    }

    public String getJsonString(String coordinates) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        // Will contain the raw JSON response as a string.
        String eventsJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("http://api.eventful.com/json/events/search?...&where="
                    + coordinates + "&within=25&date=Today&app_key=w732ztLVhvrG9DN8&include=categories"
                    + "&page_size=1000");
            Log.i("URL", "doInBackground: " + url);
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
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
                return null;
            }
            eventsJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
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
        Log.i("Background", "doInBackground: " + eventsJsonStr);
        return eventsJsonStr;
    }


        public List parseJsonString(String jsonString) throws JSONException {
            List eventsList = new ArrayList();
            JSONObject eventfulString = new JSONObject(jsonString);

            JSONObject eventString = eventfulString.getJSONObject("events");
            JSONArray events = eventString.getJSONArray("event");
            JSONObject checkEvent = events.getJSONObject(0);
            Log.i("Check Parsed Object", "parseJson: " + checkEvent.toString());
            for (int i = 0; i < events.length(); i++) {
                JSONObject jsonEvent = events.getJSONObject(i);

                String name = jsonEvent.getString("title");
                String date = jsonEvent.getString("start_time");
                String venueName = jsonEvent.getString("venue_name");
                String address = jsonEvent.getString("venue_address") + ", " + jsonEvent.getString("city_name") + ", " + jsonEvent.getString("region_name");
                String description = jsonEvent.getString("description");
                String category = jsonEvent.getString("categories");
                String linkToOrigin = jsonEvent.getString("url");

                FonoEvent newFonoEvent = new FonoEvent(name, date, venueName, address, description, category, linkToOrigin);
                Log.i("New FonoEvent", "parseJson: " + newFonoEvent.toString());
                eventsList.add(newFonoEvent);

            }
            return eventsList;

        }

    public void updateListView(List<FonoEvent> eventsList) {

        List summaryList = new ArrayList();
        for(FonoEvent event : eventsList) {
            String detailName = event.name;
            String detailVenueName = event.venueName;
            summaryList.add(detailName + "\n" + detailVenueName);
        }

        eventsListAdapter = new ArrayAdapter<Object>(
                context, //getActivity() if in fragment
                R.layout.list_item_events,
                R.id.list_item_events_textview,
                summaryList);

        //ListView listView = (ListView) findViewById(R.id.main_list_view);//Pass proper view in with constructor
        listView.setAdapter(eventsListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(context,EventDetail.class)
                        .putExtra(Intent.EXTRA_TEXT, "ID of event to display");
                context.startActivity(intent);

            }
        });
    }
}

