package com.example.lab_rest.remote;

import com.example.lab_rest.model.Participation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ParticipationService {

    @GET("participations")
    Call<List<Participation>> getAllParticipationByUserId(@Header("api-key") String apiKey, @Query("user_id[e]") int userId);

    @GET("participations/{participation_id}")
    Call<Participation> getParticipation(@Header("api-key") String apiKey, @Path("participation_id") int participation_id);

    @FormUrlEncoded
    @POST("participations")
    Call<Participation> addParticipation(@Header("api-key") String apiKey, @Field("event_id") int eventId,
                           @Field("user_id") int userId, @Field("participation_date") String participation_date);

    @FormUrlEncoded
    @POST("participations/{participation_id}")
    Call<Participation> updateParticipation(@Header("api-key") String apiKey, @Path("participation_id") int participation_id,
                              @Field("user_id") int event_id, @Field("event_id") int userId,
                              @Field("participation_date") String participation_date);

}
