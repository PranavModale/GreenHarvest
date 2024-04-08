package com.example.greenharvest.model;

public class Seller {
    private String id;
    private String sellerName;
    private String email;
    private String password;
    private String state;
    private String city;
    private String phoneNumber;
    private String profileImageUrl;

    public Seller() {
        // Default constructor required for Firebase
    }

    public Seller(String id, String sellerName, String email, String password, String state, String city, String phoneNumber, String profileImageUrl) {
        this.id = id;
        this.sellerName = sellerName;
        this.email = email;
        this.password = password;
        this.state = state;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
