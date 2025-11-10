package com.memory.wq.service;

import com.memory.wq.enumertions.EventType;

public class WebSocketMessage<T> {
    private EventType eventType; // 事件类型
    private T data; // 泛型数据体，支持任意类型

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
