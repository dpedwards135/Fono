package com.davidparkeredwards.fono;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.davidparkeredwards.fono.data.EventsContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PreferencesFragment extends Fragment {

    TextView preferencesText;
    String preferencesString;
    ArrayAdapter<String> categoriesAdapter;

    public PreferencesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_preferences, container, false);

        preferencesText = (TextView) rootView.findViewById(R.id.preferencesText);

        preferencesText.setText("Select Event Radius and Categories");

        String[] categoryStrings = {
                "Performing Arts",
                "Film",
                "Outdoors and Recreation",
                "Fundraising and Charity",
                "Other and Miscellaneous",
                "Sports",
                "Education",
                "Museums and Attractions",
                "Holiday",
                "Art Galleries and Exhibits",
                "Neighborhood",
                "Kids and Family",
                "Science",
                "Business and Networking",
                "Health and Wellness",
                "Food and Wine",
                "Concerts and Tour Dates",
                "Comedy",
                "University and Alumni",
                "Politics and Activism",
                "Conferences and Tradeshows",
                "Nightlife and Singles"
        };
        Arrays.sort(categoryStrings);

        List<CheckBox> categoryCheckboxArray = new ArrayList<>();
        Log.i("Preferences", "CheckBox Array Length: " + categoryCheckboxArray.size());
        LinearLayout checkBoxHome = (LinearLayout)rootView.findViewById(R.id.checkBoxHome);
        Log.i("Check if Home created", "onCreateView: " + checkBoxHome.getTag());

        for (int i = 0; i<categoryStrings.length; i++) {

            categoryCheckboxArray.add(i,new CheckBox(getActivity()));
            Log.i("CheckBoxCreator", "Creating CheckBox");
            CheckBox checkBox = (CheckBox) getLayoutInflater(null).inflate(R.layout.categories_selector, null);
            checkBoxHome.addView(checkBox);
            checkBox.setId(i);
            checkBox.setText(categoryStrings[i]);


        }

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
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


}