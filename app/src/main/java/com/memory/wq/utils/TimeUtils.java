package com.memory.wq.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class TimeUtils {
    public static long stringTime2Stamp(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
        long timestamp = dateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        return timestamp;
    }

    public static String convertTime(long timestamp) {
        long now = System.currentTimeMillis() / 1000;
        long gap = now - timestamp;
        String timeGap;
        if (gap > 24 * 60 * 60) {
            timeGap = gap / (24 * 60 * 60) + "天前";
        } else if (gap > 60 * 60) {
            timeGap = gap / (60 * 60) + "小时前";
        } else if (gap > 60) {
            timeGap = gap / 60 + "分钟前";
        } else {
            timeGap = "刚刚";
        }
        return timeGap;
    }

}
