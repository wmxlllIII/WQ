package com.memory.wq.sp;

import android.content.Context;
import android.content.SharedPreferences;

public class SpHelper {
    private final SharedPreferences sharedPreferences;

    public SpHelper(Context context, String name) {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void saveLong(String key, Long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public Long getLong(String key, Long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void saveString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void saveBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void removeKey(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }
}
