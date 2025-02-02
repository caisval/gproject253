package com.example.lab_rest.remote;

import com.example.lab_rest.model.Book;
import com.example.lab_rest.model.DeleteResponse;
import com.example.lab_rest.model.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EventService {

    // fetch list of records
    @GET("events")
    Call<List<Event>> getAllEvents(@Header("api-key") String api_key);

    // fetch single record
    @GET("events/{id}")
    Call<Event> getEvent(@Header("api-key") String api_key, @Path("id") int id);

    // create new record

    @FormUrlEncoded
    @POST("events")
    Call<Event> addEvent(@Header("api-key") String apiKey,
                         @Field("event_name") String event_name,
                         @Field("description") String description,
                         @Field("location") String location,
                         @Field("date") String date,
                         @Field("category") String category,
                         @Field("organizer_id") String organizer_id);

    @FormUrlEncoded
    @POST("events/{event_id}")
    Call<Event> updateEvent(@Header ("api-key") String apiKey, @Path("event_id") int event_id,
                            @Field("event_name") String event_name,
                            @Field("description") String description,
                            @Field("location") String location,
                            @Field("category") String category,
                            @Field("date") String date,
                            @Field("organizer_id") int organizer_id);


    @DELETE ("events/{id}")
    Call<DeleteResponse> deleteEvent(@Header("api-key") String api_key, @Path("id") int id);


}
