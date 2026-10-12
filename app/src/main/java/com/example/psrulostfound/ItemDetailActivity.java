package com.example.psrulostfound;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class ItemDetailActivity extends AppCompatActivity {

    private ImageView imgItem;
    private TextView tvTitle, tvDescription, tvLocation, tvPhone, tvReporterName, tvType;
    private Button btnClaim;
    private FirebaseFirestore db;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        db = FirebaseFirestore.getInstance();

        // ผูก View
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvLocation = findViewById(R.id.tvLocation);
        tvPhone = findViewById(R.id.tvPhone);
        tvReporterName = findViewById(R.id.tvReporterName);
        tvType = findViewById(R.id.tvType);
        imgItem = findViewById(R.id.imgItem);
        btnClaim = findViewById(R.id.btnClaim);

        // รับค่าจาก Intent
        if (getIntent() != null) {
            documentId = getIntent().getStringExtra("documentId");
            String title = getIntent().getStringExtra("title");
            String description = getIntent().getStringExtra("description");
            String location = getIntent().getStringExtra("location");
            String phone = getIntent().getStringExtra("phone");
            String reporterName = getIntent().getStringExtra("reporterName");
            String type = getIntent().getStringExtra("type");
            String imageUrl = getIntent().getStringExtra("imageUrl");
            String postUserId = getIntent().getStringExtra("userId");
            String correctPin = getIntent().getStringExtra("pin");

            if (tvTitle != null) tvTitle.setText(title != null ? title : "");
            if (tvDescription != null) tvDescription.setText(description != null ? description : "");
            if (tvLocation != null) tvLocation.setText("สถานที่: " + (location != null ? location : "-"));
            if (tvPhone != null) tvPhone.setText("เบอร์โทร: " + (phone != null ? phone : "-"));
            if (tvReporterName != null) tvReporterName.setText("ผู้แจ้ง: " + (reporterName != null ? reporterName : "-"));
            if (tvType != null) tvType.setText(type != null ? type : "");

            if (imgItem != null && imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).into(imgItem);
            }
        }

        // เมื่อกดปุ่มรับคืนแล้ว
        if (btnClaim != null) {
            btnClaim.setOnClickListener(v -> {
                // ดึง UID ของผู้ใช้งานเครื่องปัจจุบัน
                String currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null
                        ? com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid()
                        : "";

                // 1. ถ้าเป็นเครื่องเจ้าของโพสต์เดิม (Smart Check) -> อัปเดตสถานะได้ทันที
                String postUserId = getIntent().getStringExtra("userId");
                if (currentUserId != null && !currentUserId.isEmpty() && currentUserId.equals(postUserId)) {
                    updateStatusToClaimed(documentId);
                } else {
                    // 2. ถ้าเป็นเครื่องอื่น/ผู้ใช้อื่น -> บังคับแสดง Dialog ให้ใส่ PIN 4 หลัก
                    String correctPin = getIntent().getStringExtra("pin");
                    showPinDialogToClaim(documentId, correctPin);
                }
            });
        }
    }
    // ฟังก์ชันสำหรับอัปเดตสถานะเป็น claimed
    private void updateStatusToClaimed(String docId) {
        if (docId != null && !docId.isEmpty()) {
            db.collection("posts").document(docId)
                    .update("status", "claimed")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ItemDetailActivity.this, "อัปเดตสถานะเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ItemDetailActivity.this, "เกิดข้อผิดพลาด: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "ไม่พบรหัสเอกสาร (documentId)", Toast.LENGTH_SHORT).show();
        }
    }

    // ฟังก์ชันแสดง Dialog ให้กรอก PIN 4 หลัก
    private void showPinDialogToClaim(String docId, String correctPin) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("ยืนยัน PIN 4 หลัก");
        builder.setMessage("กรุณากรอก PIN 4 หลักเพื่อยืนยัน");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("ยืนยัน", (dialog, which) -> {
            String enteredPin = input.getText().toString().trim();


            String targetPin = (correctPin != null) ? correctPin.trim() : "";
            // เพิ่ม Toast เช็กค่าจริงที่ดึงมาจาก Database
            Toast.makeText(ItemDetailActivity.this, "PIN ในระบบคือ: '" + targetPin + "'", Toast.LENGTH_SHORT).show();

            if (!enteredPin.isEmpty() && enteredPin.equals(targetPin)) {
                updateStatusToClaimed(docId);
            } else {
                Toast.makeText(ItemDetailActivity.this, "รหัส PIN ไม่ถูกต้อง!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("ยกเลิก", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
