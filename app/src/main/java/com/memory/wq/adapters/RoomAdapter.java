package com.memory.wq.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.activities.AudioActivity;
import com.memory.wq.beans.RoomInfo;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.constants.AppProperties;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHoder> {
    public static final String TAG = RoomAdapter.class.getName();
    private Context context;
    private List<RoomInfo> roomList;

    public RoomAdapter(Context context, List<RoomInfo> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_cowatch_layout, null);
        return new ViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoder holder, int position) {
        RoomInfo roomInfo = roomList.get(position);
        Log.d(TAG, "onBindViewHolder: ===房间信息" + roomInfo.toString());
        holder.tv_video_name.setText(roomInfo.getMovieName());
        Glide.with(holder.itemView.getContext())
                .load(roomInfo.getMovieUrl())
                .into(holder.iv_cover);


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AudioActivity.class);
            intent.putExtra(AppProperties.ROLE_TYPE, RoleType.ROLE_TYPE_AUDIENCE);
            intent.putExtra(AppProperties.ROOM_ID, roomInfo.getRoomId());
            intent.putExtra(AppProperties.MOVIE_PATH, roomInfo.getMovieUrl());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    class ViewHoder extends RecyclerView.ViewHolder {
        TextView tv_video_name;
        ImageView iv_cover;

        public ViewHoder(@NonNull View view) {
            super(view);
            tv_video_name = (TextView) view.findViewById(R.id.tv_video_name);
            iv_cover = (ImageView) view.findViewById(R.id.iv_cover);
        }
    }
}
