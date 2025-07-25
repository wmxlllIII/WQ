package com.memory.wq.provider;


import java.io.File;
import java.util.List;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpStreamOP {


    private static final OkHttpClient client = new OkHttpClient();

    //http传统派
//    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
//        ThreadPoolManager.getInstance().execute(() -> {
//            HttpURLConnection connection = null;
//            try {
//                URL url = new URL(address);
//                connection = (HttpURLConnection) url.openConnection();
//
//                connection.setRequestMethod("POST");
//                connection.setConnectTimeout(5000);
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//
//                InputStream in = connection.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                StringBuilder response = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    response.append(line);
//                }
//                if (listener != null) {
//                    listener.onFinish(response.toString());
//                }
//
//            } catch (Exception e) {
//                listener.onError(e);
//            } finally {
//                if (connection != null)
//                    connection.disconnect();
//            }
//        });
//
//    }

    //http维新派
    private static void sendOkhttpRequest(Request request, Callback okhttpCallback) {

        client.newCall(request).enqueue(okhttpCallback);
    }

    public static void postJson(String url, String token, String body, Callback callback) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(body, JSON);
        Request request = new Request.Builder()
                .url(url)
                .header("token", token)
                .post(requestBody)
                .build();
        sendOkhttpRequest(request, callback);
    }

    public static void postJson(String url, String token, Callback callback) {
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
            builder.addFormDataPart("images",image.getName(),RequestBody.create(image,getMediaType(image)));
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
