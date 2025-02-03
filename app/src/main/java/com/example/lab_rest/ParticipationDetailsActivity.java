package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import com.example.lab_rest.model.Borrow;
import com.example.lab_rest.model.Participation;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
//import com.example.lab_rest.remote.BorrowService;
import com.example.lab_rest.remote.ParticipationService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParticipationDetailsActivity extends AppCompatActivity {

    private ParticipationService participationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_participation_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // retrieve borrow details based on selected id

        // get book id sent by BookListActivity, -1 if not found
        Intent intent = getIntent();
        int participationId = intent.getIntExtra("participation_id", -1);
        Log.d("MyApp2:", "Response:"  + participationId);

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        participationService = ApiUtils.getParticipationService();

        // execute the API query. send the token and book id
       participationService.getParticipation(token, participationId).enqueue(new Callback<Participation>() {

            @Override
            public void onResponse(Call<Participation> call, Response<Participation> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success

                    // get borrow object from response
                    Participation participation = response.body();

                    // get references to the view elements
                    TextView tvEvent_name = findViewById(R.id.tvEvent_name);
                    TextView tvDescription = findViewById(R.id.tvDescription);
                    TextView tvLocation = findViewById(R.id.tvLocation);
                    TextView tvDate = findViewById(R.id.tvDate);
                    TextView tvCategory = findViewById(R.id.tvCategory);
                    TextView tvOrganizer_id = findViewById(R.id.tvOrganizer_id);
                    TextView tvParticipationDate = findViewById(R.id.tvParticipationDate);

                    Log.d("ParticipationDetails", "Event Name: " + participation.getEvent().getEvent_name() +
                            ", Description: " + participation.getEvent().getDescription() +
                            ", Location: " + participation.getEvent().getLocation() +
                            ", Date: " + participation.getEvent().getDate() +
                            ", Category: " + participation.getEvent().getCategory() +
                            ", Organizer ID: " + participation.getEvent().getOrganizer_id() +
                            ", Participation Date: " + participation.getParticipation_date());


                    // set values
                    tvEvent_name.setText(participation.getEvent().getEvent_name());
                    tvDescription.setText(participation.getEvent().getDescription());
                    tvLocation.setText(participation.getEvent().getLocation());
                    tvDate.setText(participation.getEvent().getDate());
                    tvCategory.setText(participation.getEvent().getCategory());
                    tvOrganizer_id.setText(String.valueOf(participation.getEvent().getOrganizer_id()));
                    tvParticipationDate.setText(participation.getParticipation_date());
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
            public void onFailure(Call<Participation> call, Throwable t) {
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