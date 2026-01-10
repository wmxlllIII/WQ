package com.memory.wq.interfaces;

import com.memory.wq.beans.MovieInfo;

public interface OnMovieClickListener {
    void onCoverClick(MovieInfo movieInfo);
    void onNameClick(int movieId);
}
