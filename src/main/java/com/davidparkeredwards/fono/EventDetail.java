package com.davidparkeredwards.fono;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.davidparkeredwards.fono.data.EventDbHelper;
import com.davidparkeredwards.fono.data.EventsContract;

import java.util.ArrayList;
import java.util.List;

public class EventDetail extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.e("OnCreateEventDetail", "Created");

        setContentView(R.layout.activity_event_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.detailContainer, new DetailFragment()).commit();
        }


        /*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        getLoaderManager().initLoader(EVENTS_LOADER, null,this);

        TextView detailText = (TextView) findViewById(R.id.detailText);


        Intent starterIntent = getIntent();
        if (starterIntent != null) {
            id = starterIntent.getDataString();
        }

        Log.i("Detail View", "onCreate: " + id);
       // String event = getEvent(Integer.valueOf(id));




    }

    public String getEvent(int id) {

        Log.i("Check ID", "getEvent: " + id);
        String event = "No Event";

        EventDbHelper eventDbHelper = new EventDbHelper(this);
        SQLiteDatabase db = eventDbHelper.getReadableDatabase();

        Cursor cursor = db.query("EVENTS",
                new String[]{"NAME", "VENUE_NAME", "_id", "ADDRESS", "DESCRIPTION", "CATEGORY", "LINK_TO_ORIGIN"},
                //null,
                "_id = ?",
                //new String[]{"%The Secret Societies Tour%"},
                new String[]{Integer.toString(id)},
                //null,
                null, null, null);

        if (cursor.moveToFirst()) {

            Log.i("getEventsList", "readValues: " + cursor.getString(2));
            String detailName = cursor.getString(0);
            String detailVenueName = cursor.getString(1);
            String detailAddress = cursor.getString(3);
            String detailDescription = cursor.getString(4);
            String detailCategory = cursor.getString(5);
            String detailLinkToOrigin = cursor.getString(6);
            //id = cursor.getInt(2);

            event = detailName + "\n" + "\n"
                    + detailVenueName + "\n" + "\n"
                    + detailAddress + "\n" + "\n"
                    + detailDescription + "\n" + "\n"
                    + detailCategory + "\n" + "\n"
                    + detailLinkToOrigin;
        }
        //Log.i("Check getEvent", "getEvent: " + event.toString());
        cursor.close();
        return event;
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Sort Order if desired

        return new CursorLoader(getActivity(),
                id,
                EVENTS_COLUMNS,
                null,
                null);
    }


    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        eventsAdapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        eventsAdapter.swapCursor(null);
    }
}
*/
    }
}
