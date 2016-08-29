package com.davidparkeredwards.fono.data;

import android.app.usage.UsageEvents;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * EventsProvider is the ContentProvider
 */
public class EventsProvider extends ContentProvider {

    //URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private EventDbHelper mOpenHelper;

    static final int EVENTS = 100;
    static final int EVENTS_WITH_ID = 101;

    private static final SQLiteQueryBuilder sEventsQueryBuilder;

    static {
        sEventsQueryBuilder = new SQLiteQueryBuilder();
        sEventsQueryBuilder.setTables(
                EventsContract.EventEntry.TABLE_NAME
        );
    }

    private static final String eventsById =
            EventsContract.EventEntry.TABLE_NAME+
             "." + EventsContract.EventEntry._ID + " = ? ";



    private Cursor getEventsById(Uri uri, String[] projection, String sortOrder) {
        long eventId = EventsContract.EventEntry.getIdFromUri(uri);
        String[] selectionArgs;
        String selection;


        if (eventId == 0) {
            selection = null;
            selectionArgs = null;
        } else {
            selection = eventsById;
            selectionArgs = new String[] {Long.toString(eventId)};
        }

        return sEventsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null //sortOrder
        );
    }
    private Cursor getEvents(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {



        return sEventsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = EventsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, EventsContract.PATH_EVENTS, EVENTS);
        matcher.addURI(authority, EventsContract.PATH_EVENTS + "/#", EVENTS_WITH_ID);

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
            case EVENTS_WITH_ID:
                return EventsContract.EventEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.i("Events Provider Query", "query: " + uri.toString());
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case EVENTS: {
                Log.i("Case: Events", "query: ");
                retCursor = getEvents(uri, projection, selection, selectionArgs, sortOrder);
                Log.i("Provider", "Check query: " + sortOrder);
                break;
            }
            case EVENTS_WITH_ID: {
                Log.i("Case: EventsWithId", "query: ");
                retCursor = getEventsById(uri, projection, sortOrder);

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
                    returnUri = EventsContract.EventEntry.buildEventsUriWithId(_id);
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

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int updatedRows = 0;

        switch (match) {
            case EVENTS: {
                updatedRows = db.update(EventsContract.EventEntry.TABLE_NAME, values, selection, selectionArgs);
                if (updatedRows > 0 || selection == null)
                    getContext().getContentResolver().notifyChange(uri, null);
                return updatedRows;
            }
            case EVENTS_WITH_ID: {
                Log.i("Case: EventsWithId", "query: ");
                updatedRows = db.update(EventsContract.EventEntry.TABLE_NAME, values, selection, selectionArgs);
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

