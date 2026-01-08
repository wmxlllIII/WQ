package com.memory.wq.adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ItemRecommendBinding;

import java.util.List;

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "WQ_RecommendAdapter";

    private List<PostInfo> recommentList;

    private OnItemClickListener itemClickListener;
    private OnLikeClickListener likeClickListener;


    public RecommendAdapter(List<PostInfo> recommentList) {
        this.recommentList = recommentList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecommendBinding binding = ItemRecommendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostInfo postInfo = recommentList.get(position);
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        Log.d(TAG, "[test] onBindViewHolder #46"+postInfo);
        setItemHolder(itemHolder, postInfo);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null && position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position, recommentList.get(position));
                }
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
                    .into(holder.iv_cover);
//            Log.d(TAG, "setItemHolder: ===setItemHolder #70 " + AppProperties.HTTP_SERVER_ADDRESS + postInfo.getCommentCoverUrl());
        }

        //TODO 更改成用户头像
        if (TextUtils.isEmpty(postInfo.getCommentCoverUrl())) {

        } else {
            Glide.with(holder.iv_avatar.getContext())
                    .load(postInfo.getPosterAvatar())
                    .placeholder(R.mipmap.loading_default)
                    .error(R.mipmap.loading_failure)
                    .into(holder.iv_avatar);
        }

        holder.iv_like.setImageResource(R.mipmap.icon_like_empty);
        holder.tv_likescount.setText(String.valueOf(postInfo.getLikeCount()));
        holder.tv_title.setText(postInfo.getTitle());
    }

    @Override
    public int getItemCount() {
        return recommentList.size();
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, PostInfo postInfo);
    }

    public void setOnLikeClickListener(OnLikeClickListener onLikeClickListener) {
        this.likeClickListener = onLikeClickListener;
    }

    public interface OnLikeClickListener {
        void OnLikeClick(int position, PostInfo postInfo, ImageView likeView);
    }

}