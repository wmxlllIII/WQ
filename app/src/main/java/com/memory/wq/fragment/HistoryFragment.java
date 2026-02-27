package com.memory.wq.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.memory.wq.adapters.VpHistoryAdapter;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.beans.WatchHistoryInfo;
import com.memory.wq.databinding.HistoryLayoutBinding;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.managers.PostManager;
import com.memory.wq.utils.PageResult;
import com.memory.wq.utils.ResultCallback;

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    private HistoryLayoutBinding mBinding;
    private static final int PAGE_WORK = 0;
    private static final int PAGE_LIKE = 1;
    private static final int PAGE_TRACE = 2;
    private final MovieManager mMovieManager = new MovieManager();
    private final PostManager mPostManager = new PostManager();
    private final GetWatchHistoryCallback mWatchHistoryCallback = new GetWatchHistoryCallback();
    private final GetMyPostCallback mGetMyPostCallback = new GetMyPostCallback();

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

    private void initData() {
        mMovieManager.getWatchHistory(mWatchHistoryCallback);
        mPostManager.getMyPost(new QueryPostInfo(), mGetMyPostCallback);
    }

    private void initView() {
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

    private class GetWatchHistoryCallback implements ResultCallback<WatchHistoryInfo> {

        @Override
        public void onSuccess(WatchHistoryInfo watchHistory) {

        }

        @Override
        public void onError(String err) {

        }
    }

    private class GetMyPostCallback implements ResultCallback<PageResult<PostInfo>> {
        @Override
        public void onSuccess(PageResult<PostInfo> result) {

        }

        @Override
        public void onError(String err) {

        }
    }
}
