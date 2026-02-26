package com.memory.wq.adapters.diffcallbacks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.MovieCateInfo;

public class MovieCateDiffCallback extends DiffUtil.ItemCallback<MovieCateInfo>{
    @Override
    public boolean areItemsTheSame(@NonNull MovieCateInfo oldItem, @NonNull MovieCateInfo newItem) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(@NonNull MovieCateInfo oldItem, @NonNull MovieCateInfo newItem) {
        return false;
    }
}
