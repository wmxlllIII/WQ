package com.memory.wq.adapters.diffcallbacks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.PostCommentInfo;

public class PostCommentDiffCallback extends DiffUtil.ItemCallback<PostCommentInfo> {

    @Override
    public boolean areItemsTheSame(@NonNull PostCommentInfo oldItem, @NonNull PostCommentInfo newItem) {
        return false;
    }

    public boolean areContentsTheSame(@NonNull PostCommentInfo oldItem, @NonNull PostCommentInfo newItem) {
        return false;
    }
}
