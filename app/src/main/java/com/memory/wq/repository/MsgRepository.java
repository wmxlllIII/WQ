package com.memory.wq.repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.UiChatInfo;
import com.memory.wq.db.op.MsgSqlOP;
import com.memory.wq.enumertions.ChatType;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class MsgRepository {

    private static final String TAG = "WQ_MsgRepository";
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final MsgSqlOP mMsgSqlOP = new MsgSqlOP();

    public void loadMessages(long chatId, ChatType chatType, ResultCallback<List<MsgInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            List<MsgInfo> list = mMsgSqlOP.queryAllMsg(chatId, chatType);
            mHandler.post(() -> {
                Log.d(TAG, "[test] loadMessages: #26加载消息：" + list);
                callback.onSuccess(list);
            });
        });
    }
}
