package com.example.psrulostfound;

public class ItemModel {
    private String documentId;
    private String type;
    private String title;
    private String location;
    private String detail;
    private String contactName;
    private String contactPhone;
    private String imageUrl;

    // Constructor เปล่า (จำเป็นสำหรับ Firestore/Firebase)
    public ItemModel() {
    }

    // Constructor รับค่า 8 พารามิเตอร์ (ที่กำลังเรียกใช้ใน AddPostActivity)
    public ItemModel(String documentId, String type, String title, String location, String detail, String contactName, String contactPhone, String imageUrl) {
        this.documentId = documentId;
        this.type = type;
        this.title = title;
        this.location = location;
        this.detail = detail;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getDocumentId() { return documentId; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDetail() { return detail; }
    public String getContactName() { return contactName; }
    public String getContactPhone() { return contactPhone; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public void setType(String type) { this.type = type; }
    public void setTitle(String title) { this.title = title; }
    public void setLocation(String location) { this.location = location; }
    public void setDetail(String detail) { this.detail = detail; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}