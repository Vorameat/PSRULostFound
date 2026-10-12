package com.example.psrulostfound;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<ItemModel> historyList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerViewHistory);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            historyList = new ArrayList<>();
            adapter = new ItemAdapter(this, historyList);
            recyclerView.setAdapter(adapter);
        }

        db = FirebaseFirestore.getInstance();
        loadHistoryData();
    }

    private void loadHistoryData() {
        db.collection("posts")
                .whereEqualTo("status", "claimed")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        historyList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            ItemModel item = doc.toObject(ItemModel.class);
                            if (item != null) {
                                item.setId(doc.getId());
                                historyList.add(item);
                            }
                        }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}