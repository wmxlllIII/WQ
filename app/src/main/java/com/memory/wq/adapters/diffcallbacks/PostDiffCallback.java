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
        return oldItem.getPostId() == newItem.getPostId() &&
                oldItem.getPoster() == newItem.getPoster() &&
                TextUtils.equals(oldItem.getTitle(), newItem.getTitle()) &&
                TextUtils.equals(oldItem.getCommentCoverUrl(), newItem.getCommentCoverUrl()) &&
                TextUtils.equals(oldItem.getPosterAvatar(), newItem.getPosterAvatar()) &&
                TextUtils.equals(oldItem.getContent(), newItem.getContent()) &&
                oldItem.getLikeCount() == newItem.getLikeCount() &&
                oldItem.isLiked() == newItem.isLiked() &&
                oldItem.getTimestamp() == newItem.getTimestamp() &&
                oldItem.getContentImagesUrlList().equals(newItem.getContentImagesUrlList());
    }
}
