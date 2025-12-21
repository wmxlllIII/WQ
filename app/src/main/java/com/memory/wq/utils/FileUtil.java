package com.memory.wq.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.memory.wq.thread.ThreadPoolManager;

import java.io.File;
import java.io.FileOutputStream;
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

//    public static void saveImageToGallery(@NonNull Context context,
//                                          @NonNull Bitmap bitmap,
//                                          @NonNull ResultCallback callback) {
//        ThreadPoolManager.getInstance().execute(() -> {
//            boolean success = false;
//            String errorMessage = null;
//
//            try {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    // Android 10+ 使用 MediaStore
//                    success = saveImageUsingMediaStore(context, bitmap);
//                } else {
//                    // Android 9 及以下使用文件系统
//                    success = saveImageUsingFile(context, bitmap);
//                }
//            } catch (Exception e) {
//                errorMessage = e.getMessage();
//                e.printStackTrace();
//            }
//
//            final boolean finalSuccess = success;
//            final String finalError = errorMessage;
//            new Handler(Looper.getMainLooper()).post(()->{
//                callback.onSuccess();
//            });
//            // 切回主线程回调
//            ContextCompat.getMainExecutor(context).execute(() ->
//                    callback.onResult(finalSuccess, finalError)
//            );
//        });
//
//    }
//
//    private static boolean saveImageUsingFile(Context context, Bitmap bitmap) {
//        try {
//            String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/WQ/";
//            File dir = new File(imagesDir);
//            if (!dir.exists()){
//                dir.mkdirs();
//            }
//
//            File imageFile = new File(dir, fileName);
//            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                fos.flush();
//            }
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//
//    }
//
//    private static boolean saveImageUsingMediaStore(Context context, Bitmap bitmap) {
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
//        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/WQ");
//
//        ContentResolver resolver = context.getContentResolver();
//        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//        if (uri == null) return false;
//
//        try (OutputStream out = resolver.openOutputStream(uri)) {
//            return bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//        } catch (Exception e) {
//            // 插入失败时可尝试删除残留条目
//            resolver.delete(uri, null, null);
//            return false;
//        }
//    }
}
