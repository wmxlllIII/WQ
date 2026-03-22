package com.memory.wq.adapters.diffcallbacks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.MsgListInfo;

import java.util.Objects;

public class MsgListDiffCallback extends DiffUtil.ItemCallback<MsgListInfo> {
    @Override
    public boolean areItemsTheSame(@NonNull MsgListInfo oldItem, @NonNull MsgListInfo newItem) {
        return oldItem.getChatId() == newItem.getChatId() && oldItem.getChatType() == newItem.getChatType();
    }

    @Override
    public boolean areContentsTheSame(@NonNull MsgListInfo oldItem, @NonNull MsgListInfo newItem) {
        return oldItem.getChatId() == newItem.getChatId() &&
                oldItem.getChatType() == newItem.getChatType() &&
                Objects.equals(oldItem.getDisplayName(), newItem.getDisplayName()) &&
                Objects.equals(oldItem.getAvatar(), newItem.getAvatar()) &&
                Objects.equals(oldItem.getLastMsg(), newItem.getLastMsg()) &&
                oldItem.getCreateAt() == newItem.getCreateAt();    }
}
