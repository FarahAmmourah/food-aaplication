package com.farah.foodapp.menu;

public class FoodItem {
    private String name;
    private String description;
    private String restaurant;
    private double smallPrice;
    private double largePrice;
    private float rating;
    private int imageResId;

    public FoodItem(String name, String description, String restaurant,
                    double smallPrice, double largePrice, float rating, int imageResId) {
        this.name = name;
        this.description = description;
        this.restaurant = restaurant;
        this.smallPrice = smallPrice;
        this.largePrice = largePrice;
        this.rating = rating;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getRestaurant() { return restaurant; }
    public double getSmallPrice() { return smallPrice; }
    public double getLargePrice() { return largePrice; }
    public float getRating() { return rating; }
    public int getImageResId() { return imageResId;}
}