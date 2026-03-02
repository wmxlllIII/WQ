package com.memory.wq.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.adapters.diffcallbacks.ImageDiffCallback;
import com.memory.wq.databinding.ItemPostDetailImagesLayoutBinding;

public class PostImagesAdapter extends ListAdapter<String, PostImagesAdapter.ImageViewHolder> {

    public PostImagesAdapter() {
        super(new ImageDiffCallback());
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostDetailImagesLayoutBinding binding = ItemPostDetailImagesLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ImageViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = getCurrentList().get(position);
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.mipmap.loading_default)
                .error(R.mipmap.loading_failure)
                .into(holder.iv_image);
        if (getCurrentList().isEmpty()) {
            holder.ll_photo_indicator.setVisibility(View.GONE);
            return;
        }

        holder.tv_photo_posi.setText(String.valueOf(position + 1));
        holder.tv_photo_size.setText(String.valueOf(getCurrentList().size()));
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView tv_photo_posi;
        TextView tv_photo_size;
        LinearLayout ll_photo_indicator;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            tv_photo_posi = (TextView) itemView.findViewById(R.id.tv_photo_posi);
            tv_photo_size = (TextView) itemView.findViewById(R.id.tv_photo_size);
            ll_photo_indicator = (LinearLayout) itemView.findViewById(R.id.ll_photo_indicator);
        }
    }
}
