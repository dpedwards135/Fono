package com.davidparkeredwards.fono.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by User on 8/10/2016.
 */
public class FonoAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private FonoAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new FonoAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}