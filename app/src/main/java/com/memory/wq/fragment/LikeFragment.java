package com.memory.wq.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.memory.wq.R;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.databinding.FragmentLikeBinding;
import com.memory.wq.managers.PostManager;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class LikeFragment extends Fragment {

    private FragmentLikeBinding binding;
    private final PostManager mPostManager = new PostManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLikeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView();
        initData();
    }

    private void initData() {
        mPostManager.getLikePost(new ResultCallback<List<PostInfo>>() {
            @Override
            public void onSuccess(List<PostInfo> result) {

            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private void initView() {

    }
}