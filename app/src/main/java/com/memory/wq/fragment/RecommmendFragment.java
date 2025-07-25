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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.memory.wq.R;
import com.memory.wq.adapters.BannerAdapter;
import com.memory.wq.adapters.RecommendAdapter;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.managers.BannerManager;
import com.memory.wq.managers.PostManager;
import com.memory.wq.managers.SPManager;
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
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 1;
            }
        });
        rv_recomment.setLayoutManager(layoutManager);

        recommendAdapter = new RecommendAdapter(postInfoList, bannerHeader);
        rv_recomment.setAdapter(recommendAdapter);

        setBanner();
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
//        for (int i = 0; i < 20; i++) {
//            postInfoList.add(new PostInfo());
//        }
        postManager = new PostManager();
        token = SPManager.getUserInfo(getContext()).getToken();

        QueryPostInfo queryPostInfo = new QueryPostInfo();
        queryPostInfo.setPage(1);
        queryPostInfo.setSize(10);
        postManager.getPosts(token, queryPostInfo, new ResultCallback<PageResult<PostInfo>>() {
            @Override
            public void onSuccess(PageResult<PostInfo> result) {
                postInfoList.addAll(result.getResultList());
                getActivity().runOnUiThread(()->{
                    recommendAdapter.notifyDataSetChanged();
                });

            }

            @Override
            public void onError(String err) {

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
