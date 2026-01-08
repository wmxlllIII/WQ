package com.memory.wq.managers;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.MovieInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.RoomInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.db.op.MsgSqlOP;
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
    private final Handler mhandler = new Handler(Looper.getMainLooper());

    public void getMovies(String token, ResultCallback<List<MovieInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.MOVIES, "{}", new Callback() {
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

    public void getRooms(ResultCallback<List<RoomInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {

            HttpStreamOP.postJson(AppProperties.ROOMS, "{}", new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        mhandler.post(() -> callback.onError("getRooms出错了"));
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            JSONArray roomList = json.getJSONArray("data");
                            List<RoomInfo> roomInfoList = JsonParser.roomParer(roomList);
                            mhandler.post(() -> callback.onSuccess(roomInfoList));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void saveRoom(String token, long roomId, int movieId) {
        String json = GenerateJson.getSaveRoomJson(roomId, movieId);

        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.SAVE_ROOM, json, new Callback() {
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
            HttpStreamOP.postJson(AppProperties.REMOVE_ROOM, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                }
            });
        });
    }

    public void shareRoom(Context context, String token, MsgInfo shareMsg, ResultCallback<Boolean> callback) {
        String json = GenerateJson.getShareMsgJson(shareMsg);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.SHARE_ROOM, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mhandler.post(() -> callback.onError(null));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        mhandler.post(() -> callback.onError(null));
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        Log.d(TAG, "onResponse: ===分享房间返回码" + code);
                        if (code == 1) {
                            mhandler.post(() -> callback.onSuccess(true));
                            //TODO 保存到数据库
                            MsgSqlOP msgSqlOP = new MsgSqlOP();
                            List<MsgInfo> msgInfoList = new ArrayList<>();
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
