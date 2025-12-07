package com.memory.wq.repository;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.memory.wq.beans.MsgInfo;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.provider.MsgProvider;
import com.memory.wq.provider.MsgSqlOP;
import com.memory.wq.provider.WqApplication;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class MsgRepository {

    private static final String TAG = "WQ_MsgRepository";
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void loadMessages(String me, String target, ResultCallback<List<MsgInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            MsgSqlOP op = new MsgSqlOP(WqApplication.getInstance());
            Log.d(TAG, "loadMessages me: " + me + "===target: " + target);
            List<MsgInfo> list = op.queryAllMsg(me, target);
            mHandler.post(() -> {
                Log.d(TAG, "loadMessages: 加载消息："+ list.size());
                callback.onSuccess(list);
            });
        });
    }

    public void insertMessages(List<MsgInfo> list) {
        ThreadPoolManager.getInstance().execute(() -> {
            MsgSqlOP op = new MsgSqlOP(WqApplication.getInstance());
            boolean b = op.insertMessages(list);
        });

    }
}
