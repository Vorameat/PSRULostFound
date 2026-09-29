package com.example.psrulostfound;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class ItemDetailActivity extends AppCompatActivity {

    private String phoneNumber = "";
    private String documentId = "";
    private String itemTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        ImageView imgDetail = findViewById(R.id.imgDetail);
        TextView tvDetailTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDetailCategory = findViewById(R.id.tvDetailCategory);
        TextView tvDetailLocation = findViewById(R.id.tvDetailLocation);
        TextView tvDetailDescription = findViewById(R.id.tvDetailDescription);
        TextView tvDetailReporter = findViewById(R.id.tvDetailReporter);
        Button btnCall = findViewById(R.id.btnCall);
        Button btnComplete = findViewById(R.id.btnComplete);

        Intent intent = getIntent();
        if (intent != null) {
            documentId = intent.getStringExtra("documentId");
            itemTitle = intent.getStringExtra("title");
            String category = intent.getStringExtra("category");
            String location = intent.getStringExtra("location");
            String description = intent.getStringExtra("description");
            String reporter = intent.getStringExtra("reporter");
            phoneNumber = intent.getStringExtra("phone");
            String imageUrl = intent.getStringExtra("imageUrl");

            if (tvDetailTitle != null) tvDetailTitle.setText(itemTitle != null ? itemTitle : "");

            if (tvDetailCategory != null) {
                String statusText = category != null ? category : "ไม่ระบุ";
                if (statusText.contains("ได้รับ") || statusText.contains("คืนแล้ว") || statusText.contains("เสร็จสิ้น")) {
                    tvDetailCategory.setText("สถานะ: ได้รับของคืนแล้ว");
                    tvDetailCategory.setTextColor(Color.parseColor("#4CAF50"));
                } else if (statusText.contains("พบของ")) {
                    tvDetailCategory.setText("สถานะ: " + statusText);
                    tvDetailCategory.setTextColor(Color.parseColor("#2196F3"));
                } else {
                    tvDetailCategory.setText("สถานะ: " + statusText);
                    tvDetailCategory.setTextColor(Color.parseColor("#F44336"));
                }
            }

            if (tvDetailLocation != null) tvDetailLocation.setText("สถานที่: " + (location != null ? location : ""));
            if (tvDetailDescription != null) tvDetailDescription.setText(description != null ? description : "");

            if (tvDetailReporter != null) {
                if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                    tvDetailReporter.setText("ผู้แจ้ง: " + (reporter != null ? reporter : "") + " (" + phoneNumber + ")");
                } else {
                    tvDetailReporter.setText("ผู้แจ้ง: " + (reporter != null ? reporter : ""));
                }
            }

            if (imgDetail != null && imageUrl != null && !imageUrl.trim().isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .into(imgDetail);
            }

            // ปุ่มโทรหาผู้แจ้ง
            if (btnCall != null) {
                btnCall.setOnClickListener(v -> {
                    if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                        dialIntent.setData(Uri.parse("tel:" + phoneNumber.trim()));
                        startActivity(dialIntent);
                    } else {
                        Toast.makeText(this, "ไม่พบเบอร์โทรศัพท์ผู้แจ้ง", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // ปุ่มทำรายการเสร็จสิ้น (บันทึกและอัปเดตลง Firestore)
            if (btnComplete != null) {
                btnComplete.setOnClickListener(v -> {
                    String updatedStatus = "ได้รับของคืนแล้ว";

                    // 1. ลองค้นหาและอัปเดตผ่าน documentId ใน collection "posts"
                    if (documentId != null && !documentId.isEmpty()) {
                        FirebaseFirestore.getInstance().collection("posts")
                                .document(documentId)
                                .update("type", updatedStatus)
                                .addOnSuccessListener(aVoid -> {
                                    updateUIAndFinish(tvDetailCategory, updatedStatus);
                                })
                                .addOnFailureListener(e -> {
                                    // ถ้าอัปเดตผ่าน documentId โดยตรงไม่สำเร็จ ให้ค้นจาก docId/title แทน
                                    searchAndUpdateByQuery(tvDetailCategory, updatedStatus);
                                });
                    } else {
                        searchAndUpdateByQuery(tvDetailCategory, updatedStatus);
                    }
                });
            }
        }
    }

    private void searchAndUpdateByQuery(TextView tvDetailCategory, String updatedStatus) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // ค้นหาใน Collection "posts" จากฟิลด์ title
        db.collection("posts")
                .whereEqualTo("title", itemTitle)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        queryDocumentSnapshots.getDocuments().get(0).getReference()
                                .update("type", updatedStatus)
                                .addOnSuccessListener(aVoid -> updateUIAndFinish(tvDetailCategory, updatedStatus))
                                .addOnFailureListener(e -> Toast.makeText(this, "บันทึกไม่สำเร็จ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "ไม่พบข้อมูลรายการนี้ใน Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "เกิดข้อผิดพลาดในการเชื่อมต่อ", Toast.LENGTH_SHORT).show());
    }

    private void updateUIAndFinish(TextView tvDetailCategory, String updatedStatus) {
        if (tvDetailCategory != null) {
            tvDetailCategory.setText("สถานะ: " + updatedStatus);
            tvDetailCategory.setTextColor(Color.parseColor("#4CAF50"));
        }
        Toast.makeText(ItemDetailActivity.this, "บันทึกสถานะเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
        finish(); // ปิดหน้ารายละเอียด เพื่อกลับไปดูผลในหน้าหลัก
    }
}