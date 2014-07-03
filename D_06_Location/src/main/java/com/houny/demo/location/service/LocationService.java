package com.houny.demo.location.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Houny Chang on 2014/5/23.
 */
public class LocationService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
