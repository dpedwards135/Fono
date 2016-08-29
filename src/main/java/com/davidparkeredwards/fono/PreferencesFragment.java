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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**This fragment gives the user the option to choose what categories of events
 * they would like to see at the top of the Radar
 */

public class PreferencesFragment extends Fragment {

    //List of image assets mapped to category names

    public static final Map<String, Integer> categoryImages = new HashMap<String, Integer>() {

        {
            put("Performing Arts", R.drawable.ballerina);
            put("Film", R.drawable.projector);
            put("Outdoors and Recreation", R.drawable.hiker);
            put("Fundraising and Charity", R.drawable.heart );
            put("Other and Miscellaneous", R.drawable.questionmark);
            put("Sports",R.drawable.baseball);
            put("Education", R.drawable.blackboard);
            put("Museums and Attractions", R.drawable.museum);
            put("Holiday",R.drawable.balloons);
            put("Art Galleries and Exhibits", R.drawable.palette);
            put("Neighborhood", R.drawable.tree);
            put("Kids and Family", R.drawable.crayons);
            put("Science", R.drawable.chemical);
            put("Business and Networking", R.drawable.handshake);
            put("Health and Wellness", R.drawable.apple);
            put("Food and Wine", R.drawable.wineandcheese);
            put("Concerts and Tour Dates", R.drawable.musicnotes);
            put("Comedy", R.drawable.comedy);
            put("University and Alumni", R.drawable.mortarboard);
            put("Politics and Activism", R.drawable.statueofliberty);
            put("Conferences and Tradeshows", R.drawable.podium);
            put("Nightlife and Singles", R.drawable.discoball);
            put("Literary and Books", R.drawable.book);
            put("Festivals", R.drawable.fireworks);
            put("Sales and Retail", R.drawable.special);
            put("Organizations and Meetups", R.drawable.nametag);
            put("Religion and Spirituality", R.drawable.sunburst);
            put("Technology", R.drawable.radiosignal);
            put("Pets", R.drawable.dog);
        }
    };

    TextView preferencesText;
    String preferencesString;
    ArrayAdapter<String> categoriesAdapter;
    List<CheckBox> categoryCheckboxArray;


    public PreferencesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //OnCreateView a series of checkboxes are initialized with values corresponding to the string array
        // of categories below

        SharedPreference sharedPreference = new SharedPreference();
        Set<String> oldCategories = sharedPreference.getCategoriesList(getContext());
        View rootView = inflater.inflate(R.layout.fragment_preferences, container, false);
        preferencesText = (TextView) rootView.findViewById(R.id.preferencesText);

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
                "Nightlife and Singles",
                "Literary and Books",
                "Festivals",
                "Sales and Retail",
                "Organizations and Meetups",
                "Religion and Spirituality",
                "Technology",
                "Pets"
        };
        Arrays.sort(categoryStrings);

        categoryCheckboxArray = new ArrayList<>();
        LinearLayout checkBoxHome = (LinearLayout)rootView.findViewById(R.id.checkBoxHome);

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
        Log.i("Preferences", "savePreferences: Categories Saved");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    //Save Preferences onDetach
    @Override
    public void onDetach() {
        savePreferences();
        super.onDetach();
    }
}