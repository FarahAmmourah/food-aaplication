package com.farah.foodapp.orders;

import java.util.List;

public class OrderModel {
    private String id;
    private String userId;
    private String restaurantName;
    private double total;
    private String status;
    private String address;
    private double lat;
    private double lon;
    private long createdAt;
    private List<String> items;
    private String eta;

    public OrderModel() {}

    public OrderModel(String id, String userId, String restaurantName, double total,
                      String status, String address, double lat, double lon,
                      long createdAt, List<String> items, String eta) {
        this.id = id;
        this.userId = userId;
        this.restaurantName = restaurantName;
        this.total = total;
        this.status = status;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.createdAt = createdAt;
        this.items = items;
        this.eta = eta;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getRestaurantName() { return restaurantName; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    public String getAddress() { return address; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public long getCreatedAt() { return createdAt; }
    public List<String> getItems() { return items; }
    public String getEta() { return eta; }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }
}
