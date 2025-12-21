package com.memory.wq.managers;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.RtcInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.thread.ThreadPoolManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TokenManager {
    public static final String TAG = "WQ_Agora_TokenManager";
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void getToken(String userId, String token, String roomId, int role, ResultCallback<RtcInfo> callback) {
        //TODO 用户如果不是邮箱登录呢
        String json = GenerateJson.getRtcToken(roomId, role, userId);

        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.AGORA_TOKEN, json, new Callback() {
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
                        Log.d(TAG, "onResponse: ======声网token:" + json.toString());
                        if (code == 1) {
                            JSONObject data = json.getJSONObject("data");
                            RtcInfo rtcInfo = JsonParser.rtcTokenParser(data);
                            mHandler.post(()->callback.onSuccess(rtcInfo));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }
}
