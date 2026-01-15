package com.memory.wq.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.memory.wq.thread.ThreadPoolManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
        return "WQ_" + uuid + extension;
    }

    public static boolean saveBitmapToGallery(
            Context context,
            Bitmap bitmap,
            String fileName
    ) {
        OutputStream os = null;
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/WQ"
            );
            values.put(MediaStore.Images.Media.IS_PENDING, 1);

            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
            );

            if (uri == null) {
                return false;
            }

            os = resolver.openOutputStream(uri);
            if (os == null) {
                return false;
            }

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();

            // 写入完成，解除 pending
            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(uri, values, null, null);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception ignored) {
                }
            }
        }
    }


}
