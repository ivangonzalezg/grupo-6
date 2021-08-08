package com.mintic.marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mintic.marketplace.utils.Constants;
import com.mintic.marketplace.utils.Keyboard;
import com.mintic.marketplace.utils.SharedPref;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddProductActivity";

    EditText productNameEditText;
    EditText productBrandEditText;
    EditText productCategoryEditText;
    EditText productPriceEditText;
    EditText productDescriptionEditText;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.add_product_title);

        productNameEditText = findViewById(R.id.product_name_edittext);
        productBrandEditText = findViewById(R.id.product_brand_edittext);
        productCategoryEditText = findViewById(R.id.product_category_edittext);
        productPriceEditText = findViewById(R.id.product_price_edittext);
        productDescriptionEditText = findViewById(R.id.product_description_edittext);
        Button addProductButton = findViewById(R.id.add_product_button);

        addProductButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_product_button) {
            String productName = productNameEditText.getText().toString();
            String productBrand = productBrandEditText.getText().toString();
            String productCategory = productCategoryEditText.getText().toString();
            String productPrice = productPriceEditText.getText().toString();
            String productDescription = productDescriptionEditText.getText().toString();
            if (productName.isEmpty()) {
                productNameEditText.requestFocus();
                Toast.makeText(this, R.string.add_product_name_error, Toast.LENGTH_SHORT).show();
                return;
            }
            if (productBrand.isEmpty()) {
                productBrandEditText.requestFocus();
                Toast.makeText(this, R.string.add_product_brand_error, Toast.LENGTH_SHORT).show();
                return;
            }
            if (productCategory.isEmpty()) {
                productCategoryEditText.requestFocus();
                Toast.makeText(this, R.string.add_product_category_error, Toast.LENGTH_SHORT).show();
                return;
            }
            if (productPrice.isEmpty()) {
                productPriceEditText.requestFocus();
                Toast.makeText(this, R.string.add_product_category_error, Toast.LENGTH_SHORT).show();
                return;
            }
            if (productDescription.isEmpty()) {
                productDescriptionEditText.requestFocus();
                Toast.makeText(this, R.string.add_product_description_error, Toast.LENGTH_SHORT).show();
                return;
            }
            Keyboard.hideSoft(this);
            String userId = SharedPref.getString(AddProductActivity.this, Constants.userId);
            Map<String, String> product = new HashMap<>();
            product.put(Constants.name, productName);
            product.put(Constants.brand, productBrand);
            product.put(Constants.category, productCategory);
            product.put(Constants.price, productPrice);
            product.put(Constants.description, productDescription);
            product.put(Constants.photo, "");
            product.put(Constants.userId, userId);
            DocumentReference userDocumentRef = db.collection(Constants.users).document(userId);
            userDocumentRef.collection(Constants.products).document().set(product)
                    .addOnSuccessListener(success -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this).
                                setMessage(R.string.add_product_success)
                                .setPositiveButton(R.string.add_product_success_button, (dialogInterface, i) -> finish());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            builder.setOnDismissListener(dialogInterface -> finish());
                        }
                        builder.show();
                    })
                    .addOnFailureListener(failure -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this).
                                setMessage(R.string.add_product_error)
                                .setPositiveButton(R.string.add_product_error_button, (dialogInterface, i) -> finish());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            builder.setOnDismissListener(dialogInterface -> finish());
                        }
                        builder.show();
                    });
        }
    }
}