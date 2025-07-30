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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.memory.wq.R;
import com.memory.wq.RecommendViewModel;
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

    public static final String TAG = "RecommmendFragment";
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
    private final int pageSize = 10;
    private boolean hasNextPage = true;
    private boolean isLoading = false; // 是否正在加载数据
    private RecommendViewModel recommendVM;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recommendVM = new ViewModelProvider(this).get(RecommendViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommend_layout, null, false);
        initView(view);
        if (recommendVM.postInfoList.isEmpty()) {
            initData();
        } else {
            restoreData();
        }
        setRecyclerView();
        return view;
    }

    private void restoreData() {
        postInfoList = recommendVM.postInfoList;
        currentPage = recommendVM.currentPage;
        hasNextPage = recommendVM.hasNextPage;
    }

    private void setRecyclerView() {
        createHeaderView();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        HeaderAdapter headerAdapter = new HeaderAdapter(bannerHeader);
        recommendAdapter = new RecommendAdapter(recommendVM.postInfoList);
        rv_recomment.setLayoutManager(layoutManager);
        ConcatAdapter concatAdapter = new ConcatAdapter(headerAdapter, recommendAdapter);
        rv_recomment.setAdapter(concatAdapter);
        rv_recomment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !recommendVM.isLoading && recommendVM.hasNextPage) {
                    StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                    int[] lastVisibleItems = layoutManager.findLastVisibleItemPositions(null);
                    int lastVisibleItem = getMaxPosition(lastVisibleItems);
                    int totalItemCount = layoutManager.getItemCount();

                    if (hasNextPage && lastVisibleItem >= totalItemCount - 4) {
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
        if (recommendVM.bannerImageList.isEmpty()) {
            recommendVM.bannerImageList.add(R.mipmap.ic_bannertest1);
            recommendVM.bannerImageList.add(R.mipmap.ic_bannertest2);
            recommendVM.bannerImageList.add(R.mipmap.ic_bannertest3);
        }
        bannerImageList = recommendVM.bannerImageList;

        postManager = new PostManager();
        token = SPManager.getUserInfo(getContext()).getToken();
        loadNextPageData();
    }

    private void loadNextPageData() {
        if (recommendVM.isLoading || !recommendVM.hasNextPage)
            return;

        recommendVM.isLoading = true;
        QueryPostInfo queryPostInfo = new QueryPostInfo();
        queryPostInfo.setPage(recommendVM.currentPage);
        queryPostInfo.setSize(pageSize);
        postManager.getPosts(token, queryPostInfo, new ResultCallback<PageResult<PostInfo>>() {
            @Override
            public void onSuccess(PageResult<PostInfo> result) {
                getActivity().runOnUiThread(() -> {
                    int oldSize = recommendVM.postInfoList.size();
                    List<PostInfo> newData = result.getResultList();
                    if (newData != null && !newData.isEmpty()) {
                        recommendVM.postInfoList.addAll(newData);
                        recommendAdapter.notifyItemRangeInserted(oldSize, newData.size());
                        recommendVM.currentPage++;
                    }
                    recommendVM.hasNextPage = result.isHasNext();
                    recommendVM.isLoading = false;
                });


            }

            @Override
            public void onError(String err) {
                recommendVM.isLoading = false;
            }
        });


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
