package com.memory.wq.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.memory.wq.R;
import com.memory.wq.adapters.FriendRelaAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ActivityTestWsactivityBinding;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.interfaces.IWebSocketListener;
import com.memory.wq.interfaces.OnFriItemClickListener;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.provider.FriendProvider;
import com.memory.wq.service.IWebSocketService;
import com.memory.wq.service.WebService;
import com.memory.wq.service.WebSocketMessage;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import java.util.EnumSet;
import java.util.List;

public class FriendRelaActivity extends BaseActivity<ActivityTestWsactivityBinding> implements IWebSocketListener {

    private static final String TAG = "WQ_FriendRelaActivity";

    private WebService mWebService;
    private MyConn mConn;
    private boolean isBind = false;
    private final EnumSet<EventType> EVENT_TYPE_SET = EnumSet.of(EventType.EVENT_TYPE_REQUEST_FRIEND);
    private final FriendRelaAdapter mAdapter = new FriendRelaAdapter(new FriItemClickListener());
    private MsgManager mMsgManager;
    private FriendManager mFriendManager;
    private ContentObserver mFriRelaProvider;
    private final FriRelaCallback mFriRelaCallback = new FriRelaCallback();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        registerContentObserver();
        initData();
        showLV();
    }

    private void registerContentObserver() {
        mFriRelaProvider = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                refreshData();
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                refreshData();
            }
        };

        getContentResolver().registerContentObserver(
                FriendProvider.CONTENT_URI_FRIEND_RELATIONSHIP,
                true,
                mFriRelaProvider
        );
    }

    private void refreshData() {
        mMsgManager.getRelation(mFriRelaCallback);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_wsactivity;
    }

    private void initData() {
        Intent intent = new Intent(this, WebService.class);
        startService(intent);
        mConn = new MyConn();
        mMsgManager = new MsgManager();
        bindService(intent, mConn, BIND_AUTO_CREATE);
        mFriendManager = new FriendManager();
    }

    private void showLV() {
        mBinding.rvFriendReq.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvFriendReq.setAdapter(mAdapter);
        refreshData();
    }

    private void initView() {
        mBinding.llSearch.setOnClickListener(view -> {
            startActivity(new Intent(this, SearchUserActivity.class));
        });

        mBinding.ivScan.setOnClickListener(view -> scanQRCode());

        mBinding.ivBack.setOnClickListener(view -> finish());
    }

    @Override
    public EnumSet<EventType> getEvents() {
        return EVENT_TYPE_SET;
    }

    @Override
    public <T> void onMessage(WebSocketMessage<T> message) {
        switch (message.getEventType()) {
            case EVENT_TYPE_REQUEST_FRIEND:
                List<FriendRelaInfo> friRelaList = (List<FriendRelaInfo>) message.getData();
                if (friRelaList == null || friRelaList.isEmpty()) {
                    Log.d(TAG, "[x] onMessage #103");
                    return;
                }

                mAdapter.submitList(friRelaList, () -> mBinding.rvFriendReq.scrollToPosition(mAdapter.getItemCount() - 1));
                break;
        }
    }


    @Override
    public void onConnectionChanged(boolean isConnected) {

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
        if (mFriRelaProvider != null) {
            getContentResolver().unregisterContentObserver(mFriRelaProvider);
        }

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
        mFriendManager.searchUser(type, targetAccount, new ResultCallback<FriendInfo>() {
            @Override
            public void onSuccess(FriendInfo result) {
                Intent intent = new Intent(FriendRelaActivity.this, PersonInfoActivity.class);
                intent.putExtra(AppProperties.PERSON_ID, result.getUuNumber());
                startActivity(intent);
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private class FriItemClickListener implements OnFriItemClickListener {

        @Override
        public void onItemClick(long targetId) {
            // 进主页
        }

        @Override
        public void onItemLongClick() {

        }

        @Override
        public void onAcceptClick(long targetId) {
            mMsgManager.updateRela(targetId, true, new ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    MyToast.showToast(FriendRelaActivity.this, result ? "已同意" : "已拒绝");
                }

                @Override
                public void onError(String err) {

                }
            });
        }

        @Override
        public void onRejectClick(long targetId) {
            mMsgManager.updateRela(targetId, false, new ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {

                }

                @Override
                public void onError(String err) {

                }
            });
        }
    }

    private class FriRelaCallback implements ResultCallback<List<FriendRelaInfo>> {

        @Override
        public void onSuccess(List<FriendRelaInfo> result) {
            mAdapter.submitList(result);
        }

        @Override
        public void onError(String err) {

        }
    }

}