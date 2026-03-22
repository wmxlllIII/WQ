package com.memory.wq.utils.parsers;

import com.memory.wq.beans.GroupDetailInfo;
import com.memory.wq.interfaces.JsonDataParser;

import org.json.JSONArray;

import java.util.Collections;
import java.util.List;

public class GroupDetailParser implements JsonDataParser<List<GroupDetailInfo>> {
    @Override
    public List<GroupDetailInfo> parse(JSONArray jsonArray) {
        return Collections.emptyList();
    }
}
