package com.memory.wq.adapters.diffcallbacks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.MovieInfo;
import com.memory.wq.beans.MovieProfileInfo;

public class MovieProfileDiffCallback extends DiffUtil.ItemCallback<MovieProfileInfo> {

    @Override
    public boolean areItemsTheSame(@NonNull MovieProfileInfo oldItem, @NonNull MovieProfileInfo newItem) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(@NonNull MovieProfileInfo oldItem, @NonNull MovieProfileInfo newItem) {
        return false;
    }
}
