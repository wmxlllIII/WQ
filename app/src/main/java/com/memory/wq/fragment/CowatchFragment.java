package com.memory.wq.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;
import com.memory.wq.activities.AudioActivity;
import com.memory.wq.activities.ChooseMovieActivity;
import com.memory.wq.adapters.RoomAdapter;
import com.memory.wq.beans.RoomInfo;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.utils.ResultCallback;

import java.util.ArrayList;
import java.util.List;

public class CowatchFragment extends Fragment implements View.OnClickListener {

    private View view;
    private RelativeLayout rl_create_room;
    private RelativeLayout rl_random;
    private RecyclerView rv_room;
    private RoomAdapter roomAdapter;
    private String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cowatch_layout, null, false);
        initView(view);
        initData();
        return view;
    }

    private void initData() {

        SharedPreferences sp = getContext().getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");

        MovieManager movieManager = new MovieManager();
        movieManager.getRooms(new ResultCallback<List<RoomInfo>>() {
            @Override
            public void onSuccess(List<RoomInfo> result) {
                if (getContext() != null)
                    ((Activity) getContext()).runOnUiThread(() -> {
                        rv_room.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        roomAdapter = new RoomAdapter(getContext(), result);
                        rv_room.setAdapter(roomAdapter);
                    });
            }

            @Override
            public void onError(String err) {

            }
        });

    }

    private void initView(View view) {

        rl_create_room = (RelativeLayout) view.findViewById(R.id.rl_create_room);
        rl_random = (RelativeLayout) view.findViewById(R.id.rl_random);
        rv_room = (RecyclerView) view.findViewById(R.id.rv_room);

        rl_create_room.setOnClickListener(this);

        rl_random.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.rl_create_room:
                intent = new Intent(getContext(), ChooseMovieActivity.class);
                break;
            case R.id.rl_random:
                intent = new Intent(getContext(), AudioActivity.class);
                break;
        }
        if (intent != null)
            startActivity(intent);
    }
}
