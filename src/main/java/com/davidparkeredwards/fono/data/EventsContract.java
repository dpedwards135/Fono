package com.davidparkeredwards.fono.data;

import android.provider.BaseColumns;

/**
 * Created by User on 7/30/2016.
 */
public class EventsContract {

    public static final class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";

        //All columns presently String type
        public static final String COLUMN_LOCATION_COORDINATES = "location_coordinates";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_VENUE_NAME = "venue_name";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_LINK_TO_ORIGIN = "link_to_origin";
        public static final String COLUMN_DOWNLOAD_DATE = "download_date";
    }
}
