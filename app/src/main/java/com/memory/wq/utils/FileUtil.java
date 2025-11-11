package com.memory.wq.utils;

import android.text.TextUtils;
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
        return "WQ_" + uuid + "." + extension;
    }

    public static String tagConvertUrl(String fileName, String bucketName, String endPoint) {
        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "[x] tagConvertUrl #31");
            return null;
        }

        return "https://" + bucketName + "." + endPoint + "/" + fileName;
    }

}
