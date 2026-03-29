package com.memory.wq.adapters.diffcallbacks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.FriendInfo;

public class SearchUserDiffCallback extends DiffUtil.ItemCallback<FriendInfo>{
    @Override
    public boolean areItemsTheSame(@NonNull FriendInfo oldItem, @NonNull FriendInfo newItem) {
        return oldItem.getUuNumber() == newItem.getUuNumber();
    }

    @Override
    public boolean areContentsTheSame(@NonNull FriendInfo oldItem, @NonNull FriendInfo newItem) {
        return oldItem.getUuNumber() == newItem.getUuNumber() &&
                oldItem.getNickname().equals(newItem.getNickname()) &&
                oldItem.getAvatarUrl().equals(newItem.getAvatarUrl());
    }
}
