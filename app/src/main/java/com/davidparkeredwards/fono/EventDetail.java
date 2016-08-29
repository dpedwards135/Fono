package com.davidparkeredwards.fono;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.davidparkeredwards.fono.data.EventDbHelper;
import com.davidparkeredwards.fono.data.EventsContract;

import java.util.ArrayList;
import java.util.List;

//EventDetail Activity contains DetailFragment, where the action happens
public class EventDetail extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.e("OnCreateEventDetail", "Created");

        setContentView(R.layout.activity_event_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.detailContainer, new DetailFragment()).commit();
        }
    }
}
