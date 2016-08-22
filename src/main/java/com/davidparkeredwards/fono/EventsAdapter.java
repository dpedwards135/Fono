package com.davidparkeredwards.fono;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.davidparkeredwards.fono.data.EventDbManager;
import com.davidparkeredwards.fono.data.EventsContract;
import com.davidparkeredwards.fono.data.FonoEventScored;

import java.util.ArrayList;

/**
 * Created by User on 8/9/2016.
 */
public class EventsAdapter extends CursorAdapter {

    public EventsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String convertCursorRowToUxFormat(Cursor cursor) {

        return cursor.getString(EventDbManager.COL_NAME)
                + "\n" + cursor.getString(EventDbManager.COL_VENUE_NAME)
                + "\n" + cursor.getString(EventDbManager.COL_EVENT_SCORE)
                + "\n" + Long.toString(cursor.getLong(EventDbManager.COL_ID))
                + "\n" + Long.toString(cursor.getLong(EventDbManager.COL_DISTANCE))
                + "\n" + cursor.getString(EventDbManager.COL_REQUESTER);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        View view = LayoutInflater.from(context).inflate(R.layout.list_item_events, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view;
        tv.setText(convertCursorRowToUxFormat(cursor));
    }
}

