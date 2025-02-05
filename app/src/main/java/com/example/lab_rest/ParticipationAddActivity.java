package com.example.lab_rest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.example.lab_rest.model.Book;
//import com.example.lab_rest.model.Borrow;
import com.example.lab_rest.model.Event;
import com.example.lab_rest.model.Participation;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.BookService;
//import com.example.lab_rest.remote.BorrowService;
import com.example.lab_rest.remote.EventService;
import com.example.lab_rest.remote.ParticipationService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParticipationAddActivity extends AppCompatActivity {

    private EventService eventService;
    private Event event;
    private static TextView tvParticipationDate; // static because need to be accessed by DatePickerFragment
    private static Date participationAtDate; // static because need to be accessed by DatePickerFragment

    /**
     * Date picker fragment class
     * Reference: https://developer.android.com/guide/topics/ui/controls/pickers
     */
    public static class ParticipationDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            // create a date object from selected year, month and day
            participationAtDate = new GregorianCalendar(year, month, day).getTime();

            // display in the label beside the button with specific date format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
            tvParticipationDate.setText( sdf.format(participationAtDate) );
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_participation_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // retrieve borrow details based on selected id

        // get book id sent by BookListActivity, -1 if not found
        Intent intent = getIntent();
        int eventId = intent.getIntExtra("event_id", -1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#FF1BA7"));
        }

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // get book service instance
        eventService = ApiUtils.getEventService();

        // execute the API query. send the token and book id
        eventService.getEvent(token, eventId).enqueue(new Callback<Event>() {

            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success

                    // get borrow object from response
                    event = response.body();

                    // get references to the view elements
                    TextView tvEvent_name = findViewById(R.id.tvEvent_name);
                    TextView tvDescription = findViewById(R.id.tvDescription);
                    TextView tvLocation = findViewById(R.id.tvLocation);
                    TextView tvDate = findViewById(R.id.tvDate);
                    TextView tvCategory = findViewById(R.id.tvCategory);
                    TextView tvOrganizer_id = findViewById(R.id.tvOrganizer_id);
                    TextView tvParticipationDate = findViewById(R.id.tvParticipationDate);


                    tvEvent_name.setText(event.getEvent_name());
                    tvDescription.setText(event.getDescription());
                    tvLocation.setText(event.getLocation());
                    tvDate.setText(event.getDate());
                    tvCategory.setText(event.getCategory());
                    tvOrganizer_id.setText(String.valueOf(event.getOrganizer_id()));

                    participationAtDate = new Date();
                    // set default return date to after 3 days
//                    Calendar c = Calendar.getInstance();
//                    c.setTime(borrowAtDate);
//                    c.add(Calendar.DATE, 3);  // number of days to add
//                    returnDate = c.getTime();  // dt is now the new date

                    // display in the label beside the button with specific date format
                    tvParticipationDate.setText( sdf.format(participationAtDate) );
//                    tvReturnDate.setText( sdf.format(returnDate) );

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
            public void onFailure(Call<Event> call, Throwable t) {
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

    public void showParticipationDatePickerDialog(View v) {
        DialogFragment newFragment = new ParticipationAddActivity.ParticipationDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    public void addNewParticipation(View v) {
        // get values in form
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        int eventId = event.getEvent_id();
        int userId = user.getId();

        // convert createdAt date to format in DB
        // reference: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String pDate = sdf.format(participationAtDate);

        // send request to add new borrow record to the REST API
        ParticipationService participationService = ApiUtils.getParticipationService();
        Call<Participation> call = participationService.addParticipation(user.getToken(), eventId, userId, pDate);

        // execute
        call.enqueue(new Callback<Participation>() {
            @Override
            public void onResponse(Call<Participation> call, Response<Participation> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 201) {
                    // borrow record added successfully
                    Participation addedParticipation = response.body();
                    // display message
                    Toast.makeText(getApplicationContext(),
                            addedParticipation.getEvent().getEvent_name() + " successfully participated.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(getApplicationContext(), ParticipationListActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (response.code() == 401) {
                    // invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    // server return other error
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Participation> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Error [" + t.getMessage() + "]",
                        Toast.LENGTH_LONG).show();
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });
    }
}