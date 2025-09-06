package com.memory.wq.managers;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.PostCommentInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.utils.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CommentManager {
    public static final String TAG = "CommentManager";
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void getCommentByPostId(String token, int postId, QueryPostInfo queryPostInfo, ResultCallback<List<PostCommentInfo>> callback) {
        String json = GenerateJson.getCommentJson(postId, queryPostInfo);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.COMMENT_GET, "", json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] getCommentByPostId #30");
                    callback.onError(e.getMessage());
                    return;
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getCommentByPostId #36");
                        handler.post(() -> {
                            callback.onError(response.message());
                        });

                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
//                        int code = json.getInt("code");
//                        if (code==1){
                        List<PostCommentInfo> commentInfoList = JsonParser.commentParser(json);
                        handler.post(() -> {
                            callback.onSuccess(commentInfoList);
                        });

//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void addComment(String token, PostCommentInfo postCommentInfo, ResultCallback<Boolean> callback) {
        String json = GenerateJson.getAddCommentJson(postCommentInfo);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.COMMENT_ADD, token, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] addComment #76");
                        handler.post(()->{
                            callback.onError("");
                        });
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            handler.post(()->{
                                callback.onSuccess(true);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }
}
