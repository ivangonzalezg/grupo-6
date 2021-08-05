package com.mintic.marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mintic.marketplace.utils.SharedPref;

import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private Activity mySelf;

    FloatingActionButton addProductFAB;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView product_list_recycler;
    ArrayList<Map<String, Object>> product_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mySelf = this;

        product_list_recycler = findViewById(R.id.product_list_recyclerview);
        product_list_recycler.setLayoutManager(new LinearLayoutManager(this));

        addProductFAB = findViewById(R.id.add_product_fab);
        addProductFAB.setOnClickListener(v -> {
            Intent addProductIntent = new Intent(mySelf, AddProductActivity.class);
            startActivity(addProductIntent);
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
    protected void onResume() {
        super.onResume();

        db.collectionGroup("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> product = document.getData();

                    product_list.add(product);
                }
                ProductListAdapter productListAdapter = new ProductListAdapter(product_list);
                product_list_recycler.setAdapter(productListAdapter);
                product_list_recycler.addItemDecoration(new DividerItemDecoration(mySelf, DividerItemDecoration.VERTICAL));
            }
        });

        // ProductListAdapter productListAdapter = new ProductListAdapter(product_list);
        // product_list_recycler.setAdapter(productListAdapter);
    }
}