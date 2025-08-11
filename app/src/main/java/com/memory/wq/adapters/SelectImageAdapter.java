package com.memory.wq.adapters;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;

import java.io.File;
import java.util.List;

public class SelectImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "SelectedImageAdapter";

    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_ADD = 1;

    private List<File> imageList;
    private OnAddOrRemoveClickListener addClickListener;

    public SelectImageAdapter(List<File> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE) {
            View view = View.inflate(parent.getContext(), R.layout.item_select_iamges_layout, null);
            return new ImageViewHolder(view);
        } else {
            View view = View.inflate(parent.getContext(), R.layout.item_add_iamge_layout, null);
            return new AddViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_IMAGE) {
            File file = imageList.get(position);
            ImageViewHolder imageHolder = (ImageViewHolder) holder;
            Glide.with(imageHolder.iv_image.getContext())
                    .load(file)
                    .placeholder(R.mipmap.loading_default)
                    .error(R.mipmap.loading_failure)
                    .into(imageHolder.iv_image);

            Glide.with(imageHolder.iv_remove.getContext())
                    .load(R.mipmap.icon_cancel)
                    .placeholder(R.mipmap.loading_default)
                    .error(R.mipmap.loading_failure)
                    .into(imageHolder.iv_remove);

            imageHolder.iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addClickListener.onRemoveClick(position);
                }
            });
        } else {
            ((AddViewHolder) holder).iv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (addClickListener == null){
                        Log.d(TAG, "===[x] onBindViewHolder #58");
                        return;
                    }

                    addClickListener.onAddClick();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size() < 9 ? imageList.size() + 1 : imageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position < imageList.size() ? TYPE_IMAGE : TYPE_ADD;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_image;
        ImageView iv_remove;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            iv_remove = (ImageView) itemView.findViewById(R.id.iv_remove);
        }

    }

    static class AddViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_add;

        public AddViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_add = (ImageView) itemView.findViewById(R.id.iv_add);
        }

    }

    public void setOnAddClickListener(OnAddOrRemoveClickListener listener) {
        this.addClickListener = listener;
    }

    public interface OnAddOrRemoveClickListener {
        void onAddClick();
        void onRemoveClick(int position);
    }

}