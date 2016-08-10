package com.davidparkeredwards.fono;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.davidparkeredwards.fono.data.EventsContract;

/**
 * Created by User on 8/9/2016.
 */
public class EventsAdapter extends CursorAdapter {

    public EventsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String convertCursorRowToUxFormat(Cursor cursor) {

        return cursor.getString(ResultsFragment.COL_NAME) + "\n" + cursor.getString(ResultsFragment.COL_VENUE_NAME);
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
