package com.memory.wq.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.memory.wq.R;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.databinding.ItemMovieLayoutBinding;
import com.memory.wq.interfaces.OnMovieClickListener;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private List<MovieInfo> movieList;
    private OnMovieClickListener onMovieClickListener;

    public MoviesAdapter(List<MovieInfo> movieList, OnMovieClickListener onMovieClickListener) {
        this.movieList = movieList;
        this.onMovieClickListener = onMovieClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMovieLayoutBinding binding = ItemMovieLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MovieInfo movie = movieList.get(position);

        Glide.with(holder.itemView)
                .load(movie.getCoverUrl())
                .placeholder(R.mipmap.loading_default)
                .error(R.mipmap.loading_failure)
                .transform(
                        new MultiTransformation<>(
                                new CenterCrop(),
                                new RoundedCorners(25)
                        )
                )
                .into(holder.iv_cover);

        holder.tv_movie_name.setText(movie.getTitle());
        holder.tv_movie_name.setOnClickListener(v -> {
            onMovieClickListener.onNameClick(movie.getMovieId());
        });

        holder.tv_movie_length.setText(String.valueOf(movie.getLength()));

        holder.iv_cover.setOnClickListener(v -> {
            onMovieClickListener.onCoverClick(movie);
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
