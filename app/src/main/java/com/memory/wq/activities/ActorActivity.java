package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.memory.wq.R;
import com.memory.wq.adapters.MovieCateAdapter;
import com.memory.wq.adapters.MoviesAdapter;
import com.memory.wq.beans.ActorInfo;
import com.memory.wq.beans.MovieCateInfo;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ActivityActorBinding;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.interfaces.OnCateClickListener;
import com.memory.wq.interfaces.OnMovieClickListener;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class ActorActivity extends BaseActivity<ActivityActorBinding> {

    public static final String TAG = "WQ_ActorActivity";

    private final MoviesAdapter mMovieAdapter = new MoviesAdapter(new OnMovieClickImpl());
    private final MovieCateAdapter mCateAdapter = new MovieCateAdapter(new OnMovieCateClickImpl());
    private final MovieManager mMovieManager = new MovieManager();
    private int mActorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        Intent intent = getIntent();
        mActorId = intent.getIntExtra(AppProperties.ACTOR_ID, -1);
        if (mActorId < 0) {
            finish();
            return;
        }

        mBinding.ivBack.setOnClickListener(view -> finish());
        mBinding.rvCategories.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mBinding.rvCategories.setAdapter(mCateAdapter);
        mBinding.rvMovies.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mBinding.rvMovies.setAdapter(mMovieAdapter);
    }

    private void initData() {
        mMovieManager.getActorInfo(mActorId, new ResultCallback<ActorInfo>() {
            @Override
            public void onSuccess(ActorInfo result) {
                setActorUI(result);
            }

            @Override
            public void onError(String err) {

            }
        });

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

    private void setActorUI(ActorInfo actor) {
        Glide.with(mBinding.getRoot().getContext())
                .load(actor.getAvatarUrl())
                .placeholder(R.mipmap.icon_default_avatar)
                .error(R.mipmap.icon_default_avatar)
                .transform(new RoundedCorners(10))
                .centerCrop()
                .into(mBinding.ivAvatar);

        mBinding.tvGender.setText(actor.getGender() + "演员");
        mBinding.tvName.setText(actor.getActorName());
        mBinding.tvDesc.setText(actor.getIntroduction());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_actor;
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
            Intent intent = new Intent(ActorActivity.this, AudioActivity.class);
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