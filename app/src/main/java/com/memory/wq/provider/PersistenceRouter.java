package com.memory.wq.provider;

import android.content.Context;
import android.util.Log;

import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.db.op.FriendSqlOP;
import com.memory.wq.db.op.MsgSqlOP;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.service.WebSocketMessage;
import com.memory.wq.thread.ThreadPoolManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistenceRouter {

    private static final String TAG = "WQ_PersistenceRouter";

    private static final Map<EventType, PersistHandler> HANDLERS = new HashMap<>();

    static {
        HANDLERS.put(EventType.EVENT_TYPE_MSG, (msg, context) -> {

            if (!(msg.getData() instanceof List)) {
                Log.d(TAG, "[x] persist EVENT_TYPE_MSG");
                return;
            }

            ThreadPoolManager.getInstance().execute(() -> {
                MsgSqlOP op = new MsgSqlOP();
                op.insertMessages((List<MsgInfo>) msg.getData());
            });

        });

        HANDLERS.put(EventType.EVENT_TYPE_SHAREMSG, (msg, context) -> {
            if (!(msg.getData() instanceof List)) {
                Log.d(TAG, "[x] persist EVENT_TYPE_SHAREMSG");
                return;
            }

            ThreadPoolManager.getInstance().execute(() -> {
                MsgSqlOP op = new MsgSqlOP();
                op.insertMessages((List<MsgInfo>) msg.getData());
            });

        });

        HANDLERS.put(EventType.EVENT_TYPE_REQUEST_FRIEND, (msg, context) -> {

            if (!(msg.getData() instanceof List)) {
                Log.d(TAG, "[x] persist EVENT_TYPE_REQUEST_FRIEND");
                return;
            }

            ThreadPoolManager.getInstance().execute(() -> {
                FriendSqlOP op = new FriendSqlOP();
                op.insertRelations((List<FriendRelaInfo>) msg.getData());
            });

        });
    }

    public static void persistIfSupported(EventType type, WebSocketMessage<?> msg, Context context) {
        PersistHandler handler = HANDLERS.get(type);
        if (handler != null) {
            handler.persist(msg, context);
        }
    }

    private interface PersistHandler {
        void persist(WebSocketMessage<?> msg, Context context);
    }

}
