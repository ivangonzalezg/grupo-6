package com.mintic.marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mintic.marketplace.utils.Constants;
import com.mintic.marketplace.utils.Firestore;
import com.mintic.marketplace.utils.SharedPref;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HomeActivity";

    FloatingActionButton addProductFAB;

    FirebaseAuth auth;
    FirebaseFirestore db;

    RecyclerView productsListRecycler;
    SwipeRefreshLayout productsSwipeRefreshLayout;

    ArrayList<Firestore.Product> productList = new ArrayList<>();
    ArrayList<String> productFavorites = new ArrayList<>();
    ArrayList<String> productCart = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        productsListRecycler = findViewById(R.id.products_list_recyclerview);
        productsSwipeRefreshLayout = findViewById(R.id.products_swipe_refresh_layout);
        addProductFAB = findViewById(R.id.add_product_fab);

        addProductFAB.setOnClickListener(this);
        productsSwipeRefreshLayout.setOnRefreshListener(this::getProducts);

        getProducts();

        String userId = SharedPref.getString(this, Constants.userId);
        db.collection(Constants.users).document(userId).addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            if (value != null && value.exists()) {
                ArrayList<String> favorites = (ArrayList<String>) Objects.requireNonNull(value.getData()).get(Constants.favorites);
                if (favorites == null) {
                    favorites = new ArrayList<>();
                }
                productFavorites = favorites;
                ArrayList<String> cart = (ArrayList<String>) Objects.requireNonNull(value.getData()).get(Constants.cart);
                if (cart == null) {
                    cart = new ArrayList<>();
                }
                productCart = cart;
                renderList();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProducts();
    }

    private void getProducts() {
        productsSwipeRefreshLayout.setRefreshing(true);
        db.collectionGroup(Constants.products).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {
                productList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Firestore.Product product = new Firestore.Product();
                    product.setId(document.getId());
                    product.setName((String) document.getData().get(Constants.name));
                    product.setBrand((String) document.getData().get(Constants.brand));
                    product.setCategory((String) document.getData().get(Constants.category));
                    product.setDescription((String) document.getData().get(Constants.description));
                    product.setPrice((String) document.getData().get(Constants.price));
                    product.setPhoto((String) document.getData().get(Constants.photo));
                    productList.add(product);
                    renderList();
                }
            }
            productsSwipeRefreshLayout.setRefreshing(false);
        });
    }

    private void renderList() {
        ProductListAdapter productListAdapter = new ProductListAdapter(HomeActivity.this, productList, productFavorites, productCart);
        productsListRecycler.setLayoutManager(new LinearLayoutManager(this));
        productsListRecycler.setAdapter(productListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.log_out_item) {
            auth.signOut();
            SharedPref.clear(HomeActivity.this);
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_product_fab) {
            startActivity(new Intent(HomeActivity.this, AddProductActivity.class));
        }
    }
}