package com.davidparkeredwards.fono;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;


import com.davidparkeredwards.fono.data.EventsContract;


public class DetailFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {


    private static final int DETAIL_LOADER = 0;

    private static final String[] EVENTS_COLUMNS = {
            EventsContract.EventEntry.TABLE_NAME + "." + EventsContract.EventEntry._ID,
            EventsContract.EventEntry.COLUMN_NAME,
            EventsContract.EventEntry.COLUMN_VENUE_NAME,
            EventsContract.EventEntry.COLUMN_DOWNLOAD_DATE,
            EventsContract.EventEntry.COLUMN_LINK_TO_ORIGIN,
            EventsContract.EventEntry.COLUMN_ADDRESS,
            EventsContract.EventEntry.COLUMN_CATEGORY_1,
            EventsContract.EventEntry.COLUMN_CATEGORY_2,
            EventsContract.EventEntry.COLUMN_CATEGORY_3,
            EventsContract.EventEntry.COLUMN_DESCRIPTION,
            EventsContract.EventEntry.COLUMN_LOCATION_COORDINATES,
            EventsContract.EventEntry.COLUMN_REQUEST_COORDINATES,
            EventsContract.EventEntry.COLUMN_EVENT_SCORE};

    static final int COL_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_VENUE_NAME = 2;
    static final int COL_DOWNLOAD_DATE = 3;
    static final int COL_LINK_TO_ORIGIN = 4;
    static final int COL_ADDRESS = 5;
    static final int COL_CATEGORY_1 = 60;
    static final int COL_CATEGORY_2 = 61;
    static final int COL_CATEGORY_3 = 62;
    static final int COL_DESCRIPTION = 9;
    static final int COL_LOCATION_COORDINATES = 10;
    static final int COL_REQUEST_COORDINATES = 11;
    static final int COL_EVENT_SCORE = 12;

    TextView detailText;
    String detailString = "No Text";

    public DetailFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

/*  Put Menu items here
    @Override
 *   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
 *       inflater.inflate(R.menu.eventsfragment, menu);
 *
 */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        detailText = (TextView) rootView.findViewById(R.id.detailText);



        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    //private void updateEvents --> Put AsyncTask here


    @Override
    public void onStart() {
        super.onStart();
        /*AsyncTask<String, Void, String> setDisplay = new EventRequest(this, locCoordinates, listView)
                .execute(locCoordinates);
        */
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle args) {


        Intent intent = getActivity().getIntent();

        if (intent == null) {
            return null;
        }
        return new android.support.v4.content.CursorLoader(
                    getActivity(),
                    intent.getData(),
                    EVENTS_COLUMNS,
                    null,
                    null,
                    null);


    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        Log.i("onLoadFinished", "In on load finished");

        if (!data.moveToFirst()) { return; }

        String name = data.getString(COL_NAME);
        String description = data.getString(COL_DESCRIPTION);
        String category_1 = data.getString(COL_CATEGORY_1);
        String category_2 = data.getString(COL_CATEGORY_2);
        String category_3 = data.getString(COL_CATEGORY_3);

        Log.i("Detail Check", "onLoadFinished: " + name + description);
        detailString = name + "\n" +
                description + "\n" +
                category_1 + "\n" +
                category_2 + "\n" +
                category_3;

        detailText.setText(detailString);
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }
}

