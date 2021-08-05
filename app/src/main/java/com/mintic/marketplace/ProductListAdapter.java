package com.mintic.marketplace;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mintic.marketplace.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
    private static final String TAG = "ProductListAdapter";

    ArrayList<Map<String, Object>> productList;

    public ProductListAdapter(ArrayList<Map<String, Object>> productList) {
        this.productList = productList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView productNameTextview;
        private final TextView productBrandTextview;
        private final TextView productDescriptionTextview;
        private final TextView productPriceTextview;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            productNameTextview = itemView.findViewById(R.id.product_name_textview);
            productBrandTextview = itemView.findViewById(R.id.product_brand_textview);
            productDescriptionTextview = itemView.findViewById(R.id.product_description_textview);
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
        String productName = Objects.requireNonNull(productList.get(position).get(Constants.name)).toString();
        String productBrand = Objects.requireNonNull(productList.get(position).get(Constants.brand)).toString();
        String productDescription = Objects.requireNonNull(productList.get(position).get(Constants.description)).toString();
        String productPrice = Objects.requireNonNull(productList.get(position).get(Constants.price)).toString();

        if (!productName.isEmpty()) {
            holder.productNameTextview.setText(productName);
        }
        if (!productBrand.isEmpty()) {
            holder.productBrandTextview.setText(productBrand);
        }
        if (!productDescription.isEmpty()) {
            holder.productDescriptionTextview.setText(productDescription);
        }
        if (!productPrice.isEmpty()) {
            holder.productPriceTextview.setText(NumberFormat.getCurrencyInstance(Locale.US).format(Integer.parseInt(productPrice)));
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}