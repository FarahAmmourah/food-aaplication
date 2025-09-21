package com.farah.foodapp.reel;

import java.util.List;

public class ReelItem {
    private int videoResId;
    private String title;
    private String restaurant;
    private int likesCount;
    private int commentsCount;
    private List<String> comments;
    private boolean liked = false;

    public ReelItem(int videoResId, String title, String restaurant,
                    int likesCount, int commentsCount, List<String> comments) {
        this.videoResId = videoResId;
        this.title = title;
        this.restaurant = restaurant;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.comments = comments;
    }

    public int getVideoResId() { return videoResId; }
    public String getTitle() { return title; }
    public String getRestaurant() { return restaurant; }
    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
    public List<String> getComments() { return comments; }
    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }
}
