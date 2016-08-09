package com.davidparkeredwards.fono.data;

import android.app.usage.UsageEvents;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by User on 8/8/2016.
 */
public class EventsProvider extends ContentProvider {

    //Right now all I need is to pull all events from the database, no custom queries.

    //URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private EventDbHelper mOpenHelper;

    static final int EVENTS = 100;

    private static final SQLiteQueryBuilder sEventsQueryBuilder;

    static {
        sEventsQueryBuilder = new SQLiteQueryBuilder();
        sEventsQueryBuilder.setTables(
                EventsContract.EventEntry.TABLE_NAME
        );
    }

    private Cursor getEvents(Uri uri, String[] projection, String sortOrder) {



        return sEventsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null, //selection,
                null, //selectionArgs,
                null,
                null,
                null //sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = EventsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, EventsContract.PATH_EVENTS, EVENTS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new EventDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch(match) {
            case EVENTS:
                return EventsContract.EventEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case EVENTS: {
                retCursor = null;
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case EVENTS: {
                long _id = db.insert(EventsContract.EventEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = EventsContract.EventEntry.buildEventsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int deletedRows = 0;

        switch (match) {
            case EVENTS: {
                deletedRows = db.delete(EventsContract.EventEntry.TABLE_NAME, selection, selectionArgs);
                if (deletedRows > 0 || selection == null)
                    getContext().getContentResolver().notifyChange(uri, null);
                return deletedRows;

            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //needs something more here
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int updatedRows = 0;

        switch (match) {
            case EVENTS: {
                updatedRows = db.delete(EventsContract.EventEntry.TABLE_NAME, selection, selectionArgs);
                if (updatedRows > 0 || selection == null)
                    getContext().getContentResolver().notifyChange(uri, null);
                return updatedRows;

            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENTS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(EventsContract.EventEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }

    }
}

