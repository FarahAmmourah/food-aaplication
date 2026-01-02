package com.farah.foodapp.admin.managemenu;

public class FoodItemAdmin {

    private String name;
    private String description;
    private String restaurantId;
    private String restaurantName;
    private double smallPrice;
    private String ingredients;
    private float rating;
    private String imageUrl;

    public FoodItemAdmin() {}

    public FoodItemAdmin(String name,
                         String description,
                         String restaurantId,
                         String restaurantName,
                         double smallPrice,
                         String ingredients,
                         float rating,
                         String imageUrl) {

        this.name = name;
        this.description = description;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.smallPrice = smallPrice;
        this.ingredients = ingredients;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getRestaurantId() { return restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public double getSmallPrice() { return smallPrice; }
    public float getRating() { return rating; }
    public String getImageUrl() { return imageUrl; }
    public String getIngredients() { return ingredients; }
}