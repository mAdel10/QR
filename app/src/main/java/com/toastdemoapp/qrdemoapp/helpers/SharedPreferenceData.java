package com.toastdemoapp.qrdemoapp.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import  com.toastdemoapp.qrdemoapp.utilities.Utilities;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceData {

    private static final String PREF_FILE_NAME = "BasePrefFileName";
    public Context context;
    private String PrefName = PREF_FILE_NAME;

    public SharedPreferenceData(Context context) {
        this.context = context;
    }

    /**
     * Save Object in Memory
     *
     * @param key          String
     * @param value        Object
     * @param prefFileName String
     */
    @SuppressLint("ApplySharedPref")
    public void save(String key, Object value, String prefFileName) {

        if (Utilities.isNullString(prefFileName)) prefFileName = PrefName;

        SharedPreferences settings = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        if (value instanceof String)
            editor.putString(key, value.toString());
        else if (value instanceof Integer)
            editor.putInt(key, Integer.valueOf(value.toString()));
        else if (value instanceof Boolean)
            editor.putBoolean(key, Boolean.valueOf(value.toString()));
        else if (value instanceof Long)
            editor.putLong(key, Long.valueOf(value.toString()));
        editor.commit();
    }

    /**
     * Save Object in Memory in file "BasePrefFileName"
     *
     * @param key   String
     * @param value Object
     */
    public void save(String key, Object value) {
        save(key, value, PrefName);
    }

    /**
     * Get Object from Shared data
     *
     * @param key          String
     * @param objectClass  Class
     * @param prefFileName String
     * @return Object
     */
    public Object get(String key, Class objectClass, String prefFileName) {
        if (Utilities.isNullString(prefFileName)) prefFileName = PrefName;

        Object value = null;

        synchronized (this) {
            try {
                SharedPreferences settings = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
                if (objectClass == String.class)
                    value = settings.getString(key, "null");
                else if (objectClass == Integer.class)
                    value = settings.getInt(key, -1);
                else if (objectClass == Boolean.class)
                    value = settings.getBoolean(key, false);
                else if (objectClass == Long.class)
                    value = settings.getLong(key, 0);
                // settings = null;
            } catch (Exception | Error e) {
                e.printStackTrace();
            }
        }

        System.gc();
        return value;
    }

    /**
     * Get object in "BasePrefFileName"
     *
     * @param key         String
     * @param objectClass Class
     * @return Object
     */
    public Object get(String key, Class objectClass) {
        return get(key, objectClass, PrefName);
    }

    /**
     * Change Sign Header State [true to appear, false to not appear]
     * -- Perform state to be true in SplashActivity finish to appear again when enter the app.
     *
     * @param state boolean
     */
    public void changeSignHeaderState(boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences("HEADER_DATA", MODE_PRIVATE).edit();
        editor.putBoolean("state", state);
        editor.apply();
    }

    /**
     * Get Sign Header State true to appear, false to not appear.
     *
     * @return boolean
     */
    public boolean getSignHeaderState() {
        SharedPreferences prefs = context.getSharedPreferences("HEADER_DATA", MODE_PRIVATE);
        return prefs.getBoolean("state", true);
    }
}
