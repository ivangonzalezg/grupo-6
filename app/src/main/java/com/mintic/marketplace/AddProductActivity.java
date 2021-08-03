package com.mintic.marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {
    // private static final String TAG = "AddProductActivity";
    private Activity mySelf;

    private EditText productNameEditText;
    private EditText productBrandEditText;
    private EditText productCategoryEditText;
    private EditText productPriceEditText;
    private EditText productDescriptionEditText;
    private Button addProductButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mySelf = this;

        productNameEditText = findViewById(R.id.product_name_edittext);
        productBrandEditText = findViewById(R.id.product_brand_edittext);
        productCategoryEditText = findViewById(R.id.product_category_edittext);
        productPriceEditText = findViewById(R.id.product_price_edittext);
        productDescriptionEditText = findViewById(R.id.product_description_edittext);
        addProductButton = findViewById(R.id.add_product_button);

        addProductButton.setOnClickListener(view -> {
            String productName = productNameEditText.getText().toString();
            String productBrand = productBrandEditText.getText().toString();
            String productCategory = productCategoryEditText.getText().toString();
            String productPrice = productPriceEditText.getText().toString();
            String productDescription = productDescriptionEditText.getText().toString();

            if (productName.isEmpty()) {
                productNameEditText.requestFocus();
                Toast.makeText(mySelf, R.string.add_product_name_error, Toast.LENGTH_LONG).show();
                return;
            }

            if (productBrand.isEmpty()) {
                productBrandEditText.requestFocus();
                Toast.makeText(mySelf, R.string.add_product_brand_error, Toast.LENGTH_LONG).show();
                return;
            }

            if (productCategory.isEmpty()) {
                productCategoryEditText.requestFocus();
                Toast.makeText(mySelf, R.string.add_product_category_error, Toast.LENGTH_LONG).show();
                return;
            }

            if (productPrice.isEmpty()) {
                productPriceEditText.requestFocus();
                Toast.makeText(mySelf, R.string.add_product_category_error, Toast.LENGTH_SHORT).show();
                return;
            }

            if (productDescription.isEmpty()) {
                productDescriptionEditText.requestFocus();
                Toast.makeText(mySelf, R.string.add_product_description_error, Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> product = new HashMap<>();
            product.put("name", productName);
            product.put("brand", productBrand);
            product.put("category", productCategory);
            product.put("price", productPrice);
            product.put("description", productDescription);

            db.collection("products").document().set(product)
                    .addOnSuccessListener(success -> {
                        // Log.e(TAG, "Product added successfully");
                        Snackbar.make(findViewById(R.id.add_product_view), R.string.add_product_success, Snackbar.LENGTH_LONG).show();

                    })
                    .addOnFailureListener(failure -> {
                        // Log.e(TAG, "ERROR adding product")
                        Snackbar.make(findViewById(R.id.add_product_view), R.string.add_product_error, Snackbar.LENGTH_LONG).show();
                    });

            finish();
        });
    }
}