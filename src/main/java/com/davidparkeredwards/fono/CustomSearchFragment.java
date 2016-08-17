package com.davidparkeredwards.fono;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.davidparkeredwards.fono.data.EventsContract;


public class CustomSearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EVENTS_LOADER = 0;


    private static final String[] EVENTS_COLUMNS = {
            EventsContract.EventEntry.TABLE_NAME + "." + EventsContract.EventEntry._ID,
            EventsContract.EventEntry.COLUMN_NAME,
            EventsContract.EventEntry.COLUMN_DESCRIPTION,
            EventsContract.EventEntry.COLUMN_REQUEST_COORDINATES,
            EventsContract.EventEntry.COLUMN_LOCATION_COORDINATES,
            EventsContract.EventEntry.COLUMN_VENUE_NAME,
            EventsContract.EventEntry.COLUMN_ADDRESS,
            EventsContract.EventEntry.COLUMN_CATEGORY_1,
            EventsContract.EventEntry.COLUMN_CATEGORY_2,
            EventsContract.EventEntry.COLUMN_CATEGORY_3,
            EventsContract.EventEntry.COLUMN_LINK_TO_ORIGIN,
            EventsContract.EventEntry.COLUMN_DOWNLOAD_DATE,
            EventsContract.EventEntry.COLUMN_EVENT_SCORE,
            EventsContract.EventEntry.COLUMN_DISTANCE,
    };

    static final int COL_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_DESCRIPTION = 2;
    static final int COL_REQUEST_COORDINATES = 3;
    static final int COL_LOCATION_COORDINATES = 4;
    static final int COL_VENUE_NAME = 5;
    static final int COL_ADDRESS = 6;
    static final int COL_CATEGORY_1 = 7;
    static final int COL_CATEGORY_2 = 8;
    static final int COL_CATEGORY_3 = 9;
    static final int COL_LINK_TO_ORIGIN = 10;
    static final int COL_DOWNLOAD_DATE = 11;
    static final int COL_EVENT_SCORE = 12;
    static final int COL_DISTANCE = 13;

    private EventsAdapter eventsAdapter;

    public CustomSearchFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        eventsAdapter = new EventsAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_custom_search, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.custom_search_list_view);

        listView.setAdapter(eventsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), EventDetail.class)
                            .setData(EventsContract.EventEntry.buildEventsUriWithId(cursor.getLong(COL_ID)));
                    startActivity(intent);
                }
            }
        });

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(EVENTS_LOADER,null,this);
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

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Sort Order if desired
        String sortOrder = EventsContract.EventEntry.COLUMN_EVENT_SCORE + " DESC";
        Uri eventsUri = EventsContract.EventEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                eventsUri,
                EVENTS_COLUMNS,
                null,
                null,
                sortOrder);
    }


    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        eventsAdapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        eventsAdapter.swapCursor(null);
    }
}

