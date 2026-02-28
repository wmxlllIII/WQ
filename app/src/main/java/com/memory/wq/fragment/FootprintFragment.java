package com.memory.wq.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.memory.wq.R;
import com.memory.wq.databinding.FragmentFootprintBinding;

public class FootprintFragment extends Fragment {

    private FragmentFootprintBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFootprintBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}