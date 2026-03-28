package com.memory.wq.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.memory.wq.activities.AudioActivity;
import com.memory.wq.activities.SearchMovieActivity;
import com.memory.wq.adapters.RoomAdapter;
import com.memory.wq.beans.RoomInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.CowatchLayoutBinding;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.interfaces.OnRoomClickListener;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CowatchFragment extends Fragment {

    private static final String TAG = "WQ_CowatchFragment";
    private final RoomAdapter mRoomAdapter = new RoomAdapter(new OnRoomClickListenerImpl());
    private CowatchLayoutBinding mBinding;
    private final MovieManager mMovieManager = new MovieManager();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = CowatchLayoutBinding.inflate(inflater, container, false);
        initView();
        initData();
        return mBinding.getRoot();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            loadRooms();
        }
    }

    private void initData() {
        loadRooms();
    }

    private void loadRooms() {
        mMovieManager.getRooms(new ResultCallback<List<RoomInfo>>() {
            @Override
            public void onSuccess(List<RoomInfo> result) {
                Log.d(TAG, "onSuccess: " + result);
                mRoomAdapter.submitList(result);
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private void initView() {
        mBinding.rvRoom.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.rvRoom.setAdapter(mRoomAdapter);

        mBinding.tvCreateRoom.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), SearchMovieActivity.class));
        });

        mBinding.tvJoinRandom.setOnClickListener(view -> {
            mMovieManager.getRooms(new ResultCallback<List<RoomInfo>>() {

                @Override
                public void onSuccess(List<RoomInfo> result) {
                    if (result!=null && !result.isEmpty()) {
                        int index = ThreadLocalRandom.current().nextInt(result.size());
                        RoomInfo roomInfo = result.get(index);

                        Intent intent = new Intent(getContext(), AudioActivity.class);
                        intent.putExtra(AppProperties.ROLE_TYPE, RoleType.ROLE_TYPE_AUDIENCE);
                        intent.putExtra(AppProperties.ROOM_ID, roomInfo.getRoomId());
                        startActivity(intent);

                        return;
                    }

                    MyToast.showToast(getContext(), "暂时没有可加入的房间");
                }

                @Override
                public void onError(String err) {

                }
            });
        });
    }

    private class OnRoomClickListenerImpl implements OnRoomClickListener {

        @Override
        public void onRoomClick(RoomInfo roomInfo) {
            Intent intent = new Intent(getContext(), AudioActivity.class);
            intent.putExtra(AppProperties.ROLE_TYPE, RoleType.ROLE_TYPE_AUDIENCE);
            intent.putExtra(AppProperties.ROOM_ID, roomInfo.getRoomId());
            startActivity(intent);
        }
    }
}