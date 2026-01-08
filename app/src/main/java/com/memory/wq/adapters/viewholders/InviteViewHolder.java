package com.memory.wq.adapters.viewholders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.databinding.ItemInviteBinding;
import com.memory.wq.interfaces.OnMemberClickListener;

public class InviteViewHolder extends RecyclerView.ViewHolder {
    private ItemInviteBinding binding;
    private OnMemberClickListener mListener;
    public InviteViewHolder(@NonNull ItemInviteBinding binding, OnMemberClickListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.mListener = listener;
    }
    public void bind() {
        binding.getRoot().setOnClickListener(v -> {
            mListener.onInviteClick();
        });
    }
}
