package com.memory.wq.adapters.diffcallbacks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.MovieInfo;

public class MovieDiffCallback extends DiffUtil.ItemCallback<MovieInfo>{
    @Override
    public boolean areItemsTheSame(@NonNull MovieInfo oldItem, @NonNull MovieInfo newItem) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(@NonNull MovieInfo oldItem, @NonNull MovieInfo newItem) {
        return false;
    }
}
