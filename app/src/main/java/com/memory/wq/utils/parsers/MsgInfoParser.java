package com.memory.wq.utils.parsers;

import android.util.Log;

import com.memory.wq.beans.MsgInfo;
import com.memory.wq.interfaces.JsonDataParser;
import com.memory.wq.utils.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MsgInfoParser implements JsonDataParser<List<MsgInfo>> {
    private static final String TAG = "WQ_MsgInfoParser";

    @Override
    public List<MsgInfo> parse(JSONObject dataJson) {
        try {
            return JsonParser.msgParser(dataJson.getJSONArray("msg_list"));
        } catch (Exception e) {
            Log.d(TAG, "[X] MsgInfoParser e" + e.getMessage());
            return new ArrayList<>();
        }
    }
}
