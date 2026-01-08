package com.memory.wq.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;
import com.memory.wq.databinding.ItemVpWorksLayoutBinding;

public class WorksVH extends RecyclerView.ViewHolder {

    public ImageView cover;

    public WorksVH(@NonNull View itemView) {
        super(itemView);
        cover = (ImageView) itemView.findViewById(R.id.iv_vp_item_cover);
    }
}
