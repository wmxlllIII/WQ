package com.memory.wq.managers;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.db.op.FriendSqlOP;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.db.op.MsgSqlOP;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.utils.ResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MsgManager {

    public static final String TAG = "WQ_MsgManager";
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final MsgSqlOP mMsgSqlOP = new MsgSqlOP();
    private final FriendSqlOP mFriendSqlOP = new FriendSqlOP();

    public void getRelation(ResultCallback<List<FriendRelaInfo>> callback) {
        List<FriendRelaInfo> friendRelaInfoList =mFriendSqlOP.queryAllRelations();
        mHandler.post(() -> callback.onSuccess(friendRelaInfoList));
    }

    private static void getRelationFromServer(String url, ResultCallback<List<FriendRelaInfo>> callback) {
        HttpStreamOP.postJson(url, "{}", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (!response.isSuccessful() || response.body() == null) {
                    Log.d(TAG, "onResponse:====回复失败 " + response.body());
                    return;
                }
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.getInt("code") == 1) {
                        JSONArray requestList = json.getJSONArray("data");
                        List<FriendRelaInfo> friendRelaList = JsonParser.friReqParser(requestList);
                        callback.onSuccess(friendRelaList);
                    } else {
                        Log.d(TAG, "onResponse: ====返回码不是1");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void updateRela(long sourceUuNumber, boolean isAgree, ResultCallback<Boolean> callback) {
        String json = GenerateJson.getUpdateRelaJson(sourceUuNumber, isAgree);
        Log.d(TAG, "updateRela: is"+isAgree);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.FRIEND_RES, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] updateRela #82 " + e.getMessage());
                    mHandler.post(() -> callback.onError("网络异常"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] updateRela #165 " + response.code());
                        mHandler.post(() -> callback.onError("更新好友信息失败"));
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");

                        Log.d(TAG, "[✓] updateRela #96" + code);
                        Log.d(TAG, "[✓] updateRela #97" + json);
                        if (code == 1) {
                            JSONObject data = json.getJSONObject("data");
                            JSONObject friRelaJson = data.getJSONObject("friendRelationship");
                            FriendRelaInfo friendRela = JsonParser.friRelaParser(friRelaJson);
                            if (friendRela.getState().equals("accepted")) {
                                JSONObject userJson = data.getJSONObject("user");
                                List<FriendInfo> friendInfoList = JsonParser.friParser(userJson);
                                mFriendSqlOP.insertFriends(friendInfoList);
                            }

                            List<FriendRelaInfo> friendRelaList = new ArrayList<>();
                            friendRelaList.add(friendRela);
                            mFriendSqlOP.updateRelations(friendRelaList);
                            mHandler.post(() -> callback.onSuccess(true));

                        } else {
                            Log.d(TAG, "[x] updateRela #181");
                            mHandler.post(() -> callback.onError("更新好友信息失败"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void sendMsg(long targetUuNumber, String content, ResultCallback<List<MsgInfo>> callback) {
        String json = GenerateJson.getSendMsgJson(targetUuNumber, content,-1);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.SEND_MSG, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] sendMsg #290" + e.getMessage());
                    mHandler.post(() -> callback.onError("网络异常"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] sendMsg #254 " + response.code());
                        mHandler.post(() -> callback.onError("网络异常"));
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        Log.d(TAG, "[✓] sendMsg json"+json);
                        if (json.getInt("code") == 1) {
                            List<MsgInfo> msgList = JsonParser.msgParser(json.getJSONArray("data"));
                            mMsgSqlOP.insertMessages(msgList);
                            mHandler.post(() -> callback.onSuccess(msgList));
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "[x] sendMsg #310" + e.getMessage());
                        mHandler.post(() -> callback.onError("网络异常"));
                    }
                }
            });
        });
    }

    public void deleteMsg(int msgId, ResultCallback<Boolean> booleanResultCallback) {
        ThreadPoolManager.getInstance().execute(() -> {

        });
    }
}
