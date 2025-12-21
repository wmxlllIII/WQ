package com.memory.wq.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.memory.wq.activities.AudioActivity;
import com.memory.wq.activities.ChooseMovieActivity;
import com.memory.wq.adapters.RoomAdapter;
import com.memory.wq.beans.RoomInfo;
import com.memory.wq.databinding.CowatchLayoutBinding;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class CowatchFragment extends Fragment {

    private static final String TAG = "WQ_CowatchFragment";
    private RoomAdapter mRoomAdapter;
    private String token;
    private CowatchLayoutBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = CowatchLayoutBinding.inflate(inflater, container, false);
        initView();
        initData();
        return mBinding.getRoot();
    }

    private void initData() {

        SharedPreferences sp = getContext().getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");

        MovieManager movieManager = new MovieManager();
        movieManager.getRooms(new ResultCallback<List<RoomInfo>>() {
            @Override
            public void onSuccess(List<RoomInfo> result) {
                mRoomAdapter = new RoomAdapter(getContext(), result);
                mBinding.rvRoom.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                mBinding.rvRoom.setAdapter(mRoomAdapter);

            }

            @Override
            public void onError(String err) {

            }
        });

    }

    private void initView() {
        mBinding.tvCreateRoom.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), ChooseMovieActivity.class));
        });

        mBinding.tvJoinRandom.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), AudioActivity.class));
        });
    }

}