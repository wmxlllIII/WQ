package com.memory.wq.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;

import java.util.List;

public class PostImagesAdapter extends RecyclerView.Adapter<PostImagesAdapter.ImageViewHolder> {
    private Context context;
    private List<String> imageUrlList;

    public PostImagesAdapter(Context context, List<String> imageUrlList) {
        this.context = context;
        this.imageUrlList = imageUrlList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_detail_images_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrlList.get(position);
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.mipmap.loading_default)
                .error(R.mipmap.loading_failure)
                .into(holder.iv_image);
        if (imageUrlList == null || imageUrlList.isEmpty()) {
            holder.ll_photo_indicator.setVisibility(View.GONE);
            return;
        }

        holder.tv_photo_posi.setText(String.valueOf(position + 1));
        holder.tv_photo_size.setText(String.valueOf(imageUrlList.size()));
    }

    @Override
    public int getItemCount() {
        return imageUrlList == null ? 0 : imageUrlList.size();
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
