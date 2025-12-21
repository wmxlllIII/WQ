package com.memory.wq.repository;

import android.os.Handler;
import android.os.Looper;

import com.memory.wq.beans.FriendInfo;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.db.op.FriendSqlOP;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class FriendRepository {

    private static final String TAG = "WQ_FriendRepository";
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final FriendSqlOP mSqlOP =new FriendSqlOP();
    private final long userId = AccountManager.getUserInfo().getUuNumber();

    public void loadFriends(ResultCallback<List<FriendInfo>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            List<FriendInfo> friendList = mSqlOP.queryAllFriend(userId);
            mHandler.post(()->{callback.onSuccess(friendList);});
        });
    }
}
