package com.mintic.marketplace.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mintic.marketplace.R;
import com.mintic.marketplace.utils.Firestore;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Iván González on 8/08/21.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private static final String TAG = "CartAdapter";

    CartActivity context;
    ArrayList<Firestore.Product> products;

    FirebaseFirestore db;

    public CartAdapter(CartActivity context, ArrayList<Firestore.Product> products) {
        this.context = context;
        this.products = products;

        this.db = FirebaseFirestore.getInstance();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productPhotoImageView;
        private final TextView productNameTextview;
        private final TextView productPriceTextview;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            productPhotoImageView = itemView.findViewById(R.id.product_photo_imageview);
            productNameTextview = itemView.findViewById(R.id.product_name_textview);
            productPriceTextview = itemView.findViewById(R.id.product_price_textview);
        }
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CartAdapter.ViewHolder holder, int position) {
        String productPhoto = products.get(position).getPhoto();
        String productName = products.get(position).getName();
        String productPrice = products.get(position).getPrice();

        if (!productPhoto.isEmpty()) {
            Glide.with(context).load(productPhoto).into(holder.productPhotoImageView);
        }
        if (!productName.isEmpty()) {
            holder.productNameTextview.setText(productName);
        }
        if (!productPrice.isEmpty()) {
            NumberFormat numberFormatInstance = NumberFormat.getCurrencyInstance(Locale.US);
            numberFormatInstance.setMaximumFractionDigits(0);
            holder.productPriceTextview.setText(numberFormatInstance.format(Integer.parseInt(productPrice)));
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}
