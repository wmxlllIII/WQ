package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.adapters.ActorAdapter;
import com.memory.wq.beans.MovieProfileInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ActivityMovieProfileBinding;
import com.memory.wq.interfaces.OnActorClickListener;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.utils.ResultCallback;

public class MovieProfileActivity extends BaseActivity<ActivityMovieProfileBinding> {

    public static final String TAG = "WQ_MovieProfileActivity";
    private final MovieManager mMovieManager = new MovieManager();
    private final ActorAdapter mActorAdapter = new ActorAdapter(new OnActorClickListenerImpl());
    private int mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_movie_profile;
    }

    private void initData() {
        mMovieManager.getMovieProfile(mMovieId, new ResultCallback<MovieProfileInfo>() {
            @Override
            public void onSuccess(MovieProfileInfo movieProfile) {
                updateUI(movieProfile);
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private void updateUI(MovieProfileInfo movieProfile) {
        mBinding.tvMovieName.setText(movieProfile.getMovieName());

        mBinding.tvMovieDuration.setText(String.valueOf(movieProfile.getDuration()));

        mBinding.tvDesc.setText(movieProfile.getMovieDesc());
        mBinding.tvPlay.setOnClickListener(v -> {
            Intent intent = new Intent(MovieProfileActivity.this, AudioActivity.class);
            intent.putExtra(AppProperties.ROOM_ID, movieProfile.getMovieId());
            startActivity(intent);
        });

        Glide.with(this)
                .load(movieProfile.getMovieCover())
                .into(mBinding.ivMovieCover);

        mActorAdapter.submitList(movieProfile.getActors());
    }

    private void initView() {
        Intent intent = getIntent();
        mMovieId = intent.getIntExtra(AppProperties.MOVIE_ID, -1);

        mBinding.ivBack.setOnClickListener(v -> finish());
        mBinding.rvActors.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mBinding.rvActors.setAdapter(mActorAdapter);
    }

    private class OnActorClickListenerImpl implements OnActorClickListener {

        @Override
        public void onActorClick(int actorId) {
            Intent intent = new Intent(MovieProfileActivity.this, ActorActivity.class);
            intent.putExtra(AppProperties.ACTOR_ID, actorId);
            startActivity(intent);
        }

    }
}