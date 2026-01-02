package com.farah.foodapp.cart;

public class CartItem {
    private String name;
    private String restaurantName;
    private String size;
    private double price;
    private int quantity;
    private String imageUrl;
    private String restaurantId;

    public CartItem(String name, String restaurantName, String size, double price, String imageUrl, String restaurantId) {
        this.name = name;
        this.restaurantName = restaurantName;
        this.size = size != null ? size : "Regular";
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = 1;
        this.restaurantId = restaurantId;
    }

    public String getName() { return name; }
    public String getRestaurantName() { return restaurantName; }
    public String getSize() { return size; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }
    public String getRestaurantId() { return restaurantId; }

    public void increaseQuantity() { this.quantity++; }
    public void decreaseQuantity() { if (this.quantity > 0) this.quantity--; }
}