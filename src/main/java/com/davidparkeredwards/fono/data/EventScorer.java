package com.davidparkeredwards.fono.data;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.location.Location;
import android.os.RemoteException;
import android.util.Log;

import com.davidparkeredwards.fono.FonoEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by User on 8/16/2016.
 */
public class EventScorer {

    String TAG = "EventScorer";


    public void allCategoriesToLog(Context context) {
        ///////Print all categories for reference
        String uniqueCategories;
        String[] categorySelector = {"DISTINCT CATEGORY_1"};
        Cursor catCursor = context.getContentResolver().query(EventsContract.EventEntry.CONTENT_URI, categorySelector, null, null, null);
        while (catCursor.moveToNext()) {
            Log.i(TAG, "scoreEvents: " + catCursor.getString(0));
        }
    }


    public double calculateDistance(String locationCoordinates, String requestCoordinates) {
        /////////Check Distance and add distance score
        float[] results = {0, 0, 0};
        try {
            List<String> locations = Arrays.asList(locationCoordinates.split("\\s*,\\s*"));
            List<String> requests = Arrays.asList(requestCoordinates.split("\\s*,\\s*"));
            double la1 = Double.valueOf(locations.get(0));
            double lo1 = Double.valueOf(locations.get(1));
            double la2 = Double.valueOf(requests.get(0));
            double lo2 = Double.valueOf(requests.get(1));
            Location.distanceBetween(la2, lo2, la1, lo1, results);

        } catch(NumberFormatException e) {
            Log.i(TAG, "calculateDistance: NumberFormatException");

        }

                double miles = results[0] * .000621371;
        return miles;
    }

    public double scoreEvents(Context context, double distance, String category1,
                              String category2, String category3, String description) {


        //Get Category preferences
        SharedPreference sharedPreference = new SharedPreference();
        Set<String> categoriesList = sharedPreference.getCategoriesList(context);
        double score = 0;

        /*Scores = Tiered so that all the preferred categories are at the top, with null
          descriptions at the bottom, then everything else, ranked by distance. This is done
          by using differences of a magnitude for each score element.
          Preference Categories = +1000
          NotNull Description = +100
          Distance = (0-distance/1000) - Few places on earth where this will be more than |10|
        */
        //Get Score for distance
        double milesScore = 0 - (distance/1000);
        score = score + milesScore;

        ////Check for notNull description, add bonus
        if (description.matches("null")) {
            score = score + 0;
        } else {
            score = score + 100;
        }

        ////Check category score and finalize score
        if (categoriesList.contains(category1) ||
                categoriesList.contains(category2) ||
                categoriesList.contains(category3)) {
            score = score + 1000;
        }

        ///////Check score

        return score;

    }

    public List<FonoEvent> getEventsList(Context context) {
        Cursor cursor = context.getContentResolver().query(EventsContract.EventEntry.CONTENT_URI, null, null, null, null);
        List<FonoEvent> eventsList = new ArrayList<>();
        while (cursor.moveToNext()) {

        //////Get Old Data
            String name = cursor.getString(EventDbManager.COL_NAME);
            String date = cursor.getString(EventDbManager.COL_DOWNLOAD_DATE);
            String venueName = cursor.getString(EventDbManager.COL_VENUE_NAME);
            String address = cursor.getString(EventDbManager.COL_ADDRESS);
            String description = cursor.getString(EventDbManager.COL_DESCRIPTION);
            String category_1 = cursor.getString(EventDbManager.COL_CATEGORY_1);
            String category_2 = cursor.getString(EventDbManager.COL_CATEGORY_2);
            String category_3 = cursor.getString(EventDbManager.COL_CATEGORY_3);
            String linkToOrigin = cursor.getString(EventDbManager.COL_LINK_TO_ORIGIN);
            int id = cursor.getInt(EventDbManager.COL_ID);
            String locationCoordinates = cursor.getString(EventDbManager.COL_LOCATION_COORDINATES);
            String requestCoordinates = cursor.getString(EventDbManager.COL_REQUEST_COORDINATES);
            double distance = cursor.getDouble(EventDbManager.COL_DISTANCE);
            double eventScore = cursor.getDouble(EventDbManager.COL_EVENT_SCORE);
        /////Calculate New Data
            distance = calculateDistance(locationCoordinates, requestCoordinates);
            eventScore = scoreEvents(context, distance, category_1, category_2,
                    category_3, description);

            String eventRequester = cursor.getString(EventDbManager.COL_REQUESTER);
        /////Save to List
            FonoEvent newFonoEvent = new FonoEvent(name, date, venueName, address, description,
                    category_1, category_2, category_3, linkToOrigin, id, locationCoordinates, requestCoordinates, eventRequester);
            eventsList.add(newFonoEvent);
        }

        return eventsList;
    }

    /* Approach to scoring:
     *
     */

    public void bulkReScore(Context context, String requester) {

        Log.i(TAG, "scoreEvents: Scoring Events");
        //// Pull all events
        List<FonoEvent> eventsList = getEventsList(context);
        ////ReInsert Events
        EventDbManager eventDbManager = new EventDbManager(context);
        eventDbManager.deleteAndInsertEvents(requester, eventsList);

    }
}

