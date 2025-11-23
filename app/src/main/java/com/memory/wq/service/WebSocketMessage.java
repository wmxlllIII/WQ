package com.memory.wq.service;

import com.memory.wq.enumertions.EventType;

public class WebSocketMessage<T> {
    private EventType eventType;
    private T data;

    public WebSocketMessage(EventType eventType, T data) {
        this.eventType = eventType;
        this.data = data;
    }

    public EventType getEventType() {
        return eventType;
    }

    public T getData() {
        return data;
    }

}
