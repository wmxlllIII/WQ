package com.memory.wq.utils.diffutils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.FriendRelaInfo;

public class FriRelaDiffCallback extends DiffUtil.ItemCallback<FriendRelaInfo> {
    @Override
    public boolean areItemsTheSame(@NonNull FriendRelaInfo oldItem, @NonNull FriendRelaInfo newItem) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(@NonNull FriendRelaInfo oldItem, @NonNull FriendRelaInfo newItem) {
        return false;
    }
}
