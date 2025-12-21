package com.farah.foodapp.reel;

import java.util.List;

public class ReelItem {
    private String videoUrl;
    private String title;
    private String restaurant;
    private Integer likesCount;
    private Integer commentsCount;
    private List<String> comments;
    private boolean liked = false;
    private Double price;

    private String restaurantId;
    private String reelId;
    private String imageUrl;

    public ReelItem() {}

    public ReelItem(String videoUrl, String title, String restaurant,
                    Integer likesCount, Integer commentsCount, List<String> comments,
                    Double price, String restaurantId, String reelId, String imageUrl) {
        this.videoUrl = videoUrl;
        this.title = title;
        this.restaurant = restaurant;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.comments = comments;
        this.price = price;
        this.restaurantId = restaurantId;
        this.reelId = reelId;
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() { return videoUrl; }
    public String getTitle() { return title; }
    public String getRestaurant() { return restaurant; }

    public int getLikesCount() { return likesCount != null ? likesCount : 0; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public int getCommentsCount() { return commentsCount != null ? commentsCount : 0; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }

    public List<String> getComments() { return comments; }
    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }

    public double getPrice() { return price != null ? price : 0.0; }
    public void setPrice(double price) { this.price = price; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getReelId() { return reelId; }
    public void setReelId(String reelId) { this.reelId = reelId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}