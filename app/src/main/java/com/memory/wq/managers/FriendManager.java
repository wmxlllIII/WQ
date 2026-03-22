package com.memory.wq.managers;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.beans.OnlineInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.enumertions.FriendRelaStatus;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.provider.HttpStreamOP;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FriendManager {
    private static final String TAG = "WQ_FriendManager";
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final UserManager mUserManager = new UserManager();

    public void searchUser(SearchUserType type, String account, ResultCallback<FriendInfo> callback) {
        String json = GenerateJson.getSearchUserJson(type, account);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.SEARCH_USER, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] searchUser #50");
                    mHandler.post(() -> callback.onError("网络错误"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] searchUser #56 " + response.code());
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        Log.d(TAG, "[test] searchUser #63 " + json);
                        int code = json.getInt("code");
                        if (code == 1) {
                            FriendInfo friendInfo = JsonParser.searchFriendParser(json);
                            mHandler.post(() -> callback.onSuccess(friendInfo));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void getFriends(ResultCallback<List<FriendInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.ALL_FRIENDS, "{}", new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] getFriends #78" + e.getMessage());
                    mHandler.post(() -> {
                        callback.onError("获取好友错误");
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getFriends #105 " + response.code());
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            JSONArray data = json.getJSONArray("data");
                            List<FriendRelaInfo> friendInfoList = JsonParser.friReqParser(data);
                            List<FriendInfo> result = friendInfoList.stream().map(rela -> {
                                long currentUserId = AccountManager.getUserId();
                                boolean isSelfSender = rela.getSenderId() == currentUserId;

                                FriendInfo info = new FriendInfo();

                                info.setUuNumber(isSelfSender ? rela.getReceiverId() : rela.getSenderId());
                                info.setNickname(isSelfSender ? rela.getReceiverName() : rela.getSenderName());
                                info.setAvatarUrl(isSelfSender ? rela.getReceiverAvatar() : rela.getSenderAvatar());
                                info.setChatId(rela.getChatId());
                                info.setVerifyMsg(rela.getValidMsg());
                                info.setUpdateAt(rela.getUpdateAt());
                                info.setFriend(rela.getStatus() == FriendRelaStatus.ACCEPTED.toInt());

                                return info;
                            }).collect(Collectors.toList());

                            List<Long> userIdList = result.stream()
                                    .map(FriendInfo::getUuNumber)
                                    .collect(Collectors.toList());
                            mUserManager.getFriendIsOnline(userIdList, new ResultCallback<List<OnlineInfo>>() {
                                @Override
                                public void onSuccess(List<OnlineInfo> onlineInfos) {

                                    Map<Long, Boolean> onlineMap = onlineInfos.stream()
                                            .collect(Collectors.toMap(
                                                    OnlineInfo::getUserId,
                                                    OnlineInfo::isOnline
                                            ));

                                    result.forEach(friend -> {
                                        Boolean isOnline = onlineMap.get(friend.getUuNumber());
                                        friend.setOnline(isOnline != null && isOnline);
                                    });

                                    mHandler.post(() -> callback.onSuccess(result));
                                }

                                @Override
                                public void onError(String errorMsg) {
                                    mHandler.post(() -> callback.onSuccess(result));
                                }
                            });

                        }

                    } catch (JSONException e) {
                        Log.d(TAG, "");
                    }
                }
            });
        });
    }

    public void followUser(long userId, ResultCallback<Boolean> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getFollowJson(userId);
            HttpStreamOP.postJson(AppProperties.FOLLOW_USER, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] followUser #160 " + e.getMessage());
                    mHandler.post(() -> callback.onError("网络错误"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] followUser #166 " + response.code());
                        mHandler.post(() -> callback.onError("网络错误"));
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            mHandler.post(() -> callback.onSuccess(true));
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, "[x] followUser #178" + e.getMessage());
                    }

                }
            });
        });
    }

    public void unfollowUser(long userId, ResultCallback<Boolean> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getFollowJson(userId);
            HttpStreamOP.postJson(AppProperties.FOLLOW_USER, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] followUser #190 " + e.getMessage());
                    mHandler.post(() -> callback.onError("网络错误"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] followUser #197 " + response.code());
                        mHandler.post(() -> callback.onError("网络错误"));
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            mHandler.post(() -> callback.onSuccess(true));
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, "");
                    }

                }
            });
        });
    }

}
