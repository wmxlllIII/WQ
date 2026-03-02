package com.memory.wq.adapters.viewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.databinding.ItemVpWorksLayoutBinding;
import com.memory.wq.interfaces.OnPostClickListener;

public class WorksViewHolder extends RecyclerView.ViewHolder {

    private final ItemVpWorksLayoutBinding binding;

    public WorksViewHolder(@NonNull ItemVpWorksLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(PostInfo post, OnPostClickListener listener) {
        Glide.with(binding.getRoot().getContext())
                .load(post.getCommentCoverUrl())
                .into(binding.ivVpItemCover);

        binding.getRoot().setOnClickListener(v -> {
            listener.onPostClick(getBindingAdapterPosition(), post);
        });
    }
}
