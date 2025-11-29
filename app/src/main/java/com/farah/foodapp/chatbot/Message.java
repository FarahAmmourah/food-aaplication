package com.farah.foodapp.chatbot;

public class Message {
    private String text;
    private boolean isUser;
    private boolean isTyping;

    public Message(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
        this.isTyping = false;
    }

    public Message(boolean isTyping) {
        this.isTyping = true;
        this.text = "";
        this.isUser = false;
    }

    public boolean isTyping() { return isTyping; }
    public String getText() { return text; }
    public boolean isUser() { return isUser; }
}
