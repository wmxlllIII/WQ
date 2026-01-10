package com.memory.wq.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.memory.wq.R;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.interfaces.OnPostClickListener;
import com.memory.wq.vm.PostViewModel;
import com.memory.wq.activities.PostDetailActivity;
import com.memory.wq.adapters.BannerAdapter;
import com.memory.wq.adapters.HeaderAdapter;
import com.memory.wq.adapters.RecommendAdapter;
import com.memory.wq.databinding.RecommendLayoutBinding;
import com.memory.wq.managers.BannerManager;
import com.memory.wq.constants.AppProperties;

import java.util.ArrayList;
import java.util.List;

public class RecommmendFragment extends Fragment {

    public static final String TAG = "RecommmendFragment";
    private BannerManager manager;

    private List<ImageView> indicatorsList = new ArrayList<>();
    private View bannerHeader;
    private final RecommendAdapter mRecommendAdapter = new RecommendAdapter(new OnPostClickListenerImpl());
    private BannerAdapter bannerAdapter;
    private ViewPager2 vp_banner;
    private LinearLayout ll_indicator;
    private PostViewModel mPostVM;
    private RecommendLayoutBinding mBinding;
    private final Observer<List<Integer>> mBannerObserver = this::_proBannerUpdate;
    private final Observer<List<PostInfo>> mPostObserver = this::_proPostUpdate;
    private final List<Integer> bannerImageList = new ArrayList<>();


    private void _proPostUpdate(List<PostInfo> postInfos) {
        if (postInfos == null || postInfos.isEmpty()) {
            Log.d(TAG, "[x] _proPostUpdate #57");
            return;
        }

        mRecommendAdapter.submitList(postInfos);
    }

    private void _proBannerUpdate(List<Integer> integers) {
        if (integers == null || integers.isEmpty()) {
            Log.d(TAG, "[x] _proBannerUpdate #61");
            return;
        }

        bannerImageList.clear();
        bannerImageList.addAll(integers);

        setBanner();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostVM = new ViewModelProvider(this).get(PostViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = RecommendLayoutBinding.inflate(inflater, container, false);
        initView();
        initObserver();
        initData();
        return mBinding.getRoot();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPostVM.refreshRecPosts();
        }
    }

    private void initData() {
        mPostVM.loadBanner();
        mPostVM.refreshRecPosts();
    }

    private void initObserver() {
        mPostVM.bannerList.observe(getViewLifecycleOwner(), mBannerObserver);
        mPostVM.recPostList.observe(getViewLifecycleOwner(), mPostObserver);
    }

    private void initView() {
        createHeaderView();

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        HeaderAdapter headerAdapter = new HeaderAdapter(bannerHeader);
        ConcatAdapter concatAdapter = new ConcatAdapter(headerAdapter, mRecommendAdapter);

        mBinding.rvRecomment.setLayoutManager(layoutManager);
        mBinding.rvRecomment.setAdapter(concatAdapter);


        mBinding.rvRecomment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0) return;

                StaggeredGridLayoutManager lm =
                        (StaggeredGridLayoutManager) recyclerView.getLayoutManager();

                int[] lastVisibleItems = lm.findLastVisibleItemPositions(null);
                int lastVisibleItem = getMaxPosition(lastVisibleItems);
                int totalItemCount = lm.getItemCount();

                if (lastVisibleItem >= totalItemCount - 4) {
                    mPostVM.loadNextRecPage();
                }
            }
        });
    }

    private int getMaxPosition(int[] positions) {
        int max = positions[0];
        for (int pos : positions) {
            if (pos > max) {
                max = pos;
            }
        }
        return max;
    }

    private void setBanner() {
        bannerAdapter = new BannerAdapter(bannerImageList);
        vp_banner.setAdapter(bannerAdapter);

        initIndicator();
        setViewPagerListener();
        manager = new BannerManager(vp_banner);
        manager.setupWithAdapter(bannerAdapter);
        manager.startAutoScroll();
    }

    private void createHeaderView() {
        bannerHeader = LayoutInflater.from(getContext()).inflate(R.layout.item_banner_header_layout, null);
        vp_banner = (ViewPager2) bannerHeader.findViewById(R.id.vp_banner);
        ll_indicator = (LinearLayout) bannerHeader.findViewById(R.id.ll_indicator);
    }


    private void initIndicator() {
        ll_indicator.removeAllViews();
        indicatorsList.clear();
        for (int i = 0; i < bannerImageList.size(); i++) {
            ImageView indicator = new ImageView(getContext());
            indicator.setImageResource(R.drawable.indicator_default);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            indicator.setLayoutParams(params);
            ll_indicator.addView(indicator);
            indicatorsList.add(indicator);
        }
        updateIndicator(0);
    }

    private void updateIndicator(int position) {
        int actualPosition = position % bannerImageList.size();
        for (int i = 0; i < indicatorsList.size(); i++) {
            indicatorsList.get(i).setImageResource(i == actualPosition ? R.drawable.indicator_selected : R.drawable.indicator_default);
        }
    }

    private void setViewPagerListener() {
        vp_banner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    manager.pauseAutoScroll();
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    manager.startAutoScroll();
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (manager != null) {
            manager.startAutoScroll();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (manager != null) {
            manager.pauseAutoScroll();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (manager != null) {
            manager.stopAutoScroll();
            manager = null;
        }
        if (bannerHeader != null) {
            bannerHeader = null;
        }
    }

    private class OnPostClickListenerImpl implements OnPostClickListener {

        @Override
        public void onPostClick(int position, PostInfo postInfo) {
            Intent intent = new Intent(getContext(), PostDetailActivity.class);
            intent.putExtra(AppProperties.POSTINFO, postInfo);
            startActivity(intent);
        }
    }
}
