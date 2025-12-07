package com.memory.wq.provider;

import android.app.Application;

public class WqApplication extends Application {
    private static WqApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    public static WqApplication getInstance() {
        return instance;
    }
}
