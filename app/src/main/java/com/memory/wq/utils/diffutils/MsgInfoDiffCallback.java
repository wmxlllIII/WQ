package com.memory.wq.utils.diffutils;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.MsgInfo;

public class MsgInfoDiffCallback extends DiffUtil.ItemCallback<MsgInfo> {
    @Override
    public boolean areItemsTheSame(@NonNull MsgInfo oldItem, @NonNull MsgInfo newItem) {
        return oldItem.getMsgId() == newItem.getMsgId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull MsgInfo oldItem, @NonNull MsgInfo newItem) {
        return oldItem.getMsgId() == newItem.getMsgId();
    }
}
