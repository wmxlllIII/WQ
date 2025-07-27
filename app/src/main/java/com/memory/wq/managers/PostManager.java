package com.memory.wq.managers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.utils.PageResult;
import com.memory.wq.utils.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PostManager {
    public static final String TAG = PostManager.class.getName();


    public void publishPost(String token, PostInfo postInfo, List<File> imageList, ResultCallback<Boolean> callback) {
        String json = GenerateJson.getPostContentJson(postInfo);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.publishPost(AppProperties.POST_PUBLISH, token, json, imageList, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "===[x] publishPost #54");
                        callback.onError(response.message());
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            //TODO 成功上传,保存本地
                            callback.onSuccess(true);
                            Log.d(TAG, "onResponse: ===发布成功");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        });
    }

    public void getPosts(String token, QueryPostInfo queryPostInfo, ResultCallback<PageResult<PostInfo>> callback) {
        String json = GenerateJson.getQueryPostJson(queryPostInfo);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.POST_GET, token, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //TODO 解析帖子
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "===[x] getPosts #89");
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
