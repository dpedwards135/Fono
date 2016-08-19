package com.davidparkeredwards.fono;

import android.app.AlertDialog;
import android.app.Dialog;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.davidparkeredwards.fono.R;
import com.davidparkeredwards.fono.data.EventDbManager;

public class SearchDialogFragment extends DialogFragment {


    EditText keywordET;
    EditText locationET;
    DatePicker datePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //rootView = inflater.inflate(R.layout.fragment_custom_search, container, false);



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.search_popup, null);
        builder.setView(view)
                // Add action buttons
                .setNeutralButton("Search Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("SearchDialogFragment", "Just Clicked Search");
                        customEventRequest();
                    }
                });
        keywordET = (EditText) view.findViewById(R.id.customKeywords);
        locationET = (EditText) view.findViewById(R.id.customLocation);
        datePicker = (DatePicker) view.findViewById(R.id.customDatePicker);

        return builder.create();
    }


    public void customEventRequest() {


        //Format date for JSON = YYYYMMDD00-YYYYMMDD00
        int day = datePicker.getDayOfMonth();
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
        String customKeywords = keywordET.getText().toString();
        String customLocation = locationET.getText().toString();
        Log.i("customEventRequest", "customEventRequest: " +
                "Year: " + yearString +
                "Month: " + monthString +
                "Day: " + dayString
        );
        Log.i("customEventRequest", customDate + " " + customKeywords + " " + customLocation);
        EventRequest customEventRequest = new EventRequest(FONO.getContext(), customDate, customKeywords,
                EventDbManager.CUSTOM_SEARCH_REQUEST, customLocation);
        customEventRequest.execute();
    }
}