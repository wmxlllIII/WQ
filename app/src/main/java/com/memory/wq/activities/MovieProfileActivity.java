package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;

import com.memory.wq.R;
import com.memory.wq.beans.MovieProfileInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ActivityMovieProfileBinding;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.utils.ResultCallback;

public class MovieProfileActivity extends BaseActivity<ActivityMovieProfileBinding> {

    public static final String TAG = "WQ_MovieProfileActivity";
    private final MovieManager mMovieManager = new MovieManager();
    private int mMovieId;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        mMovieManager.getMovieProfile(mMovieId, new ResultCallback<MovieProfileInfo>() {
            @Override
            public void onSuccess(MovieProfileInfo movieProfile) {

            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private void initView() {
        Intent intent = getIntent();
        mMovieId = intent.getIntExtra(AppProperties.MovieId, -1);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_movie_profile;
    }
}