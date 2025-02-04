package com.example.lab_rest.remote;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import com.example.lab_rest.model.Event;
import com.example.lab_rest.model.User;

import java.util.List;

public interface UserService {

    @FormUrlEncoded
    @POST("users/login")
    Call<User> login(@Field("username") String username, @Field("password") String password);

    @GET("users")
    Call<List<User>> getAllUsers(@Header("api-key") String apiKey);

    @FormUrlEncoded
    @POST("users/register")
    Call<User> register(@Field("email") String email, @Field("username") String username, @Field("password") String password);


}
