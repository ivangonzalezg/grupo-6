package com.mintic.marketplace.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Iván González on 11/08/21.
 */

public class MarketplaceAPI {
    private static final String TAG = "MarketplaceAPI";

    private static MarketplaceAPI instance;

    private final Retrofit retrofit;

    public MarketplaceAPI() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://marketplace-mintic.herokuapp.com/")
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
    }

    public static MarketplaceAPI getInstance() {
        if (instance == null) {
            instance = new MarketplaceAPI();
        }
        return instance;
    }

    public <T> T getService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
