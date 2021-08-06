package com.mintic.marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mintic.marketplace.utils.Constants;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        TextView productNameTextview = findViewById(R.id.product_name_textview);
        TextView productBrandTextview = findViewById(R.id.product_brand_textview);
        TextView productDescriptionTextview = findViewById(R.id.product_description_textview);
        TextView productPriceTextview = findViewById(R.id.product_price_textview);
        ImageView productPhotoImageView = findViewById(R.id.product_photo_imageview);

        Bundle bundle = getIntent().getExtras();

        String productName = bundle.getString(Constants.name);
        String productBrand = bundle.getString(Constants.brand);
        String productDescription = bundle.getString(Constants.description);
        String productPrice = bundle.getString(Constants.price);
        String productPhoto = bundle.getString(Constants.photo);


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
            productPriceTextview.setText(NumberFormat.getCurrencyInstance(Locale.US).format(Integer.parseInt(productPrice)));
        }
        if (!productPhoto.isEmpty()) {
            Glide.with(this).load(productPhoto).into(productPhotoImageView);
        }
    }
}