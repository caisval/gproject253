package com.example.lab_rest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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

import com.example.lab_rest.model.Book;
import com.example.lab_rest.model.Event;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.BookService;
import com.example.lab_rest.remote.EventService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsActivity extends AppCompatActivity {

    private EventService eventService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // retrieve book details based on selected id
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#FF1BA7"));
        }
        // get book id sent by BookListActivity, -1 if not found
        Intent intent = getIntent();
        int eventId = intent.getIntExtra("event_id", -1);
        Log.d("MyApp", "Received event_id: " + eventId);

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        eventService = ApiUtils.getEventService();

        // execute the API query. send the token and book id
        eventService.getEvent(token, eventId).enqueue(new Callback<Event>() {

            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                // for debug purpose
                Log.d("MyApp:", "Response Connected Success: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success
                    Log.d("MyApp2:", "Response Connected Success: " + response.raw().toString());

                    // get book object from response
                    Event event = response.body();
                    Log.d("MyApp3:", "Response Connected Success: " + event);

                    // get references to the view elements
                    TextView tvEvent_name = findViewById(R.id.tvEvent_name);
                    TextView tvDescription = findViewById(R.id.tvDescription);
                    TextView tvLocation = findViewById(R.id.tvLocation);
                    TextView tvDate = findViewById(R.id.tvDate);
                    TextView tvCategory = findViewById(R.id.tvCategory);
                    TextView tvOrganizer_id = findViewById(R.id.tvOrganizer_id);

                    // set values
                    Log.d("MyApp4:", "Information Retrieved : " + event.getEvent_name() + event.getDescription() + event.getLocation() + event.getDate() + event.getCategory() + event.getOrganizer_id());
                    tvEvent_name.setText(event.getEvent_name());
                    tvDescription.setText(event.getDescription());
                    tvLocation.setText(event.getLocation());
                    tvDate.setText(event.getDate());
                    tvCategory.setText(event.getCategory());
                    tvOrganizer_id.setText(String.valueOf(event.getOrganizer_id()));

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
                Toast.makeText(EventDetailsActivity.this, "Error connecting", Toast.LENGTH_LONG).show();
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