package com.memory.wq.provider;


import android.util.Log;

import com.memory.wq.managers.AccountManager;

import java.io.File;
import java.util.List;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpStreamOP {

    private static final String TAG = "WQ_HttpStreamOP";

    private static final OkHttpClient client = new OkHttpClient();

    private static void sendOkhttpRequest(Request request, Callback okhttpCallback) {

        client.newCall(request).enqueue(okhttpCallback);
    }

    public static void postJson(String url, String body, Callback callback) {
        String token = AccountManager.getUserInfo().getToken();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(body, JSON);
        Request request = new Request.Builder()
                .url(url)
                .header("token", token == null ? "" : token)
                .post(requestBody)
                .build();
        Log.d(TAG, "[TOKEN] postJson: " + token);
        sendOkhttpRequest(request, callback);
    }

    public static void postJson(String url, Callback callback) {
        String token = AccountManager.getUserInfo().getToken();
        Request request = new Request.Builder()
                .url(url)
                .header("token", token)
                .build();
        sendOkhttpRequest(request, callback);
    }


    public static void postFile(String url, String token, File file, Callback callback) {

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(file, getMediaType(file)))
                .build();


        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .post(body)
                .build();

        sendOkhttpRequest(request, callback);
    }

    public static void publishPost(String url, String token, String json, List<File> imagesList, Callback callback) {

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("content", json);

        for (File image : imagesList) {
            builder.addFormDataPart("images", image.getName(), RequestBody.create(image, getMediaType(image)));
        }


        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .post(builder.build())
                .build();

        sendOkhttpRequest(request, callback);
    }


    private static MediaType getMediaType(File file) {
        String mimeType = "application/octet-stream";
        String fileName = file.getName();

        if (fileName.endsWith(".png")) {
            mimeType = "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            mimeType = "image/jpeg";
        } else if (fileName.endsWith(".pdf")) {
            mimeType = "application/pdf";
        }

        return MediaType.parse(mimeType);
    }


}
