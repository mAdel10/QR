package com.toastdemoapp.qrdemoapp.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class LocaleManager {

    private static final String TAG = "LocaleManager";

    private Context ctx;
    private Resources res;
    private SharedPreferences settingPrefs;

    public LocaleManager(Context ctx) {
        this.ctx = ctx;
        this.res = ctx.getResources();
    }

    /**
     * Use this method to store the app language with other preferences.
     * This makes it possible to use the language set before, at any time, whenever
     * the app will started.
     */
    public void restoreAppLanguage() {
        Log.v(TAG, "restoreAppLanguage");
        settingPrefs = ctx.getSharedPreferences("ConfigData", MODE_PRIVATE);
        String lang = settingPrefs.getString("AppLanguage", "");
        if (!settingPrefs.getAll().isEmpty() && lang.length() == 2) {
            Locale myLocale;
            myLocale = new Locale(lang);
            Locale.setDefault(myLocale);
            Configuration config = new Configuration();
            config.locale = myLocale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
    }

    /**
     * Store app language on demand.
     */
    public void storeAppLanguage(String lang) {
        Log.v(TAG, "storeAppLanguage -> " + lang);
        settingPrefs = ctx.getSharedPreferences("ConfigData", MODE_PRIVATE);
        SharedPreferences.Editor ed = settingPrefs.edit();
        Locale myLocale;
        myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        res.updateConfiguration(config, res.getDisplayMetrics());
        ed.putString("AppLanguage", lang);
        ed.apply();
    }

    /**
     * Use this method together with getAppLanguage() to set and then restore
     * language, where ever you need, for example, the specifically localized
     * resources.
     */
    public void setAppLanguage(String lang) {
        Log.v(TAG, "setAppLanguage -> " + lang);
        Locale myLocale;
        myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    /**
     * Use this method to obtain the current app language name.
     */
    public String getAppLanguage() {
        String APP_LANG;
        settingPrefs = ctx.getSharedPreferences("ConfigData", MODE_PRIVATE);
        String lang = settingPrefs.getString("AppLanguage", "");
        if (!settingPrefs.getAll().isEmpty() && lang.length() == 2) {
            APP_LANG = lang;
        } else {
            APP_LANG = Locale.getDefault().getLanguage();
        }
        Log.v(TAG, "getAppLanguage = " + APP_LANG);
        return APP_LANG;
    }

    /**
     * Use this method to store locale of app to the stored language.
     */
    public void setStoredLanguage() {
        Log.v(TAG, "setStoredLanguage");
        Locale locale = new Locale(getAppLanguage());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        ctx.getResources().updateConfiguration(config, null);
    }
}