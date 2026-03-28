package com.memory.wq.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.memory.wq.adapters.diffcallbacks.WatchHistoryDiffCallback;
import com.memory.wq.adapters.viewholders.WatchHistoryViewHolder;
import com.memory.wq.beans.WatchHistoryInfo;
import com.memory.wq.databinding.ItemWatchHistoryBinding;
import com.memory.wq.interfaces.OnMovieClickListener;

public class WatchHistoryAdapter extends ListAdapter<WatchHistoryInfo, WatchHistoryViewHolder> {

    private final OnMovieClickListener mListener;
    public WatchHistoryAdapter(OnMovieClickListener listener) {
        super(new WatchHistoryDiffCallback());
        mListener = listener;
    }

    @NonNull
    @Override
    public WatchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWatchHistoryBinding binding = ItemWatchHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WatchHistoryViewHolder(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchHistoryViewHolder holder, int position) {
        WatchHistoryInfo watchHistoryInfo = getItem(position);
        holder.bind(watchHistoryInfo);
    }
}
