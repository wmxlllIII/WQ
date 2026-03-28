package com.memory.wq.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.activities.AudioActivity;
import com.memory.wq.adapters.diffcallbacks.RoomDiffCallback;
import com.memory.wq.adapters.viewholders.CowatchViewHolder;
import com.memory.wq.beans.RoomInfo;
import com.memory.wq.databinding.ItemCowatchLayoutBinding;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.interfaces.OnRoomClickListener;

import java.util.List;

public class RoomAdapter extends ListAdapter<RoomInfo, CowatchViewHolder> {

    public static final String TAG = "WQ_RoomAdapter";
    private OnRoomClickListener mListener;

    public RoomAdapter(OnRoomClickListener listener) {
        super(new RoomDiffCallback());
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CowatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCowatchLayoutBinding binding = ItemCowatchLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CowatchViewHolder(binding,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CowatchViewHolder holder, int position) {
        RoomInfo roomInfo = getItem(position);
        holder.bind(roomInfo);
    }
}
