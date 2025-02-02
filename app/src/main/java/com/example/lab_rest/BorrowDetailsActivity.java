//package com.example.lab_rest;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.lab_rest.model.Book;
//import com.example.lab_rest.model.Borrow;
//import com.example.lab_rest.model.User;
//import com.example.lab_rest.remote.ApiUtils;
//import com.example.lab_rest.remote.BookService;
//import com.example.lab_rest.remote.BorrowService;
//import com.example.lab_rest.sharedpref.SharedPrefManager;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class BorrowDetailsActivity extends AppCompatActivity {
//
//    private BorrowService borrowService;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_borrow_details);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // retrieve borrow details based on selected id
//
//        // get book id sent by BookListActivity, -1 if not found
//        Intent intent = getIntent();
//        int borrowId = intent.getIntExtra("borrow_id", -1);
//
//        // get user info from SharedPreferences
//        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
//        User user = spm.getUser();
//        String token = user.getToken();
//
//        // get book service instance
//        borrowService = ApiUtils.getBorrowService();
//
//        // execute the API query. send the token and book id
//        borrowService.getBorrow(token, borrowId).enqueue(new Callback<Borrow>() {
//
//            @Override
//            public void onResponse(Call<Borrow> call, Response<Borrow> response) {
//                // for debug purpose
//                Log.d("MyApp:", "Response: " + response.raw().toString());
//
//                if (response.code() == 200) {
//                    // server return success
//
//                    // get borrow object from response
//                    Borrow borrow = response.body();
//
//                    // get references to the view elements
//                    TextView tvTitle = findViewById(R.id.tvTitle);
//                    TextView tvDesc = findViewById(R.id.tvDescription);
//                    TextView tvAuthor = findViewById(R.id.tvAuthor);
//                    TextView tvISBN = findViewById(R.id.tvISBN);
//                    TextView tvYear = findViewById(R.id.tvYear);
//                    TextView tvCreatedAt = findViewById(R.id.tvCreatedAt);
//                    TextView tvUpdatedAt = findViewById(R.id.tvUpdatedAt);
//                    TextView tvCategory = findViewById(R.id.tvCategory);
//                    TextView tvBorrowDate = findViewById(R.id.tvBorrowDate);
//                    TextView tvReturnDate = findViewById(R.id.tvReturnDate);
//
//                    // set values
//                    tvTitle.setText(borrow.getBook().getName());
//                    tvAuthor.setText(borrow.getBook().getAuthor());
//                    tvISBN.setText(borrow.getBook().getIsbn());
//                    tvYear.setText(borrow.getBook().getYear());
//                    tvDesc.setText(borrow.getBook().getDescription());
//                    tvCreatedAt.setText(borrow.getBook().getCreatedAt());
//                    tvUpdatedAt.setText(borrow.getBook().getUpdatedAt());
//                    if (borrow.getBook().getCategory() != null)
//                        tvCategory.setText(borrow.getBook().getCategory().getCategoryName());
//                    else
//                        tvCategory.setText("Category no selected");
//
//                    tvBorrowDate.setText(borrow.getBorrow_date());
//                    tvReturnDate.setText(borrow.getReturn_date());
//                }
//                else if (response.code() == 401) {
//                    // unauthorized error. invalid token, ask user to relogin
//                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
//                    clearSessionAndRedirect();
//                }
//                else {
//                    // server return other error
//                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
//                    Log.e("MyApp: ", response.toString());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Borrow> call, Throwable t) {
//                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }
//
//    public void clearSessionAndRedirect() {
//        // clear the shared preferences
//        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
//        spm.logout();
//
//        // terminate this MainActivity
//        finish();
//
//        // forward to Login Page
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//
//    }
//}