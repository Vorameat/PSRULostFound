package com.example.psrulostfound;

public class ItemModel {
    private String id;
    private String title;
    private String description;
    private String location;
    private String phone;
    private String reporterName;
    private String type;
    private String imageUrl;
    private String status;
    private String pinCode; // เพิ่มรองรับ PIN 4 หลัก
    private String userId;  // เพิ่มรองรับ User ID

    // Constructor เปล่า (จำเป็นสำหรับ Firebase Firestore)
    public ItemModel() { }

    // --- Getter and Setter ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

}