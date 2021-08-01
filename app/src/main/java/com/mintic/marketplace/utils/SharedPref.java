package com.mintic.marketplace.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by Iván González on 1/08/21.
 */

public class SharedPref {
    private static final String PREF_APP = "pref_app";

    private SharedPref() {
        throw new UnsupportedOperationException(
                "Should not create instance of SharedPref class. Please use as static.");
    }

    static public boolean getBoolean(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getBoolean(key, false);
    }

    static public int getInt(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getInt(key, 0);
    }

    static public String getString(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getString(key, "");
    }

    static public Map<String, ?> getAll(Context context) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getAll();
    }

    static public void save(Context context, String key, String val) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().putString(key, val).apply();
    }

    static public void save(Context context, String key, int val) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().putInt(key, val).apply();
    }

    static public void save(Context context, String key, boolean val) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, val)
                .apply();
    }

    static public void save(SharedPreferences.Editor editor) {
        editor.apply();
    }

    static public void clear(Context context) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().clear().apply();
    }

}
