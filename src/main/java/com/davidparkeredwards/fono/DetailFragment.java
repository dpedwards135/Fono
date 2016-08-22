package com.davidparkeredwards.fono;

import android.app.LoaderManager;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;


import com.davidparkeredwards.fono.data.EventDbManager;
import com.davidparkeredwards.fono.data.EventScorer;
import com.davidparkeredwards.fono.data.EventsContract;

import org.w3c.dom.Text;


public class DetailFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {


    private static final int DETAIL_LOADER = 0;
    TextView detailName;
    TextView detailDateAndLocation;
    TextView detailDescription;
    TextView detailCategories;
    //TextView detailLinkToOrigin;
    ImageView backButton;
    LinearLayout mapButton;
    TextView mapButtonText;
    Button goToWebPage;

    String name;
    double distance;
    String locationCoordinates;
    String linkToOrigin;

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

        detailName = (TextView) rootView.findViewById(R.id.detailName);
        detailDateAndLocation = (TextView) rootView.findViewById(R.id.detailDateAndLocation);
        detailDescription = (TextView) rootView.findViewById(R.id.detailDescription);
        detailCategories = (TextView) rootView.findViewById(R.id.detailCategories);
        //detailLinkToOrigin = (TextView) rootView.findViewById(R.id.detailLinkToOrigin);
        backButton = (ImageView) rootView.findViewById(R.id.backButton);
        mapButton = (LinearLayout) rootView.findViewById(R.id.goToMap);
        mapButtonText = (TextView) rootView.findViewById(R.id.goToMapText);
        goToWebPage = (Button) rootView.findViewById(R.id.goToWebPage);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });


        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
            }
        });

        goToWebPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWebPage();
            }
        });


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
                EventDbManager.EVENTS_COLUMNS,
                    null,
                    null,
                    null);


    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        Log.i("onLoadFinished", "In on load finished");

        if (!data.moveToFirst()) { return; }

        name = data.getString(EventDbManager.COL_NAME);
        String date = data.getString(EventDbManager.COL_DOWNLOAD_DATE);
        String venueName = data.getString(EventDbManager.COL_VENUE_NAME);
        String address = data.getString(EventDbManager.COL_ADDRESS);
        String description = data.getString(EventDbManager.COL_DESCRIPTION);
        String category_1 = data.getString(EventDbManager.COL_CATEGORY_1);
        String category_2 = data.getString(EventDbManager.COL_CATEGORY_2);
        String category_3 = data.getString(EventDbManager.COL_CATEGORY_3);
        linkToOrigin = data.getString(EventDbManager.COL_LINK_TO_ORIGIN);
        locationCoordinates = data.getString(EventDbManager.COL_LOCATION_COORDINATES);
        String requestCoordinates = data.getString(EventDbManager.COL_REQUEST_COORDINATES);

        EventScorer eventScorer = new EventScorer();
        distance =  (double) Math.ceil(eventScorer.calculateDistance(locationCoordinates, requestCoordinates) * 100) / 100;


        Log.i("Detail Check", "onLoadFinished: " + name + description);

        detailName.setText(name);
        detailDateAndLocation.setText(date + "\n" + venueName + "\n" + address);
        mapButtonText.setText(distance + " miles away" +
                "\nGet Directions"
        );
        detailDescription.setText(Html.fromHtml(description));
        detailCategories.setText("Categories:" +
                "\n"+category_1+
                "\n"+category_2+
                "\n"+category_3 );
        //detailLinkToOrigin.setText(linkToOrigin);
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    public void goToWebPage() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(linkToOrigin)));
    }

    public void showMap() {

        Uri geoIntent = Uri.parse("geo:0,0?q=" + locationCoordinates+"(" + name + ")");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoIntent);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}

