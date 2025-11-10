package com.memory.wq.utils;

import com.memory.wq.enumertions.EventType;
import com.memory.wq.interfaces.JsonDataParser;
import com.memory.wq.utils.parsers.MsgInfoParser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ParserFactory {

    private static final Map<EventType, JsonDataParser<?>> PARSER_MAP = new HashMap<>();

    static {
        PARSER_MAP.put(EventType.EVENT_TYPE_MSG, new MsgInfoParser());
//        PARSER_MAP.put(EventType.EVENT_TYPE_SHAREMSG, new ShareMsgParser());
//        PARSER_MAP.put(EventType.EVENT_TYPE_REQUEST_FRIEND, new FriendRequestParser());
    }

    public static <T> JsonDataParser<T> getParser(EventType eventType) {
        return (JsonDataParser<T>) PARSER_MAP.getOrDefault(eventType, new DefaultParser<>());
    }

    private static class DefaultParser<T> implements JsonDataParser<T> {
        @Override
        public T parse(JSONObject dataJson) {
            return (T) dataJson;
        }
    }

}
