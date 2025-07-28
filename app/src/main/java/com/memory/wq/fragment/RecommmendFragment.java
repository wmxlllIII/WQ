package com.memory.wq.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.memory.wq.R;
import com.memory.wq.adapters.BannerAdapter;
import com.memory.wq.adapters.HeaderAdapter;
import com.memory.wq.adapters.RecommendAdapter;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.managers.BannerManager;
import com.memory.wq.managers.PostManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.PageResult;
import com.memory.wq.utils.ResultCallback;

import java.util.ArrayList;
import java.util.List;

public class RecommmendFragment extends Fragment {


    private BannerManager manager;

    private List<Integer> bannerImageList;

    private List<ImageView> indicatorsList = new ArrayList<>();
    private RecyclerView rv_recomment;
    private List<PostInfo> postInfoList;
    private View bannerHeader;
    private RecommendAdapter recommendAdapter;
    private BannerAdapter bannerAdapter;
    private ViewPager2 vp_banner;
    private LinearLayout ll_indicator;
    private PostManager postManager;
    private String token;
    private int currentPage = 1;
    private final int pageSize = 15;
    private boolean hasNextPage = true;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommend_layout, null, false);
        initView(view);
        initData();
        setRecyclerView();
        return view;
    }

    private void setRecyclerView() {
        createHeaderView();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        HeaderAdapter headerAdapter = new HeaderAdapter(bannerHeader);
        recommendAdapter = new RecommendAdapter(postInfoList);
        rv_recomment.setLayoutManager(layoutManager);
        ConcatAdapter concatAdapter = new ConcatAdapter(headerAdapter, recommendAdapter);
        rv_recomment.setAdapter(concatAdapter);
        rv_recomment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                    int[] lastVisibleItems = layoutManager.findLastVisibleItemPositions(null);
                    int lastVisibleItem = getMaxPosition(lastVisibleItems);
                    int totalItemCount = layoutManager.getItemCount();

                    if (hasNextPage && lastVisibleItem >= totalItemCount - 2) {
                        loadNextPageData();
                    }
                }
            }
        });
        setBanner();
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
        bannerAdapter = new BannerAdapter(getContext(), bannerImageList);
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

    private void initData() {
        //TODO
        bannerImageList = new ArrayList<>();
        bannerImageList.add(R.mipmap.ic_bannertest1);
        bannerImageList.add(R.mipmap.ic_bannertest2);
        bannerImageList.add(R.mipmap.ic_bannertest3);

        postInfoList = new ArrayList<>();
        postManager = new PostManager();
        token = SPManager.getUserInfo(getContext()).getToken();
        loadNextPageData();
    }

    private void loadNextPageData() {
        if (hasNextPage) {
            QueryPostInfo queryPostInfo = new QueryPostInfo();
            queryPostInfo.setPage(currentPage);
            queryPostInfo.setSize(pageSize);
            postManager.getPosts(token, queryPostInfo, new ResultCallback<PageResult<PostInfo>>() {
                @Override
                public void onSuccess(PageResult<PostInfo> result) {
                    getActivity().runOnUiThread(() -> {
                        currentPage = result.getPage();
                        hasNextPage = result.isHasNext();
                        postInfoList.addAll(result.getResultList());
                        recommendAdapter.notifyDataSetChanged();
                    });

                }

                @Override
                public void onError(String err) {
                }
            });
        } else {
            //TODO 没有下一页了
            MyToast.showToast(getContext(), "没有更多数据了");
        }

    }

    private void initView(View view) {
        rv_recomment = (RecyclerView) view.findViewById(R.id.rv_recomment);


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
}
