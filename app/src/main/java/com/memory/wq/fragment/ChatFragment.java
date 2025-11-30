package com.memory.wq.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.memory.wq.activities.AudioActivity;
import com.memory.wq.adapters.MsgAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.UiChatInfo;
import com.memory.wq.beans.UiMessageState;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.FragmentChatBinding;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.interfaces.OnMsgItemClickListener;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.ShareConfirmDialog;
import com.memory.wq.vm.ChatViewModel;

public class ChatFragment extends Fragment {
    private static final String TAG = "WQ_ChatFragment";

    private FragmentChatBinding mBinding;
    private final MsgAdapter mAdapter = new MsgAdapter(new MsgItemCLickListener());
    private MsgManager mMsgManager = new MsgManager();

    private String token;
    private SharedPreferences sp;
    private FriendInfo mFriendInfo;
    private MsgInfo mLinkInfo;
    private MovieManager mMovieManager;
    private ChatViewModel mChatVM;

    private Observer<UiChatInfo> mUiChatInfoObserver = this::_proUiChatInfoUpdate;
    private Observer<UiMessageState> mUiMessageInfoObserver = this::_proUiMessageInfoUpdate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentChatBinding.inflate(inflater, container, false);
        initView(getViewLifecycleOwner());
        initRecyclerView();
        initData();
        return mBinding.getRoot();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        mBinding.rvMsg.setLayoutManager(layoutManager);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof AppCompatActivity)) {
            Log.d(TAG, "onAttach #no match");
            return;
        }

        createViewModel((AppCompatActivity) context);
    }

    private void createViewModel(AppCompatActivity activity) {
        mChatVM = new ViewModelProvider(activity).get(ChatViewModel.class);
    }

    private void initData() {
        sp = getContext().getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");

        mBinding.rvMsg.setAdapter(mAdapter);


        if (mFriendInfo == null) {
            MyToast.showToast(getContext(), "好友信息缺失");
            return;
        }

    }

    private void initView(LifecycleOwner lifecycleOwner) {
        initObserver(lifecycleOwner);

        mBinding.ivBack.setOnClickListener(v -> requireActivity().finish());
        mBinding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });
    }

    private void initObserver(LifecycleOwner lifecycleOwner) {
        mChatVM.uiChatInfo.observe(lifecycleOwner, mUiChatInfoObserver);
        mChatVM.uiMessageState.observe(lifecycleOwner, mUiMessageInfoObserver);
    }

    private void _proUiChatInfoUpdate(UiChatInfo uiChatInfo) {
        if (uiChatInfo == null) {
            Log.d(TAG, "[x] _proUiChatInfoUpdate #134");
            return;
        }

        String diplayTitle = uiChatInfo.getDisplayName() != null ? uiChatInfo.getDisplayName() : uiChatInfo.getNickname();
        mBinding.tvNickname.setText(diplayTitle);
    }

    private void _proUiMessageInfoUpdate(UiMessageState state) {
        Log.d(TAG, "_proUiMessageInfoUpdate #144" + state);
        if (!(state instanceof UiMessageState.DisPlay)) {
            Log.d(TAG, "[x] _proUiMessageInfoUpdate #144");
            return;
        }

        mAdapter.submitList(((UiMessageState.DisPlay) state).getMsgInfoList(), () -> {
            mBinding.rvMsg.scrollToPosition(mAdapter.getItemCount() - 1);
        });
    }


    private void sendMsg() {
        String msg = mBinding.etInputText.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            MyToast.showToast(getContext(), "");
            return;
        }

        String currentEmail = sp.getString("email", "");


        mMsgManager.sendMsg(token, currentEmail, mFriendInfo.getEmail(), msg, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                MyToast.showToast(getContext(), result ? "发送成功" : "发送失败");
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
    }

}