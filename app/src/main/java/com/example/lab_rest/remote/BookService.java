package com.example.lab_rest.remote;

import com.example.lab_rest.model.Book;
import com.example.lab_rest.model.DeleteResponse;
import com.example.lab_rest.model.FileInfo;

import java.util.List;

import kotlin.ParameterName;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface BookService {

    @GET("book?order=name&orderType=asc")
    Call<List<Book>> getAllBooks(@Header("api-key") String api_key);

    @GET("book/{id}")
    Call<Book> getBook(@Header("api-key") String api_key, @Path("id") int id);

    @FormUrlEncoded
    @POST("book")
    Call<Book> addBook(@Header ("api-key") String apiKey, @Field("isbn") String isbn,
                       @Field("name") String name, @Field("year") String year,
                       @Field("author") String author, @Field("description") String description,
                       @Field("image") String image, @Field("createdAt") String createdAt,
                       @Field("updatedAt") String updatedAt, @Field("category_id") int categoryId);

    @DELETE("book/{id}")
    Call<DeleteResponse> deleteBook(@Header ("api-key") String apiKey, @Path("id") int id);

    @FormUrlEncoded
    @POST("book/{id}")
    Call<Book> updateBook(@Header ("api-key") String apiKey, @Path("id") int id,
                       @Field("isbn") String isbn, @Field("name") String name,
                       @Field("year") String year, @Field("author") String author,
                       @Field("description") String description, @Field("image") String image,
                       @Field("createdAt") String createdAt, @Field("updatedAt") String updatedAt,
                          @Field("category_id") int categoryId);

    @Multipart
    @POST("files")
    Call<FileInfo> uploadFile(@Header ("api-key") String apiKey, @Part MultipartBody.Part file);

}
