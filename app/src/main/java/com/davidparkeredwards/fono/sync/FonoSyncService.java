package com.davidparkeredwards.fono.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by User on 8/10/2016.
 */
public class FonoSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static FonoSyncAdapter fonoSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("FonoSyncService", "onCreate - FonoSyncService");
        synchronized (sSyncAdapterLock) {
            if (fonoSyncAdapter == null) {
                fonoSyncAdapter = new FonoSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return fonoSyncAdapter.getSyncAdapterBinder();
    }
}