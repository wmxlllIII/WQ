package com.memory.wq.enumertions;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
    EVENT_TYPE_REQUEST_FRIEND("FRIEND_REQUEST"),
    EVENT_TYPE_MSG("MESSAGE"),
    EVENT_TYPE_SHAREMSG("SHARE"),
    UNKNOWN("UNKNOWN");
    private String type;
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
        if (text == null)
            return UNKNOWN;
        return LOOKUP.getOrDefault(text.trim().toLowerCase(), UNKNOWN);
    }

}
