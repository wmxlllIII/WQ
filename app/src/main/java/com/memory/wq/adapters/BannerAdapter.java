package com.memory.wq.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.memory.wq.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {
    private List<Integer> imageViewList;

    public BannerAdapter(List<Integer> imageViewList) {
        this.imageViewList = imageViewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int actualPos = position % imageViewList.size();
        Glide.with(holder.itemView)
                .load(imageViewList.get(actualPos))
                .transform(
                        new MultiTransformation<>(
                                new CenterCrop(),
                                new RoundedCorners(25)
                        )
                )
                .into(holder.iv_banner);

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