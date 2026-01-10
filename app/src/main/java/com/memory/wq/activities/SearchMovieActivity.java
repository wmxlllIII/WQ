package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;
import com.memory.wq.adapters.MoviesAdapter;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ActivityChooseMovieBinding;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.interfaces.OnMovieClickListener;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class SearchMovieActivity extends BaseActivity<ActivityChooseMovieBinding> {

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
        String token = SPManager.getUserInfo().getToken();
        movieManager.getMovies(token, new ResultCallback<List<MovieInfo>>() {
            @Override
            public void onSuccess(List<MovieInfo> result) {
                adapter = new MoviesAdapter(result,new OnMovieClickImpl());
                mBinding.rvMovies.setAdapter(adapter);
            }

            @Override
            public void onError(String err) {

            }
        });
    }
    private class OnMovieClickImpl implements OnMovieClickListener {
        @Override
        public void onCoverClick(MovieInfo movie) {
            Intent intent = new Intent(SearchMovieActivity.this, AudioActivity.class);
            intent.putExtra(AppProperties.ROLE_TYPE, RoleType.ROLE_TYPE_BROADCASTER);
            intent.putExtra(AppProperties.MOVIE_PATH, movie);
            startActivity(intent);
        }

        @Override
        public void onNameClick(int movieId) {

        }
    }

}