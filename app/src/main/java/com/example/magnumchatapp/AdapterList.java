package com.example.magnumchatapp;

public class AdapterList {
    int messageType;
    String message;

    public AdapterList(int messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
