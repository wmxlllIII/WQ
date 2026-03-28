package com.memory.wq.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.memory.wq.activities.AudioActivity;
import com.memory.wq.adapters.VpHistoryAdapter;
import com.memory.wq.adapters.WatchHistoryAdapter;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.beans.WatchHistoryInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.HistoryLayoutBinding;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.interfaces.OnMovieClickListener;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    private HistoryLayoutBinding mBinding;
    private static final int PAGE_WORK = 0;
    private static final int PAGE_LIKE = 1;
    private static final int PAGE_TRACE = 2;
    private final MovieManager mMovieManager = new MovieManager();
    private final GetWatchHistoryCallback mWatchHistoryCallback = new GetWatchHistoryCallback();
    private final WatchHistoryAdapter mWatchHistoryAdapter = new WatchHistoryAdapter(new OnMovieClickListenerImpl());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = HistoryLayoutBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView();
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            loadWatchHistory();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWatchHistory();
    }

    private void initData() {
        loadWatchHistory();
    }

    private void loadWatchHistory() {
        mMovieManager.getWatchHistory(mWatchHistoryCallback);
    }

    private void initView() {
        mBinding.rvRecentView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        mBinding.rvRecentView.setAdapter(mWatchHistoryAdapter);

        VpHistoryAdapter mAdapter = new VpHistoryAdapter(this);
        mBinding.vpHistory.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mBinding.vpHistory.setAdapter(mAdapter);
        mBinding.vpHistory.setCurrentItem(PAGE_WORK, false);
        updateIndicator();
        mBinding.vpHistory.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicator();
            }
        });

        mBinding.rlPageWorks.setOnClickListener(view -> {
            mBinding.vpHistory.setCurrentItem(PAGE_WORK, true);
            updateIndicator();
        });

        mBinding.rlPageLikes.setOnClickListener(view -> {
            mBinding.vpHistory.setCurrentItem(PAGE_LIKE, true);
            updateIndicator();
        });

        mBinding.rlPageTrack.setOnClickListener(view -> {
            mBinding.vpHistory.setCurrentItem(PAGE_TRACE, true);
            updateIndicator();
        });
    }

    private void updateIndicator() {
        int posi = mBinding.vpHistory.getCurrentItem();
        mBinding.tvBarIndiA.setVisibility(getIndicatorVisibility(posi == PAGE_WORK));
        mBinding.tvBarIndiB.setVisibility(getIndicatorVisibility(posi == PAGE_LIKE));
        mBinding.tvBarIndiC.setVisibility(getIndicatorVisibility(posi == PAGE_TRACE));
    }

    private int getIndicatorVisibility(boolean shouldShow) {
        return shouldShow ? View.VISIBLE : View.GONE;
    }

    private class GetWatchHistoryCallback implements ResultCallback<List<WatchHistoryInfo>> {

        @Override
        public void onSuccess(List<WatchHistoryInfo> watchHistoryList) {
            mWatchHistoryAdapter.submitList(watchHistoryList);
        }

        @Override
        public void onError(String err) {

        }
    }

    private class OnMovieClickListenerImpl implements OnMovieClickListener {

        @Override
        public void onCoverClick(MovieInfo movieInfo) {
            Intent intent = new Intent(getActivity(), AudioActivity.class);
            intent.putExtra(AppProperties.ROLE_TYPE, RoleType.ROLE_TYPE_BROADCASTER);
            intent.putExtra(AppProperties.MOVIE, movieInfo);
            startActivity(intent);
        }

        @Override
        public void onNameClick(int movieId) {

        }
    }
}
