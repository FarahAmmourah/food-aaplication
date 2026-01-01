package com.farah.foodapp.menu;

public class FoodItem {// the info of the meal
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private float rating;
    private String restaurantName;
    private double smallPrice;
    private double largePrice;
    private String restaurantId;


    public FoodItem(String name, String description, String imageUrl, float rating,
                    String restaurantName, double smallPrice, double largePrice) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.restaurantName = restaurantName;
        this.smallPrice = smallPrice;
        this.largePrice = largePrice;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public FoodItem() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }// the setter for the name is used in the cart to add the item and size to the

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public double getSmallPrice() { return smallPrice; }
    public void setSmallPrice(double smallPrice) { this.smallPrice = smallPrice; }

    public double getLargePrice() { return largePrice; }
    public void setLargePrice(double largePrice) { this.largePrice = largePrice; }
    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
