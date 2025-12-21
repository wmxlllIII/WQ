package com.memory.wq.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.memory.wq.R;
import com.memory.wq.databinding.FragmentChatDetailBinding;
import com.memory.wq.enumertions.ChatPage;
import com.memory.wq.vm.ChatViewModel;

public class ChatDetailFragment extends Fragment {
    private static final String TAG = "WQ_ChatDetailFragment";
    private FragmentChatDetailBinding mBinding;
    private ChatViewModel mChatVM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
            mChatVM.navigateTo(ChatPage.CHAT);
        });

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mChatVM.navigateTo(ChatPage.CHAT);
            }
        });
    }


}