package com.farah.foodapp.admin.managemenu;
public class FoodItemAdmin {
    private String name;
    private String description;
    private String restaurant;
    private double smallPrice;
    private String ingredents;
    private float rating;
    private String imageUrl;

    public FoodItemAdmin() {}

    public FoodItemAdmin(String name, String description, String restaurant,
                         double smallPrice, String ingredents, float rating, String imageUrl) {
        this.name = name;
        this.description = description;
        this.restaurant = restaurant;
        this.smallPrice = smallPrice;
        this.ingredents = ingredents;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getRestaurant() { return restaurant; }
    public double getSmallPrice() { return smallPrice; }
    public float getRating() { return rating; }
    public String getImageUrl() { return imageUrl; }
    public String getIngredients() { return ingredents; }
}
