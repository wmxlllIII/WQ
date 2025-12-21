package com.memory.wq.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.viewholders.WorksVH;

import java.util.ArrayList;
import java.util.List;

public class WorksAdapter extends RecyclerView.Adapter<WorksVH> {

    private static final String TAG = "WorksAdapter";

    private final List<PostInfo> workList = new ArrayList<>();
    private OnItemClickListener listener;


    public void setData(List<PostInfo> workList) {
        this.workList.clear();
        this.workList.addAll(workList);
        notifyDataSetChanged();
    }

    public void addItem(PostInfo work) {
        workList.add(0, work);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public WorksVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_vp_works_layout, parent, false);
        return new WorksVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorksVH holder, int position) {
        PostInfo post = workList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(AppProperties.HTTP_SERVER_ADDRESS + post.getCommentCoverUrl())
                .into(holder.cover);
        holder.itemView.setOnClickListener(v -> {
            if (listener == null) {
                Log.d(TAG, "[x] onBindViewHolder #56");
                return;
            }

            listener.onItemClick(post, position);
        });
    }

    @Override
    public int getItemCount() {
        return workList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(PostInfo postInfo, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
