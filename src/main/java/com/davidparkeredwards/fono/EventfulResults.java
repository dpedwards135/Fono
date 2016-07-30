package com.davidparkeredwards.fono;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class EventfulResults extends AppCompatActivity {
    String data;
    List<FonoEvent> eventsList = new ArrayList<FonoEvent>();
    ArrayAdapter<Object> eventsListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("OnCreateEventfulResults", "Created");
        setContentView(R.layout.activity_eventful_results);
        String locCoordinates = getIntent().getStringExtra("loccoordinates");
        AsyncTask<String, Void, String> setDisplay = new FetchEventsTask().execute(locCoordinates);

        Log.i("End", "onCreate: " + setDisplay);
    }

    public void parseJson(String jsonString) throws JSONException {
        JSONObject eventfulString = new JSONObject(jsonString);

        JSONObject eventString = eventfulString.getJSONObject("events");
        JSONArray events = eventString.getJSONArray("event");
        JSONObject checkEvent = events.getJSONObject(0);
        Log.i("Check Parsed Object", "parseJson: " + checkEvent.toString());
        // Next Steps: Now that I have an array of JSON objects, parse those objects into JAVA objects that I can save to a DB
        // Decide what the event object should look like then figure out how to parse it into that form.
        // In the end put it into an ArrayList of Java objects
        // First: Come up with exact specs on how this will work.
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
        Log.i("Events List", "parseJson: " + eventsList.toString());
        TextView change = (TextView) findViewById(R.id.change);

        //Here is the list of String for the ListView, build out of desired elements in eventsList
        List summaryList = new ArrayList();
        for(FonoEvent event : eventsList) {
            String detailName = event.name;
            String detailVenueName = event.venueName;
            summaryList.add(detailName + "\n" + detailVenueName);
        }

        //Next - persist to database, create detail view with all of the pertinent info, create settings for category preferences, sort
        //events by preference, create search, layout
        //Preferences - perform searches by category first, then all, filter out any events duplicates
        //Question - Use service to check location and run new search every time, or just do it as needed? If service, what triggers it to run?
        //In any case I will need to persist the info so that it doesn't always rerun, so do that next.

        eventsListAdapter = new ArrayAdapter<Object>(
                this, //getActivity() if in fragment
                R.layout.list_item_events,
                R.id.list_item_events_textview,
                summaryList);

        ListView listView = (ListView) findViewById(R.id.main_list_view);
        listView.setAdapter(eventsListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(getApplicationContext(),EventDetail.class)
                        .putExtra(Intent.EXTRA_TEXT, "ID of event to display");
                startActivity(intent);

            }
        });
    }

    public class FetchEventsTask extends AsyncTask<String,Void,String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView change = (TextView) findViewById(R.id.change);
            try {
                parseJson(s);
            } catch (JSONException e) {
                Log.i("OPE", "onPostExecute: Failed to parse");

            }
            //change.setText(s);
            //data = s;

        }

        @Override
        protected String doInBackground(String... args) {
            String loccoordinates = args[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Log.i("Background", "doInBackground: " + loccoordinates);

            // Will contain the raw JSON response as a string.
            String eventsJsonStr = null;

            Log.i("Background", "doInBackground: Started");



            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.eventful.com/json/events/search?...&where="
                        + loccoordinates + "&within=25&date=Today&app_key=w732ztLVhvrG9DN8&include=categories"
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

    }
}


