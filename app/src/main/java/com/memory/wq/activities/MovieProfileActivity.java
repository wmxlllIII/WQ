package com.memory.wq.activities;

import android.os.Bundle;

import com.memory.wq.R;
import com.memory.wq.databinding.ActivityMovieProfileBinding;

public class MovieProfileActivity extends BaseActivity<ActivityMovieProfileBinding> {

    public static final String TAG = "WQ_MovieProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_movie_profile;
    }
}