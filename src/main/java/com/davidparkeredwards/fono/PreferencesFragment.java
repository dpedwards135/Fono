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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.davidparkeredwards.fono.data.EventDbManager;
import com.davidparkeredwards.fono.data.EventScorer;
import com.davidparkeredwards.fono.data.EventsContract;
import com.davidparkeredwards.fono.data.SharedPreference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PreferencesFragment extends Fragment {

    TextView preferencesText;
    String preferencesString;
    ArrayAdapter<String> categoriesAdapter;
    List<CheckBox> categoryCheckboxArray;

    private View.OnClickListener savePreferencesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            savePreferences();
        }
    };

    public PreferencesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        SharedPreference sharedPreference = new SharedPreference();
        Set<String> oldCategories = sharedPreference.getCategoriesList(getContext());
        Log.i("Load Preferences", "oldCategories: " + oldCategories.toString());

        View rootView = inflater.inflate(R.layout.fragment_preferences, container, false);

        preferencesText = (TextView) rootView.findViewById(R.id.preferencesText);

        preferencesText.setText("Select Event Radius and Categories");

        Button savePreferencesButton = (Button) rootView.findViewById(R.id.save_preferences);
        savePreferencesButton.setOnClickListener(savePreferencesListener);



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

        categoryCheckboxArray = new ArrayList<>();
        Log.i("Preferences", "CheckBox Array Length: " + categoryCheckboxArray.size());
        LinearLayout checkBoxHome = (LinearLayout)rootView.findViewById(R.id.checkBoxHome);
        Log.i("Check if Home created", "onCreateView: " + checkBoxHome.getTag());

        for (int i = 0; i<categoryStrings.length; i++) {


            Log.i("CheckBoxCreator", "Creating CheckBox");
            final CheckBox checkBox = (CheckBox) getLayoutInflater(null).inflate(R.layout.categories_selector, null);
            checkBoxHome.addView(checkBox);
            checkBox.setId(i);
            checkBox.setText(categoryStrings[i]);
            if (oldCategories.contains(categoryStrings[i])) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

            categoryCheckboxArray.add(i,checkBox);

        }
        Log.i("CheckBox Array", "Check CB Array " + categoryCheckboxArray.toString());
        Log.i("CheckBox Array", "Check CB Array " + categoryCheckboxArray.get(0).getId());
        return rootView;

    }


    public void savePreferences() {
        ////Get preference values
        Set<String> categorySaveString = new HashSet<>();
        for(int i=0; i<categoryCheckboxArray.size(); i++) {
            if(categoryCheckboxArray.get(i).isChecked()){
                categorySaveString.add(categoryCheckboxArray.get(i).getText().toString());
            }
        }
        Log.i("Preferences", "savePreferences Categories: " + categorySaveString.toString());

        ////Get SharedPreference and save
        SharedPreference sharedPreference = new SharedPreference();
        sharedPreference.saveCategories(getContext(), categorySaveString);
/*        EventDbManager eventDbManager = new EventDbManager(getContext());
        eventDbManager.scoreEvents();
        Log.i("savePreferences", "savePreferences: Scoring Events");
*/
        EventScorer eventScorer = new EventScorer();
        eventScorer.bulkReScore(getContext());
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