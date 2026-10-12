package com.example.psrulostfound;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<ItemModel> itemList;
    private List<ItemModel> fullList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "เชื่อมต่อระบบยืนยันตัวตนล้มเหลว", Toast.LENGTH_SHORT).show();
                }
            });
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        fullList = new ArrayList<>();
        adapter = new ItemAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddPostActivity.class)));
        }

        FloatingActionButton fabHistory = findViewById(R.id.fabHistory);
        if (fabHistory != null) {
            fabHistory.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoryActivity.class)));
        }

        EditText etSearch = findViewById(R.id.etSearch);
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterList(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        loadDataFromFirestore();
    }

    private void loadDataFromFirestore() {
        db.collection("posts")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        itemList.clear();
                        fullList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            ItemModel item = doc.toObject(ItemModel.class);
                            if (item != null) {
                                item.setId(doc.getId());
                                if (!"claimed".equalsIgnoreCase(item.getStatus())) {
                                    itemList.add(item);
                                    fullList.add(item);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void filterList(String text) {
        List<ItemModel> filteredList = new ArrayList<>();
        for (ItemModel item : fullList) {
            if ((item.getTitle() != null && item.getTitle().toLowerCase().contains(text.toLowerCase())) ||
                    (item.getLocation() != null && item.getLocation().toLowerCase().contains(text.toLowerCase()))) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }
}