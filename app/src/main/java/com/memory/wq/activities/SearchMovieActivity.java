package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;
import com.memory.wq.adapters.MovieCateAdapter;
import com.memory.wq.adapters.MoviesAdapter;
import com.memory.wq.beans.MovieCateInfo;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ActivityChooseMovieBinding;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.interfaces.OnCateClickListener;
import com.memory.wq.interfaces.OnMovieClickListener;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class SearchMovieActivity extends BaseActivity<ActivityChooseMovieBinding> {

    public static final String TAG = "WQ_SearchMovieActivity";
    private final MoviesAdapter mMovieAdapter = new MoviesAdapter(new OnMovieClickImpl());
    private final MovieCateAdapter mCateAdapter = new MovieCateAdapter(new OnMovieCateClickImpl());
    private final MovieManager mMovieManager = new MovieManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_movie;
    }

    private void initView() {
        mBinding.rvCategories.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mBinding.rvCategories.setAdapter(mCateAdapter);
        mBinding.rvMovies.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mBinding.rvMovies.setAdapter(mMovieAdapter);
    }

    private void initData() {
        mMovieManager.getCates(new ResultCallback<List<MovieCateInfo>>() {

            @Override
            public void onSuccess(List<MovieCateInfo> result) {
                mCateAdapter.submitList(result);

                if (!result.isEmpty()) {
                    loadMoviesByCate(result.get(0).getCateId());
                }
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private void loadMoviesByCate(int cateId) {
        mMovieManager.getMoviesByCate(cateId, new ResultCallback<List<MovieInfo>>() {
            @Override
            public void onSuccess(List<MovieInfo> result) {
                mMovieAdapter.submitList(result);
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

    private class OnMovieCateClickImpl implements OnCateClickListener {

        @Override
        public void onCateClick(int cateId) {
            loadMoviesByCate(cateId);
        }
    }
}