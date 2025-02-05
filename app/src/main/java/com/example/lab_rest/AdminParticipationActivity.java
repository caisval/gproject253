package com.example.lab_rest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.adapter.ParticipationAdapter;
import com.example.lab_rest.model.Participation;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.ParticipationService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminParticipationActivity extends AppCompatActivity {

    private Spinner userSpinner;
    private RecyclerView rvParticipationList;
    private ParticipationAdapter adapter;
    private ParticipationService participationService;
    private final int[] userIds = {1, 2, 3, 4}; // Now includes User IDs 1 and 2
    private int selectedUserId = -1; // Default: No selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_participation);

        userSpinner = findViewById(R.id.spinner_users);
        rvParticipationList = findViewById(R.id.rvParticipationList);
        ImageView backButton = findViewById(R.id.back_button);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#FF1BA7"));
        }
        // Initialize RecyclerView
        rvParticipationList.setLayoutManager(new LinearLayoutManager(this));
        rvParticipationList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Setup spinner with predefined users
        setupUserSpinner();

        // Back button logic
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminParticipationActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close current activity
        });
    }

    private void setupUserSpinner() {
        List<String> userNames = new ArrayList<>();
        userNames.add("Select User");
        for (int userId : userIds) {
            userNames.add("User ID: " + userId);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, userNames);
        userSpinner.setAdapter(adapter);

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedUserId = -1; // No user selected
                    rvParticipationList.setAdapter(null); // Clear list
                } else {
                    selectedUserId = userIds[position - 1]; // Adjust index
                    fetchParticipationForUser(selectedUserId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchParticipationForUser(int userId) {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        String token = spm.getUser().getToken();
        participationService = ApiUtils.getParticipationService();

        participationService.getAllParticipationByUserId(token, userId).enqueue(new Callback<List<Participation>>() {
            @Override
            public void onResponse(Call<List<Participation>> call, Response<List<Participation>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    adapter = new ParticipationAdapter(getApplicationContext(), response.body());
                    rvParticipationList.setAdapter(adapter);
                } else {
                    showNoDataAlert(userId); // Show alert if no participation found
                    rvParticipationList.setAdapter(null); // Clear list
                }
            }

            @Override
            public void onFailure(Call<List<Participation>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error fetching participations", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showNoDataAlert(int userId) {
        new AlertDialog.Builder(this)
                .setTitle("No Data Found")
                .setMessage("No participation data available for User ID: " + userId)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
