package com.example.lab_rest.remote;

public class ApiUtils {

    // REST API server URL
    public static final String BASE_URL = "http://178.128.220.20/2023379509/api/";

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    // return BookService instance
    public static EventService getEventService() {
        return RetrofitClient.getClient(BASE_URL).create(EventService.class);
    }

//    public static CategoryService getCategoryService() {
//        return RetrofitClient.getClient(BASE_URL).create(CategoryService.class);
//    }
//
    public static ParticipationService getParticipationService() {
        return RetrofitClient.getClient(BASE_URL).create(ParticipationService.class);
    }

}
