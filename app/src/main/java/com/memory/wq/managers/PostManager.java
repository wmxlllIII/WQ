package com.memory.wq.managers;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.PageResult;
import com.memory.wq.utils.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PostManager {
    public static final String TAG = PostManager.class.getName();

    public static final int REQUEST_POST_IMAGE_CODE = 40;

    public void publishPost(String token, PostInfo postInfo, List<File> imageList) {
        String json = GenerateJson.getPostContentJson(postInfo);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.publishPost(AppProperties.POST_PUBLISH, token, json, imageList, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {

                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            //TODO 成功上传,保存本地
                            Log.d(TAG, "onResponse: ===发布成功");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        });
    }

    public List<File> handlePostImageResult(int requestCode, int resultCode, Intent data, Context context) {
        List<Uri> imageUriList = new ArrayList<>();
        List<File> imageFileList = new ArrayList<>();
        if (requestCode == REQUEST_POST_IMAGE_CODE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUriList.add(imageUri);
                }
            } else if (data.getData() != null) {
                Uri uri = data.getData();
                imageUriList.add(uri);
            }

            try {
                for (Uri imageUri : imageUriList) {
                    File file = new File(getPathFromUri(context, imageUri));
                    imageFileList.add(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return imageFileList;
    }

    private String getPathFromUri(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }

    public void getPosts(String token, QueryPostInfo queryPostInfo, ResultCallback<PageResult<PostInfo>> callback) {
        String json = GenerateJson.getQueryPostJson(queryPostInfo);
        ThreadPoolManager.getInstance().execute(()->{
            HttpStreamOP.postJson(AppProperties.POST_GET, token, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //TODO 解析帖子
                    if (!response.isSuccessful()){
                        Log.d(TAG,"[x] getPosts #123");
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        PageResult<PostInfo> postInfoPageResult = JsonParser.postParser(json);
                        callback.onSuccess(postInfoPageResult);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }
}
