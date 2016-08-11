package com.davidparkeredwards.fono;

import android.app.Application;
import android.content.Context;

/**
 * Created by User on 8/11/2016.
 */
public class FONO extends Application {

    private static FONO instance;

    public static FONO getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
