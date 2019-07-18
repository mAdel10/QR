package com.toastdemoapp.qrdemoapp.utilities;

public class BaseApplication extends MyApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Engine.initialize(this);
        Engine.validateCachedData(this);
        Engine.setApplicationLanguage(this, Engine.getAppConfiguration().getLanguage());
    }
}
