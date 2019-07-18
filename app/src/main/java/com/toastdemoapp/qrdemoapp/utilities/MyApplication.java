package com.toastdemoapp.qrdemoapp.utilities;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

@SuppressLint("Registered")
public class MyApplication extends Application {

    private static Context context = null;
    private static MyApplication application = null;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        application = this;
    }

    public static Context getContext() {
        return context;
    }

    static public MyApplication getApplication() {
        return application;
    }
}
