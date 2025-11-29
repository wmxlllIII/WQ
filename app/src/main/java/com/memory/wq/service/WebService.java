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
import com.memory.wq.interfaces.IWebSocketListener;
import com.memory.wq.interfaces.JsonDataParser;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.repository.MessageRepository;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.utils.ParserFactory;

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
    private final List<IWebSocketListener> listeners = new CopyOnWriteArrayList<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

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
//        System.out.println("=======token" + token);
//        Log.d(TAG, "[✅] connectWebSocket #85" + "token: " + token);
//        Log.d(TAG, "[✅] connectWebSocket #85" + "userid: " + userid);
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
//                Log.d(TAG, "[x] onFailure ws连接失败 #99:" + t.getMessage());
//                t.printStackTrace();
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String json) {
                super.onMessage(webSocket, json);
                notifyMessageReceived(json);
                Log.d(TAG, "[✅] onMessage ws来消息了 #108");
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                super.onOpen(webSocket, response);
                notifyConnectionChanged(true);
//                Log.d(TAG, "[✅] onOpen ws连接成功 #115");
            }
        });
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    public void registerListener(IWebSocketListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(IWebSocketListener listener) {
        listeners.remove(listener);
    }


    private void notifyMessageReceived(String message) {
        Log.d(TAG, "notifyMessageReceived: =======收到原始消息：" + message);
        try {
            // 解析通用消息格式：{ "event_type": "xxx", "data": {} }
            JSONObject rootJson = new JSONObject(message);
            String eventTypeStr = rootJson.getString("event_type");
            EventType eventType = JsonParser.getJsonType(eventTypeStr);
            JSONObject dataJson = rootJson.getJSONObject("data");

            JsonDataParser<?> parser = ParserFactory.getParser(eventType);

            Object parsedData = parser.parse(dataJson);

            WebSocketMessage<?> webSocketMsg = new WebSocketMessage<>(eventType, parsedData);
            notifyListeners(webSocketMsg);

        } catch (JSONException e) {
            Log.e(TAG, "notifyMessageReceived 消息解析失败", e);
            WebSocketMessage<JSONObject> errorMsg = null;
            try {
                errorMsg = new WebSocketMessage<>(EventType.UNKNOWN, new JSONObject(message));
            } catch (JSONException ex) {
                errorMsg = new WebSocketMessage<>(EventType.UNKNOWN, null);
            }
            notifyListeners(errorMsg);
        }

    }

    private <T> void notifyListeners(WebSocketMessage<T> message) {
        if (message == null || listeners.isEmpty()) {
            Log.d(TAG, "[x] notifyListeners #231");
            return;
        }

        mHandler.post(() -> listeners.stream()
                .filter(listener -> listener.getEvents().contains(message.getEventType()))
                .forEach(listener -> {
                    try {
                        listener.onMessage(message);
                        Log.d(TAG, "notifyListeners 通知监听器成功: " + listener.getClass().getSimpleName() + " - " + message.getEventType());
                    } catch (Exception e) {
                        Log.e(TAG, "notifyListeners 通知监听器失败", e);
                    }
                }));
        MessageRepository.getInstance().postMessage(message);
    }

    private void notifyConnectionChanged(boolean isConnected) {
        mHandler.post(() -> {
            listeners.forEach(listener -> listener.onConnectionChanged(isConnected));
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