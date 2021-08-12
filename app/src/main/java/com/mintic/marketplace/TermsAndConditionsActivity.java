package com.mintic.marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

public class TermsAndConditionsActivity extends AppCompatActivity {
    private static final String TAG = "TermsAndConditionsActiv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.terms_and_conditions_title);
    }
}