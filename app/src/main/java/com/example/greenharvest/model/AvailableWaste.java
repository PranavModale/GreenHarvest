package com.example.greenharvest.model;

import java.util.Date;

public class AvailableWaste {
    private String sellerId; // Assuming you have a field for the seller ID
    private String category;
    private double weight;
    private Date date;


    // Default constructor required for Firebase deserialization
    public AvailableWaste() {
        // Default constructor required for Firebase
    }

    public AvailableWaste(String sellerId, String category, double weight, Date date) {
        this.sellerId = sellerId;
        this.category = category;
        this.weight = weight;
        this.date = date;
    }

    // Getters and setters for each field
    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
