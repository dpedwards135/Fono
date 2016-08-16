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

    private static final String[] EVENTS_COLUMNS = {
            EventsContract.EventEntry.TABLE_NAME + "." + EventsContract.EventEntry._ID,
            EventsContract.EventEntry.COLUMN_NAME,
            EventsContract.EventEntry.COLUMN_DESCRIPTION,
            EventsContract.EventEntry.COLUMN_REQUEST_COORDINATES,
            EventsContract.EventEntry.COLUMN_LOCATION_COORDINATES,
            EventsContract.EventEntry.COLUMN_VENUE_NAME,
            EventsContract.EventEntry.COLUMN_ADDRESS,
            EventsContract.EventEntry.COLUMN_CATEGORY_1,
            EventsContract.EventEntry.COLUMN_CATEGORY_2,
            EventsContract.EventEntry.COLUMN_CATEGORY_3,
            EventsContract.EventEntry.COLUMN_LINK_TO_ORIGIN,
            EventsContract.EventEntry.COLUMN_DOWNLOAD_DATE,
            EventsContract.EventEntry.COLUMN_EVENT_SCORE,
            EventsContract.EventEntry.COLUMN_DISTANCE,
    };

    static final int COL_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_DESCRIPTION = 2;
    static final int COL_REQUEST_COORDINATES = 3;
    static final int COL_LOCATION_COORDINATES = 4;
    static final int COL_VENUE_NAME = 5;
    static final int COL_ADDRESS = 6;
    static final int COL_CATEGORY_1 = 7;
    static final int COL_CATEGORY_2 = 8;
    static final int COL_CATEGORY_3 = 9;
    static final int COL_LINK_TO_ORIGIN = 10;
    static final int COL_DOWNLOAD_DATE = 11;
    static final int COL_EVENT_SCORE = 12;
    static final int COL_DISTANCE = 13;

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
        List<String> locations = Arrays.asList(locationCoordinates.split("\\s*,\\s*"));
        List<String> requests = Arrays.asList(requestCoordinates.split("\\s*,\\s*"));
        double la1 = Double.valueOf(locations.get(0));
        double lo1 = Double.valueOf(locations.get(1));
        double la2 = Double.valueOf(requests.get(0));
        double lo2 = Double.valueOf(requests.get(1));
        float[] results = {0, 0, 0};
        Location.distanceBetween(la2, lo2, la1, lo1, results);

        double miles = results[0] * .000621371;
        return miles;
    }

    public double scoreEvents(Context context, double distance, String category1,
                              String category2, String category3, String description) {

        Log.i(TAG, "scoreEvents: Scoring Events");

        //Get Category preferences
        SharedPreference sharedPreference = new SharedPreference();
        Set<String> categoriesList = sharedPreference.getCategoriesList(context);
        double score = 0;

        //Get Score for distance; Change the 30 for radius number later
        double milesScore = 30 - distance;
        score = score + milesScore;
        Log.i(TAG, "Check Cat List: " + categoriesList.toString());

        ////Check for notNull description, add bonus
        if (description.matches("null")) {
            score = score + 0;
        } else {
            score = score + 10;
        }

        ////Check category score and finalize score
        if (categoriesList.contains(category1) ||
                categoriesList.contains(category2) ||
                categoriesList.contains(category3)) {
            score = score + 10;
        }

        ///////Check score
        Log.i(TAG, "Event Score: " + score);

        return score;

    }

    public List<FonoEvent> getEventsList(Context context) {
        Cursor cursor = context.getContentResolver().query(EventsContract.EventEntry.CONTENT_URI, null, null, null, null);
        List<FonoEvent> eventsList = new ArrayList<>();

        Log.i(TAG, "scoreEvents: " + cursor.getCount());
        while (cursor.moveToNext()) {

        //////Get Old Data
            String name = cursor.getString(COL_NAME);
            String date = cursor.getString(COL_DOWNLOAD_DATE);
            String venueName = cursor.getString(COL_VENUE_NAME);
            String address = cursor.getString(COL_ADDRESS);
            String description = cursor.getString(COL_DESCRIPTION);
            String category_1 = cursor.getString(COL_CATEGORY_1);
            String category_2 = cursor.getString(COL_CATEGORY_2);
            String category_3 = cursor.getString(COL_CATEGORY_3);
            String linkToOrigin = cursor.getString(COL_LINK_TO_ORIGIN);
            int id = cursor.getInt(COL_ID);
            String locationCoordinates = cursor.getString(COL_LOCATION_COORDINATES);
            String requestCoordinates = cursor.getString(COL_REQUEST_COORDINATES);
            double distance = cursor.getDouble(COL_DISTANCE);
            double eventScore = cursor.getDouble(COL_EVENT_SCORE);
        /////Calculate New Data
            distance = calculateDistance(locationCoordinates, requestCoordinates);
            eventScore = scoreEvents(context, distance, category_1, category_2,
                    category_3, description);
            Log.i(TAG, "New Event Score - Name: " + name + "; Score: " + eventScore);

        /////Save to List
            FonoEvent newFonoEvent = new FonoEvent(name, date, venueName, address, description,
                    category_1, category_2, category_3, linkToOrigin, id, locationCoordinates, requestCoordinates,
                    distance, eventScore);
            eventsList.add(newFonoEvent);
        }

        return eventsList;
    }


    public void bulkReScore(Context context) {

        Log.i(TAG, "scoreEvents: Scoring Events");
        //// Pull all events
        List<FonoEvent> eventsList = getEventsList(context);
        ////ReInsert Events
        EventDbManager eventDbManager = new EventDbManager(context);
        eventDbManager.createDbTable(eventsList);

    }
}

