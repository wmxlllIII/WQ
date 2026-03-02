package com.memory.wq.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.memory.wq.adapters.diffcallbacks.PostDiffCallback;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.adapters.viewholders.WorksViewHolder;
import com.memory.wq.databinding.ItemVpWorksLayoutBinding;
import com.memory.wq.interfaces.OnPostClickListener;

public class WorksAdapter extends ListAdapter<PostInfo, WorksViewHolder> {

    private static final String TAG = "WorksAdapter";
    private final OnPostClickListener mListener;

    public WorksAdapter(OnPostClickListener listener) {
        super(new PostDiffCallback());
        this.mListener = listener;
    }

    @NonNull
    @Override
    public WorksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVpWorksLayoutBinding binding = ItemVpWorksLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WorksViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorksViewHolder holder, int position) {
        PostInfo post = getCurrentList().get(position);
        holder.bind(post, mListener);

    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }

}
