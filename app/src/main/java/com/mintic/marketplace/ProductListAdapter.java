package com.mintic.marketplace;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import static android.text.TextUtils.concat;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
    ArrayList<Map<String, Object>> product_list;

    public ProductListAdapter(ArrayList<Map<String, Object>> product_list) {
        this.product_list = product_list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView productNameTextview;
        private final TextView productPriceTextview;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            productNameTextview = itemView.findViewById(R.id.product_name_textview);
            productPriceTextview = itemView.findViewById(R.id.product_price_textview);
        }
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductListAdapter.ViewHolder holder, int position) {
        String productName = product_list.get(position).get("name").toString();
        String productPrice = product_list.get(position).get("price").toString();

        holder.productNameTextview.setText(productName);
        holder.productPriceTextview.setText(concat("$ ", productPrice));
    }

    @Override
    public int getItemCount() {
        return product_list.size();
    }
}