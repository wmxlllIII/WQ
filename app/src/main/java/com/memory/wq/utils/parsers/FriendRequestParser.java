package com.memory.wq.utils.parsers;


import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.interfaces.JsonDataParser;
import com.memory.wq.utils.JsonParser;

import org.json.JSONArray;

import java.util.List;

public class FriendRequestParser implements JsonDataParser<List<FriendRelaInfo>> {
    private static final String TAG = "WQ_FriendRequestParser";

    @Override
    public List<FriendRelaInfo> parse(JSONArray dataJson) {
        return JsonParser.friReqParser(dataJson);
    }
}
