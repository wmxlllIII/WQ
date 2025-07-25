package com.memory.wq.managers;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.MovieInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.RoomInfo;
import com.memory.wq.properties.AppProperties;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MovieManager {
    public static final String TAG = MovieManager.class.getName();

    public void getMovies(String token, ResultCallback<List<MovieInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {

            HttpStreamOP.postJson(AppProperties.MOVIES, token, "{}", new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {

                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            JSONArray movieList = json.getJSONArray("data");
                            List<MovieInfo> movieInfoList = JsonParser.movieParser(movieList);
                            callback.onSuccess(movieInfoList);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        });
    }

    public void getRooms(String token, ResultCallback<List<RoomInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {

            HttpStreamOP.postJson(AppProperties.ROOMS, token, "{}", new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onError("getRooms出错了");
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            JSONArray roomList = json.getJSONArray("data");
                            List<RoomInfo> roomInfoList = JsonParser.roomParer(roomList);
                            callback.onSuccess(roomInfoList);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void saveRoom(String token, String roomId, int movieId) {
        String json = GenerateJson.getSaveRoomJson(roomId, movieId);

        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.SAVE_ROOM, token, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                }
            });
        });

    }

    public void releaseRoom(String token, String roomId) {
        String json = GenerateJson.getReleaseRoomJson(roomId);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.REMOVE_ROOM, token, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                }
            });
        });
    }

    public void shareRoom(Context context,String token, MsgInfo shareMsg, ResultCallback<Boolean> callback) {
        String json = GenerateJson.getShareMsgJson(shareMsg);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.SHARE_ROOM, token, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError(null);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {

                        callback.onError(null);
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        Log.d(TAG, "onResponse: ===分享房间返回码" + code);
                        if (code == 1) {
                            callback.onSuccess(true);
                            //TODO 保存到数据库
                            MsgSqlOP msgSqlOP = new MsgSqlOP(context);
                            List<MsgInfo> msgInfoList=new ArrayList<>();
                            msgInfoList.add(shareMsg);
                            msgSqlOP.insertMessages(msgInfoList);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        });
    }
}
