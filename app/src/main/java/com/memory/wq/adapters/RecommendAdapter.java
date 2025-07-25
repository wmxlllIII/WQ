package com.memory.wq.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;
import com.memory.wq.beans.PostInfo;

import java.util.List;

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<PostInfo> recommentList;
    private View headerView;

    public RecommendAdapter(List<PostInfo> recommentList, View headerView) {
        this.recommentList = recommentList;
        this.headerView = headerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(headerView);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            int itemPosition = position - 1;
            PostInfo postInfo = recommentList.get(itemPosition);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            setItemHolder(itemHolder, postInfo);
        }
    }

    private void setItemHolder(ItemViewHolder holder, PostInfo postInfo) {
        holder.iv_cover.setImageResource(R.mipmap.ic_bannertest3);
        holder.iv_avatar.setImageResource(R.mipmap.ic_wx);
        holder.iv_like.setImageResource(R.mipmap.ic_t2);
        holder.tv_likescount.setText("2.1w");
        holder.tv_title.setText("标题标题标题标题标题标题标题标题标题标题标题标题标题标题");
    }

    @Override
    public int getItemCount() {
        return recommentList.size() + (headerView != null ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
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
}