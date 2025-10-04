package com.farah.foodapp.admin.admin_reels;

public class ReelModel {
    private String id;
    private String title;
    private String description;
    private String videoUrl;
    private long createdAt;

    public ReelModel() {}

    public ReelModel(String id, String title, String description, String videoUrl, long createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getVideoUrl() { return videoUrl; }
    public long getCreatedAt() { return createdAt; }
}
