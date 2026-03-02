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

import com.memory.wq.activities.PostDetailActivity;
import com.memory.wq.adapters.WorksAdapter;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.databinding.FragmentWorksLayoutBinding;
import com.memory.wq.interfaces.OnPostClickListener;
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
    private final WorksAdapter mAdapter = new WorksAdapter(new OnPostClickListenerImpl());
    private final PostManager mPostManager = new PostManager();
    ;

    private int currentPage = 1;
    private final int pageSize = 15;
    private boolean hasNextPage = true;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentWorksLayoutBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView();
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            loadNextPage();
        }
    }

    private void initData() {
        loadNextPage();
    }

    private void initView() {
        mBinding.rvWorks.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mBinding.rvWorks.setAdapter(mAdapter);

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

    private void loadNextPage() {
        if (isLoading || !hasNextPage) {
            return;
        }
        isLoading = true;

        QueryPostInfo query = new QueryPostInfo();
        query.setPage(currentPage);
        query.setSize(pageSize);
        mPostManager.getMyPost(query, new ResultCallback<PageResult<PostInfo>>() {
            @Override
            public void onSuccess(PageResult<PostInfo> result) {
                List<PostInfo> newData = result.getResultList();
                if (newData != null && !newData.isEmpty()) {
                    mAdapter.submitList(newData);
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

    private class OnPostClickListenerImpl implements OnPostClickListener {

        @Override
        public void onPostClick(int position, PostInfo post) {
            Intent intent = new Intent(getContext(), PostDetailActivity.class);
            intent.putExtra(AppProperties.POSTID, post.getPostId());
            startActivity(intent);
        }
    }
}
