package com.memory.wq.utils.parsers;


import android.util.Log;

import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.interfaces.JsonDataParser;
import com.memory.wq.utils.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestParser implements JsonDataParser<List<FriendRelaInfo>> {
    private static final String TAG = "WQ_FriendRequestParser";

    @Override
    public List<FriendRelaInfo> parse(JSONArray dataJson) {
        return JsonParser.friendRelaParser(dataJson);
    }
}
