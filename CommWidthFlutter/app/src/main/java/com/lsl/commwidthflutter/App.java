package com.lsl.commwidthflutter;

import android.app.Application;

import io.flutter.facade.Flutter;

/**
 * Created by liusilong on 2019/1/18.
 * version:1.0
 * Describe:
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Flutter.startInitialization(this);
    }
}
