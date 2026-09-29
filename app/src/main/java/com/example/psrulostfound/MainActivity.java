package com.example.psrulostfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<ItemModel> itemList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fabAddPost = findViewById(R.id.fabAddPost);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        adapter = new ItemAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // ปุ่มกดไปหน้าแจ้งเรื่อง
        fabAddPost.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
            startActivity(intent);
        });

        // ดึงข้อมูล realtime จาก Firestore
        loadDataFromFirestore();
    }

    private void loadDataFromFirestore() {
        db.collection("posts").addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }

            if (value != null) {
                itemList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    ItemModel item = doc.toObject(ItemModel.class);
                    if (item != null) {
                        String type = item.getType();
                        // แสดงเฉพาะรายการที่ยังไม่ได้คืนของ
                        if (type == null || (!type.contains("ได้รับ") && !type.contains("คืนแล้ว") && !type.contains("เสร็จสิ้น"))) {
                            itemList.add(item);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}