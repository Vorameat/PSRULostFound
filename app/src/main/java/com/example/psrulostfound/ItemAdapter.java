package com.example.psrulostfound;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

        if (holder.tvStatus != null) {
            String type = item.getType();
            if (type != null && !type.trim().isEmpty()) {
                if (type.contains("ได้รับ") || type.contains("คืนแล้ว") || type.contains("เสร็จสิ้น") || type.contains("Completed")) {
                    holder.tvStatus.setText("ได้รับของคืนแล้ว");
                    holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // สีเขียว
                } else if (type.contains("พบของ") || type.contains("Found")) {
                    holder.tvStatus.setText(type);
                    holder.tvStatus.setTextColor(Color.parseColor("#2196F3")); // สีฟ้า
                } else {
                    holder.tvStatus.setText(type);
                    holder.tvStatus.setTextColor(Color.parseColor("#F44336")); // สีแดง
                }
            } else {
                holder.tvStatus.setText("ไม่ระบุ");
            }
        }

        holder.tvTitle.setText(item.getTitle() != null ? item.getTitle() : "");
        holder.tvLocation.setText("สถานที่: " + (item.getLocation() != null ? item.getLocation() : ""));
        holder.tvContact.setText("ผู้แจ้ง: " + (item.getContactName() != null ? item.getContactName() : "") +
                " (" + (item.getContactPhone() != null ? item.getContactPhone() : "") + ")");

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.imgItem);
        } else {
            holder.imgItem.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("documentId", item.getDocumentId());
            intent.putExtra("title", item.getTitle());
            intent.putExtra("category", item.getType());
            intent.putExtra("location", item.getLocation());
            intent.putExtra("description", item.getDetail());
            intent.putExtra("reporter", item.getContactName());
            intent.putExtra("phone", item.getContactPhone());

            String imgUrl = item.getImageUrl();
            if (imgUrl != null && !imgUrl.startsWith("data:image") && imgUrl.length() < 1000) {
                intent.putExtra("imageUrl", imgUrl);
            }

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvTitle, tvLocation, tvContact;
        ImageView imgItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvContact = itemView.findViewById(R.id.tvContact);
            imgItem = itemView.findViewById(R.id.imgItem);
        }
    }
}