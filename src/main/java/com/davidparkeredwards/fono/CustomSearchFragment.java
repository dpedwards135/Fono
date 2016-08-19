package com.davidparkeredwards.fono;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.PopupWindowCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.davidparkeredwards.fono.data.EventDbManager;
import com.davidparkeredwards.fono.data.EventsContract;
import com.davidparkeredwards.fono.data.FonoEventScored;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/* Next:
 * Set up array to properly sort
 * Create and hook up sort by radio buttons
 * Apply changes to ResultsFragment
 * Take a break
 * Build Layout:
 *  Main Activity Frame
 *  Search popout
 *  ListViews
 *  EventDetail
 * Add mapView to EventDetail
 * Add navigation intent
 */
public class CustomSearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EVENTS_LOADER = 0;
    private View rootView;
    List<FonoEventScored> listViewInfo;
    //private EventsAdapter eventsAdapter;
    ArrayAdapter<FonoEventScored> arrayAdapter;


    public CustomSearchFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Set view settings
        rootView = inflater.inflate(R.layout.fragment_custom_search, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.custom_search_list_view);
        View searchToggle = rootView.findViewById(R.id.searchToggle);
        //Button customSearchButton = (Button) rootView.findViewById(R.id.customSearchButton);

        View.OnClickListener searchToggleListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDialogFragment newSDF = new SearchDialogFragment();
                android.app.FragmentManager fm = getActivity().getFragmentManager();
                newSDF.show(fm, "Search Dialog");

            }
        };
        searchToggle.setOnClickListener(searchToggleListener);


        arrayAdapter = new ArrayAdapter<FonoEventScored>(getActivity(), R.layout.list_item_events);


        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ((ListView) parent).getItemAtPosition(position);
                Log.i("OnListViewClick", "position: " + position);
                int _id = listViewInfo.get(position).getId();
                Log.i("OnListViewClick", "_id: " + _id);
                Intent intent = new Intent(getActivity(), EventDetail.class)
                        .setData(EventsContract.EventEntry.buildEventsUriWithId(_id));
                startActivity(intent);
            }
        });

    return rootView;
/*
                    Intent intent = new Intent(getActivity(), EventDetail.class);

                    startActivity(intent);
*/

/*
        //Set onClickListener to Search  Button

        View.OnClickListener customSearchListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customEventRequest();
            }
        };
        customSearchButton.setOnClickListener(customSearchListener);

        //Set MinDate to DatePicker

        datePicker =  ((DatePicker) rootView.findViewById(R.id.customDatePicker));
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        datePicker.setSpinnersShown(true);
        datePicker.setCalendarViewShown(false);
        return rootView;

    }


    public void customEventRequest() {
        setSearchBoxVisibility();

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
*/

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
        //eventsAdapter.swapCursor(cursor);
        //Swap ArrayAdapter with new adapter
        EventDbManager eventDbManager = new EventDbManager(getContext());
        listViewInfo = eventDbManager.getEventsScoredArray(cursor);
        if(listViewInfo.size()>0) {
            Log.i("onLoadFinished", "String Check: " + listViewInfo.get(0).toString() +
                    "\n" + "Name of first listViewInfo Event: " + listViewInfo.get(0).getName() +
                    "\n" + "Size of listViewInfo = " + listViewInfo.size());
        }
        arrayAdapter.clear();
        arrayAdapter.addAll(listViewInfo);
    }



    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        arrayAdapter.notifyDataSetChanged();
        Log.i("onLoaderReset", "Loader Reset");
    }
}

