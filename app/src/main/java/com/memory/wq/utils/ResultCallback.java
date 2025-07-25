package com.memory.wq.utils;

public interface ResultCallback<T> {
    void onSuccess(T result);
    void onError(String err);
}
