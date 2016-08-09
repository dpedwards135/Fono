package com.davidparkeredwards.fono;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.davidparkeredwards.fono.data.EventDbHelper;

import java.util.ArrayList;
import java.util.List;

public class EventDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Intent starterIntent = getIntent();
        int id = starterIntent.getIntExtra("Record ID", 1);
        Log.i("Detail View", "onCreate: " + id);
        String event = getEvent(id);
        TextView detailText = (TextView) findViewById(R.id.detailText);
        detailText.setText(event);



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
}
