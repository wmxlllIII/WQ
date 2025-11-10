package com.memory.wq.utils;

import android.util.Log;

import java.util.UUID;

public class FileUtil {

    public static final String TAG = "WQ_FileUtil";

    public static String generateUniqueObjectKey(String originalFileName) {
        if (originalFileName == null) {
            Log.d(TAG, "[x] generateUniqueObjectKey #11");
            return "";
        }

        String extension = "";
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFileName.substring(lastDotIndex);
        }

        String uuid = UUID.randomUUID().toString();
        String name = "WQ_" + uuid + "." + extension;
        return name;
    }

}
