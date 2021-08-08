package com.mintic.marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.mintic.marketplace.cart.CartActivity;
import com.mintic.marketplace.utils.Constants;
import com.mintic.marketplace.utils.Firestore;
import com.mintic.marketplace.utils.SharedPref;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "HomeActivity";

    DrawerLayout drawer;

    FloatingActionButton addProductFAB;

    FirebaseAuth auth;
    FirebaseFirestore db;

    RecyclerView productsListRecycler;
    SwipeRefreshLayout productsSwipeRefreshLayout;

    ArrayList<Firestore.Product> productList = new ArrayList<>();
    ArrayList<String> productsFavorites = new ArrayList<>();
    ArrayList<String> productsInCart = new ArrayList<>();
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer = findViewById(R.id.drawer_layout);

        String firstName = SharedPref.getString(this, Constants.firstName);
        String lastName = SharedPref.getString(this, Constants.lastName);
        String email = SharedPref.getString(this, Constants.email);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userFullName = (TextView) headerView.findViewById(R.id.nav_header_user_full_name);
        userFullName.setText(firstName.concat(" ").concat(lastName));
        TextView userEmail = (TextView) headerView.findViewById(R.id.nav_header_user_email);
        userEmail.setText(email);

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
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (value != null && value.exists()) {
                ArrayList<String> favorites = (ArrayList<String>) Objects.requireNonNull(value.getData()).get(Constants.favorites);
                if (favorites == null) {
                    favorites = new ArrayList<>();
                }
                productsFavorites = favorites;
                ArrayList<String> cart = (ArrayList<String>) Objects.requireNonNull(value.getData()).get(Constants.cart);
                if (cart == null) {
                    cart = new ArrayList<>();
                }
                productsInCart = cart;
                verifyCart();
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
                    verifyCart();
                }
            }
            productsSwipeRefreshLayout.setRefreshing(false);
        });
    }

    private void renderList() {
        ProductListAdapter productListAdapter = new ProductListAdapter(HomeActivity.this, productList, productsFavorites, productsInCart);
        productsListRecycler.setLayoutManager(new LinearLayoutManager(this));
        productsListRecycler.setAdapter(productListAdapter);
    }

    private void verifyCart() {
        if (menu != null) {
            menu.findItem(R.id.cart_item).setVisible(productsInCart.size() > 0 && productList.size() > 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;
        verifyCart();
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_item:
                auth.signOut();
                SharedPref.clear(HomeActivity.this);
                Intent intentLogin = new Intent(HomeActivity.this, LoginActivity.class);
                intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogin);
                return true;
            case R.id.cart_item:
                Intent intentCart = new Intent(HomeActivity.this, CartActivity.class);
                intentCart.putExtra(Constants.cart, new Gson().toJson(productsInCart));
                intentCart.putExtra(Constants.products, new Gson().toJson(productList));
                startActivity(intentCart);
                return true;
            case android.R.id.home:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_product_fab) {
            startActivity(new Intent(HomeActivity.this, AddProductActivity.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.nav_favorites){
            // TODO: Open favorites activity
        }
        return false;
    }
}