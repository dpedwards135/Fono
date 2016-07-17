package com.davidparkeredwards.fono;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class EventfulResults extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("OnCreateEventfulResults", "Created");
        setContentView(R.layout.activity_eventful_results);
        String locCoordinates = getIntent().getStringExtra("loccoordinates");
        AsyncTask<String, Void, String> setDisplay = new FetchEventsTask().execute(locCoordinates);

        Log.i("End", "onCreate: " + setDisplay);
    }
    public class FetchEventsTask extends AsyncTask<String,Void,String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView change = (TextView) findViewById(R.id.change);
            change.setText(s);
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
                URL url = new URL("http://api.eventful.com/rest/events/search?...&where=" + loccoordinates + "&within=25&date=Future&app_key=w732ztLVhvrG9DN8");

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


