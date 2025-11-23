package com.memory.wq.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;
import com.memory.wq.adapters.MoviesAdapter;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.databinding.ActivityChooseMovieBinding;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class ChooseMovieActivity extends BaseActivity<ActivityChooseMovieBinding> {

    private MoviesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_movie;
    }

    private void initData() {
        mBinding.rvMovies.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        MovieManager movieManager = new MovieManager();
        String token = SPManager.getUserInfo(this).getToken();
        movieManager.getMovies(token, new ResultCallback<List<MovieInfo>>() {
            @Override
            public void onSuccess(List<MovieInfo> result) {

                runOnUiThread(() -> {
                    adapter = new MoviesAdapter(ChooseMovieActivity.this, result);
                    mBinding.rvMovies.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String err) {

            }
        });


    }

}