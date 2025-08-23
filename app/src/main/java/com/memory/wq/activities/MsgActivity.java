package com.memory.wq.activities;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.memory.wq.R;
import com.memory.wq.adapters.MsgAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.databinding.ActivityMsgBinding;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.service.IWebSocketService;
import com.memory.wq.service.WebService;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.ShareConfirmDialog;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MsgActivity extends BaseActivity<ActivityMsgBinding> implements WebService.WebSocketListener, ResultCallback<List<MsgInfo>>, MsgAdapter.OnLinkClickListener {
    public static final String TAG = MsgActivity.class.getName();
    private final EnumSet<EventType> eventTypes = EnumSet.of(EventType.EVENT_TYPE_MSG);
    private MsgAdapter mAdapter;
    private final List<MsgInfo> mMsgInfoList = new ArrayList<>();
    private WebService mMsgService;
    private MsgConn mMsgConn;
    private MsgManager mMsgManager;

    private String token;
    private SharedPreferences sp;
    private FriendInfo mFriendInfo;
    private MsgInfo mLinkInfo;
    private MovieManager mMovieManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        show();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_msg;
    }

    private void initData() {
        sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        mAdapter = new MsgAdapter(this, mMsgInfoList);
        mAdapter.setOnLinkClickListener(this);
        Intent intent = new Intent(this, WebService.class);
        if (mMsgConn == null)
            mMsgConn = new MsgConn();
        bindService(intent, mMsgConn, BIND_AUTO_CREATE);
        mMsgManager = new MsgManager(this);

        Intent intent1 = getIntent();
        mFriendInfo = (FriendInfo) intent1.getSerializableExtra(AppProperties.FRIENDINFO);
        if (intent1.hasExtra(AppProperties.SHARE_MESSAGE)) {
            mLinkInfo = (MsgInfo) intent1.getSerializableExtra(AppProperties.SHARE_MESSAGE);
            mMovieManager = new MovieManager();
            showShareUI();
        }

        mMsgManager.getAllMsg(mFriendInfo.getEmail(), this);
    }

    private void showShareUI() {
        Log.d(TAG, "onShare: ===分享信息" + mFriendInfo.toString() + "aaa+++" + mLinkInfo.toString());

        ShareConfirmDialog dialog = new ShareConfirmDialog(this, mFriendInfo, mLinkInfo, new ShareConfirmDialog.OnShareActionListener() {
            @Override
            public void onShare(MsgInfo shareMsg) {

                mMovieManager.shareRoom(MsgActivity.this, token, shareMsg, new ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        runOnUiThread(() -> {
                            MyToast.showToast(MsgActivity.this, "分享成功");
                            finish();
                        });
                    }

                    @Override
                    public void onError(String err) {
                        runOnUiThread(() -> {
                            MyToast.showToast(MsgActivity.this, "分享失败");
                            finish();
                        });
                    }
                });
            }

            @Override
            public void onCancel() {
                MyToast.showToast(MsgActivity.this, "用户取消分享");
                finish();
            }
        });
        dialog.show();

    }

    private void show() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        mBinding.rvMsg.setLayoutManager(layoutManager);
        mBinding.rvMsg.setAdapter(mAdapter);
        mBinding.tvNickname.setText(mFriendInfo.getNickname());
    }

    private void initView() {
        mBinding.btnSend.setOnClickListener(view -> {
            sendMsg();
        });
    }

    @Override
    public EnumSet<EventType> getEvents() {
        return eventTypes;
    }

    @Override
    public void onEventMessage(EventType eventType) {
        switch (eventType) {
            case EVENT_TYPE_MSG:
                mMsgManager.getAllMsg(mFriendInfo.getEmail(), new ResultCallback<List<MsgInfo>>() {
                    @Override
                    public void onSuccess(List<MsgInfo> result) {
                        runOnUiThread(() -> {

                            mAdapter.updateItem(result);
                            if (mMsgInfoList.size() > 0) {
                                Log.d(TAG, "===1正在滚动到最后位置: " + (mMsgInfoList.size() - 1));
                                mBinding.rvMsg.post(() -> {
                                    mBinding.rvMsg.smoothScrollToPosition(mMsgInfoList.size() - 1);
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(String err) {

                    }
                });
                break;
        }
    }

    @Override
    public void onConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onSuccess(List<MsgInfo> msgInfoList) {
        runOnUiThread(() -> {
            mAdapter.updateItem(msgInfoList);
            Log.d(TAG, "onSuccess: ===滚动到最后位置" + (msgInfoList.size() - 1));
            mBinding.rvMsg.smoothScrollToPosition(msgInfoList.size() - 1);

        });
    }

    @Override
    public void onError(String err) {
        //获取本地消息记录失败回调
    }

    private void sendMsg() {
        String msg = mBinding.etInputText.getText().toString().trim();
        if (TextUtils.isEmpty(msg))
            return;

        String currentEmail = sp.getString("email", "");


        mMsgManager.sendMsg(token, currentEmail, mFriendInfo.getEmail(), msg, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                mMsgManager.getAllMsg(mFriendInfo.getEmail(), MsgActivity.this);
            }

            @Override
            public void onError(String err) {

            }
        });
        mBinding.etInputText.setText("");
    }

    @Override
    public void onClick(MsgInfo msgInfo) {
        Intent intent = new Intent(this, AudioActivity.class);
        intent.putExtra(AppProperties.ROLE_TYPE, RoleType.ROLE_TYPE_AUDIENCE);
        intent.putExtra(AppProperties.ROOM_ID, msgInfo.getLinkContent());
        startActivity(intent);
        System.out.println("=====加入房间id" + msgInfo.getLinkContent());
    }

    class MsgConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMsgService = ((IWebSocketService) service).getService();
            mMsgService.registerListener(MsgActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMsgService.unregisterListener(MsgActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mMsgConn);
        mMsgService.unregisterListener(this);
    }
}