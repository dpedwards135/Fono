package com.davidparkeredwards.fono.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by User on 7/30/2016.
 */
public class EventDbHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "events.db";


    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("onCreate", "onCreate: Attempting to create database");
        final String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE " + EventsContract.EventEntry.TABLE_NAME
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EventsContract.EventEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + EventsContract.EventEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + EventsContract.EventEntry.COLUMN_REQUEST_COORDINATES + " TEXT NOT NULL, "
                + EventsContract.EventEntry.COLUMN_LOCATION_COORDINATES + " TEXT NOT NULL, "
                + EventsContract.EventEntry.COLUMN_VENUE_NAME + " TEXT NOT NULL, "
                + EventsContract.EventEntry.COLUMN_ADDRESS + " TEXT NOT NULL, "
                + EventsContract.EventEntry.COLUMN_CATEGORY_1 + " TEXT NOT NULL, "
                + EventsContract.EventEntry.COLUMN_CATEGORY_2 + " TEXT NOT NULL, "
                + EventsContract.EventEntry.COLUMN_CATEGORY_3 + " TEXT NOT NULL, "
                + EventsContract.EventEntry.COLUMN_LINK_TO_ORIGIN + " TEXT NOT NULL, "
                + EventsContract.EventEntry.COLUMN_DOWNLOAD_DATE + " TEXT NOT NULL, "
                + EventsContract.EventEntry.COLUMN_EVENT_SCORE + " REAL); ";

        sqLiteDatabase.execSQL(SQL_CREATE_EVENTS_TABLE);
    }

    private static void insertEvent(SQLiteDatabase db, String name){
        ContentValues eventValues = new ContentValues();
        eventValues.put("NAME", name);
        db.insert("events", null, eventValues);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventsContract.EventEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
