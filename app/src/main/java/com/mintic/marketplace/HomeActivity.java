package com.mintic.marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.mintic.marketplace.utils.SharedPref;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private Activity mySelf;

    FloatingActionButton addProductFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addProductFAB = findViewById(R.id.add_product_fab);

        mySelf = this;

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
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            SharedPref.clear(HomeActivity.this);
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}