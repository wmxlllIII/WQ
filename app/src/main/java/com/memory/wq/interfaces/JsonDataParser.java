package com.memory.wq.interfaces;

import org.json.JSONObject;

public interface JsonDataParser<T> {
    T parse(JSONObject dataJson);
}
