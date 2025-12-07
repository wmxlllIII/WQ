package com.memory.wq.interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

public interface JsonDataParser<T> {
    T parse(JSONArray jsonArray);
}
