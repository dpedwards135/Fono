package com.davidparkeredwards.fono.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        final String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE " + EventsContract.EventEntry.TABLE_NAME + " (" +
                EventsContract.EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EventsContract.EventEntry.COLUMN_LOCATION_COORDINATES + " TEXT NOT NULL, " +
                EventsContract.EventEntry.COLUMN_ADDRESS + " TEXT NOT NULL, " +
                EventsContract.EventEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                EventsContract.EventEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                EventsContract.EventEntry.COLUMN_DOWNLOAD_DATE + " TEXT NOT NULL, " +
                EventsContract.EventEntry.COLUMN_LINK_TO_ORIGIN + " TEXT NOT NULL, " +
                EventsContract.EventEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                EventsContract.EventEntry.COLUMN_VENUE_NAME + " TEXT NOT NULL, ";
        sqLiteDatabase.execSQL(SQL_CREATE_EVENTS_TABLE);

    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventsContract.EventEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
