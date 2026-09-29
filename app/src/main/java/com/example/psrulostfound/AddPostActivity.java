package com.example.psrulostfound;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class AddPostActivity extends AppCompatActivity {

    private Spinner spinnerType, spinnerLocation;
    private EditText etTitle, etDetail, etContactName, etContactPhone;
    private Button btnSelectImage, btnSubmit;
    private ImageView imgPreview;
    private Uri imageUri;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        spinnerType = findViewById(R.id.spinnerType);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        etTitle = findViewById(R.id.etTitle);
        etDetail = findViewById(R.id.etDetail);
        etContactName = findViewById(R.id.etContactName);
        etContactPhone = findViewById(R.id.etContactPhone);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgPreview = findViewById(R.id.imgPreview);

        // ตัวเลือกประเภท
        String[] types = {"ของหาย (Lost)", "พบของ (Found)"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spinnerType.setAdapter(typeAdapter);

        // ตัวเลือกสถานที่ใน PSRU
        String[] locations = {
                "คณะวิทยาการจัดการ",
                "คณะครุศาสตร์",
                "คณะวิทยาศาสตร์และเทคโนโลยี",
                "คณะเทคโนโลยีการเกษตร",
                "คณะมนุษยศาสตร์และสังคมศาสตร์",
                "คณะเทคโนโลยีอุตสาหกรรม",
                "คณะพยาบาลศาสตร์",
                "อาคารเรียนรวม (e-Testing)",
                "สำนักวิทยบริการฯ (ห้องสมุด)",
                "โรงอาหารกลาง / ลานกิจกรรม",
                "หอพักนักศึกษา",
                "สถานที่อื่นๆ ภายในมหาวิทยาลัย"
        };
        ArrayAdapter<String> locAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations);
        spinnerLocation.setAdapter(locAdapter);

        // เลือกระบบรูปภาพ
        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imgPreview.setImageURI(imageUri);
                        imgPreview.setVisibility(View.VISIBLE);
                    }
                }
        );

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnSubmit.setOnClickListener(v -> savePost());
    }

    private void savePost() {
        String title = etTitle.getText().toString().trim();
        String detail = etDetail.getText().toString().trim();
        String name = etContactName.getText().toString().trim();
        String phone = etContactPhone.getText().toString().trim();

        if (title.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        Toast.makeText(this, "กำลังบันทึกข้อมูล...", Toast.LENGTH_SHORT).show();

        if (imageUri != null) {
            try {
                // 1. แปลง Uri เป็น Bitmap และย่อขนาดรูปไม่ให้เกิน 600px
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float ratio = (float) width / (float) height;
                int newWidth = 600;
                int newHeight = (int) (newWidth / ratio);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

                // 2. บีบอัดภาพเป็น Base64 String (คุณภาพ 60%)
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                byte[] data = baos.toByteArray();
                String base64Image = "data:image/jpeg;base64," + Base64.encodeToString(data, Base64.DEFAULT);

                // 3. ส่ง Base64 String ไปบันทึกลง Firestore ทันที
                uploadData(base64Image);

            } catch (IOException e) {
                e.printStackTrace();
                btnSubmit.setEnabled(true);
                Toast.makeText(this, "จัดการรูปภาพไม่สำเร็จ", Toast.LENGTH_SHORT).show();
            }
        } else {
            uploadData("");
        }
    }

    private void uploadData(String imageUrl) {
        String docId = UUID.randomUUID().toString();
        ItemModel item = new ItemModel(
                docId,
                spinnerType.getSelectedItem().toString(),
                etTitle.getText().toString().trim(),
                spinnerLocation.getSelectedItem().toString(),
                etDetail.getText().toString().trim(),
                etContactName.getText().toString().trim(),
                etContactPhone.getText().toString().trim(),
                imageUrl
        );

        db.collection("posts").document(docId).set(item)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "บันทึกเรียบร้อยแล้ว!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(this, "เกิดข้อผิดพลาดในการบันทึก", Toast.LENGTH_SHORT).show();
                });
    }
}