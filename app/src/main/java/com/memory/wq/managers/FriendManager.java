package com.memory.wq.managers;


import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.memory.wq.beans.FriendInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.provider.FriendProvider;
import com.memory.wq.db.op.FriendSqlOP;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.provider.WqApplication;
import com.memory.wq.repository.FriendRepository;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.utils.ResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FriendManager {
    private static final String TAG = "WQ_FriendManager";
    private ContentObserver mContentObserver;
    private final FriendRepository mRepository = new FriendRepository();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

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
        ContentResolver resolver = WqApplication.getInstance().getContentResolver();
        if (mContentObserver != null) {
            resolver.unregisterContentObserver(mContentObserver);
        }

        mContentObserver = new ContentObserver(mHandler) {
            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri) {
                mRepository.loadFriends(callback);
            }
        };

        resolver.registerContentObserver(
                FriendProvider.CONTENT_URI_FRIEND,
                true,
                mContentObserver
        );

        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.ALL_FRIENDS, "{}", new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

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
                            Log.d(TAG, "getFriends json " + json);
                            List<FriendInfo> friendInfoList = JsonParser.friendInfoListParser(data);
                            saveFriend2DB(friendInfoList);

                        }

                    } catch (JSONException e) {
                        Log.d(TAG, "");
                    }
                }
            });
        });
    }


    public void saveFriend2DB(List<FriendInfo> friendInfoList) {
        FriendSqlOP friendSqlOP = new FriendSqlOP();
        int count = 0;
        while (!friendSqlOP.insertFriends(friendInfoList)) {
            if (++count == 3)
                break;
        }
        if (count != 3) ;

    }

    public void getAllFriends(ResultCallback<List<FriendInfo>> callback) {
        List<FriendInfo> friendsFromDB = getFriendFromDB();
        mHandler.post(() -> callback.onSuccess(friendsFromDB));

        getFriends(callback);
    }

    private List<FriendInfo> getFriendFromDB() {
        FriendSqlOP op = new FriendSqlOP();
        //TODO
        long id = SPManager.getUserInfo().getUuNumber();
        return op.queryAllFriend(id);
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
