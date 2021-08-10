package com.mintic.marketplace;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mintic.marketplace.utils.Constants;
import com.mintic.marketplace.utils.Keyboard;
import com.mintic.marketplace.utils.SharedPref;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddProductActivity";

    EditText productNameEditText;
    EditText productBrandEditText;
    EditText productCategoryEditText;
    EditText productPriceEditText;
    EditText productDescriptionEditText;
    ImageButton productPhotoImageButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;

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
        productPhotoImageButton = findViewById(R.id.product_photo_image_button);
        Button addProductButton = findViewById(R.id.add_product_button);

        addProductButton.setOnClickListener(this);
        productPhotoImageButton.setOnClickListener(this);
    }

    private String getImageName() {
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(new Date()) + "-" + UUID.randomUUID();
    }

    private void saveProduct(Map<String, String> product, String userId) {
        ProgressDialog progress = ProgressDialog.show(this, this.getResources().getString(R.string.progress_dialog_title), this.getResources().getString(R.string.progress_dialog_message_product_saving));
        DocumentReference userDocumentRef = db.collection(Constants.users).document(userId);
        userDocumentRef.collection(Constants.products).document().set(product)
                .addOnSuccessListener(success -> {
                    progress.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this).
                            setMessage(R.string.add_product_success)
                            .setPositiveButton(R.string.add_product_success_button, (dialogInterface, i) -> finish());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        builder.setOnDismissListener(dialogInterface -> finish());
                    }
                    builder.show();
                })
                .addOnFailureListener(failure -> {
                    progress.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this).
                            setMessage(R.string.add_product_error)
                            .setPositiveButton(R.string.add_product_error_button, (dialogInterface, i) -> finish());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        builder.setOnDismissListener(dialogInterface -> finish());
                    }
                    builder.show();
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.product_photo_image_button:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                break;
            case R.id.add_product_button:
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
                product.put(Constants.date, Long.toString(new Date().getTime()));
                if (filePath != null) {
                    ProgressDialog progress = ProgressDialog.show(this, this.getResources().getString(R.string.progress_dialog_message_image_uploading), this.getResources().getString(R.string.progress_dialog_title));
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(getImageName());
                    storageReference.putFile(filePath)
                            .addOnSuccessListener(taskSnapshot -> {
                                storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        product.put(Constants.photo, Objects.requireNonNull(task.getResult()).toString());
                                        saveProduct(product, userId);
                                    } else {
                                        Toast.makeText(AddProductActivity.this, R.string.add_product_photo_error_upload, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                progress.dismiss();
                            })
                            .addOnFailureListener(e -> Toast.makeText(AddProductActivity.this, R.string.add_product_photo_error_upload, Toast.LENGTH_SHORT).show())
                            .addOnProgressListener(snapshot -> {
                                double value = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                                progress.setMessage((int) value + "%");
                            });
                } else {
                    saveProduct(product, userId);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                try {
                    if (data == null) {
                        throw new Exception();
                    }
                    filePath = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    productPhotoImageButton.setImageBitmap(bitmap);
                    productPhotoImageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
                } catch (IOException exception) {
                    Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception exception) {
                    Toast.makeText(this, R.string.add_product_photo_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.add_product_photo_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}