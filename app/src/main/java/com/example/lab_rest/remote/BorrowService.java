package com.example.lab_rest.remote;

import com.example.lab_rest.model.Borrow;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BorrowService {

    @GET("borrow")
    Call<List<Borrow>> getAllBorrowByUserId(@Header("api-key") String apiKey, @Query("user_id[e]") int userId);

    @GET("borrow/{borrowId}")
    Call<Borrow> getBorrow(@Header("api-key") String apiKey, @Path("borrowId") int borrowId);

    @FormUrlEncoded
    @POST("borrow")
    Call<Borrow> addBorrow(@Header("api-key") String apiKey, @Field("book_id") int bookId,
                           @Field("user_id") int userId, @Field("borrow_date") String borrowDate,
                           @Field("return_date") String returnDate);

    @FormUrlEncoded
    @POST("borrow/{borrow_id}")
    Call<Borrow> updateBorrow(@Header("api-key") String apiKey, @Path("borrow_id") int borrowId,
                              @Field("book_id") int bookId, @Field("user_id") int userId,
                              @Field("borrow_date") String borrowDate, @Field("return_date") String returnDate);

}
