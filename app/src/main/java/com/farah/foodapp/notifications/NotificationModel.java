package com.farah.foodapp.notifications;

public class NotificationModel {
    private String title;
    private String message;
    private long timestamp;
    private String userId;

    public NotificationModel() {}

    public NotificationModel(String title, String message, long timestamp, String userId) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }
}
