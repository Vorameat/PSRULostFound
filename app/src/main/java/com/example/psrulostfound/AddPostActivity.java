package com.example.psrulostfound;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    // 1. ประกาศตัวแปรทั้งหมดไว้บนสุดตรงนี้ (นอกทุกเมธอด)
    private EditText etTitle, etDescription, etLocation, etPhone, etReporterName, etType, etImageUrl, etPinCode;
    private Button btnSubmit;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post2); // หรือชื่อ layout ที่คุณใช้

        // 2. ผูก View ใน onCreate
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etPhone = findViewById(R.id.etPhone);
        etReporterName = findViewById(R.id.etReporterName);
        etType = findViewById(R.id.etType);
        etImageUrl = findViewById(R.id.etImageUrl);
        etPinCode = findViewById(R.id.etPinCode); // เพิ่มตัวนี้
        btnSubmit = findViewById(R.id.btnSubmit);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance(); // เพิ่มตัวนี้

        btnSubmit.setOnClickListener(v -> savePost());
    }

    private void savePost() {
        // 3. อ่านค่าจากช่องต่างๆ ใน savePost
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String reporterName = etReporterName.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String pinCode = etPinCode.getText().toString().trim(); // อ่านค่า PIN

        if (title.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "กรุณากรอกชื่อสิ่งของและสถานที่", Toast.LENGTH_SHORT).show();
            return;
        }

        // ตรวจสอบความยาวรหัส PIN
        if (pinCode.length() != 4) {
            Toast.makeText(this, "กรุณาตั้งรหัส PIN ให้ครบ 4 หลัก", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);

        // ดึง User ID จากระบบ Anonymous Auth ที่ล็อกอินไว้เบื้องหลัง
        String currentUserId = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : "";

        Map<String, Object> post = new HashMap<>();
        post.put("title", title);
        post.put("description", description);
        post.put("location", location);
        post.put("phone", phone);
        post.put("reporterName", reporterName);
        post.put("type", type);
        post.put("imageUrl", imageUrl);
        post.put("pinCode", pinCode);          // เก็บรหัส PIN
        post.put("userId", currentUserId);     // เก็บ User ID คนโพสต์
        post.put("status", "active");

        db.collection("posts")
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddPostActivity.this, "เพิ่มประกาศเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(AddPostActivity.this, "เกิดข้อผิดพลาด: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}