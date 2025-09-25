package com.farah.foodapp.reel;

import java.util.List;

public class ReelItem {
    private String videoUrl;
    private String title;
    private String restaurant;
    private int likesCount;
    private int commentsCount;
    private List<String> comments;
    private boolean liked = false;
    private double price;

    public ReelItem() {} // لازم للفirebase

    public ReelItem(String videoUrl, String title, String restaurant,
                    int likesCount, int commentsCount, List<String> comments, double price) {
        this.videoUrl = videoUrl;
        this.title = title;
        this.restaurant = restaurant;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.comments = comments;
        this.price = price;
    }

    public String getVideoUrl() { return videoUrl; }
    public String getTitle() { return title; }
    public String getRestaurant() { return restaurant; }
    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
    public List<String> getComments() { return comments; }
    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }
    public double getPrice() { return price; }
}
