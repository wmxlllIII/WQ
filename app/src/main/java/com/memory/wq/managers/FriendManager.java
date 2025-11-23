package com.memory.wq.managers;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.memory.wq.beans.FriendInfo;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.constants.AppProperties;

import com.memory.wq.provider.FriendSqlOP;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.thread.ThreadPoolManager;

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
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void searchUser(SearchUserType type, String account, String token, ResultCallback<FriendInfo> callback) {
        String json = GenerateJson.getSearchUserJson(type, account);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.SEARCH_USER, token, json, new Callback() {
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
                        if (code == 1) {
                            FriendInfo friendInfo = JsonParser.searchFriendParser(json);
                            mHandler.post(()-> callback.onSuccess(friendInfo));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void getAllFriendFromServer(Context context, String token, ResultCallback<List<FriendInfo>> callback) {

        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.ALL_FRIENDS, token, "{}", new Callback() {
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
                        if (code == 1) {
                            JSONArray data = json.getJSONArray("data");
                            List<FriendInfo> friendInfoList = JsonParser.friendInfoListParser(data);
                            saveFriend2DB(context, friendInfoList);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }


    public void saveFriend2DB(Context context, List<FriendInfo> friendInfoList) {
        FriendSqlOP friendSqlOP = new FriendSqlOP(context);
        //TODO
        String id = SPManager.getUserInfo(context).getId();
        int count = 0;
        while (!friendSqlOP.insertFriends(friendInfoList, id)) {
            if (++count == 3)
                break;
        }
        if (count != 3) ;

    }

    public void getAllFriends(Context context, String token, ResultCallback<List<FriendInfo>> callback) {
        List<FriendInfo> friendsFromDB = getFriendFromDB(context);
        callback.onSuccess(friendsFromDB);
        getAllFriendFromServer(context, token, new ResultCallback<List<FriendInfo>>() {
            @Override
            public void onSuccess(List<FriendInfo> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private List<FriendInfo> getFriendFromDB(Context context) {
        FriendSqlOP op = new FriendSqlOP(context);
        //TODO
        String id = SPManager.getUserInfo(context).getId();
        return op.queryAllFriend(id);
    }
}
