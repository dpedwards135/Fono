package com.davidparkeredwards.fono;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.davidparkeredwards.fono.data.EventDbManager;
import com.davidparkeredwards.fono.data.EventsContract;
import com.davidparkeredwards.fono.data.FonoEventScored;
import com.davidparkeredwards.fono.data.SharedPreference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.zip.Inflater;


public class CustomSearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EVENTS_LOADER = 0;
    private View rootView;
    List<FonoEvent> listViewInfo;
    //private EventsAdapter eventsAdapter;
    ArrayAdapter<FonoEventScored> arrayAdapter;
    String requester;
    List<FonoEventScored> listViewInfoScored;
    Bundle savedState;

    public CustomSearchFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.savedState = savedInstanceState;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        if(savedState != null){
            scoreAndProcessEvents(listViewInfo);
        }

        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Set view settings
        requester = getArguments().getString("Requester");
        rootView = inflater.inflate(R.layout.fragment_custom_search, container, false);


        ListView listView = (ListView) rootView.findViewById(R.id.custom_search_list_view);
        View searchToggle = rootView.findViewById(R.id.searchToggle);


        if (requester == EventDbManager.RADAR_SEARCH_REQUEST) {
            searchToggle.setVisibility(View.GONE);
        }
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

        arrayAdapter = new ArrayAdapter<FonoEventScored>(getActivity(), R.layout.list_item_events){

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            class ViewHolder {
                TextView name;
                TextView venueName;
                ImageView categoryImage;
                TextView distance;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // A ViewHolder keeps references to children views
                // to avoid unneccessary calls to findViewById() on each row.
                ViewHolder holder;

                if (null == convertView) {
                    convertView = inflater.inflate(R.layout.list_item_events, null);

                    // Creates a ViewHolder and store references to
                    // the two children views we want to bind data to.
                    holder = new ViewHolder();
                    holder.name = (TextView) convertView.findViewById(R.id.listItemName);
                    holder.venueName = (TextView) convertView.findViewById(R.id.listItemVenueName);
                    holder.categoryImage = (ImageView) convertView.findViewById(R.id.listItemCategoryImage);
                    holder.distance = (TextView) convertView.findViewById(R.id.listItemDistance);
                    convertView.setTag(holder);
                } else {
                    // Get the ViewHolder back to get fast access to the TextView
                    // and the ImageView.
                    holder = (ViewHolder) convertView.getTag();

                }
                // Bind the data efficiently with the holder.

                holder.name.setText(getItem(position).getName());
                holder.venueName.setText(getItem(position).getVenueName());
                holder.distance.setText(String.valueOf(Math.ceil((100*getItem(position).getDistance()))/100 )+ " mi");

                ///Select Category Image to show
                String category; // Variable that holds the category whose icon will show on listitem
                String category1 = getItem(position).getCategory_1();
                String category2 = getItem(position).getCategory_2();
                String category3 = getItem(position).getCategory_3();
                SharedPreference sp = new SharedPreference();
                Set<String> categoriesList = sp.getCategoriesList(getContext());
                if(categoriesList.contains(category1)) {
                    category = category1;
                } else if(categoriesList.contains(category2)) {
                    category = category2;
                } else if(categoriesList.contains(category3)) {
                    category = category3;
                } else {
                    category = category1;
                }

                holder.categoryImage.setImageResource(PreferencesFragment.categoryImages.get(category));


                return convertView;
            }
        };


        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ((ListView) parent).getItemAtPosition(position);
                int wrongId = arrayAdapter.getItem(position).getId();
                int _id = listViewInfoScored.get(position).getId();

                Intent intent = new Intent(getActivity(), EventDetail.class)
                        .setData(EventsContract.EventEntry.buildEventsUriWithId(_id));
                startActivity(intent);
            }
        });

    return rootView;

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(EVENTS_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String selection = EventsContract.EventEntry.COLUMN_REQUESTER + "=?";
        String[] selectionArgs = new String[] {requester};

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
        EventDbManager eventDbManager = new EventDbManager(getContext());
        listViewInfo = eventDbManager.getEventsArray(cursor);
        if(listViewInfo.size()>0) {
            scoreAndProcessEvents(listViewInfo);
        }



    }

    public void scoreAndProcessEvents(List<FonoEvent> listViewInfo) {
        listViewInfoScored = new ArrayList<>();
        for (FonoEvent fonoEvent : listViewInfo) {
            FonoEventScored fonoEventScored = new FonoEventScored(
                    fonoEvent.getName(),
                    fonoEvent.getDate(),
                    fonoEvent.getVenueName(),
                    fonoEvent.getAddress(),
                    fonoEvent.getDescription(),
                    fonoEvent.getCategory_1(),
                    fonoEvent.getCategory_2(),
                    fonoEvent.getCategory_3(),
                    fonoEvent.getLinkToOrigin(),
                    fonoEvent.getId(),
                    fonoEvent.getLocationCoordinates(),
                    fonoEvent.getRequestCoordinates(),
                    fonoEvent.getRequester()
            );
            listViewInfoScored.add(fonoEventScored);
        }

        Collections.sort(listViewInfoScored, new Comparator<FonoEventScored>() {
            @Override
            public int compare(FonoEventScored lhs, FonoEventScored rhs) {
                return Double.compare(rhs.getEventScore(), lhs.getEventScore());
            }});
        arrayAdapter.clear();

        arrayAdapter.addAll(listViewInfoScored);
        arrayAdapter.notifyDataSetChanged();
    }


    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        arrayAdapter.notifyDataSetChanged();
    }

}


