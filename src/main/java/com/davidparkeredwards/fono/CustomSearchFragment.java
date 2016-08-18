package com.davidparkeredwards.fono;

import android.app.usage.UsageEvents;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.davidparkeredwards.fono.data.EventDbManager;
import com.davidparkeredwards.fono.data.EventsContract;

import java.util.Date;

// Next - Create input fields that store values to be passed to EventRequest - Keywords, date, location

public class CustomSearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EVENTS_LOADER = 0;
    private View rootView;

    private EventsAdapter eventsAdapter;

    private View.OnClickListener customSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customEventRequest();
        }
    };

    DatePicker datePicker;
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

        rootView = inflater.inflate(R.layout.fragment_custom_search, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.custom_search_list_view);

        listView.setAdapter(eventsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), EventDetail.class)
                            .setData(EventsContract.EventEntry.buildEventsUriWithId(cursor.getLong(EventDbManager.COL_ID)));
                    startActivity(intent);
                }
            }
        });

        //Set onClickListener to Search  Button
        Button customSearchButton = (Button) rootView.findViewById(R.id.customSearchButton);
        customSearchButton.setOnClickListener(customSearchListener);

        //Set MinDate to DatePicker

        datePicker =  ((DatePicker) rootView.findViewById(R.id.customDatePicker));
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        datePicker.setSpinnersShown(true);
        datePicker.setCalendarViewShown(false);
        return rootView;

    }

    public void customEventRequest() {


        //Format date for JSON = YYYYMMDD00-YYYYMMDD00
        int day =datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        String dayString = Integer.toString(day);
        String monthString = Integer.toString(month);
        String yearString = Integer.toString(year);

        String customDate;
        if (day < 10 && month < 10) {
            customDate = "" + yearString + "0" + monthString +
                    "0" + dayString + "00";
        } else if(day >= 10 && month < 10) {
            customDate = "" + yearString + "0" + monthString  + dayString + "00";
        } else if(day < 10 && month >= 10) {
            customDate = "" + yearString + monthString + "0" + dayString + "00";
        }
        else {
            customDate = "" + yearString + monthString + dayString + "00";
        }
        customDate = customDate + "-" + customDate;


        //Get Keywords and Location
        String customKeywords = ((EditText) rootView.findViewById(R.id.customKeywords)).getText().toString();
        String customLocation = ((EditText) rootView.findViewById(R.id.customLocation)).getText().toString();
        Log.i("customEventRequest", "customEventRequest: " +
                "Year: " + yearString +
                "Month: " + monthString +
                "Day: " + dayString
        );
        Log.i("customEventRequest", customDate + " " + customKeywords + " " + customLocation);
        EventRequest customEventRequest = new EventRequest(getContext(), customDate, customKeywords,
                EventDbManager.CUSTOM_SEARCH_REQUEST, customLocation);
        customEventRequest.execute();
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
        String selection = EventsContract.EventEntry.COLUMN_REQUESTER + "=?";
        String[] selectionArgs = new String[] {EventDbManager.CUSTOM_SEARCH_REQUEST};

        String sortOrder = EventsContract.EventEntry.COLUMN_EVENT_SCORE + " DESC";
        Uri eventsUri = EventsContract.EventEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                eventsUri,
                EventDbManager.EVENTS_COLUMNS,
                selection,
                selectionArgs,
                sortOrder);
    }


    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        eventsAdapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        eventsAdapter.swapCursor(null);
    }
}

