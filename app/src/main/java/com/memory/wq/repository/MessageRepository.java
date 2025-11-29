package com.memory.wq.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.memory.wq.enumertions.EventType;
import com.memory.wq.interfaces.IWebSocketListener;
import com.memory.wq.service.WebService;
import com.memory.wq.service.WebSocketMessage;

import java.util.EnumSet;

public class MessageRepository {
    private static MessageRepository instance;
    private MutableLiveData<WebSocketMessage<?>> messageLiveData = new MutableLiveData<>();

    public MessageRepository() {
    }

    public static MessageRepository getInstance() {
        if (instance == null) {
            instance = new MessageRepository();
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
