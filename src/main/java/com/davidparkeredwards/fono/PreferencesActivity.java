package com.davidparkeredwards.fono;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.e("OnCreateEventDetail", "Created");

        setContentView(R.layout.activity_preferences);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.preferencesContainer, new PreferencesFragment()).commit();
        }

    }
}