package com.memory.wq.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.memory.wq.adapters.diffcallbacks.MovieProfileDiffCallback;
import com.memory.wq.adapters.viewholders.MovieProfileViewHolder;
import com.memory.wq.beans.MovieProfileInfo;
import com.memory.wq.interfaces.OnMovieProfileClickListener;

public class MovieProfileAdapter extends ListAdapter<MovieProfileInfo, MovieProfileViewHolder> {

    private OnMovieProfileClickListener mListener;

    public MovieProfileAdapter(OnMovieProfileClickListener listener) {
        super(new MovieProfileDiffCallback());
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MovieProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieProfileViewHolder holder, int position) {

    }
}
