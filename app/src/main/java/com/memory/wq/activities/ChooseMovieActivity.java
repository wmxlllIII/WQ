package com.memory.wq.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;
import com.memory.wq.adapters.MoviesAdapter;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class ChooseMovieActivity extends BaseActivity {

    private EditText et_search_movie;
    private RecyclerView rv_movies;
    private MoviesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_movie);
        initView();
        initData();
    }

    private void initData() {
        rv_movies.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));


        MovieManager movieManager = new MovieManager();
        SharedPreferences sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        movieManager.getMovies(token,new ResultCallback<List<MovieInfo>>() {
            @Override
            public void onSuccess(List<MovieInfo> result) {

                runOnUiThread(()->{
                    adapter = new MoviesAdapter(ChooseMovieActivity.this,result);
                    rv_movies.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String err) {

            }
        });


    }

    private void initView() {
        et_search_movie = (EditText) findViewById(R.id.et_search_movie);
        rv_movies = (RecyclerView) findViewById(R.id.rv_movies);
    }



}