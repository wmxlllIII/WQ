package com.memory.wq.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        holder.siv_cover.setImageUrl(movieInfo.getCoverUrl());
        holder.tv_movie_name.setText(movieInfo.getTitle());
        holder.tv_movie_length.setText(movieInfo.getLength()+"");
        holder.siv_cover.setOnClickListener(new View.OnClickListener() {
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
        SmartImageView siv_cover;
        TextView tv_movie_length;
        TextView tv_movie_name;

        public ViewHolder(@NonNull View view) {
            super(view);
            siv_cover = (SmartImageView) view.findViewById(R.id.siv_cover);
            tv_movie_length = (TextView) view.findViewById(R.id.tv_movie_length);
            tv_movie_name = (TextView) view.findViewById(R.id.tv_movie_name);
        }
    }
}
