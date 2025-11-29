package com.memory.wq.beans;

import java.util.List;

public class UiMessageState {
    private UiMessageState() {
    }

    public static class Loading extends UiMessageState {

    }

    public static class DisPlay extends UiMessageState {
        private final List<MsgInfo> data;

        public DisPlay(List<MsgInfo> data) {
            this.data = data;
        }

        public List<MsgInfo> getMsgInfoList() {
            return data;
        }
    }

    public static class Error extends UiMessageState {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
