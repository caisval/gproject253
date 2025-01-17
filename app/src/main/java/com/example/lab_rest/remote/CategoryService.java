package com.example.lab_rest.remote;

import com.example.lab_rest.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface CategoryService {

    @GET("category")
    Call<List<Category>> getAllCategories(@Header("api-key") String apiKey);

}
