package com.example.greenharvest.model;

import java.util.ArrayList;
import java.util.Date;

public class Container {
    private String containerId;
    private String containerNumber;
    private String containerName;
    private Date creationDate;
    private int daysToConvert; // Number of days it takes to convert waste to fertilizer
        private ArrayList<String> arrayOfSellers;
    private double totalWeight;
    private double capacity;
    private String adminId; // New field for adminId
    private String category; // New field for category

    // Default constructor
    public Container() {
        // Initialize ArrayList
        this.arrayOfSellers = new ArrayList<>();
    }

    // Getters and setters
    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getContainerNumber() {
        return containerNumber;
    }

    public void setContainerNumber(String containerNumber) {
        this.containerNumber = containerNumber;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getDaysToConvert() {
        return daysToConvert;
    }

    public void setDaysToConvert(int daysToConvert) {
        this.daysToConvert = daysToConvert;
    }

    public ArrayList<String> getArrayOfSellers() {
        return arrayOfSellers;
    }

    public void setArrayOfSellers(ArrayList<String> arrayOfSellers) {
        this.arrayOfSellers = arrayOfSellers;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
