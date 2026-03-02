package com.memory.wq.adapters.viewholders;

import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.beans.MovieCateInfo;
import com.memory.wq.databinding.ItemMovieCateBinding;
import com.memory.wq.interfaces.OnCateClickListener;

public class CateViewHolder extends RecyclerView.ViewHolder {
    private final ItemMovieCateBinding binding;
    private final OnCateClickListener listener;
    public CateViewHolder(ItemMovieCateBinding binding, OnCateClickListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.listener = listener;
    }

    public void bind(MovieCateInfo cateInfo) {
        binding.tvCate.setText(cateInfo.getCateName());
        binding.getRoot().setOnClickListener(v -> {
            listener.onCateClick(cateInfo.getCateId());
        });
    }
}
