package com.mintic.marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.mintic.marketplace.utils.Constants;
import com.mintic.marketplace.utils.SharedPref;
import com.stripe.android.PaymentConfiguration;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PaymentConfiguration.init(this, Constants.stripePublishableKey);

        boolean isUserLoggedIn = SharedPref.getBoolean(this, Constants.isUserLoggedIn);

        if (isUserLoggedIn) {
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();
    }
}