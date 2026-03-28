package com.memory.wq.beans;

public class WatchHistoryInfo {
    private MovieInfo movieInfo;
    private long userId;
    private int watchCount;
    private int progress;
    private long createAt;
    private long updateAt;

    public MovieInfo getMovieInfo() {
        return movieInfo;
    }

    public void setMovieInfo(MovieInfo movieInfo) {
        this.movieInfo = movieInfo;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "WatchHistoryInfo{" +
                "movie=" + movieInfo +
                ", userId=" + userId +
                ", watchCount=" + watchCount +
                ", progress=" + progress +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
