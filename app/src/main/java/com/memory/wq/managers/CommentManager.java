package com.memory.wq.managers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.PostCommentInfo;
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

    public void getCommentByPostId(String token, int postId, ResultCallback<List<PostCommentInfo>> callback) {
        String json = GenerateJson.getCommentJson(postId);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.COMMENT_GET, token, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG,"[x] getCommentByPostId #30");
                    callback.onError(e.getMessage());
                    return;
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG,"[x] getCommentByPostId #36");
                        callback.onError(response.message());
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        JsonParser.commentParser(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }
}
