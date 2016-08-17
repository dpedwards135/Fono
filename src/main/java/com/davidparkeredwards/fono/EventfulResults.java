package com.davidparkeredwards.fono;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.davidparkeredwards.fono.sync.FonoSyncAdapter;


public class EventfulResults extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.e("OnCreateEventfulResults", "Created");
        FonoSyncAdapter fonoSyncAdapter = new FonoSyncAdapter(this, true);
        //fonoSyncAdapter.syncImmediately(this);

        setContentView(R.layout.activity_eventful_results);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ResultsFragment()).commit();
        }
        /*

        */


    }


}
