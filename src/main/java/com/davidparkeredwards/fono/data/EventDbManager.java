package com.davidparkeredwards.fono.data;

import android.app.usage.UsageEvents;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.davidparkeredwards.fono.FonoEvent;

import java.util.Date;
import java.util.List;

/**
 * Created by User on 7/31/2016.
 */
public class EventDbManager {
    private Context context;
    String TAG = "EventDbManager";

    public EventDbManager(Context context) {
        this.context = context;
    }

    public void createDbTable(List eventsList, String centerLocation){

        try {
            EventDbHelper eventDbHelper = new EventDbHelper(context);
            SQLiteDatabase db = eventDbHelper.getWritableDatabase();
            insertValues(db);
            readValues(db);
            deleteEventRecords(db);
            Log.i(TAG, "createDbTable: Deleted Events");
            readValues(db);
            saveEventsToDb(db, eventsList, centerLocation);
            readValues(db);

        }
        catch (SQLiteException e) {
            Log.i(TAG, "createDbTable: Unable to get DB");
        }
    }

    private void saveEventsToDb(SQLiteDatabase db, List<FonoEvent> eventsList, String centerLocation) {
        int listLength = eventsList.size();
        for(int i = 0; i < listLength; i++) {
            FonoEvent fonoEvent = eventsList.get(i);
            Date today = new Date();

            ContentValues eventValues = new ContentValues();
            eventValues.put("REQUEST_COORDINATES", centerLocation);
            eventValues.put("LOCATION_COORDINATES", "Location coordinates unavailable");
            eventValues.put("NAME", fonoEvent.getName());
            eventValues.put("VENUE_NAME", fonoEvent.getVenueName());
            eventValues.put("ADDRESS", fonoEvent.getAddress());
            eventValues.put("DESCRIPTION", fonoEvent.getDescription());
            eventValues.put("CATEGORY", fonoEvent.getCategory());
            eventValues.put("LINK_TO_ORIGIN", fonoEvent.getLinkToOrigin());
            eventValues.put("DOWNLOAD_DATE", today.toString());
            db.insert("events", null, eventValues);
        }
    }

    public void deleteEventRecords(SQLiteDatabase db) {
        db.execSQL("delete from EVENTS");
    }

    public void insertValues(SQLiteDatabase db) {
        ContentValues eventValues = new ContentValues();
        eventValues.put("NAME", "Test Event 2");
        db.insert("events", null, eventValues);

    }

    public void readValues(SQLiteDatabase db) {
        Cursor cursor = db.query("EVENTS",
                new String[] {"NAME", "DESCRIPTION"},
                null,null,null,null,null);

        while (cursor.moveToNext()) {

            Log.i(TAG, "readValues: " + cursor.getString(0) + cursor.getString(1));

        }

    }

}
