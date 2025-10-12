package com.farah.foodapp.cart;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final List<CartItem> cartItems = new ArrayList<>();
    private static final double DELIVERY_FEE = 3.0;

    public static void addItem(String name, String restaurant, String size, double price, String imageUrl) {
        for (CartItem item : cartItems) {
            if (item.getName().equals(name) && item.getSize().equals(size)) {
                item.increaseQuantity();
                return;
            }
        }
        cartItems.add(new CartItem(name, restaurant, size, price, imageUrl));
    }

    public static List<CartItem> getCartItems() {
        return cartItems;
    }

    public static double getSubtotal() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        return subtotal;
    }

    public static double getDeliveryFee() {
        return DELIVERY_FEE;
    }

    public static double getTotalPrice() {
        return getSubtotal() + DELIVERY_FEE;
    }

    public static int getTotalQuantity() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    public static void increaseItem(CartItem item) {
        item.increaseQuantity();
    }

    public static void decreaseItem(CartItem item) {
        item.decreaseQuantity();
        if (item.getQuantity() == 0) {
            cartItems.remove(item);
        }
    }

    public static void clearCart() {
        cartItems.clear();
    }

    public static List<String> getItemsAsList() {
        List<String> items = new ArrayList<>();
        for (CartItem item : cartItems) {
            String entry = item.getName() + " x" + item.getQuantity();
            if (item.getSize() != null && !item.getSize().isEmpty()) {
                entry += " (" + item.getSize() + ")";
            }
            items.add(entry);
        }
        return items;
    }
}
