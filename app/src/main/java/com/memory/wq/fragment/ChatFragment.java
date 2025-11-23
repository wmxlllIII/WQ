package com.memory.wq.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.memory.wq.activities.AudioActivity;
import com.memory.wq.adapters.MsgAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.databinding.FragmentChatBinding;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.interfaces.IWebSocketListener;
import com.memory.wq.interfaces.OnMsgItemClickListener;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.service.IWebSocketService;
import com.memory.wq.service.WebService;
import com.memory.wq.service.WebSocketMessage;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.ShareConfirmDialog;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ChatFragment extends Fragment implements IWebSocketListener, ResultCallback<List<MsgInfo>> {
    private static final String TAG = "WQ_ChatFragment";

    private FragmentChatBinding mBinding;
    private final EnumSet<EventType> eventTypes = EnumSet.of(EventType.EVENT_TYPE_MSG);
    private final MsgAdapter mAdapter = new MsgAdapter(new MsgItemCLickListener());
    private final List<MsgInfo> mMsgInfoList = new ArrayList<>();
    private WebService mMsgService;
    private WebSocketConn mWebSocketConn;
    private MsgManager mMsgManager;

    private String token;
    private SharedPreferences sp;
    private FriendInfo mFriendInfo;
    private MsgInfo mLinkInfo;
    private MovieManager mMovieManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentChatBinding.inflate(inflater, container, false);
        initView();
        initData();
        show();
        return mBinding.getRoot();
    }

    private void initData() {
        sp = getContext().getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");

        mBinding.rvMsg.setAdapter(mAdapter);

        Bundle args = getArguments();
        if (args != null) {
            mFriendInfo = (FriendInfo) args.getSerializable(AppProperties.FRIENDINFO);
            if (args.containsKey(AppProperties.SHARE_MESSAGE)) {
                mLinkInfo = (MsgInfo) args.getSerializable(AppProperties.SHARE_MESSAGE);
                mMovieManager = new MovieManager();
                showShareUI();
            }
        }

        if (mFriendInfo == null) {
            MyToast.showToast(getContext(), "好友信息缺失");
            requireActivity().finish();
            return;
        }

        if (mWebSocketConn == null) {
            mWebSocketConn = new WebSocketConn();
        }
        Intent intent = new Intent(getContext(), WebService.class);
        getContext().bindService(intent, mWebSocketConn, Context.BIND_AUTO_CREATE);

        mMsgManager = new MsgManager(getContext());
        mMsgManager.getAllMsg(mFriendInfo.getEmail(), this);
    }

    private void initView() {
        mBinding.ivBack.setOnClickListener(v -> requireActivity().finish());
        mBinding.btnSend.setOnClickListener(v -> sendMsg());
    }

    private void show() {
        mBinding.tvNickname.setText(mFriendInfo.getNickname());

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        mBinding.rvMsg.setLayoutManager(layoutManager);
    }

    private void sendMsg() {
        String msg = mBinding.etInputText.getText().toString().trim();
        if (TextUtils.isEmpty(msg))
            return;

        String currentEmail = sp.getString("email", "");


        mMsgManager.sendMsg(token, currentEmail, mFriendInfo.getEmail(), msg, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                mMsgManager.getAllMsg(mFriendInfo.getEmail(), ChatFragment.this);
            }

            @Override
            public void onError(String err) {

            }
        });
        mBinding.etInputText.setText("");
    }

    private void showShareUI() {
        Log.d(TAG, "onShare: ===分享信息" + mFriendInfo.toString() + "aaa+++" + mLinkInfo.toString());

        ShareConfirmDialog dialog = new ShareConfirmDialog(getContext(), mFriendInfo, mLinkInfo, new ShareConfirmDialog.OnShareActionListener() {
            @Override
            public void onShare(MsgInfo shareMsg) {

                mMovieManager.shareRoom(getContext(), token, shareMsg, new ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        MyToast.showToast(getContext(), "分享成功");
                        getActivity().finish();
                    }

                    @Override
                    public void onError(String err) {
                        MyToast.showToast(getContext(), "分享失败");
                        getActivity().finish();
                    }
                });
            }

            @Override
            public void onCancel() {
                MyToast.showToast(getContext(), "用户取消分享");
                getActivity().finish();
            }
        });
        dialog.show();

    }

    @Override
    public EnumSet<EventType> getEvents() {
        return eventTypes;
    }

    @Override
    public <T> void onMessage(WebSocketMessage<T> message) {
        switch (message.getEventType()) {
            case EVENT_TYPE_MSG:
                List<MsgInfo> newMsgList = (List<MsgInfo>) message.getData();
                if (newMsgList == null || newMsgList.isEmpty()) {
                    Log.d(TAG, "[x] onMessage #151");
                    return;
                }

                mAdapter.submitList(newMsgList);
                break;
        }
    }

    @Override
    public void onConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onSuccess(List<MsgInfo> msgInfoList) {
        mAdapter.submitList(msgInfoList, () -> {
            Log.d(TAG, "onSuccess: ===滚动到最后位置" + (msgInfoList.size() - 1));
            mBinding.rvMsg.smoothScrollToPosition(msgInfoList.size() - 1);
        });

    }

    @Override
    public void onError(String err) {

    }

    class WebSocketConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMsgService = ((IWebSocketService) service).getService();
            mMsgService.registerListener(ChatFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMsgService.unregisterListener(ChatFragment.this);
        }

    }

    private class MsgItemCLickListener implements OnMsgItemClickListener {
        @Override
        public void onLinkClick(MsgInfo msgInfo) {
            Intent intent = new Intent(getContext(), AudioActivity.class);
            intent.putExtra(AppProperties.ROLE_TYPE, RoleType.ROLE_TYPE_AUDIENCE);
            intent.putExtra(AppProperties.ROOM_ID, msgInfo.getLinkContent());
            startActivity(intent);
            System.out.println("=====加入房间id" + msgInfo.getLinkContent());
        }

        @Override
        public void onMsgLongClick(MsgInfo msgInfo) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unbindService(mWebSocketConn);
        mMsgService.unregisterListener(this);
    }
}