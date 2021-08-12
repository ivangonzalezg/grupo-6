package com.mintic.marketplace.favorites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mintic.marketplace.R;
import com.mintic.marketplace.utils.Constants;
import com.mintic.marketplace.utils.Firestore;

import java.util.ArrayList;
import java.util.Objects;

public class FavoritesActivity extends AppCompatActivity {
    private static final String TAG = "FavoritesActivity";

    RecyclerView favoriteProductsRecycler;
    TextView favoriteProductsTextview;

    FirebaseFirestore db;

    ArrayList<Firestore.Product> productList;
    ArrayList<String> productsFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.favorites_title);

        favoriteProductsRecycler = findViewById(R.id.favorite_products_recyclerview);
        favoriteProductsTextview = findViewById(R.id.favorite_products_textview);

        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();

        productList = new Gson().fromJson(bundle.getString(Constants.products), new TypeToken<ArrayList<Firestore.Product>>() {
        }.getType());
        productsFavorites = new Gson().fromJson(bundle.getString(Constants.favorites), new TypeToken<ArrayList<String>>() {
        }.getType());

        if (!productsFavorites.isEmpty()) {
            favoriteProductsRecycler.setVisibility(View.VISIBLE);
            favoriteProductsTextview.setVisibility(View.GONE);
            renderUI();
        } else {
            favoriteProductsRecycler.setVisibility(View.GONE);
            favoriteProductsTextview.setVisibility(View.VISIBLE);
        }
    }

    private void renderUI() {
        ArrayList<Firestore.Product> products = new ArrayList<>();

        for (int i = 0; i < productList.size(); i++) {
            if (productsFavorites.contains(productList.get(i).getId())) {
                products.add(productList.get(i));
            }
        }

        FavoritesAdapter favoritesAdapter = new FavoritesAdapter(FavoritesActivity.this, products);
        favoriteProductsRecycler.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));
        favoriteProductsRecycler.setAdapter(favoritesAdapter);
    }
}