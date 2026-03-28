package com.memory.wq.managers;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.OnlineInfo;
import com.memory.wq.beans.UiChatInfo;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.enumertions.SelectImageType;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.provider.FileOP;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.provider.WqApplication;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.utils.ResultCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserManager {

    public static final String TAG = "WQ_UserManager";
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public static final int REQUEST_CAMERA_CODE = 10;
    public static final int REQUEST_ALBUM_CODE = 20;
    public static final int REQUEST_CROP_CODE = 30;
    private File tempImageFile;

    public void upLoadAvatar(File file, ResultCallback<String> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postFile(AppProperties.UPLOAD_URL, AccountManager.getUserInfo().getToken(), file, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mHandler.post(() -> {
                        callback.onError(e.getMessage());
                    });
                    Log.d(TAG, "onFailure: ======错误1");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    mHandler.post(() -> {
                        if (!response.isSuccessful()) {
                            callback.onError(response.toString());
                            Log.d(TAG, "[x] upLoadAvatar #65");
                            return;
                        }
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            Log.d(TAG, "[test] upLoadAvatar #70" + json);
                            int code = json.getInt("code");
                            if (code == 1) {
                                UserInfo userInfo = JsonParser.updateAvatarParser(json);
                                if (!TextUtils.isEmpty(userInfo.getAvatarUrl())) {
                                    callback.onSuccess(userInfo.getAvatarUrl());
                                } else{
                                    callback.onError("[x] 头像地址为空");
                                }
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        callback.onError("上传错误");
                    });
                }
            });
        });
    }


    public void deleteFriend(long friendId, ResultCallback<Boolean> callback) {
        String json = GenerateJson.getDeleteFriendJson(friendId);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.FRIEND_DELETE, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] deleteFriend #167" + e.getMessage());
                    mHandler.post(() -> callback.onError("删除失败"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] deleteFriend #174 " + response.message());
                        mHandler.post(() -> callback.onError("删除失败"));
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        Log.d(TAG, "[test] deleteFriend #180 " + json);
                        if (json.getInt("code") == 1) {
                            boolean isDelete = json.getBoolean("data");
                            if (isDelete) {
                                mHandler.post(() -> callback.onSuccess(true));
                            } else {
                                mHandler.post(() -> callback.onError("删除失败"));
                            }
                        }

                    } catch (Exception e) {
                        Log.d(TAG, "[x] deleteFriend #150 " + e.getMessage());
                    }
                }
            });
        });
    }

    public void getUserById(long userId, ResultCallback<FriendInfo> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getUserByIdJson(userId);
            HttpStreamOP.postJson(AppProperties.GET_USER_BY_ID, json, new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getUserById #213");
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("code") == 1) {
                            Log.d(TAG, "[test] getUserById #218 " + json);
                            JSONObject data = json.getJSONObject("data");
                            FriendInfo friend = JsonParser.userByIdParser(data);
                            mHandler.post(() -> callback.onSuccess(friend));
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "[x] getUserById #174 " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }
            });
        });
    }

    public void getUserListByIdList(List<Long> userIdList, ResultCallback<List<FriendInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getUserListByIdListJson(userIdList);
            HttpStreamOP.postJson(AppProperties.GET_USER_LIST_BY_ID_LIST, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] getUserListByIdList #194");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getUserListByIdList #200");
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        Log.d(TAG, "[test] getUserListByIdList #206");
                        if (json.getInt("code") == 1) {
                            List<FriendInfo> friend = JsonParser.userListByIdListParser(json);
                            if (friend == null) {
                                mHandler.post(() -> callback.onSuccess(new ArrayList<>()));
                            }

                            mHandler.post(() -> callback.onSuccess(friend));
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "[x] getUserListByIdList #222 " + e.getMessage());
                    }
                }
            });
        });
    }

    public void getFriendIsOnline(List<Long> userIdList, ResultCallback<List<OnlineInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getIsOnlineJson(userIdList);
            HttpStreamOP.postJson(AppProperties.GET_IS_ONLINE, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] getFriendIsOnline #233");
                    mHandler.post(() -> callback.onError("获取在线状态失败"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getFriendIsOnline #239");
                        mHandler.post(() -> callback.onError("获取在线状态失败"));
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("code") == 1) {
                            List<OnlineInfo> onlineInfos = JsonParser.onlineListParser(json);
                            mHandler.post(() -> callback.onSuccess(onlineInfos));
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "[x] getFriendIsOnline #251 " + e.getMessage());
                    }
                }
            });
        });
    }

    public void getChatInfoById(Long chatId, int chatType, ResultCallback<UiChatInfo> resultCallback) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getChatInfoByIdJson(chatId,chatType);
            HttpStreamOP.postJson(AppProperties.GET_CHAT_INFO_BY_ID, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] getChatInfoById #262");
                    mHandler.post(() -> resultCallback.onError("获取聊天信息失败"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getChatInfoById #268");
                        mHandler.post(() -> resultCallback.onError("获取聊天信息失败"));
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        Log.d(TAG, "[test] getChatInfoById: " + json);
                        if (json.getInt("code") == 1) {
                            UiChatInfo uiChatInfo = JsonParser.chatInfoByIdParser(json);
                            mHandler.post(() -> resultCallback.onSuccess(uiChatInfo));
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "[x] getChatInfoById #284 " + e.getMessage());
                    }
                    mHandler.post(() -> resultCallback.onError("获取聊天信息失败"));
                }
            });
        });
    }
}
