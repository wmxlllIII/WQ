package com.memory.wq.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.memory.wq.R;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.managers.PostManager;
import com.memory.wq.utils.PageResult;
import com.memory.wq.utils.ResultCallback;

import java.util.ArrayList;
import java.util.List;

public class PostViewModel extends ViewModel {

    private static final String TAG = "WQ_PostViewModel";
    private int currentPage = 1;
    private final int pageSize = 10;
    private boolean hasNextPage = true;
    private boolean isLoading = false;

    private int follower_currentPage = 1;
    private final int follower_pageSize = 10;
    private boolean follower_hasNextPage = true;
    private boolean follower_isLoading = false;

    // 数据 LiveData
    private final MutableLiveData<List<PostInfo>> _recPostList = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<PostInfo>> recPostList = _recPostList;

    private final MutableLiveData<List<PostInfo>> _followerPostList = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<PostInfo>> followerPostList = _followerPostList;

    private final MutableLiveData<List<Integer>> _bannerList = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Integer>> bannerList = _bannerList;

    private final PostManager postManager = new PostManager();

    public void loadNextRecPage() {
        if (isLoading || !hasNextPage) {
            return;
        }

        isLoading = true;
        QueryPostInfo query = new QueryPostInfo();
        query.setPage(currentPage);
        query.setSize(pageSize);

        postManager.getPosts(query, new ResultCallback<PageResult<PostInfo>>() {
            @Override
            public void onSuccess(PageResult<PostInfo> result) {
                List<PostInfo> currentList = _recPostList.getValue();
                if (currentList == null) {
                    currentList = new ArrayList<>();
                }

                List<PostInfo> newData = result.getResultList();
                if (newData != null && !newData.isEmpty()) {
                    currentList.addAll(newData);
                    _recPostList.setValue(currentList);
                    currentPage++;
                }

                hasNextPage = result.isHasNext();
                isLoading = false;
            }

            @Override
            public void onError(String err) {
                isLoading = false;
            }
        });
    }

    public void refreshRecPosts() {
        // 重置分页状态
        currentPage = 1;
        hasNextPage = true;
        isLoading = false;

        QueryPostInfo query = new QueryPostInfo();
        query.setPage(currentPage);
        query.setSize(pageSize);

        isLoading = true;

        postManager.getPosts(query, new ResultCallback<PageResult<PostInfo>>() {
            @Override
            public void onSuccess(PageResult<PostInfo> result) {
                List<PostInfo> newData = result.getResultList();
                if (newData == null) {
                    newData = new ArrayList<>();
                }

                // 直接替换列表
                _recPostList.setValue(newData);

                hasNextPage = result.isHasNext();
                currentPage++;
                isLoading = false;
            }

            @Override
            public void onError(String err) {
                isLoading = false;
            }
        });
    }

    public void loadFollowerNextPage() {
        if (follower_isLoading || !follower_hasNextPage) {
            return;
        }

        follower_isLoading = true;
        QueryPostInfo query = new QueryPostInfo();
        query.setPage(follower_currentPage);
        query.setSize(follower_pageSize);

        postManager.getFollowerPost(query, new ResultCallback<PageResult<PostInfo>>() {
            @Override
            public void onSuccess(PageResult<PostInfo> result) {
                List<PostInfo> currentList = _followerPostList.getValue();
                if (currentList == null) {
                    currentList = new ArrayList<>();
                }

                List<PostInfo> newData = result.getResultList();
                if (newData != null && !newData.isEmpty()) {
                    currentList.addAll(newData);
                    _followerPostList.setValue(currentList);
                    follower_currentPage++;
                }

                follower_hasNextPage = result.isHasNext();
                follower_isLoading = false;
            }

            @Override
            public void onError(String err) {
                follower_isLoading = false;
            }
        });
    }

    public void refreshFollowerPosts() {
        // 1. 重置分页状态
        follower_currentPage = 1;
        follower_hasNextPage = true;
        follower_isLoading = false;

        QueryPostInfo query = new QueryPostInfo();
        query.setPage(follower_currentPage);
        query.setSize(follower_pageSize);

        follower_isLoading = true;

        postManager.getFollowerPost(query, new ResultCallback<PageResult<PostInfo>>() {
            @Override
            public void onSuccess(PageResult<PostInfo> result) {
                List<PostInfo> newData = result.getResultList();
                if (newData == null) {
                    newData = new ArrayList<>();
                }
                _followerPostList.setValue(newData);

                follower_hasNextPage = result.isHasNext();
                follower_currentPage++;

                follower_isLoading = false;
            }

            @Override
            public void onError(String err) {
                follower_isLoading = false;
            }
        });
    }


    public void loadBanner() {
        List<Integer> banners = new ArrayList<>();
        banners.add(R.mipmap.ic_bannertest1);
        banners.add(R.mipmap.ic_bannertest2);
        banners.add(R.mipmap.ic_bannertest3);

        _bannerList.setValue(banners);
    }


}
