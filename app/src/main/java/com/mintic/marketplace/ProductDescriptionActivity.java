package com.mintic.marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mintic.marketplace.utils.Constants;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class ProductDescriptionActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProductDescriptionActiv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        TextView productNameTextview = findViewById(R.id.product_name_textview);
        TextView productBrandTextview = findViewById(R.id.product_brand_textview);
        TextView productDescriptionTextview = findViewById(R.id.product_description_textview);
        TextView productPriceTextview = findViewById(R.id.product_price_textview);
        ImageView productPhotoImageView = findViewById(R.id.product_photo_imageview);
        Button seeLocationButton = findViewById(R.id.product_see_location_button);

        seeLocationButton.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();

        String productName = bundle.getString(Constants.name);
        String productBrand = bundle.getString(Constants.brand);
        String productDescription = bundle.getString(Constants.description);
        String productPrice = bundle.getString(Constants.price);
        String productPhoto = bundle.getString(Constants.photo);

        Objects.requireNonNull(getSupportActionBar()).setTitle(productName);

        if (!productName.isEmpty()) {
            productNameTextview.setText(productName);
        }
        if (!productBrand.isEmpty()) {
            productBrandTextview.setText(productBrand);
        }
        if (!productDescription.isEmpty()) {
            productDescriptionTextview.setText(productDescription);
        }
        if (!productPrice.isEmpty()) {
            NumberFormat numberFormatInstance = NumberFormat.getCurrencyInstance(Locale.US);
            numberFormatInstance.setMaximumFractionDigits(0);
            productPriceTextview.setText(numberFormatInstance.format(Integer.parseInt(productPrice)));
        }
        if (!productPhoto.isEmpty()) {
            Glide.with(this).load(productPhoto).into(productPhotoImageView);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.product_see_location_button) {
            startActivity(new Intent(ProductDescriptionActivity.this, MapsActivity.class));
        }
    }
}