package com.memory.wq.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {
    private Context context;
    private List<Integer> imageViewList;

    public BannerAdapter(Context context, List<Integer> imageViewList) {
        this.context = context;
        this.imageViewList = imageViewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int actualPos = position % imageViewList.size();
        holder.iv_banner.setImageResource(imageViewList.get(actualPos));
    }

    @Override
    public int getItemCount() {
        return imageViewList.isEmpty() ? 0 : Integer.MAX_VALUE;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_banner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_banner = (ImageView) itemView.findViewById(R.id.iv_banner);
        }
    }
}