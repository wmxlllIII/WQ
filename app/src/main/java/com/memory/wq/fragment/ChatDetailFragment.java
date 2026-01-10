package com.memory.wq.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.memory.wq.activities.PersonInfoActivity;
import com.memory.wq.adapters.MemberAdapter;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.FragmentChatDetailBinding;
import com.memory.wq.enumertions.ChatPage;
import com.memory.wq.interfaces.OnMemberClickListener;
import com.memory.wq.managers.UserManager;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.vm.ChatViewModel;

public class ChatDetailFragment extends Fragment {
    private static final String TAG = "WQ_ChatDetailFragment";
    private FragmentChatDetailBinding mBinding;
    private ChatViewModel mChatVM;
    private final UserManager mUserManager = new UserManager();
    private final MemberAdapter mMemberAdapter = new MemberAdapter(new OnMemberClickListenerImpl());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentChatDetailBinding.inflate(inflater, container, false);
        initView();
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

    private void createViewModel(AppCompatActivity activity) {
        mChatVM = new ViewModelProvider(activity).get(ChatViewModel.class);
    }

    private void initView() {
        mBinding.ivBack.setOnClickListener(v -> {
            mChatVM.navigateTo(ChatPage.CHAT_INDIVIDUAL);
        });

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mChatVM.navigateTo(ChatPage.CHAT_INDIVIDUAL);
            }
        });

        mBinding.tvDelete.setOnClickListener(v -> {
            mUserManager.deleteFriend(mChatVM.chatId.getValue(), new ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    MyToast.showToast(getContext(),"删除成功");
                }

                @Override
                public void onError(String err) {
                    MyToast.showToast(getContext(),"删除失败");
                }
            });
        });

        mBinding.rvChatMember.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mBinding.rvChatMember.setAdapter(mMemberAdapter);
    }

    private class OnMemberClickListenerImpl implements OnMemberClickListener {
        @Override
        public void onMemberClick(long memberId) {
            Intent intent = new Intent(getContext(), PersonInfoActivity.class);
            intent.putExtra(AppProperties.PERSON_ID, memberId);
            startActivity(intent);
        }

        @Override
        public void onInviteClick() {
            mChatVM.navigateTo(ChatPage.CHAT_INVITE_MEMBER);
        }

        @Override
        public void onDisplayClick() {

        }
    }

}