package com.memory.wq.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.memory.wq.enumertions.EventType;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;


public class WebService extends Service {
    private static final String TAG = "WQ_WebService";

    private WebSocket webSocket;
    private OkHttpClient client;
    private final List<WebSocketListener> listeners = new CopyOnWriteArrayList<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public interface WebSocketListener {
        EnumSet<EventType> getEvents();

        void onEventMessage(EventType eventType);

        void onConnectionChanged(boolean isConnected);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    private class MyBinder extends Binder implements IWebSocketService {

        @Override
        public WebService getService() {
            return WebService.this;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        client = new OkHttpClient.Builder()
                .pingInterval(60, TimeUnit.SECONDS)
                .build();
        connectWebSocket();
    }

    private void connectWebSocket() {
        SharedPreferences sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        String userid = sp.getString("userId", "");
        Request request = new Request.Builder()
                .url(AppProperties.WEB_SOCKET_SERVER_ADDRESS + userid)
                .addHeader("token", token)
                .build();
        System.out.println("=======token" + token);
        Log.d(TAG, "[✅] connectWebSocket #85"+"token: "+token);
        Log.d(TAG, "[✅] connectWebSocket #85"+"userid: "+userid);
        webSocket = client.newWebSocket(request, new okhttp3.WebSocketListener() {
            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosed(webSocket, code, reason);
                notifyConnectionChanged(false);
                Log.d(TAG, "[✅] onClosed ws连接关闭 #92");
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                reconnect();
                Log.d(TAG, "[x] onFailure ws连接失败 #99:" + t.getMessage());
                t.printStackTrace();
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                super.onMessage(webSocket, text);
                notifyMessageReceived(text);
                Log.d(TAG, "[✅] onMessage ws来消息了 #108");
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                super.onOpen(webSocket, response);
                notifyConnectionChanged(true);
                Log.d(TAG, "[✅] onOpen ws连接成功 #115");
            }
        });
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    public void registerListener(WebSocketListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(WebSocketListener listener) {
        listeners.remove(listener);
    }


    private void notifyMessageReceived(String message) {
        Log.d(TAG, "notifyMessageReceived: =======收到原始消息：" + message);
        EventType type = JsonParser.getJsonType(message);

        switch (type) {
            case EVENT_TYPE_REQUEST_FRIEND:
                try {
                    JSONObject json = new JSONObject(message);
                    JSONArray requestList = json.getJSONArray("request_list");
                    MsgManager.receiveFriendRela(this, requestList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case EVENT_TYPE_MSG:
                try {
                    JSONObject json = new JSONObject(message);
                    JSONArray msgList = json.getJSONArray("msg_list");
                    System.out.println("==============原始消息列表" + msgList.toString());

                    mHandler.post(() -> {

                        try {
//                            MyToast.showToast(this, "来消息了:" + msgList.get(0));
                            System.out.println("===========来消息了:" + msgList.get(0));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

                    ThreadPoolManager.getInstance().execute(() -> {
                        MsgManager.receiveMsg(this, msgList);
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case EVENT_TYPE_SHAREMSG:
                try {
                    JSONObject json = new JSONObject(message);
                    JSONArray shareMsgList = json.getJSONArray("share_list");
                    System.out.println("==============原始分享消息列表" + shareMsgList.toString());
                    ThreadPoolManager.getInstance().execute(() -> {
                        MsgManager.receiveShareMsg(this, shareMsgList);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }


        for (WebSocketListener listener : listeners) {
            if (listener.getEvents().contains(type)) {
                mHandler.post(() -> listener.onEventMessage(type));
            }
        }


    }


    private void notifyConnectionChanged(boolean isConnected) {
        new Handler(Looper.getMainLooper()).post(() -> {
            for (WebSocketListener listener : listeners) {
                mHandler.post(() -> listener.onConnectionChanged(isConnected));
            }
        });
    }


    private void reconnect() {
        new Handler(Looper.getMainLooper()).postDelayed(this::connectWebSocket, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "服务销毁");
        }
        client.dispatcher().executorService().shutdown();
    }


}