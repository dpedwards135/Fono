package com.davidparkeredwards.fono;

import android.app.AlertDialog;
import android.app.Dialog;

import com.davidparkeredwards.fono.R;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import com.davidparkeredwards.fono.data.EventDbManager;
import com.davidparkeredwards.fono.data.EventScorer;

public class SearchDialogFragment extends DialogFragment {


    EditText keywordET;
    EditText locationET;
    DatePicker datePicker;
    Button submitButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //rootView = inflater.inflate(R.layout.fragment_custom_search, container, false);



        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.search_popup, null);
        dialog.setView(view);
                // Add action buttons
                /*
                .setNeutralButton("Search Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("SearchDialogFragment", "Just Clicked Search");
                        customEventRequest();
                    }
                });
                */
        keywordET = (EditText) view.findViewById(R.id.customKeywords);
        locationET = (EditText) view.findViewById(R.id.customLocation);
        datePicker = (DatePicker) view.findViewById(R.id.customDatePicker);
        submitButton = (Button) view.findViewById(R.id.submitSearch);

        keywordET.setNextFocusDownId(R.id.customLocation);
        locationET.setNextFocusDownId(R.id.customDatePicker);
        datePicker.setNextFocusDownId(R.id.submitSearch);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Log.i("SearchDialogFragment", "Just Clicked Search");
                customEventRequest();
            }
        });

        keywordET.requestFocus();
        datePicker.setFocusableInTouchMode(true);
        Log.i("DatePicker", "DatePicker Requested Focus");
        datePicker.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("DatePicker", "Key Entered" + event);
                if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    Log.i("SearchDialogFragment", "Just hit enter after spinner");
                    customEventRequest();
                    dialog.dismiss();
                    return true;
                }

                return false;
            }
        });
        return dialog;
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
        EventRequest customEventRequest = new EventRequest(FONO.getContext(), customLocation, customKeywords, customDate, EventDbManager.CUSTOM_SEARCH_REQUEST);
        customEventRequest.execute();
        Toast toast = Toast.makeText(FONO.getContext(), "Searching", Toast.LENGTH_LONG);
        toast.show();
       // EventScorer es = new EventScorer();
       // es.allCategoriesToLog(FONO.getContext());
    }
}