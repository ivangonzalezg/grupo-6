package com.mintic.marketplace;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mintic.marketplace.utils.Constants;
import com.mintic.marketplace.utils.Firestore;
import com.mintic.marketplace.utils.SharedPref;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
    private static final String TAG = "ProductListAdapter";

    HomeActivity context;
    ArrayList<Firestore.Product> productList;
    ArrayList<String> productsFavorites;
    ArrayList<String> productsInCart;

    FirebaseFirestore db;

    public ProductListAdapter(HomeActivity context, ArrayList<Firestore.Product> productList, ArrayList<String> productsFavorites, ArrayList<String> productsInCart) {
        this.context = context;
        this.productList = productList;
        this.productsFavorites = productsFavorites;
        this.productsInCart = productsInCart;

        this.db = FirebaseFirestore.getInstance();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout productContainerLinearLayout;
        private final TextView productNameTextview;
        private final TextView productBrandTextview;
        private final TextView productDescriptionTextview;
        private final TextView productPriceTextview;
        private final ImageView productPhotoImageView;
        private final ImageButton productAddToFavoriteImageButton;
        private final ImageButton productAddToCartImageButton;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            productContainerLinearLayout = itemView.findViewById(R.id.product_container_linearlayout);
            productNameTextview = itemView.findViewById(R.id.product_name_textview);
            productBrandTextview = itemView.findViewById(R.id.product_brand_textview);
            productDescriptionTextview = itemView.findViewById(R.id.product_description_textview);
            productPriceTextview = itemView.findViewById(R.id.product_price_textview);
            productPhotoImageView = itemView.findViewById(R.id.product_photo_imageview);
            productAddToFavoriteImageButton = itemView.findViewById(R.id.product_add_to_favorite_imagebutton);
            productAddToCartImageButton = itemView.findViewById(R.id.product_add_to_cart_imagebutton);
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
        String productId = productList.get(position).getId();
        String productName = productList.get(position).getName();
        String productBrand = productList.get(position).getBrand();
        String productDescription = productList.get(position).getDescription();
        String productPrice = productList.get(position).getPrice();
        String productPhoto = productList.get(position).getPhoto();
        String userId = SharedPref.getString(context, Constants.userId);
        DocumentReference userDocument = db.collection(Constants.users).document(userId);
        boolean isFavorite = productsFavorites.contains(productId);
        boolean isInCart = productsInCart.contains(productId);

        holder.productContainerLinearLayout.setOnClickListener(v -> {
            Intent description = new Intent(context, ProductDescriptionActivity.class);
            description.putExtra(Constants.name, productName);
            description.putExtra(Constants.brand, productBrand);
            description.putExtra(Constants.description, productDescription);
            description.putExtra(Constants.price, productPrice);
            description.putExtra(Constants.photo, productPhoto);
            context.startActivity(description);
        });

        if (isFavorite) {
            holder.productAddToFavoriteImageButton.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.productAddToFavoriteImageButton.setImageResource(R.drawable.ic_favorite_border);
        }

        if (isInCart) {
            holder.productAddToCartImageButton.setImageResource(R.drawable.ic_remove_shopping_cart);
        } else {
            holder.productAddToCartImageButton.setImageResource(R.drawable.ic_add_shopping_cart);
        }

        holder.productAddToFavoriteImageButton.setOnClickListener(v -> {
                    holder.productAddToFavoriteImageButton.setEnabled(false);
                    userDocument.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ArrayList<String> favorites = (ArrayList<String>) Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getData()).get(Constants.favorites);
                            if (favorites == null) {
                                favorites = new ArrayList<>();
                            }
                            if (isFavorite) {
                                favorites.remove(productId);
                            } else {
                                favorites.add(productId);
                            }
                            userDocument.update(Constants.favorites, favorites).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    if (isFavorite) {
                                        Toast.makeText(context, R.string.product_list_success_remove_from_favorite, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, R.string.product_list_success_add_to_favorite, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(context, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                }
                                holder.productAddToFavoriteImageButton.setEnabled(true);
                            });
                        } else {
                            Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            holder.productAddToFavoriteImageButton.setEnabled(true);
                        }
                    });
                }
        );

        holder.productAddToCartImageButton.setOnClickListener(v -> {
            holder.productAddToCartImageButton.setEnabled(false);
            userDocument.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<String> cart = (ArrayList<String>) Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getData()).get(Constants.cart);
                    if (cart == null) {
                        cart = new ArrayList<>();
                    }
                    if (isInCart) {
                        cart.remove(productId);
                    } else {
                        cart.add(productId);
                    }
                    userDocument.update(Constants.cart, cart).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            if (isInCart) {
                                Toast.makeText(context, R.string.product_list_success_remove_from_cart, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, R.string.product_list_success_add_to_cart, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                        holder.productAddToCartImageButton.setEnabled(true);
                    });
                } else {
                    Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    holder.productAddToCartImageButton.setEnabled(true);
                }
            });
        });

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
            NumberFormat numberFormatInstance = NumberFormat.getCurrencyInstance(Locale.US);
            numberFormatInstance.setMaximumFractionDigits(0);
            holder.productPriceTextview.setText(numberFormatInstance.format(Integer.parseInt(productPrice)));
        }
        if (!productPhoto.isEmpty()) {
            Glide.with(context).load(productPhoto).into(holder.productPhotoImageView);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}