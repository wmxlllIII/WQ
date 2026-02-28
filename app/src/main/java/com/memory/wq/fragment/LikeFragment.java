package com.memory.wq.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.memory.wq.R;
import com.memory.wq.databinding.FragmentLikeBinding;

public class LikeFragment extends Fragment {

    private FragmentLikeBinding binding;

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
}