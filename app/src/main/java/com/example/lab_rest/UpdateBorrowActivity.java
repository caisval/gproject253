//package com.example.lab_rest;
//
//import android.app.DatePickerDialog;
//import android.app.Dialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.DatePicker;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.fragment.app.DialogFragment;
//
//import com.example.lab_rest.model.Book;
//import com.example.lab_rest.model.Borrow;
//import com.example.lab_rest.model.User;
//import com.example.lab_rest.remote.ApiUtils;
//import com.example.lab_rest.remote.BookService;
//import com.example.lab_rest.remote.BorrowService;
//import com.example.lab_rest.sharedpref.SharedPrefManager;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.Locale;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class UpdateBorrowActivity extends AppCompatActivity {
//
//    private BorrowService borrowService;
//    private Borrow borrow;
//    private static TextView tvBorrowDate; // static because need to be accessed by DatePickerFragment
//    private static Date borrowAtDate; // static because need to be accessed by DatePickerFragment
//    private static TextView tvReturnDate; // static because need to be accessed by DatePickerFragment
//    private static Date returnDate; // static because need to be accessed by DatePickerFragment
//
//    /**
//     * Date picker fragment class
//     * Reference: https://developer.android.com/guide/topics/ui/controls/pickers
//     */
//    public static class BorrowDatePickerFragment extends DialogFragment
//            implements DatePickerDialog.OnDateSetListener {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current date as the default date in the picker
//            final Calendar c = Calendar.getInstance();
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH);
//            int day = c.get(Calendar.DAY_OF_MONTH);
//
//            // Create a new instance of DatePickerDialog and return it
//            return new DatePickerDialog(getActivity(), this, year, month, day);
//        }
//
//        public void onDateSet(DatePicker view, int year, int month, int day) {
//            // Do something with the date chosen by the user
//
//            // create a date object from selected year, month and day
//            borrowAtDate = new GregorianCalendar(year, month, day).getTime();
//
//            // display in the label beside the button with specific date format
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
//            tvBorrowDate.setText( sdf.format(borrowAtDate) );
//        }
//    }
//
//    /**
//     * Date picker fragment class
//     * Reference: https://developer.android.com/guide/topics/ui/controls/pickers
//     */
//    public static class ReturnDatePickerFragment extends DialogFragment
//            implements DatePickerDialog.OnDateSetListener {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current date as the default date in the picker
//            final Calendar c = Calendar.getInstance();
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH);
//            int day = c.get(Calendar.DAY_OF_MONTH);
//
//            // Create a new instance of DatePickerDialog and return it
//            return new DatePickerDialog(getActivity(), this, year, month, day);
//        }
//
//        public void onDateSet(DatePicker view, int year, int month, int day) {
//            // Do something with the date chosen by the user
//
//            // create a date object from selected year, month and day
//            returnDate = new GregorianCalendar(year, month, day).getTime();
//
//            // display in the label beside the button with specific date format
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
//            tvReturnDate.setText( sdf.format(returnDate) );
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_borrow_update);
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
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//        // get book service instance
//        borrowService = ApiUtils.getBorrowService();
//
//        // execute the API query. send the token and borrow id
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
//                    borrow = response.body();
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
//                    tvBorrowDate = findViewById(R.id.tvBorrowDate);
//                    tvReturnDate = findViewById(R.id.tvReturnDate);
//
//                    // set values
//                    tvTitle.setText(borrow.getBook().getName());
//                    tvAuthor.setText(borrow.getBook().getAuthor().toString());
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
//                    // set default createdAt value to current date
//                    try {
//                        borrowAtDate = sdf.parse(borrow.getBorrow_date());
//                        returnDate = sdf.parse(borrow.getReturn_date());
//                    } catch (ParseException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                    // display in the label beside the button with specific date format
//                    tvBorrowDate.setText( sdf.format(borrowAtDate) );
//                    tvReturnDate.setText( sdf.format(returnDate) );
//
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
//
//    public void showBorrowDatePickerDialog(View v) {
//        DialogFragment newFragment = new UpdateBorrowActivity.BorrowDatePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "datePicker");
//    }
//
//    public void showReturnDatePickerDialog(View v) {
//        DialogFragment newFragment = new UpdateBorrowActivity.ReturnDatePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "datePicker");
//    }
//
//    public void updateBorrow(View v) {
//        // get values in form
//        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
//        User user = spm.getUser();
//
//        int bookId = borrow.getBook().getId();
//        int userId = user.getId();
//
//        // convert createdAt date to format in DB
//        // reference: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//        String bDate = sdf.format(borrowAtDate);
//        String rDate = sdf.format(returnDate);
//
//        // send request to add new borrow record to the REST API
//        BorrowService borrowService = ApiUtils.getBorrowService();
//        Call<Borrow> call = borrowService.updateBorrow(user.getToken(), borrow.getId(), bookId, userId,
//                bDate, rDate);
//
//        // execute
//        call.enqueue(new Callback<Borrow>() {
//            @Override
//            public void onResponse(Call<Borrow> call, Response<Borrow> response) {
//
//                // for debug purpose
//                Log.d("MyApp:", "Response: " + response.raw().toString());
//
//                if (response.code() == 200) {
//                    // borrow record updated successfully
//                    Borrow updatedBorrow = response.body();
//                    // display message
//                    Toast.makeText(getApplicationContext(),
//                            updatedBorrow.getBook().getName() + " successfully update borrow record.",
//                            Toast.LENGTH_LONG).show();
//
//                    // end this activity and forward user to BookListActivity
//                    Intent intent = new Intent(getApplicationContext(), BorrowedListActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//                else if (response.code() == 401) {
//                    // invalid token, ask user to relogin
//                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
//                    clearSessionAndRedirect();
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
//                    // server return other error
//                    Log.e("MyApp: ", response.toString());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Borrow> call, Throwable t) {
//                Toast.makeText(getApplicationContext(),"Error [" + t.getMessage() + "]",
//                        Toast.LENGTH_LONG).show();
//                // for debug purpose
//                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
//            }
//        });
//    }
//}