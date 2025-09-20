package com.farah.foodapp;

public class ReelItem {
    private int videoResId;
    private String title;
    private String restaurant;

    public ReelItem(int videoResId, String title, String restaurant) {
        this.videoResId = videoResId;
        this.title = title;
        this.restaurant = restaurant;
    }

    public int getVideoResId() { return videoResId; }
    public String getTitle() { return title; }
    public String getRestaurant() { return restaurant; }
}
