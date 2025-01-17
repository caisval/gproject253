package com.example.lab_rest.remote;

public class ApiUtils {

    // REST API server URL
    public static final String BASE_URL = "https://codelah.my/bakri/api/";

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    // return BookService instance
    public static BookService getBookService() {
        return RetrofitClient.getClient(BASE_URL).create(BookService.class);
    }

    public static CategoryService getCategoryService() {
        return RetrofitClient.getClient(BASE_URL).create(CategoryService.class);
    }

    public static BorrowService getBorrowService() {
        return RetrofitClient.getClient(BASE_URL).create(BorrowService.class);
    }

}
