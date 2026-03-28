package com.memory.wq.managers;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.MsgListInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.db.op.FriendSqlOP;
import com.memory.wq.enumertions.ChatType;
import com.memory.wq.enumertions.ContentType;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.db.op.MsgSqlOP;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.utils.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            long selfId = AccountManager.getUserId();
            List<FriendRelaInfo> friendRelaInfoList = mFriendSqlOP.queryAllRelations(selfId);

            if (friendRelaInfoList == null || friendRelaInfoList.isEmpty()) {
                mHandler.post(() -> callback.onSuccess(new ArrayList<>()));
                return;
            }

            List<Long> targetIdList = new ArrayList<>();
            friendRelaInfoList.forEach(friendRelaInfo -> {
                boolean isSender = friendRelaInfo.getSenderId() == selfId;
                long targetId = isSender ? friendRelaInfo.getReceiverId() : friendRelaInfo.getSenderId();
                targetIdList.add(targetId);
            });

            mUserManager.getUserListByIdList(targetIdList, new ResultCallback<List<FriendInfo>>() {
                @Override
                public void onSuccess(List<FriendInfo> result) {
                    if (result == null || result.isEmpty()) {
                        mHandler.post(() -> callback.onSuccess(friendRelaInfoList));
                        return;
                    }

                    Map<Long, FriendInfo> userMap = result.stream()
                            .collect(Collectors.toMap(
                                    FriendInfo::getUuNumber,
                                    Function.identity()
                            ));

                    for (FriendRelaInfo rela : friendRelaInfoList) {
                        boolean isSender = rela.getSenderId() == selfId;

                        long targetId = isSender
                                ? rela.getReceiverId()
                                : rela.getSenderId();

                        FriendInfo user = userMap.get(targetId);
                        if (user == null) {
                            continue;
                        }

                        if (isSender) {
                            rela.setReceiverName(user.getNickname());
                            rela.setReceiverAvatar(user.getAvatarUrl());
                        } else {
                            rela.setSenderName(user.getNickname());
                            rela.setSenderAvatar(user.getAvatarUrl());
                        }
                    }

                    mHandler.post(() -> callback.onSuccess(friendRelaInfoList));
                }

                @Override
                public void onError(String err) {
                    Log.d(TAG, "[x] getRelation #68" + err);
                    mHandler.post(() -> callback.onSuccess(new ArrayList<>()));
                }
            });
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

    public void buildGroup(String groupName, Set<Long> selectedUsers,ResultCallback<Boolean> callback) {
        if (selectedUsers == null || selectedUsers.isEmpty()) {
            Log.d(TAG, "[x] buildGroupOrChat: #195");
            return;
        }
        if (selectedUsers.size() < 3) {
            Log.d(TAG, "[x] buildGroupOrChat: #199");
            return;
        }

        String json = GenerateJson.getBuildGroupJson(groupName, selectedUsers);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.BUILD_GROUP, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mHandler.post(() -> callback.onError("网络异常"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] buildGroupOrChat #213 " + response.code());
                        mHandler.post(() -> callback.onError("创建失败"));
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        Log.d(TAG, "[✓] buildGroupOrChat #199" + json);
                        if (json.getInt("code") == 1) {
                            mHandler.post(() -> callback.onSuccess(true));
                            return;
                        }

                        mHandler.post(() -> callback.onError("创建失败"));
                    } catch (Exception e) {
                        Log.d(TAG, "[x] buildGroupOrChat #205" + e.getMessage());
                        mHandler.post(() -> callback.onError("创建失败"));
                    }
                }
            });
        });
    }

    public void getMsgList(ResultCallback<List<MsgListInfo>> mMsgCallback) {
        ThreadPoolManager.getInstance().execute(() -> {
            List<MsgListInfo> msgList = mMsgSqlOP.queryMsgList();
            getMsgListByChatId(msgList, mMsgCallback);
        });
    }

    private void getMsgListByChatId(List<MsgListInfo> msgList, ResultCallback<List<MsgListInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getMsgListJson(msgList);
            HttpStreamOP.postJson(AppProperties.GET_MSG_LIST_BY_CHAI_ID, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] getMsgListByChatId #252" + e.getMessage());
                    mHandler.post(() -> callback.onError("网络异常"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getMsgListByChatId #257 " + response.code());
                        mHandler.post(() -> callback.onError("网络异常"));
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("code") == 1) {
                            List<MsgListInfo> infoList = JsonParser.msgListParser(json);
                            msgList.forEach(msgInfo -> infoList.stream()
                                    .filter(info -> info.getChatId() == msgInfo.getChatId() && info.getChatType() == msgInfo.getChatType())
                                    .findFirst()
                                    .ifPresent(info -> {
                                        info.setLastMsg(msgInfo.getLastMsg());
                                        info.setCreateAt(msgInfo.getCreateAt());
                                    }));
                            mHandler.post(() -> callback.onSuccess(infoList));
                            return;
                        }

                        mHandler.post(() -> callback.onError("更新数据失败，请稍后再试"));
                    } catch (Exception e) {
                        Log.d(TAG, "[x] getMsgListByChatId #267" + e.getMessage());
                        mHandler.post(() -> callback.onError("网络异常"));
                    }
                }
            });
        });
    }
}
