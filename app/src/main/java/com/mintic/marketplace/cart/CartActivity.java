package com.mintic.marketplace.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mintic.marketplace.R;
import com.mintic.marketplace.utils.Constants;
import com.mintic.marketplace.utils.Firestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";

    TextView totalTextview;
    RecyclerView cartProductsRecycler;

    FirebaseFirestore db;

    ArrayList<Firestore.Product> productList;
    ArrayList<String> productsInCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        totalTextview = findViewById(R.id.cart_total);
        cartProductsRecycler = findViewById(R.id.cart_products_recyclerview);

        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();

        productList = new Gson().fromJson(bundle.getString(Constants.products), new TypeToken<ArrayList<Firestore.Product>>() {
        }.getType());
        productsInCart = new Gson().fromJson(bundle.getString(Constants.cart), new TypeToken<ArrayList<String>>() {
        }.getType());

        renderUI();
    }

    private void renderUI() {
        ArrayList<Firestore.Product> products = new ArrayList<>();
        int total = 0;

        for (int i = 0; i < productList.size(); i++) {
            if (productsInCart.contains(productList.get(i).getId())) {
                products.add(productList.get(i));
                total += Integer.parseInt(productList.get(i).getPrice());
            }
        }

        NumberFormat numberFormatInstance = NumberFormat.getCurrencyInstance(Locale.US);
        numberFormatInstance.setMaximumFractionDigits(0);

        totalTextview.setText(numberFormatInstance.format(total));

        CartAdapter cartAdapter = new CartAdapter(CartActivity.this, products);
        cartProductsRecycler.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        cartProductsRecycler.setAdapter(cartAdapter);
    }
}