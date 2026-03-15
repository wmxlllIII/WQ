package com.memory.wq.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.memory.wq.adapters.InviteMemberAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.databinding.FragmentInviteMemberBinding;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.vm.ChatViewModel;

import java.util.List;
import java.util.Set;

public class InviteMemberFragment extends Fragment {

    private static final String TAG = "WQ_InviteMemberFragment";
    private FragmentInviteMemberBinding mBinding;
    private ChatViewModel mChatVM;
    private final FriendManager mFriendManager = new FriendManager();
    private final FriendCallback mFriendCallback = new FriendCallback();
    private final InviteMemberAdapter mInviteMemberAdapter = new InviteMemberAdapter();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentInviteMemberBinding.inflate(inflater, container, false);
        initView();
        initData();
        return mBinding.getRoot();
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

    private void initData() {
        mFriendManager.getFriends(mFriendCallback);
    }

    private void createViewModel(AppCompatActivity activity) {
        mChatVM = new ViewModelProvider(activity).get(ChatViewModel.class);
    }

    private void initView() {
        mBinding.rvMembers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.rvMembers.setAdapter(mInviteMemberAdapter);
        mBinding.ivBack.setOnClickListener(v -> {
            mChatVM.navigateBack();
        });


        mBinding.tvConfirm.setOnClickListener(v -> {
            if (mInviteMemberAdapter.getSelectedUsers() == null || mInviteMemberAdapter.getSelectedUsers().isEmpty()) {
                Log.d(TAG, "[x] initView #75");
                return;
            }
            Set<Long> selectedUsers = mInviteMemberAdapter.getSelectedUsers();
            mChatVM.buildGroup("群名称","群头像", selectedUsers);
        });
    }

    private class FriendCallback implements ResultCallback<List<FriendInfo>> {

        @Override
        public void onSuccess(List<FriendInfo> result) {
            mInviteMemberAdapter.submitList(result);
        }

        @Override
        public void onError(String err) {

        }
    }


}