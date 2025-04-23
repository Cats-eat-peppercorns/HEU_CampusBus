package com.example.myapplication.Activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LinkStatusCheck extends Service {
    public LinkStatusCheck() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}