package com.davidparkeredwards.fono;

import android.app.Application;
import android.content.Context;

/** This class provides an instance and context for the application for use where needed
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
