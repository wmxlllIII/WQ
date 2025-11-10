package com.memory.wq.utils.parsers;

import com.memory.wq.beans.MsgInfo;
import com.memory.wq.interfaces.JsonDataParser;
import com.memory.wq.utils.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MsgInfoParser implements JsonDataParser<List<MsgInfo>> {
    @Override
    public List<MsgInfo> parse(JSONObject dataJson) {
        try {
            JSONArray msgArray = dataJson.getJSONArray("msg_list");
            return JsonParser.msgParser(msgArray);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
