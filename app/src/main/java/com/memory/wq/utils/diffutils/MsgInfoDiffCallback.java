package com.memory.wq.utils.diffutils;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.MsgInfo;

public class MsgInfoDiffCallback extends DiffUtil.ItemCallback<MsgInfo> {
    @Override
    public boolean areItemsTheSame(@NonNull MsgInfo oldItem, @NonNull MsgInfo newItem) {
        return oldItem.getMsgId()== newItem.getMsgId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull MsgInfo oldItem, @NonNull MsgInfo newItem) {
        //哪个字段变化需要更新ui
        return TextUtils.equals(oldItem.getContent(), newItem.getContent())
                || TextUtils.equals(oldItem.getLinkTitle(), newItem.getLinkTitle())
                || TextUtils.equals(oldItem.getLinkImageUrl(), newItem.getLinkImageUrl())
                || oldItem.getMsgType() == newItem.getMsgType();
    }
}
