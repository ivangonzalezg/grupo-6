package com.mintic.marketplace.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mintic.marketplace.R;
import com.mintic.marketplace.api.MarketplaceAPI;
import com.mintic.marketplace.api.models.request.PostRequest;
import com.mintic.marketplace.api.models.response.PostResponse;
import com.mintic.marketplace.api.services.Post;
import com.mintic.marketplace.utils.Constants;
import com.mintic.marketplace.utils.Firestore;
import com.mintic.marketplace.utils.SharedPref;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CartActivity";

    TextView totalTextview;
    RecyclerView cartProductsRecycler;

    FirebaseFirestore db;

    ArrayList<Firestore.Product> productList;
    ArrayList<String> productsInCart;
    Button payButton;

    Integer total = 0;

    private PaymentSheet paymentSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.cart_title);

        totalTextview = findViewById(R.id.cart_total);
        cartProductsRecycler = findViewById(R.id.cart_products_recyclerview);
        payButton = findViewById(R.id.cart_pay);

        payButton.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        Bundle bundle = getIntent().getExtras();

        productList = new Gson().fromJson(bundle.getString(Constants.products), new TypeToken<ArrayList<Firestore.Product>>() {
        }.getType());
        productsInCart = new Gson().fromJson(bundle.getString(Constants.cart), new TypeToken<ArrayList<String>>() {
        }.getType());

        renderUI();
    }

    private void renderUI() {
        ArrayList<Firestore.Product> products = new ArrayList<>();
        total = 0;

        for (int i = 0; i < productList.size(); i++) {
            if (productsInCart.contains(productList.get(i).getId())) {
                products.add(productList.get(i));
                total += Integer.parseInt(productList.get(i).getPrice());
            }
        }

        NumberFormat numberFormatInstance = NumberFormat.getCurrencyInstance(Locale.US);
        numberFormatInstance.setMaximumFractionDigits(0);

        totalTextview.setText(numberFormatInstance.format(total));

        CartAdapter cartAdapter = new CartAdapter(CartActivity.this, products);
        cartProductsRecycler.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        cartProductsRecycler.setAdapter(cartAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cart_pay) {
            ProgressDialog progress = ProgressDialog.show(this, this.getResources().getString(R.string.progress_dialog_title), this.getResources().getString(R.string.progress_dialog_message_generating_payment));
            String email = SharedPref.getString(this, Constants.email);
            String firstName = SharedPref.getString(this, Constants.firstName);
            String lastName = SharedPref.getString(this, Constants.lastName);
            String name = firstName + " " + lastName;
            Post post = MarketplaceAPI.getInstance().getService(Post.class);
            PostRequest postRequest = new PostRequest();
            postRequest.setAmount(total);
            postRequest.setEmail(email);
            postRequest.setName(name);
            post.post(postRequest).enqueue(new Callback<PostResponse>() {
                @Override
                public void onResponse(@NotNull Call<PostResponse> call, @NotNull Response<PostResponse> response) {
                    progress.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration(
                                "Marketplace MinTIC",
                                new PaymentSheet.CustomerConfiguration(response.body().getCustomer(), response.body().getEphemeralKey())
                        );
                        paymentSheet.presentWithPaymentIntent(response.body().getPaymentIntent(), configuration);
                    } else {
                        Toast.makeText(CartActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<PostResponse> call, @NotNull Throwable t) {
                    progress.dismiss();
                    Toast.makeText(CartActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment Canceled", Toast.LENGTH_LONG).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this, "Payment Failed. See logcat for details.", Toast.LENGTH_LONG).show();
            Log.e("App", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment Complete", Toast.LENGTH_LONG).show();
        }
    }
}
