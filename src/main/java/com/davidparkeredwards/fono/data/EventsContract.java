package com.davidparkeredwards.fono.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.davidparkeredwards.fono.R;

import java.util.List;

/**
 * Created by User on 7/30/2016.
 */
public class EventsContract {

    public static final String CONTENT_AUTHORITY = "com.davidparkeredwards.com";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_EVENTS = "events";

    public static final class EventEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTS;

        public static final String TABLE_NAME = "EVENTS";

        //All columns presently String type
        public static final String COLUMN_REQUEST_COORDINATES = "REQUEST_COORDINATES";
        public static final String COLUMN_LOCATION_COORDINATES = "LOCATION_COORDINATES";
        public static final String COLUMN_NAME = "NAME";
        public static final String COLUMN_VENUE_NAME = "VENUE_NAME";
        public static final String COLUMN_ADDRESS = "ADDRESS";
        public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
        public static final String COLUMN_CATEGORY_1 = "CATEGORY_1";
        public static final String COLUMN_CATEGORY_2 = "CATEGORY_2";
        public static final String COLUMN_CATEGORY_3 = "CATEGORY_3";
        public static final String COLUMN_LINK_TO_ORIGIN = "LINK_TO_ORIGIN";
        public static final String COLUMN_DOWNLOAD_DATE = "DOWNLOAD_DATE";
        public static final String COLUMN_EVENT_SCORE = "EVENT_SCORE";

        public static Uri buildEventsUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            long id = Long.parseLong(uri.getPathSegments().get(1));
            Log.i("getIdFromUri", "getIdFromUri: " + id);
            return id;
        }



    }
}
