package com.memory.wq.adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.memory.wq.R;
import com.memory.wq.adapters.diffcallbacks.PostDiffCallback;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.databinding.ItemRecommendBinding;
import com.memory.wq.interfaces.OnPostClickListener;

import java.util.List;

public class RecommendAdapter extends ListAdapter<PostInfo, RecyclerView.ViewHolder> {

    public static final String TAG = "WQ_RecommendAdapter";
    private OnPostClickListener postClickListener;


    public RecommendAdapter(OnPostClickListener postClickListener) {
        super(new PostDiffCallback());
        this.postClickListener = postClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecommendBinding binding = ItemRecommendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostInfo postInfo = getItem(position);

        Log.d(TAG, "[test] onBindViewHolder #46 " + postInfo);
        setItemHolder((ItemViewHolder) holder, postInfo);

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (postClickListener != null
                    && adapterPosition != RecyclerView.NO_POSITION) {
                postClickListener.onPostClick(
                        adapterPosition,
                        getItem(adapterPosition)
                );
            }
        });
    }

    private void setItemHolder(ItemViewHolder holder, PostInfo postInfo) {
        if (TextUtils.isEmpty(postInfo.getCommentCoverUrl())) {
            holder.iv_cover.setVisibility(View.GONE);
        } else {
            Glide.with(holder.iv_cover.getContext())
                    .load(postInfo.getCommentCoverUrl())
                    .placeholder(R.mipmap.loading_default)
                    .error(R.mipmap.loading_failure)
                    .transform(
                            new MultiTransformation<>(
                                    new CenterCrop(),
                                    new RoundedCorners(15)
                            )
                    )
                    .into(holder.iv_cover);
//            Log.d(TAG, "setItemHolder: ===setItemHolder #70 " + AppProperties.HTTP_SERVER_ADDRESS + postInfo.getCommentCoverUrl());
        }

        Glide.with(holder.iv_avatar.getContext())
                .load(postInfo.getPosterAvatar())
                .placeholder(R.mipmap.loading_default)
                .error(R.mipmap.loading_failure)
                .into(holder.iv_avatar);


        holder.iv_like.setImageResource(R.mipmap.icon_like_empty);
        holder.tv_likescount.setText(String.valueOf(postInfo.getLikeCount()));
        holder.tv_title.setText(postInfo.getTitle());
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_cover;
        ImageView iv_avatar;
        ImageView iv_like;
        TextView tv_likescount;
        TextView tv_title;

        public ItemViewHolder(View itemView) {
            super(itemView);
            iv_cover = (ImageView) itemView.findViewById(R.id.iv_cover);
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            iv_like = (ImageView) itemView.findViewById(R.id.iv_like);
            tv_likescount = (TextView) itemView.findViewById(R.id.tv_likescount);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

}