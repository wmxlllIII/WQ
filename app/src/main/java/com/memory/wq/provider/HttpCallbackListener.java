package com.memory.wq.provider;

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
