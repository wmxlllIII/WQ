package com.memory.wq.beans;

import java.io.Serializable;
import java.util.Arrays;

public class MovieInfo implements Serializable {
    private int movieId;
    private String title;
    private String movieUrl;
    private Double length;
    private String coverUrl;
    private String actors;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    @Override
    public String toString() {
        return "MovieInfo{" +
                "title='" + title + '\'' +
                ", movieUrl='" + movieUrl + '\'' +
                ", length=" + length +
                ", coverUrl='" + coverUrl + '\'' +
                ", actors='" + actors + '\'' +
                '}';
    }
}
