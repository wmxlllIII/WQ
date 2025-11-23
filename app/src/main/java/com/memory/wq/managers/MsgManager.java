package com.memory.wq.managers;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.provider.FriendSqlOP;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.provider.MsgSqlOP;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.thread.ThreadPoolManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MsgManager {

    public static final String TAG = "WQ_MsgManager";
    private final Handler mHandler=new Handler(Looper.getMainLooper());
    private Context context;
    public MsgManager(Context context) {
        this.context = context;
    }

    public void getAllRelation(Context context, boolean isForceRefresh, String url, String token, ResultCallback<List<FriendRelaInfo>> callback) {

        List<FriendRelaInfo> allRelationFromDB = getAllRelationFromDB(context);
        callback.onSuccess(allRelationFromDB);

        if (shouldFetchFromServer(isForceRefresh)) {

            getAllRelationFromServer(url, token, new ResultCallback<List<FriendRelaInfo>>() {
                @Override
                public void onSuccess(List<FriendRelaInfo> allRelationFromServer) {
                    List<FriendRelaInfo> friendRelaList = mergeData(allRelationFromDB, allRelationFromServer);
                    saveFriendRelaToDB(friendRelaList);
                    mHandler.post(() -> {
                        callback.onSuccess(friendRelaList);
                    });
                }

                @Override
                public void onError(String err) {

                }
            });

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putLong("last_sync_time", System.currentTimeMillis()).commit();
        }

    }

    private void saveFriendRelaToDB(List<FriendRelaInfo> friendRelaList) {
        FriendSqlOP op = new FriendSqlOP(context);
        int count = 0;
        while (!op.insertRelations(friendRelaList)) {
            if (++count == 3)
                break;
            System.out.println("==========================saveFriendRelaToDB======失败");
            //失败
        }
        if (count != 3) ;
        System.out.println("==========================saveFriendRelaToDB======成功");
    }

    private List<FriendRelaInfo> mergeData(List<FriendRelaInfo> local, List<FriendRelaInfo> remote) {
        Map<String, FriendRelaInfo> mergedMap = new HashMap<>();

        for (FriendRelaInfo remoteItem : remote) {
            mergedMap.put(remoteItem.getId() + "", remoteItem);
        }

        for (FriendRelaInfo localItem : local) {
            String serverId = localItem.getId() + "";
            //本地数据id存在于远程数据中   比较时间戳
            if (!mergedMap.containsKey(serverId)) {
                continue;
            }
            //如果远程时间戳大了,更新
            FriendRelaInfo remoteItem = mergedMap.get(serverId);
            Log.d(TAG, "mergeData: ========remoteItem.getUpdateAt() > localItem.getUpdateAt()" + remoteItem.getUpdateAt() + "===" + localItem.getUpdateAt());

            if (remoteItem.getUpdateAt() > localItem.getUpdateAt()) {
                mergedMap.put(serverId, remoteItem);
                Log.d(TAG, "mergeData: ===更新了" + remoteItem.toString());
                //本地不可能大,只能等于
            }


        }

        return new ArrayList<>(mergedMap.values());
    }


    private static List<FriendRelaInfo> getAllRelationFromDB(Context context) {
        return new FriendSqlOP(context).queryAllRelations();
    }

    private boolean shouldFetchFromServer(boolean forceRefresh) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        return isConnected && (forceRefresh || isLocalDataExpired());
    }


    private static void getAllRelationFromServer(String url, String token, ResultCallback<List<FriendRelaInfo>> callback) {
        HttpStreamOP.postJson(url, token, "{}", new Callback() {
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
                    //parseJson-->List
                    ResponseBody body = response.body();
                    JSONObject json = new JSONObject(body.string());
                    Log.d(TAG, "onResponse:====code " + json.getInt("code"));
                    if (json.getInt("code") == 1) {
                        JSONArray requestList = json.getJSONArray("data");
                        List<FriendRelaInfo> friendRelaList = JsonParser.friendRelaParser(requestList);
                        callback.onSuccess(friendRelaList);
                    } else
                        Log.d(TAG, "onResponse: ====返回码不是1");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private boolean isLocalDataExpired() {
        long lastSyncTime = PreferenceManager.getDefaultSharedPreferences(context).getLong("last_sync_time", 0);
        return System.currentTimeMillis() - lastSyncTime > 5 * 60 * 1000;
    }

    public  void receiveFriendRela(Context context, JSONArray requestList) {
        List<FriendRelaInfo> friendRelList = JsonParser.friendRelaParser(requestList);
        ThreadPoolManager.getInstance().execute(() -> {
            FriendSqlOP op = new FriendSqlOP(context);
            op.insertRelations(friendRelList);
        });
    }

    public void updateRela(Context context, boolean isAgree, String sourceEmail, ResultCallback<Boolean> callback) {
        String json = GenerateJson.getUpdateRelaJson(sourceEmail, isAgree);
        String token = context.getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE).getString("token", "");
        if (token != null && !TextUtils.isEmpty(token))
            ThreadPoolManager.getInstance().execute(() -> {
                HttpStreamOP.postJson(AppProperties.FRIEND_RES, token, json, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        callback.onError(e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            callback.onError("=====错误码" + response.code());
                            return;
                        }
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            int code = json.getInt("code");

                            Log.d(TAG, "onResponse: ======" + code);
                            if (code == 1) {
                                mHandler.post(() -> {
                                    callback.onSuccess(true);
                                });

                            } else {
                                mHandler.post(() -> {
                                    String msg = json.optString("msg");
                                    callback.onError("======MsgManager====onRes错误" + msg);
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            });
    }

    public static void receiveMsg(Context context, JSONArray msgList) {
        List<MsgInfo> msgInfoList = JsonParser.msgParser(msgList);
        //TODO 持久化
        MsgSqlOP sqlOP = new MsgSqlOP(context);
        boolean b = sqlOP.insertMessages(msgInfoList);

    }
    public static void receiveShareMsg(Context context, JSONArray shareMsgList) {
        List<MsgInfo> msgInfoList = JsonParser.shareMsgParser(shareMsgList);
        MsgSqlOP sqlOP = new MsgSqlOP(context);
        boolean b = sqlOP.insertMessages(msgInfoList);

    }

    public void getAllMsg(String targetEmail, ResultCallback<List<MsgInfo>> callback) {
        SharedPreferences sp = context.getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        String currentEmail = sp.getString("email", "");
        MsgSqlOP msgSqlOP = new MsgSqlOP(context);
        List<MsgInfo> messages = msgSqlOP.queryAllMsg(currentEmail, targetEmail);
        callback.onSuccess(messages);

    }

    public void sendMsg(String token,String senderEmail, String targetEmail, String content,ResultCallback<Boolean> callback) {
        String json = GenerateJson.getMsgJson(targetEmail, content);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.SEND_MSG, token, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject jsonResponse = new JSONObject(response.body().string());
                            if (jsonResponse.getInt("code") == 1) {
                                // 插入数据库成功后触发回调
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    MsgInfo msgInfo = new MsgInfo();
                                    msgInfo.setSenderEmail(senderEmail);
                                    msgInfo.setReceiverEmail(targetEmail);
                                    msgInfo.setContent(content);
                                    boolean insertSuccess = new MsgSqlOP(context).insertMessages(Collections.singletonList(msgInfo));
                                    callback.onSuccess(insertSuccess);
                                });
                            }
                        }
                    } catch (Exception e) {
                        callback.onError(e.getMessage());
                    }
                }
            });
        });
    }

    public void saveMsg(){

    }

}
