package com.memory.wq.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.activities.AudioActivity;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.caches.SmartImageView;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.properties.AppProperties;

import java.util.List;
import java.util.UUID;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder>{
    private Context context;
    private List<MovieInfo> movieList;

    public MoviesAdapter(Context context, List<MovieInfo> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_movie_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MovieInfo movieInfo = movieList.get(position);

        Glide.with(context)
                .load(movieInfo.getCoverUrl())
                .error(R.mipmap.loading_failure)
                .into(holder.iv_cover);
        holder.tv_movie_name.setText(movieInfo.getTitle());
        holder.tv_movie_length.setText(movieInfo.getLength()+"");
        holder.iv_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Intent intent = new Intent(context, AudioActivity.class);
                intent.putExtra(AppProperties.ROLE_TYPE, RoleType.ROLE_TYPE_BROADCASTER);
                intent.putExtra(AppProperties.MOVIE_PATH,movieInfo);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_cover;
        TextView tv_movie_length;
        TextView tv_movie_name;

        public ViewHolder(@NonNull View view) {
            super(view);
            iv_cover = (ImageView) view.findViewById(R.id.iv_cover);
            tv_movie_length = (TextView) view.findViewById(R.id.tv_movie_length);
            tv_movie_name = (TextView) view.findViewById(R.id.tv_movie_name);
        }
    }
}
