package com.memory.wq.caches;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Base64;

import androidx.annotation.NonNull;

import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.thread.ThreadPoolManager;

import java.io.BufferedOutputStream;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

 class ImageCacheOP {

    private File diskCacheDir;


    private void cleanOldAvatar() {
        if (diskCacheDir.exists() && diskCacheDir.isDirectory()) {
            File[] cachedFiles = diskCacheDir.listFiles();
            if (cachedFiles.length == 0)
                return;
            for (File f : cachedFiles) {
                if (f.exists() && f.isFile()) {
                    f.delete();
                }
            }
        }
    }

    private void cacheBitmapToDisk(Bitmap bitmap, File file) {

        if (bitmap == null || file == null)
            return;
        ThreadPoolManager.getInstance().execute(() -> {
            FileOutputStream fos = null;
            BufferedOutputStream outputStream = null;
            try {
                fos = new FileOutputStream(file);
                outputStream = new BufferedOutputStream(fos, 1024);//内存不需要关流
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    outputStream.flush();
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void getBitmapFromUrl(Context context,String url, String token, ResultCallback<Bitmap> callback) {
        diskCacheDir = new File(context.getCacheDir(), "avatars");
        if (!diskCacheDir.exists() || !diskCacheDir.isDirectory()){
            diskCacheDir.mkdirs();
        }
        File file = new File(this.diskCacheDir.getPath(), Base64.encodeToString(url.getBytes(), Base64.DEFAULT));
        if (file.exists() && file.length() > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null)
                callback.onSuccess(bitmap);
            return;
        }

        HttpStreamOP.postJson(url, token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Request failed: " + response.code());
                    return;
                }
                try (InputStream inputStream = response.body().byteStream()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap == null) {
                        callback.onError("Failed to decode bitmap");
                        return;
                    }

                    cacheBitmapToDisk(bitmap, file);
                    callback.onSuccess(bitmap);
                } catch (Exception e) {
                    callback.onError(e.getMessage());
                }
            }
        });


    }

}
