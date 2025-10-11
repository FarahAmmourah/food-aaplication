package com.farah.foodapp.menu;

import com.farah.foodapp.R;

public class FoodItem {
    private String name;
    private String description;
    private String restaurantName;
    private double smallPrice;
    private double largePrice;
    private float rating;
    private int imageResId;

    public FoodItem() {
    }

    public FoodItem(String name, String description, String restaurantName,
                    double smallPrice, double largePrice, float rating, int imageResId) {
        this.name = name;
        this.description = description;
        this.restaurantName = restaurantName;
        this.smallPrice = smallPrice;
        this.largePrice = largePrice;
        this.rating = rating;
        this.imageResId = imageResId;
    }


    public FoodItem(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.restaurantName = "";
        this.smallPrice = price;
        this.largePrice = price + 2;
        this.rating = 5;
        this.imageResId = R.drawable.ic_food_placeholder;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getRestaurant() { return restaurantName; }
    public double getSmallPrice() { return smallPrice; }
    public double getLargePrice() { return largePrice; }
    public float getRating() { return rating; }
    public int getImageResId() { return imageResId;}
}
