package com.memory.wq.managers;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.beans.ActorInfo;
import com.memory.wq.beans.MovieCateInfo;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.beans.MovieProfileInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.RoomInfo;
import com.memory.wq.beans.WatchHistoryInfo;
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
    public static final String TAG = "WQ_MovieManager";
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void getMoviesByCate(int cateId, ResultCallback<List<MovieInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getMoviesByCateJson(cateId);
            HttpStreamOP.postJson(AppProperties.GET_MOVIE_BY_CATE, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] getMoviesByCate #43");
                    mHandler.post(() -> callback.onError("getMoviesByCate出错了"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getMoviesByCate #49");
                        mHandler.post(() -> callback.onError("getMoviesByCate 出错了"));
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            JSONArray movieList = json.getJSONArray("data");
                            List<MovieInfo> movieInfoList = JsonParser.movieParser(movieList);
                            Log.d(TAG, "[test] getMoviesByCate " + movieInfoList.size());
                            mHandler.post(() -> callback.onSuccess(movieInfoList));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        });
    }

    public void getMoviesByActor(int actorId, ResultCallback<List<MovieInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getMoviesByActorJson(actorId);
            HttpStreamOP.postJson(AppProperties.GET_MOVIE_BY_ACTOR, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] getMoviesByActor #81");
                    mHandler.post(() -> callback.onError("getMoviesByActor 出错了"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getMoviesByActor #88");
                        mHandler.post(() -> callback.onError("getMoviesByActor 出错了"));
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            JSONArray movieList = json.getJSONArray("data");
                            List<MovieInfo> movieInfoList = JsonParser.movieParser(movieList);
                            mHandler.post(() -> callback.onSuccess(movieInfoList));
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
                    Log.d(TAG, "[x] getRooms #118");
                    mHandler.post(() -> callback.onError("getRooms 出错了"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getRooms #125");
                        mHandler.post(() -> callback.onError("获取房间数据失败"));
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            JSONArray roomList = json.getJSONArray("data");
                            List<RoomInfo> roomInfoList = JsonParser.roomParer(roomList);
                            mHandler.post(() -> callback.onSuccess(roomInfoList));
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, "[x] getRooms #139" + e.getMessage());
                    }
                    mHandler.post(() -> {
                        callback.onError("获取房间数据失败");
                    });
                }
            });
        });
    }

    public void saveRoom(long roomId, int movieId) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getSaveRoomJson(roomId, movieId);
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

    public void releaseRoom(String roomId) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getReleaseRoomJson(roomId);
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
                    mHandler.post(() -> callback.onError(null));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        mHandler.post(() -> callback.onError(null));
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        Log.d(TAG, "onResponse: ===分享房间返回码" + code);
                        if (code == 1) {
                            mHandler.post(() -> callback.onSuccess(true));
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

    public void getCates(ResultCallback<List<MovieCateInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.GET_MOVIE_CATE, "{}", new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] getCates #219");
                    mHandler.post(() -> callback.onError(null));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getCates #226");
                        mHandler.post(() -> callback.onError(null));
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            List<MovieCateInfo> movieCateInfoList = JsonParser.movieCateParser(json.getJSONArray("data"));
                            mHandler.post(() -> callback.onSuccess(movieCateInfoList));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void saveWatchProgress(int movieId, int currentSecondPosition) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getSaveProgressJson(movieId, currentSecondPosition);
            HttpStreamOP.postJson(AppProperties.SAVE_WATCH_PROGRESS, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] saveWatchProgress #251");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                }
            });
        });
    }

    public void getWatchHistory(ResultCallback<List<WatchHistoryInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.GET_WATCH_HISTORY, "{}", new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] getWatchHistory #268");
                    mHandler.post(() -> callback.onError("getWatchHistory 出错了"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getWatchHistory #280");
                        mHandler.post(() -> callback.onError(null));
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            Log.d(TAG, "[test] getWatchHistory " + json);
                            List<WatchHistoryInfo> watchHistoryList = JsonParser.watchHistoryParser(json.getJSONArray("data"));
                            mHandler.post(() -> callback.onSuccess(watchHistoryList));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void getActorInfo(int actorId, ResultCallback<ActorInfo> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getActorInfoJson(actorId);
            HttpStreamOP.postJson(AppProperties.GET_ACTOR_INFO, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] getActorInfo #286");
                    mHandler.post(() -> callback.onError("getActorInfo 出错了"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getActorInfo #295");
                        mHandler.post(() -> callback.onError(null));
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            JSONObject data = json.getJSONObject("data");
                            ActorInfo actor = JsonParser.actorProfileParser(data);
                            mHandler.post(() -> callback.onSuccess(actor));
                            return;
                        }
                        Log.d(TAG, "[x] getActorInfo #309");
                        mHandler.post(() -> callback.onError(null));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void getMovieProfile(int movieId, ResultCallback<MovieProfileInfo> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getMovieDetailJson(movieId);
            HttpStreamOP.postJson(AppProperties.GET_MOVIE_DETAIL, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] getMovieProfile #306");
                    mHandler.post(() -> callback.onError("getWatchHistory 出错了"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] getCates #226");
                        mHandler.post(() -> callback.onError(null));
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            JSONObject data = json.getJSONObject("data");
                            MovieProfileInfo movieProfile = JsonParser.movieProfileParser(data);
                            Log.d(TAG, "onResponse: " + movieProfile);
                            mHandler.post(() -> callback.onSuccess(movieProfile));
                            return;
                        }
                        Log.d(TAG, "[x] getMovieProfile #343");
                        mHandler.post(() -> callback.onError(null));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void searchMovie(String keyword, ResultCallback<List<MovieInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getSearchMovieJson(keyword);
            HttpStreamOP.postJson(AppProperties.SEARCH_MOVIE, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] searchMovie #340");
                    mHandler.post(() -> callback.onError("网络错误"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] searchMovie #340");
                        mHandler.post(() -> callback.onError(null));
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            JSONArray data = json.getJSONArray("data");
                            List<MovieInfo> movieInfoList = JsonParser.movieParser(data);
                            mHandler.post(() -> callback.onSuccess(movieInfoList));
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, "[x] searchMovie #360" + e.getMessage());
                        mHandler.post(() -> callback.onError("电影解析错误"));
                    }
                }
            });
        });
    }
}
