package com.memory.wq.adapters.diffcallbacks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.FriendRelaInfo;

import java.util.Objects;

public class FriRelaDiffCallback extends DiffUtil.ItemCallback<FriendRelaInfo> {
    @Override
    public boolean areItemsTheSame(@NonNull FriendRelaInfo oldItem, @NonNull FriendRelaInfo newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull FriendRelaInfo oldItem, @NonNull FriendRelaInfo newItem) {
        return  oldItem.getId() == newItem.getId() &&
                oldItem.getSenderId() == newItem.getSenderId() &&
                Objects.equals(oldItem.getSenderName(), newItem.getSenderName()) &&
                Objects.equals(oldItem.getSenderAvatar(), newItem.getSenderAvatar()) &&
                oldItem.getReceiverId() == newItem.getReceiverId() &&
                Objects.equals(oldItem.getReceiverName(), newItem.getReceiverName()) &&
                Objects.equals(oldItem.getReceiverAvatar(), newItem.getReceiverAvatar()) &&
                Objects.equals(oldItem.getValidMsg(), newItem.getValidMsg()) &&
                oldItem.getStatus() == newItem.getStatus() &&  // status变化会被检测到
                oldItem.getCreateAt() == newItem.getCreateAt() &&
                oldItem.getUpdateAt() == newItem.getUpdateAt();
    }
}
