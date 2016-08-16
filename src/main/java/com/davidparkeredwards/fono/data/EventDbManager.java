package com.davidparkeredwards.fono.data;

import android.app.usage.UsageEvents;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.davidparkeredwards.fono.EventfulResults;
import com.davidparkeredwards.fono.FonoEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Created by User on 7/31/2016.
 */
public class EventDbManager {

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

    private Context context;
    String TAG = "EventDbManager";

    public EventDbManager(Context context) {
        this.context = context;
    }

    public void createDbTable(List eventsList, String centerLocation){

        try {
            deleteEventRecords();
            bulkInsert(eventsList, centerLocation);

        }
        catch (SQLiteException e) {
            Log.i(TAG, "createDbTable: Unable to get DB");
        }
    }

    public void scoreEvents() {

        Log.i(TAG, "scoreEvents: Scoring Events");
        Cursor cursor = context.getContentResolver().query(EventsContract.EventEntry.CONTENT_URI,null, null, null, null);


        Log.i(TAG, "scoreEvents: " + cursor.getCount());

        while (cursor.moveToNext()) {
            String name = cursor.getString(COL_NAME);
            String description = cursor.getString(COL_DESCRIPTION);
            String category_1 = cursor.getString(COL_CATEGORY_1);
            String category_2 = cursor.getString(COL_CATEGORY_2);
            String category_3 = cursor.getString(COL_CATEGORY_3);
            double oldScore = cursor.getDouble(COL_EVENT_SCORE);
            String location_coordinates = cursor.getString(COL_LOCATION_COORDINATES);
            String request_coordinates = cursor.getString(COL_REQUEST_COORDINATES);
            Long _id = cursor.getLong(COL_ID);
                        /*
                                    Log.i("Detail Check", "onLoadFinished: Name " + name + "\n Description " +
                                            description + "\nC1 " +
                                            category_1 + "\nC2 " +
                                            category_2 + "\nC3 " +
                                            category_3 + "\noldScore " +
                                            oldScore + "\nID " +
                                            _id + "\nLocationC " +
                                            location_coordinates + "\nRequestC " +
                                            request_coordinates + "\n"

                                    );     */
            //Get Category preferences
            SharedPreference sharedPreference = new SharedPreference();
            //Set<String> categoryInsert = new HashSet<String>();
            //categoryInsert.add("Outdoors and Recreation");
            //sharedPreference.saveCategories(context, categoryInsert);
            Set<String> categoriesList = sharedPreference.getCategoriesList(context);
            double score = 0;

            /////////Check Distance and add distance score
            List<String> locations = Arrays.asList(location_coordinates.split("\\s*,\\s*"));
            List<String> requests = Arrays.asList(request_coordinates.split("\\s*,\\s*"));
            double la1 = Double.valueOf(locations.get(0));
            double lo1 = Double.valueOf(locations.get(1));
            double la2 = Double.valueOf(requests.get(0));
            double lo2 = Double.valueOf(requests.get(1));
            float[] results = {0,0,0};
            Location.distanceBetween(la2,lo2,la1,lo1,results);

            double miles = results[0]*.000621371;

            //Change the 30 for radius number later
            double milesScore = 30-miles;
            score = score + milesScore;
            Log.i(TAG, "Check Cat List: " + categoriesList.toString());

            ////Check for notNull description, add bonus
            if(description.matches("null")) {
                score = score + 0;
            } else {
                score = score + 10;
            }

            ////Check category score and finalize score
            if(categoriesList.contains(category_1) ||
               categoriesList.contains(category_2) ||
                categoriesList.contains(category_3)) {
                score = score + 10;
            }

            ///////Check score
            Log.i(TAG, "Event Score: " + score);
            String[] idString = {_id.toString()};

            //////Save score to DB
            ContentValues newScore = new ContentValues();
            newScore.put(EventsContract.EventEntry.COLUMN_EVENT_SCORE, score);
            newScore.put(EventsContract.EventEntry.COLUMN_DISTANCE, miles);
            context.getContentResolver().update(EventsContract.EventEntry.buildEventsUriWithId(_id),
                    newScore, EventsContract.EventEntry._ID + " = ?",idString);

        }

        cursor.close();

        ///////Print all categories for reference
        String uniqueCategories;
        String[] categorySelector = {"DISTINCT CATEGORY_1"};
        Cursor catCursor = context.getContentResolver().query(EventsContract.EventEntry.CONTENT_URI,categorySelector, null,null,null);
        while (catCursor.moveToNext()) {
            Log.i(TAG, "scoreEvents: " + catCursor.getString(0));
        }
    }


    public void deleteEventRecords() {

        //db.execSQL("delete from EVENTS");
        context.getContentResolver().delete(EventsContract.EventEntry.CONTENT_URI, null, null);
        Log.i(TAG, "deleteEventRecords: deleting from content provider");
    }


    public void bulkInsert(List<FonoEvent> eventsList, String centerLocation) {
        Log.i(TAG, "bulkInsert: attempting bulk insert through content provider");
        int listLength = eventsList.size();
        Vector<ContentValues> cVVector = new Vector<ContentValues>(listLength);

        for(int i = 0; i < listLength; i++) {
            FonoEvent fonoEvent = eventsList.get(i);
            Date today = new Date();

            ContentValues eventValues = new ContentValues();
            eventValues.put(EventsContract.EventEntry.COLUMN_REQUEST_COORDINATES, centerLocation);
            eventValues.put(EventsContract.EventEntry.COLUMN_LOCATION_COORDINATES, fonoEvent.getLocationCoordinates());
            eventValues.put(EventsContract.EventEntry.COLUMN_NAME, fonoEvent.getName());
            eventValues.put(EventsContract.EventEntry.COLUMN_VENUE_NAME, fonoEvent.getVenueName());
            eventValues.put(EventsContract.EventEntry.COLUMN_ADDRESS, fonoEvent.getAddress());
            eventValues.put(EventsContract.EventEntry.COLUMN_DESCRIPTION, fonoEvent.getDescription());
            eventValues.put(EventsContract.EventEntry.COLUMN_CATEGORY_1, fonoEvent.getCategory_1());
            eventValues.put(EventsContract.EventEntry.COLUMN_CATEGORY_2, fonoEvent.getCategory_2());
            eventValues.put(EventsContract.EventEntry.COLUMN_CATEGORY_3, fonoEvent.getCategory_3());
            eventValues.put(EventsContract.EventEntry.COLUMN_LINK_TO_ORIGIN, fonoEvent.getLinkToOrigin());
            eventValues.put(EventsContract.EventEntry.COLUMN_DOWNLOAD_DATE, today.toString());

            cVVector.add(i, eventValues);

        }

        if (cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            context.getContentResolver().bulkInsert(EventsContract.EventEntry.CONTENT_URI, cvArray);
            Log.i("Bulk Insert", "bulkInsert: New Values Inserted");
        }

    }

}
