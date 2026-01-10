package com.memory.wq.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.memory.wq.activities.PostDetailActivity;
import com.memory.wq.adapters.RecommendAdapter;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ConcernLayoutBinding;
import com.memory.wq.interfaces.OnPostClickListener;
import com.memory.wq.vm.PostViewModel;

import java.util.List;

public class ConcernFragment extends Fragment {

    private static final String TAG = "WQ_ConcernFragment";
    private ConcernLayoutBinding mBinding;
    private PostViewModel mPostVM;
    private final RecommendAdapter mRecommendAdapter = new RecommendAdapter(new OnPostClickListenerImpl());
    private final Observer<List<PostInfo>> mPostObserver = this::_proPostUpdate;

    private void _proPostUpdate(List<PostInfo> posts) {
        if (posts == null || posts.isEmpty()) {
            Log.d(TAG, "[x] _proPostUpdate #57");
            return;
        }

        mRecommendAdapter.submitList(posts);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = ConcernLayoutBinding.inflate(inflater, container, false);
        initView();
        initData();
        return mBinding.getRoot();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPostVM.refreshFollowerPosts();
        }
    }

    private void initData() {
        mPostVM.loadFollowerNextPage();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof AppCompatActivity)) {
            Log.d(TAG, "onAttach #no match");
            return;
        }

        createViewModel((AppCompatActivity) context);
    }

    private void createViewModel(AppCompatActivity activity) {
        mPostVM = new ViewModelProvider(activity).get(PostViewModel.class);
    }

    private void initView() {
        mBinding.rvConcern.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mBinding.rvConcern.setAdapter(mRecommendAdapter);
        mBinding.rvConcern.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0) return;

                StaggeredGridLayoutManager lm =
                        (StaggeredGridLayoutManager) recyclerView.getLayoutManager();

                int[] lastVisibleItems = lm.findLastVisibleItemPositions(null);
                int lastVisibleItem = getMaxPosition(lastVisibleItems);
                int totalItemCount = lm.getItemCount();

                if (lastVisibleItem >= totalItemCount - 4) {
                    mPostVM.loadFollowerNextPage();
                }
            }
        });
        initObserver();
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

    private void initObserver() {
        mPostVM.followerPostList.observe(getViewLifecycleOwner(), mPostObserver);
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
