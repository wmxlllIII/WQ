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
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.activities.PostInfoActivity;
import com.memory.wq.adapters.WorksAdapter;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.databinding.FragmentWorksLayoutBinding;
import com.memory.wq.managers.PostManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.PageResult;
import com.memory.wq.utils.ResultCallback;

import java.util.ArrayList;
import java.util.List;

public class WorksFragment extends Fragment {

    private FragmentWorksLayoutBinding mBinding;
    private WorksAdapter mAdapter;
    private PostManager mPostManager;

    private int currentPage = 1;
    private final int pageSize = 15;
    private boolean hasNextPage = true;
    private boolean isLoading = false;
    private String token;

    private List<PostInfo> mPostList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentWorksLayoutBinding.inflate(inflater, container, false);
        initRecycleView();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView();
        initData();
    }

    private void initData() {
        mPostManager = new PostManager();
        token = SPManager.getUserInfo().getToken();
        loadNextPage();
    }

    private void initView() {
        mAdapter.setOnItemClickListener((post, position) -> {
            Intent intent = new Intent(getContext(), PostInfoActivity.class);
            intent.putExtra(AppProperties.POSTINFO, post);
            startActivity(intent);
        });

        mBinding.rvWorks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && !isLoading && hasNextPage) {
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();

                    if (lastVisibleItem >= totalItemCount - 6) {
                        loadNextPage();
                    }
                }
            }
        });
    }

    private void initRecycleView() {
        mBinding.rvWorks.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAdapter = new WorksAdapter();
        mBinding.rvWorks.setAdapter(mAdapter);
    }

    private void loadNextPage() {
        if (isLoading || !hasNextPage){
            return;
        }
        isLoading = true;

        QueryPostInfo query = new QueryPostInfo();
        query.setPage(currentPage);
        query.setSize(pageSize);
        mPostManager.getMyPost(token, query, new ResultCallback<PageResult<PostInfo>>() {
            @Override
            public void onSuccess(PageResult<PostInfo> result) {
                List<PostInfo> newData = result.getResultList();
                if (newData != null && !newData.isEmpty()) {
                    int oldSize = mPostList.size();
                    mPostList.addAll(newData);
                    mAdapter.setData(mPostList);
                    currentPage++;
                    hasNextPage = result.isHasNext();
                } else {
                    hasNextPage = false;
                }
                isLoading = false;
            }

            @Override
            public void onError(String err) {
                MyToast.showToast(getContext(), "加载失败：" + err);
                isLoading = false;
            }
        });
    }
}
