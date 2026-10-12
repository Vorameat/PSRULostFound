package com.example.psrulostfound;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    private List<ItemModel> itemList;

    public ItemAdapter(Context context, List<ItemModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel item = itemList.get(position);

        holder.tvTitle.setText(item.getTitle() != null ? item.getTitle() : "");
        holder.tvLocation.setText("สถานที่: " + (item.getLocation() != null ? item.getLocation() : ""));
        holder.tvReporter.setText("ผู้แจ้ง: " + (item.getReporterName() != null ? item.getReporterName() : ""));

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context).load(item.getImageUrl()).into(holder.imgItem);
        } else {
            holder.imgItem.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("documentId", item.getId());
            intent.putExtra("title", item.getTitle());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("location", item.getLocation());
            intent.putExtra("phone", item.getPhone());
            intent.putExtra("reporterName", item.getReporterName());
            intent.putExtra("imageUrl", item.getImageUrl());
            intent.putExtra("type", item.getType());
            intent.putExtra("status", item.getStatus());
            intent.putExtra("userId", item.getUserId());
            intent.putExtra("pin", item.getPinCode());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    // เมธอดรองรับการค้นหา
    public void filterList(List<ItemModel> filteredList) {
        this.itemList = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvReporter;
        ImageView imgItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvReporter = itemView.findViewById(R.id.tvReporter);
            imgItem = itemView.findViewById(R.id.imgItem);
        }
    }
}