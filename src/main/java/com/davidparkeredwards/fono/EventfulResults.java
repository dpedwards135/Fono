package com.davidparkeredwards.fono;

import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidparkeredwards.fono.data.EventDbHelper;
import com.davidparkeredwards.fono.sync.FonoSyncAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class EventfulResults extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.e("OnCreateEventfulResults", "Created");
        FonoSyncAdapter fonoSyncAdapter = new FonoSyncAdapter(this, true);
        fonoSyncAdapter.syncImmediately(this);

        setContentView(R.layout.activity_eventful_results);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ResultsFragment()).commit();
        }
        /*
        String locCoordinates = getIntent().getStringExtra("loccoordinates");
        listView = (ListView) findViewById(R.id.main_list_view);
        AsyncTask<String, Void, String> setDisplay = new EventRequest(this, locCoordinates, listView)
                .execute(locCoordinates);

        eventsAdapter = new EventsAdapter(this, null, 0);
        listView.setAdapter(eventsAdapter);

        */


    }


}
