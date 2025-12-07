package com.memory.wq.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.memory.wq.service.WebSocketMessage;

public class WSRepository {
    private static WSRepository instance;
    private MutableLiveData<WebSocketMessage<?>> messageLiveData = new MutableLiveData<>();

    public WSRepository() {
    }

    public static WSRepository getInstance() {
        if (instance == null) {
            instance = new WSRepository();
        }
        return instance;
    }

    public void postMessage(WebSocketMessage<?> message) {
        messageLiveData.postValue(message);
    }

    public LiveData<WebSocketMessage<?>> getMessages() {
        return messageLiveData;
    }


}
