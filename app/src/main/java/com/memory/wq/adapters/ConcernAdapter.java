package com.memory.wq.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.beans.PostInfo;

import java.util.List;

public class ConcernAdapter extends RecyclerView.Adapter<ConcernAdapter.ViewHolder> {
    private List<PostInfo> postInfoList;

    public ConcernAdapter(List<PostInfo> postInfoList) {
        this.postInfoList = postInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return postInfoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
