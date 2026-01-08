package com.memory.wq.managers;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.beans.StsTokenInfo;
import com.memory.wq.constants.AppProperties;
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
    public static final String TAG = "WQ_PostManager";
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void publishPost(String token, PostInfo postInfo, List<File> imageList, ResultCallback<Boolean> callback) {
        String json = GenerateJson.getPostContentJson(postInfo);
        Log.d(TAG, "[test] publishPost #37"+json);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.publishPost(AppProperties.POST_PUBLISH, token, postInfo, imageList, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mHandler.post(() -> {
                        callback.onError(e.getMessage());
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "===[x] publishPost #54");
                        mHandler.post(() -> {
                            callback.onError(response.message());
                        });
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            //TODO 成功上传,保存本地
                            mHandler.post(() -> {
                                callback.onSuccess(true);
                            });
                            Log.d(TAG, "onResponse: ===发布成功");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
//            HttpStreamOP.postJson(AppProperties.POST_PUBLISH, json, new Callback() {
//                @Override
//                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                    mHandler.post(() -> {
//                        callback.onError(e.getMessage());
//                    });
//                }
//
//                @Override
//                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                    if (!response.isSuccessful()) {
//                        Log.d(TAG, "===[x] publishPost #54");
//                        mHandler.post(() -> {
//                            callback.onError(response.message());
//                        });
//                        return;
//                    }
//
//                    try {
//                        JSONObject json = new JSONObject(response.body().string());
//                        int code = json.getInt("code");
//                        if (code == 1) {
//                            //TODO 成功上传,保存本地
//                            mHandler.post(() -> {
//                                callback.onSuccess(true);
//                            });
//                            Log.d(TAG, "onResponse: ===发布成功");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
            });
        });
    }

    public void getPosts(QueryPostInfo queryPostInfo, ResultCallback<PageResult<PostInfo>> callback) {
        String json = GenerateJson.getQueryPostJson(queryPostInfo);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.POST_GET, json, new Callback() {
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
                        mHandler.post(() -> {
                            callback.onSuccess(postInfoPageResult);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void getMyPost(String token, QueryPostInfo queryPostInfo, ResultCallback<PageResult<PostInfo>> callback) {
        String json = GenerateJson.getMyPostJson(queryPostInfo);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.POST_MY_GET, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mHandler.post(() -> {

                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "===[x] getPosts #89");
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        PageResult<PostInfo> postInfoPageResult = JsonParser.postParser(json);
                        mHandler.post(() -> {
                            callback.onSuccess(postInfoPageResult);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void getUpLoadPermission(String token, ResultCallback<StsTokenInfo> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.STS_TOKEN, "{}", new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mHandler.post(() -> callback.onError("获取上传权限失败"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            mHandler.post(() -> callback.onSuccess(JsonParser.stsTokenParser(json)));
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "[x] getUpLoadPermission #157" + e.getMessage());
                    }
                }
            });
        });
    }
}
