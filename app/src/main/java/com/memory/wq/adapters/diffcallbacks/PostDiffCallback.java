package com.memory.wq.adapters.diffcallbacks;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.PostInfo;

public class PostDiffCallback extends DiffUtil.ItemCallback<PostInfo>{
    @Override
    public boolean areItemsTheSame(@NonNull PostInfo oldItem, @NonNull PostInfo newItem) {
        return oldItem.getPostId() == newItem.getPostId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull PostInfo oldItem, @NonNull PostInfo newItem) {
        if (oldItem.getLikeCount() != newItem.getLikeCount()) return false;
        if (!TextUtils.equals(oldItem.getTitle(), newItem.getTitle())) return false;
        if (!TextUtils.equals(oldItem.getCommentCoverUrl(), newItem.getCommentCoverUrl())) return false;
        if (!TextUtils.equals(oldItem.getPosterAvatar(), newItem.getPosterAvatar())) return false;

        return true;//没变化
    }
}
