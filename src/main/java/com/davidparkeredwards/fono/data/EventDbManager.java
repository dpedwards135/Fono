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

    public static String[] EVENTS_COLUMNS = {
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
            EventsContract.EventEntry.COLUMN_REQUESTER
    };

    public static int COL_ID = 0;
    public static int COL_NAME = 1;
    public static int COL_DESCRIPTION = 2;
    public static int COL_REQUEST_COORDINATES = 3;
    public static int COL_LOCATION_COORDINATES = 4;
    public static int COL_VENUE_NAME = 5;
    public static int COL_ADDRESS = 6;
    public static int COL_CATEGORY_1 = 7;
    public static int COL_CATEGORY_2 = 8;
    public static int COL_CATEGORY_3 = 9;
    public static int COL_LINK_TO_ORIGIN = 10;
    public static int COL_DOWNLOAD_DATE = 11;
    public static int COL_EVENT_SCORE = 12;
    public static int COL_DISTANCE = 13;
    public static int COL_REQUESTER = 14;

    public static final String CUSTOM_SEARCH_REQUEST = "Custom Search Request";
    public static final String RADAR_SEARCH_REQUEST = "Radar Search Request";

    private Context context;
    String TAG = "EventDbManager";

    public EventDbManager(Context context) {
        this.context = context;
    }

    public void deleteAndInsertEvents(String eventRequester, List<FonoEvent> eventsList){

        for(int i = 0; i<eventsList.size(); i++) {
            FonoEvent checkEvent = eventsList.get(i);
            Log.i(TAG, "Check Requester: " + checkEvent.getRequester());
        }

        try {
            deleteEventRecords(eventRequester);
            bulkInsert(eventsList);

        }
        catch (SQLiteException e) {
            Log.i(TAG, "createDbTable: Unable to get DB");
        }
    }

    public void deleteEventRecords(String eventRequester) {

        String selection = EventsContract.EventEntry.COLUMN_REQUESTER + "=?";
        String[] selectionArgs = new String[] {eventRequester};

        int deletedRows = context.getContentResolver().delete(EventsContract.EventEntry.CONTENT_URI, selection, selectionArgs);
        Log.i(TAG, "deleteEventRecords: deleted rows = " + deletedRows);
    }


    public void bulkInsert(List<FonoEvent> eventsList) {
        Log.i(TAG, "bulkInsert: attempting bulk insert through content provider");
        int listLength = eventsList.size();
        Vector<ContentValues> cVVector = new Vector<ContentValues>(listLength);

        for(int i = 0; i < listLength; i++) {
            FonoEvent fonoEvent = eventsList.get(i);
            Date today = new Date();

            ContentValues eventValues = new ContentValues();
            eventValues.put(EventsContract.EventEntry.COLUMN_REQUEST_COORDINATES, fonoEvent.getRequestCoordinates());
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
            eventValues.put(EventsContract.EventEntry.COLUMN_REQUESTER, fonoEvent.getRequester());

            cVVector.add(i, eventValues);

        }

        if (cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            context.getContentResolver().bulkInsert(EventsContract.EventEntry.CONTENT_URI, cvArray);
            Log.i("Bulk Insert", "bulkInsert: New Values Inserted");
        }

    }

    public List<FonoEvent> getEventsArray(Cursor cursor) {

        //Used to create Lists for ArrayAdapters, given a cursor from a cursor loader.
        List<FonoEvent> listViewInfo = new ArrayList<>();

        while (cursor.moveToNext()) {
            //Pull info desired to display in ListView and add to listViewInfo. Be sure to add ID
            //Add score and distance for each object as it is pulled
            int _id = cursor.getInt(EventDbManager.COL_ID);
            String name = cursor.getString(EventDbManager.COL_NAME);
            String description = cursor.getString(EventDbManager.COL_DESCRIPTION);
            String requestCoordinates = cursor.getString(EventDbManager.COL_REQUEST_COORDINATES);
            String locationCoordinates = cursor.getString(EventDbManager.COL_LOCATION_COORDINATES);
            String venueName = cursor.getString(EventDbManager.COL_VENUE_NAME);
            String address = cursor.getString(EventDbManager.COL_ADDRESS);
            String category1 = cursor.getString(EventDbManager.COL_CATEGORY_1);
            String category2 = cursor.getString(EventDbManager.COL_CATEGORY_2);
            String category3 = cursor.getString(EventDbManager.COL_CATEGORY_3);
            String linkToOrigin = cursor.getString(EventDbManager.COL_LINK_TO_ORIGIN);
            String downloadDate = cursor.getString(EventDbManager.COL_DOWNLOAD_DATE);
            String requester = cursor.getString(EventDbManager.COL_REQUESTER);

            FonoEvent fonoEvent = new FonoEvent(name, downloadDate, venueName, address, description,
                    category1, category2, category3, linkToOrigin, _id, locationCoordinates, requestCoordinates,
                    requester);

            listViewInfo.add(fonoEvent);
            /* Remove these from all DB operations:
                    EventsContract.EventEntry.COLUMN_EVENT_SCORE,
                    EventsContract.EventEntry.COLUMN_DISTANCE,
        */
        }
        return listViewInfo;
    }
}
