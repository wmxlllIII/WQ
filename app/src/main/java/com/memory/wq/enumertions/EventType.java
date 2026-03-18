package com.memory.wq.enumertions;

import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
    EVENT_TYPE_REQUEST_FRIEND("EVENT_TYPE_REQUEST_FRIEND"),
    EVENT_TYPE_MSG("EVENT_TYPE_MSG"),
    EVENT_TYPE_SHAREMSG("EVENT_TYPE_SHAREMSG"),
    UNKNOWN("UNKNOWN");
    private String type;
    private static final String TAG = "WQ_EventType";
    private static final Map<String, EventType> LOOKUP = new HashMap<>();

    public String getType() {
        return type;
    }

    EventType(String type) {
        this.type = type;
    }

    static {
        for (EventType type : values()) {
            LOOKUP.put(type.type.toLowerCase(), type);
        }
    }


    public static EventType fromString(String text) {
        if (TextUtils.isEmpty(text)) {
            Log.d(TAG, "[X] fromString #34");
            return UNKNOWN;
        }

        return LOOKUP.getOrDefault(text.trim().toLowerCase(), UNKNOWN);
    }

}
