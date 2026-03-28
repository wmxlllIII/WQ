package com.memory.wq.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.memory.wq.R;
import com.memory.wq.activities.PostDetailActivity;
import com.memory.wq.adapters.WorksAdapter;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.FragmentLikeBinding;
import com.memory.wq.interfaces.OnPostClickListener;
import com.memory.wq.managers.PostManager;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class LikeFragment extends Fragment {

    private static final String TAG = "WQ_LikeFragment";
    private FragmentLikeBinding mBinding;
    private final WorksAdapter mAdapter = new WorksAdapter(new OnPostClickListenerImpl());
    private final PostManager mPostManager = new PostManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentLikeBinding.inflate(inflater, container, false);
        initView();
        initData();
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLikePost();
    }

    private void initData() {
       loadLikePost();
    }

    private void loadLikePost(){
        mPostManager.getLikePost(new ResultCallback<List<PostInfo>>() {
            @Override
            public void onSuccess(List<PostInfo> posts) {
                mAdapter.submitList(posts);
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private void initView() {
        mBinding.rvLike.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mBinding.rvLike.setAdapter(mAdapter);
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