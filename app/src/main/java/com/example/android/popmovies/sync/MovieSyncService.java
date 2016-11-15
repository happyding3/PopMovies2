package com.example.android.popmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by happyding3 on 2016/11/14.
 */

public class MovieSyncService extends Service {
private static final Object sSyncAdapterLock=new Object();
    private static MovieSyncAdapter sMovieSyncAdapter=null;

    @Override
    public void onCreate() {

        synchronized (sSyncAdapterLock){
            if (sMovieSyncAdapter==null){
                sMovieSyncAdapter=new MovieSyncAdapter(getApplicationContext(),true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sMovieSyncAdapter.getSyncAdapterBinder();
    }
}
