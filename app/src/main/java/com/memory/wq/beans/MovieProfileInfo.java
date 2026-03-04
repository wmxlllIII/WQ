package com.memory.wq.beans;

import java.util.List;

public class MovieProfileInfo {
    private int movieId;
    private String movieName;
    private String movieCover;
    private String movieUrl;
    private String movieDesc;
    private int duration;
    List<ActorInfo> actors;

    List<MovieCateInfo> cates;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieCover() {
        return movieCover;
    }

    public void setMovieCover(String movieCover) {
        this.movieCover = movieCover;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    public List<ActorInfo> getActors() {
        return actors;
    }

    public void setActors(List<ActorInfo> actors) {
        this.actors = actors;
    }

    public String getMovieDesc() {
        return movieDesc;
    }

    public void setMovieDesc(String movieDesc) {
        this.movieDesc = movieDesc;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<MovieCateInfo> getCates() {
        return cates;
    }

    public void setCates(List<MovieCateInfo> cates) {
        this.cates = cates;
    }

    @Override
    public String toString() {
        return "MovieProfileInfo{" +
                "movieId=" + movieId +
                ", movieName='" + movieName + '\'' +
                ", movieCover='" + movieCover + '\'' +
                ", movieUrl='" + movieUrl + '\'' +
                ", movieDesc='" + movieDesc + '\'' +
                ", duration=" + duration +
                ", actors=" + actors +
                ", cates=" + cates +
                '}';
    }
}
