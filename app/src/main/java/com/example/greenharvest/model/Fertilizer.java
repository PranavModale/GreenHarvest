package com.example.greenharvest.model;

public class Fertilizer {
    private String fertilizerId;
    private String adminId;
    private String type;
    private String subtype;
    private double pricePerKg;
    private int quantity;
    private String manufacturingDate;
    private String expiryDate;

    public Fertilizer() {
        // Default constructor required for Firebase
    }

    public Fertilizer(String fertilizerId, String adminId, String type, String subtype, double pricePerKg, int quantity, String manufacturingDate, String expiryDate) {
        this.fertilizerId = fertilizerId;
        this.adminId = adminId;
        this.type = type;
        this.subtype = subtype;
        this.pricePerKg = pricePerKg;
        this.quantity = quantity;
        this.manufacturingDate = manufacturingDate;
        this.expiryDate = expiryDate;
    }

    public String getFertilizerId() {
        return fertilizerId;
    }

    public void setFertilizerId(String fertilizerId) {
        this.fertilizerId = fertilizerId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public double getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
