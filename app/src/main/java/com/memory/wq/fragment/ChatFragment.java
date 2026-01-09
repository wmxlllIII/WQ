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
import com.memory.wq.activities.PersonalActivity;
import com.memory.wq.adapters.MsgAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.UiChatInfo;
import com.memory.wq.beans.UiMessageState;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.FragmentChatBinding;
import com.memory.wq.enumertions.ChatPage;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.interfaces.OnMsgItemClickListener;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.ShareConfirmDialog;
import com.memory.wq.vm.ChatViewModel;

import java.util.List;

public class ChatFragment extends Fragment {
    private static final String TAG = "WQ_ChatFragment";

    private FragmentChatBinding mBinding;
    private final MsgAdapter mAdapter = new MsgAdapter(new MsgItemCLickListener());
    private MsgInfo mLinkInfo;
    private MovieManager mMovieManager;
    private ChatViewModel mChatVM;

    private final Observer<UiChatInfo> mUiChatInfoObserver = this::_proUiChatInfoUpdate;
    private final Observer<UiMessageState> mUiMessageInfoObserver = this::_proUiMessageInfoUpdate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentChatBinding.inflate(inflater, container, false);
        initView();
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
        mBinding.rvMsg.setAdapter(mAdapter);
    }

    private void initView() {
        initObserver();
        mBinding.ivBack.setOnClickListener(v -> mChatVM.navigateBack());
        mBinding.btnSend.setOnClickListener(v -> sendMsg());
        mBinding.ivDetail.setOnClickListener(view -> {
            mChatVM.navigateTo(ChatPage.CHAT_DETAIL);
        });
    }

    private void initObserver() {
        mChatVM.uiChatInfo.observe(getViewLifecycleOwner(), mUiChatInfoObserver);
        mChatVM.uiMessageState.observe(getViewLifecycleOwner(), mUiMessageInfoObserver);
    }

    private void _proUiChatInfoUpdate(UiChatInfo uiChatInfo) {
        if (uiChatInfo == null) {
            Log.d(TAG, "[x] _proUiChatInfoUpdate #134");
            return;
        }

        String displayName = uiChatInfo.getDisplayName();
        mBinding.tvNickname.setText(
                TextUtils.isEmpty(displayName) ?
                        uiChatInfo.getMembers().get(0).getNickname() :
                        displayName
        );
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

        mChatVM.sendMsg(msg, success -> {
            Log.d(TAG, "sendMsg:" + msg + "===isSucc" + success);
            if (success) {
                mBinding.etInputText.setText("");
            }
            MyToast.showToast(getContext(), success ? "发送成功" : "发送失败");
        });
    }

//    private void showShareUI() {
//        ShareConfirmDialog dialog = new ShareConfirmDialog(getContext(), mFriendInfo, mLinkInfo, new ShareConfirmDialog.OnShareActionListener() {
//            @Override
//            public void onShare(MsgInfo shareMsg) {
//
//                mMovieManager.shareRoom(getContext(), token, shareMsg, new ResultCallback<Boolean>() {
//                    @Override
//                    public void onSuccess(Boolean result) {
//                        MyToast.showToast(getContext(), "分享成功");
//                        getActivity().finish();
//                    }
//
//                    @Override
//                    public void onError(String err) {
//                        MyToast.showToast(getContext(), "分享失败");
//                        getActivity().finish();
//                    }
//                });
//            }
//
//            @Override
//            public void onCancel() {
//                MyToast.showToast(getContext(), "用户取消分享");
//                getActivity().finish();
//            }
//        });
//        dialog.show();
//
//    }

    private class MsgItemCLickListener implements OnMsgItemClickListener {
        @Override
        public void onLinkClick(MsgInfo msgInfo) {
            Intent intent = new Intent(getContext(), AudioActivity.class);
            intent.putExtra(AppProperties.ROLE_TYPE, RoleType.ROLE_TYPE_AUDIENCE);
            intent.putExtra(AppProperties.ROOM_ID, msgInfo.getLinkContent());
            startActivity(intent);
            Log.d(TAG, "[✓] onLinkClick #184" + msgInfo.getLinkContent());
        }

        @Override
        public void onMsgLongClick(MsgInfo msgInfo) {

        }

        @Override
        public void onAvatarClick(long userId) {
            Intent intent = new Intent(getContext(), PersonalActivity.class);
            intent.putExtra(AppProperties.PERSON_ID, userId);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}