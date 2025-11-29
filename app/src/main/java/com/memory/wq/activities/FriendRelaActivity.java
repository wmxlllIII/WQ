package com.memory.wq.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.memory.wq.R;
import com.memory.wq.adapters.FriendRelaAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.databinding.ActivityTestWsactivityBinding;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.interfaces.IWebSocketListener;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.service.IWebSocketService;
import com.memory.wq.service.WebService;
import com.memory.wq.service.WebSocketMessage;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class FriendRelaActivity extends BaseActivity<ActivityTestWsactivityBinding> implements IWebSocketListener, ResultCallback<List<FriendRelaInfo>>, FriendRelaAdapter.UpdateRelaListener {

    private static final String TAG = "WQ_FriendRelaActivity";
    private String token;

    private WebService mWebService;
    private MyConn mConn;
    private boolean isBind = false;
    private final EnumSet<EventType> EVENT_TYPE_SET = EnumSet.of(EventType.EVENT_TYPE_REQUEST_FRIEND);
    private final List<FriendRelaInfo> mFriendRelaList = new ArrayList<>();
    private FriendRelaAdapter mAdapter;
    private MsgManager mMsgManager;
    private SharedPreferences sp;
    private FriendManager mFriendManager;

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
        mFriendManager = new FriendManager();
    }

    private void showLV() {
        mBinding.lvFriendReq.setAdapter(mAdapter);
        mMsgManager.getAllRelation(this, false, AppProperties.FRIEND_RELATIONSHIP, token, this);
        //1从数据库拿-----数据每5分钟轮询get

    }

    private void initView() {
        mBinding.llSearch.setOnClickListener(view -> {
            startActivity(new Intent(this, SearchUserActivity.class));
        });
        mBinding.tvScan.setOnClickListener(view -> scanQRCode());
    }

    @Override
    public EnumSet<EventType> getEvents() {
        return EVENT_TYPE_SET;
    }

    @Override
    public <T> void onMessage(WebSocketMessage<T> message) {
        switch (message.getEventType()) {
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

    private void scanQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("扫描用户二维码");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(PortraitCaptureActivity.class);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (result.getContents() == null) {
            MyToast.showToast(this, "扫描取消");
        } else {
            String uuNum = result.getContents();
            //TODO 进主页
            enterPersonalHome(SearchUserType.SEARCH_USER_TYPE_UUNUM, uuNum);
            MyToast.showToast(this, "扫描结果:" + uuNum);
        }


    }

    private void enterPersonalHome(SearchUserType type, String targetAccount) {
        mFriendManager.searchUser(type, targetAccount, token, new ResultCallback<FriendInfo>() {
            @Override
            public void onSuccess(FriendInfo result) {
                Intent intent = new Intent(FriendRelaActivity.this, PersonalActivity.class);
                intent.putExtra("AppProperties.FRIENDINFO", result);
                startActivity(intent);
            }

            @Override
            public void onError(String err) {

            }
        });
    }
}