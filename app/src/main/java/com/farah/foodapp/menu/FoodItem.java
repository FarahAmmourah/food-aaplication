package com.farah.foodapp.menu;

import com.farah.foodapp.R; // عشان الصورة الافتراضية

public class FoodItem {
    private String name;
    private String description;
    private String restaurant;
    private double smallPrice;
    private double largePrice;
    private float rating;
    private int imageResId;

    // ✅ Constructor كامل (زي ما كان عندك)
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

    // ✅ Constructor جديد للـ Firebase menu
    public FoodItem(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.restaurant = ""; // المطعم ممكن ييجي من مكان ثاني
        this.smallPrice = price; // السعر الأساسي
        this.largePrice = price + 2; // فرضنا الكبير أغلى 2 (ممكن تعدلي)
        this.rating = 5; // افتراضي
        this.imageResId = R.drawable.ic_food_placeholder; // صورة افتراضية
    }

    // Getter methods
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getRestaurant() { return restaurant; }
    public double getSmallPrice() { return smallPrice; }
    public double getLargePrice() { return largePrice; }
    public float getRating() { return rating; }
    public int getImageResId() { return imageResId;}
}
