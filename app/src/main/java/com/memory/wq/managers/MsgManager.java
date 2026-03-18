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
import com.memory.wq.enumertions.ChatType;
import com.memory.wq.enumertions.ContentType;
import com.memory.wq.enumertions.FriendRelaStatus;
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
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MsgManager {

    public static final String TAG = "WQ_MsgManager";
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final UserManager mUserManager = new UserManager();
    private final MsgSqlOP mMsgSqlOP = new MsgSqlOP();
    private final FriendSqlOP mFriendSqlOP = new FriendSqlOP();

    public void getRelation(ResultCallback<List<FriendRelaInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            List<FriendRelaInfo> friendRelaInfoList = mFriendSqlOP.queryAllRelations(AccountManager.getUserId());

            if (friendRelaInfoList == null) {
                return;
            }
            friendRelaInfoList.forEach(friendRelaInfo -> {
                boolean isSender = friendRelaInfo.getSenderId() == AccountManager.getUserId();

                long targetId = isSender ? friendRelaInfo.getReceiverId() : friendRelaInfo.getSenderId();


                mUserManager.getUserById(targetId, new ResultCallback<FriendInfo>() {
                    @Override
                    public void onSuccess(FriendInfo result) {
                        if (isSender) {
                            friendRelaInfo.setReceiverName(result.getNickname());
                            friendRelaInfo.setReceiverAvatar(result.getAvatarUrl());
                        } else {
                            friendRelaInfo.setSenderName(result.getNickname());
                            friendRelaInfo.setReceiverAvatar(result.getAvatarUrl());
                        }
                    }

                    @Override
                    public void onError(String err) {
                        Log.d(TAG, "[x] getRelation #68");
                    }
                });
            });

            mHandler.post(() -> callback.onSuccess(friendRelaInfoList));
        });

    }

    public void updateRela(long sourceId, boolean isAgree, String validMsg, ResultCallback<Boolean> callback) {
        String json = GenerateJson.getUpdateRelaJson(sourceId, isAgree, validMsg);
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
                        Log.d(TAG, "[x] updateRela #91 " + response.code());
                        mHandler.post(() -> callback.onError("更新好友信息失败"));
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");

                        Log.d(TAG, "[✓] updateRela #97" + json);
                        if (code == 1) {
                            mHandler.post(() -> callback.onSuccess(true));

                        } else {
                            Log.d(TAG, "[x] updateRela #117");
                            mHandler.post(() -> callback.onError("更新好友信息失败"));
                        }

                    } catch (JSONException e) {
                        Log.d(TAG, "[x] updateRela #122" + e.getMessage());
                        mHandler.post(() -> callback.onError("更新好友信息失败"));
                    }
                }
            });
        });
    }

    public void sendMsg(long chatId, ChatType chatType, String content, ContentType msgType, ResultCallback<Boolean> callback) {
        String json = GenerateJson.getSendMsgJson(chatId, chatType, content, msgType);
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
                        Log.d(TAG, "[✓] sendMsg json" + json);
                        if (json.getInt("code") == 1) {
                            mHandler.post(() -> callback.onSuccess(true));
                        } else {
                            mHandler.post(() -> callback.onSuccess(false));
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

    public void buildGroup(String groupName, String groupAvatar, Set<Long> selectedUsers) {
        if (selectedUsers == null || selectedUsers.isEmpty()) {
            Log.d(TAG, "[x] buildGroupOrChat: #159");
            return;
        }
        if (selectedUsers.size() < 3) {
            Log.d(TAG, "[x] buildGroupOrChat: #164");
            return;
        }

        String json = GenerateJson.getBuildGroupJson(groupName, groupAvatar, selectedUsers);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.BUILD_GROUP, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] buildGroupOrChat #193 " + response.code());
                        return;
                    }

                    try {
                        String resp = response.body().string();
                        Log.d(TAG, "[✓] buildGroupOrChat #199" + resp);
                        JSONObject json = new JSONObject();
                    } catch (Exception e) {
                        Log.d(TAG, "[x] buildGroupOrChat #205" + e.getMessage());
                    }
                }
            });
        });
    }
}
