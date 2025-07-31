package com.memory.wq.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;

public class ConcernFragment extends Fragment {

    private RecyclerView rv_concern;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.concern_layout, null, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rv_concern = (RecyclerView) view.findViewById(R.id.rv_concern);
    }
}
