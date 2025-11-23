package com.memory.wq.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.memory.wq.R;
import com.memory.wq.databinding.FragmentChatDetailBinding;

public class ChatDetailFragment extends Fragment {
    private static final String TAG = "WQ_ChatDetailFragment";
    private FragmentChatDetailBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentChatDetailBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

}