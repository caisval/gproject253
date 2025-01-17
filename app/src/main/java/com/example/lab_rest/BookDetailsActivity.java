package com.example.lab_rest;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.lab_rest.model.Book;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.BookService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailsActivity extends AppCompatActivity {

    private BookService bookService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // retrieve book details based on selected id

        // get book id sent by BookListActivity, -1 if not found
        Intent intent = getIntent();
        int bookId = intent.getIntExtra("book_id", -1);

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        bookService = ApiUtils.getBookService();

        // execute the API query. send the token and book id
        bookService.getBook(token, bookId).enqueue(new Callback<Book>() {

            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success

                    // get book object from response
                    Book book = response.body();

                    // get references to the view elements
                    TextView tvTitle = findViewById(R.id.tvTitle);
                    TextView tvDesc = findViewById(R.id.tvDescription);
                    TextView tvAuthor = findViewById(R.id.tvAuthor);
                    TextView tvISBN = findViewById(R.id.tvISBN);
                    TextView tvYear = findViewById(R.id.tvYear);
                    TextView tvCreatedAt = findViewById(R.id.tvCreatedAt);
                    TextView tvUpdatedAt = findViewById(R.id.tvUpdatedAt);
                    TextView tvCategory = findViewById(R.id.tvCategory);
                    ImageView imgBookCover = findViewById(R.id.imgBookCover);

                    // set values
                    tvTitle.setText(book.getName());
                    tvAuthor.setText(book.getAuthor());
                    tvISBN.setText(book.getIsbn());
                    tvYear.setText(book.getYear());
                    tvDesc.setText(book.getDescription());
                    tvCreatedAt.setText(book.getCreatedAt());
                    tvUpdatedAt.setText(book.getUpdatedAt());
                    if (book.getCategory() != null)
                        tvCategory.setText(book.getCategory().getCategoryName());
                    else
                        tvCategory.setText("Category no selected");

                    // Use Glide to load the image into the ImageView
                    com.bumptech.glide.Glide.with(getApplicationContext())
                            .load("https://codelah.my/bakri/api/" + book.getImage())
                            .placeholder(R.drawable.default_cover) // Placeholder image if the URL is empty
                            .error(R.drawable.default_cover) // Error image if there is a problem loading the image
                            .into(imgBookCover);
                }
                else if (response.code() == 401) {
                    // unauthorized error. invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    // server return other error
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this MainActivity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
}