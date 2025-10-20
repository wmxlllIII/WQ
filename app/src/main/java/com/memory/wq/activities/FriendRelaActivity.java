package com.memory.wq.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;

import com.memory.wq.R;
import com.memory.wq.adapters.FriendRelaAdapter;
import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.databinding.ActivityTestWsactivityBinding;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.service.IWebSocketService;
import com.memory.wq.service.WebService;
import com.memory.wq.utils.ResultCallback;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class FriendRelaActivity extends BaseActivity<ActivityTestWsactivityBinding> implements WebService.WebSocketListener, ResultCallback<List<FriendRelaInfo>>, FriendRelaAdapter.UpdateRelaListener {

    private static final String TAG = FriendRelaActivity.class.getName();
    private String token;

    private WebService mWebService;
    private MyConn mConn;
    private boolean isBind = false;
    private final EnumSet<EventType> EVENT_TYPE_SET = EnumSet.of(EventType.EVENT_TYPE_REQUEST_FRIEND);
    private final List<FriendRelaInfo> mFriendRelaList = new ArrayList<>();
    private FriendRelaAdapter mAdapter;
    private MsgManager mMsgManager;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();

        System.out.println("===========TestWSActivity====token" + token);
        showLV();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_wsactivity;
    }

    private void initData() {
        Intent intent = new Intent(this, WebService.class);
        startService(intent);
        mConn = new MyConn();
        mMsgManager = new MsgManager(this);
        bindService(intent, mConn, BIND_AUTO_CREATE);

        if (mAdapter == null) {
            mAdapter = new FriendRelaAdapter(this, mFriendRelaList);
            mAdapter.setUpdateRelaListener(this);
        }

        sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");

    }

    private void showLV() {
        mBinding.lvFriendReq.setAdapter(mAdapter);
        mMsgManager.getAllRelation(this, false, AppProperties.FRIEND_RELATIONSHIP, token, this);
        //1从数据库拿-----数据每5分钟轮询get
    }

    private void initView() {
        mBinding.lvFriendReq.setOnClickListener(view -> {
            startActivity(new Intent(this, SearchUserActivity.class));
        });
    }

    @Override
    public EnumSet<EventType> getEvents() {
        return EVENT_TYPE_SET;
    }

    @Override
    public void onEventMessage(EventType eventType) {
        switch (eventType) {
            case EVENT_TYPE_REQUEST_FRIEND:
                mMsgManager.getAllRelation(this, true, AppProperties.FRIEND_RELATIONSHIP, token, this);
                break;
        }

    }


    @Override
    public void onConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onSuccess(List<FriendRelaInfo> result) {
        runOnUiThread(() -> {
            if (mAdapter != null) {
                mAdapter.updateAdapterDate(result);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onError(String err) {

    }


    @Override
    public void onUpdateRelaSuccess() {
        mMsgManager.getAllRelation(this, true, AppProperties.FRIEND_RELATIONSHIP, token, this);
    }


    private class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mWebService = ((IWebSocketService) service).getService();
            mWebService.registerListener(FriendRelaActivity.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mWebService.unregisterListener(FriendRelaActivity.this);
            mWebService = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind) {
            mWebService.unregisterListener(this);
            unbindService(mConn);

        }
    }
}