package com.memory.wq.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.adapters.diffcallbacks.MovieCateDiffCallback;
import com.memory.wq.beans.MovieCateInfo;
import com.memory.wq.interfaces.OnCateClickListener;

public class MovieCateAdapter extends ListAdapter<MovieCateInfo, RecyclerView.ViewHolder> {

    private OnCateClickListener listener;

    public MovieCateAdapter(OnCateClickListener listener) {
        super(new MovieCateDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }
}
