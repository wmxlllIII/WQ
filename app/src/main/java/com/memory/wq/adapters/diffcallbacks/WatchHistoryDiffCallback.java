package com.memory.wq.adapters.diffcallbacks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.WatchHistoryInfo;

public class WatchHistoryDiffCallback extends DiffUtil.ItemCallback<WatchHistoryInfo> {
    @Override
    public boolean areItemsTheSame(@NonNull WatchHistoryInfo oldItem, @NonNull WatchHistoryInfo newItem) {
        return oldItem.getMovieInfo().getMovieId() == newItem.getMovieInfo().getMovieId()
                && oldItem.getUserId() == newItem.getUserId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull WatchHistoryInfo oldItem, @NonNull WatchHistoryInfo newItem) {
        return oldItem.getMovieInfo().getMovieId() == newItem.getMovieInfo().getMovieId()
                && oldItem.getUserId() == newItem.getUserId()
                && oldItem.getWatchCount() == newItem.getWatchCount()
                && oldItem.getProgress() == newItem.getProgress()
                && oldItem.getCreateAt() == newItem.getCreateAt()
                && oldItem.getUpdateAt() == newItem.getUpdateAt();
    }
}
