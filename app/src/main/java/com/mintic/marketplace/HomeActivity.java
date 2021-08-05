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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mintic.marketplace.utils.Constants;
import com.mintic.marketplace.utils.SharedPref;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HomeActivity";

    FloatingActionButton addProductFAB;

    FirebaseAuth auth;
    FirebaseFirestore db;

    RecyclerView productsListRecycler;
    SwipeRefreshLayout productsSwipeRefreshLayout;

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
                ArrayList<Map<String, Object>> productList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> product = document.getData();
                    productList.add(product);
                }
                ProductListAdapter productListAdapter = new ProductListAdapter(productList);
                productsListRecycler.setLayoutManager(new LinearLayoutManager(this));
                productsListRecycler.setAdapter(productListAdapter);
            }
            productsSwipeRefreshLayout.setRefreshing(false);
        });
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