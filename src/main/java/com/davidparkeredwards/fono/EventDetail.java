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
        Long id = starterIntent.getLongExtra("Record ID", -1);
        Log.i("Detail View", "onCreate: " + id.toString());
        TextView detailText = (TextView) findViewById(R.id.detailText);
        detailText.setText(id.toString());
        String event = getEvent(id);
        detailText.setText(event);



    }

    public String getEvent(long id) {
        int intID = (int) id;
        Log.i("Check ID", "getEvent: " + intID);
        String event = "No Event";

        EventDbHelper eventDbHelper = new EventDbHelper(this);
        SQLiteDatabase db = eventDbHelper.getReadableDatabase();
        Cursor cursor = db.query("EVENTS",
                new String[]{"NAME", "VENUE_NAME", "_id"},
                "_id = ?",
                new String[]{Integer.toString(intID)},
                null, null, null);

        if (cursor.moveToFirst()) {

            Log.i("getEventsList", "readValues: " + cursor.getString(0) + cursor.getString(1) + cursor.getString(2));
            String detailName = cursor.getString(0);
            String detailVenueName = cursor.getString(1);
            id = cursor.getInt(2);

            event = detailName + detailVenueName;


        }
        return event;
    }
}
