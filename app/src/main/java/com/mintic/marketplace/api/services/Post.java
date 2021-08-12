package com.mintic.marketplace.api.services;

import com.mintic.marketplace.api.models.request.PostRequest;
import com.mintic.marketplace.api.models.response.PostResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Post {

    @POST("/")
    Call<PostResponse> post(@Body PostRequest request);
}
