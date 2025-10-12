package com.farah.foodapp.cart;

public class CartItem {
    private String name;
    private String restaurant;
    private String size;
    private double price;
    private int quantity;
    private String imageUrl;

    public CartItem(String name, String restaurant, String size, double price, String imageUrl) {
        this.name = name;
        this.restaurant = restaurant;
        this.size = size;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = 1;
    }

    public String getName() { return name; }
    public String getRestaurant() { return restaurant; }
    public String getSize() { return size; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }

    public void increaseQuantity() { this.quantity++; }
    public void decreaseQuantity() { if (this.quantity > 0) this.quantity--; }
}
