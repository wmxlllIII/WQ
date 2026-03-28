package com.memory.wq.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public class TimeUtils {
    public static long stringTime2Stamp(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
        long timestamp = dateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        return timestamp;
    }

    public static String convertToTextTime(long timestamp) {
        long now = System.currentTimeMillis() / 1000;
        long gap = now - timestamp;
        if (gap > 24 * 60 * 60) {
            return gap / (24 * 60 * 60) + "天前";
        } else if (gap > 60 * 60) {
            return gap / (60 * 60) + "小时前";
        } else if (gap > 60) {
            return gap / 60 + "分钟前";
        } else {
            return "刚刚";
        }
    }

    public static String convertToNumberTime(long timestamp) {
        long timestampInMillis = timestamp * 1000;

        Instant instant = Instant.ofEpochMilli(timestampInMillis);

        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm")
                .withZone(zoneId);

        return formatter.format(instant);
    }

    public static String formatSecond(int seconds) {
        if (seconds < 0) {
            return "00:00";
        }

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
        } else {
            return String.format("%02d:%02d", minutes, remainingSeconds);
        }
    }
}
