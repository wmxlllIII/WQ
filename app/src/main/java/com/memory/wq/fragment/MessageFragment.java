package com.memory.wq.fragment;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.memory.wq.R;
import com.memory.wq.activities.FriendRelaActivity;
import com.memory.wq.activities.LaunchActivity;
import com.memory.wq.activities.MainActivity;
import com.memory.wq.activities.ChatActivity;
import com.memory.wq.activities.QrCodeActivity;
import com.memory.wq.adapters.FriendAdapter;
import com.memory.wq.adapters.MsgsAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.databinding.MessageLayoutBinding;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.interfaces.IWebSocketListener;
import com.memory.wq.interfaces.OnFriItemClickListener;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.service.IWebSocketService;
import com.memory.wq.service.WebService;
import com.memory.wq.service.WebSocketMessage;
import com.memory.wq.utils.ResultCallback;

import java.util.EnumSet;
import java.util.List;

public class MessageFragment extends Fragment implements IWebSocketListener {

    private final static String TAG = "WQ_MessageFragment";
    private final FriendAdapter friendAdapter = new FriendAdapter(new onFriClickListener());
    private final FriendManager mFriendManager = new FriendManager();

    private final EnumSet<EventType> eventTypes = EnumSet.of(EventType.EVENT_TYPE_MSG, EventType.EVENT_TYPE_REQUEST_FRIEND);
    private WebService msgService;
    private MsgConn msgConn;
    private MsgsAdapter msgsAdapter;
    private AppCompatActivity mActivity;
    private MessageLayoutBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = MessageLayoutBinding.inflate(inflater, container, false);
        if (ifNeedShowLoginDialog()) {
            return mBinding.getRoot();
        }

        initView();
        initRecycleView();
        initData();
        return mBinding.getRoot();
    }

    private void initRecycleView() {
        mBinding.rvFriends.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvFriends.setAdapter(friendAdapter);
    }

    private boolean ifNeedShowLoginDialog() {
        if (!AccountManager.isVisitorUser()) {
            return false;
        }

        mBinding.llAlreadyLogin.setVisibility(View.GONE);
        mBinding.layoutNoLogin.getRoot().setVisibility(View.VISIBLE);
        new AlertDialog.Builder(mActivity)
                .setTitle("未登录")
                .setMessage("登录后即可体验完整功能哦~")
                .setIcon(R.mipmap.ic_bannertest2)
                .setNegativeButton("去登录", (dialogInterface, i) -> {
                    startActivity(new Intent(mActivity, LaunchActivity.class));
                    getActivity().finish();
                })
                .setPositiveButton("取消", null)
                .setCancelable(false)
                .show();
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            mActivity = (MainActivity) getContext();
        }
    }

    private void initData() {
        Intent intent = new Intent(mActivity, WebService.class);
        msgConn = new MsgConn();
        mActivity.startService(intent);
        mActivity.bindService(intent, msgConn, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        mBinding.ivAdd.setOnClickListener(v -> startActivity(new Intent(mActivity, QrCodeActivity.class)));

        mBinding.ivSearch.setOnClickListener(v -> {
            mBinding.ivSearchRemind.setVisibility(View.GONE);
            startActivity(new Intent(mActivity, FriendRelaActivity.class));
        });

        mBinding.lvMsg.setOnItemClickListener((parent, view1, position, id) -> {
            FriendInfo friendInfo = (FriendInfo) parent.getItemAtPosition(position);
            Intent intent = new Intent(mActivity, ChatActivity.class);
            intent.putExtra(AppProperties.CHAT_ID, friendInfo.getUuNumber());
            mActivity.startActivity(intent);
        });
        loadFriends();
    }

    private void loadFriends() {
        mFriendManager.getFriends(new Friend());
    }

    @Override
    public EnumSet<EventType> getEvents() {
        return eventTypes;
    }

    @Override
    public <T> void onMessage(WebSocketMessage<T> message) {
//        switch (eventType) {
//            case EVENT_TYPE_REQUEST_FRIEND:
//
//                break;
//            case EVENT_TYPE_MSG:
//
//                break;
//        }
    }

    @Override
    public void onConnectionChanged(boolean isConnected) {

    }

    class MsgConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msgService = ((IWebSocketService) service).getService();
            msgService.registerListener(MessageFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            msgService.unregisterListener(MessageFragment.this);
        }
    }

    class Friend implements ResultCallback<List<FriendInfo>> {

        @Override
        public void onSuccess(List<FriendInfo> result) {
            friendAdapter.submitList(result);
            msgsAdapter = new MsgsAdapter(mActivity, result);
            mBinding.lvMsg.setAdapter(msgsAdapter);

        }

        @Override
        public void onError(String err) {

        }
    }

    class onFriClickListener implements OnFriItemClickListener {

        @Override
        public void onItemClick(long targetId) {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra(AppProperties.CHAT_ID, targetId);
            startActivity(intent);
        }

        @Override
        public void onItemLongClick() {

        }

        @Override
        public void onAcceptClick(long targetId) {

        }

        @Override
        public void onRejectClick(long targetId) {

        }
    }
}
