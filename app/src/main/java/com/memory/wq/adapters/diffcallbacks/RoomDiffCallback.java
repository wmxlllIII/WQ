package com.memory.wq.adapters.diffcallbacks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.RoomInfo;

public class RoomDiffCallback extends DiffUtil.ItemCallback<RoomInfo> {
    @Override
    public boolean areItemsTheSame(@NonNull RoomInfo oldItem, @NonNull RoomInfo newItem) {
        return oldItem.getRoomId() == newItem.getRoomId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull RoomInfo oldItem, @NonNull RoomInfo newItem) {
        return oldItem.getRoomId() == newItem.getRoomId() &&
                oldItem.getMovieName().equals(newItem.getMovieName()) &&
                oldItem.getMovieUrl().equals(newItem.getMovieUrl());
    }
}
