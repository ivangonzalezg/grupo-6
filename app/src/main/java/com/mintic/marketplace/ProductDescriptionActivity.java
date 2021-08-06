package com.mintic.marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mintic.marketplace.utils.Constants;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class ProductDescriptionActivity extends AppCompatActivity {

    private TextView productNameTextview;
    private TextView productBrandTextview;
    private TextView productDescriptionTextview;
    private TextView productPriceTextview;
    private ImageView productPhotoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        productNameTextview = findViewById(R.id.product_name_textview_desc);
        productBrandTextview = findViewById(R.id.product_brand_textview_desc);
        productDescriptionTextview = findViewById(R.id.product_description_textview_desc);
        productPriceTextview = findViewById(R.id.product_price_textview_desc);
        productPhotoImageView = findViewById(R.id.product_photo_imageview_desc);

        Bundle bundle = getIntent().getExtras();

        String productName = bundle.getString("productName");
        String productBrand = bundle.getString("productBrand");
        String productDescription = bundle.getString("productDescription");
        String productPrice = bundle.getString("productPrice");
        String productPhoto = bundle.getString("productPhoto");


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