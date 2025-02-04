package com.example.lab_rest;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab_rest.model.Participation;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.ParticipationService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.example.lab_rest.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParticipationCountActivity extends AppCompatActivity {

    private ParticipationService participationService;
    private TextView tvParticipationCount;
    private LinearLayout panel1, panel2, panel3, panel4, panel5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_participation_count);

        tvParticipationCount = findViewById(R.id.tvParticipationCount);

        panel1 = findViewById(R.id.panel1);
        panel2 = findViewById(R.id.panel2);
        panel3 = findViewById(R.id.panel3);
        panel4 = findViewById(R.id.panel4);
        panel5 = findViewById(R.id.panel5);

        // Fetch and display participation count
        fetchParticipationCount();
    }

    private void fetchParticipationCount() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        participationService = ApiUtils.getParticipationService();

        participationService.getAllParticipationByUserId(token, user.getId()).enqueue(new Callback<List<Participation>>() {
            @Override
            public void onResponse(Call<List<Participation>> call, Response<List<Participation>> response) {
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    List<Participation> participations = response.body();
                    int participationCount = (participations != null) ? participations.size() : 0;

                    // Display the count
                    tvParticipationCount.setText("Total Participations: " + participationCount);

                    // Update achievement panels
                    updateAchievementPanels(participationCount);
                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Participation>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    private void updateAchievementPanels(int count) {
        // Unlock panels based on participation count
        if (count >= 1) panel1.setAlpha(1f);
        if (count >= 2) panel2.setAlpha(1f);
        if (count >= 3) panel3.setAlpha(1f);
        if (count >= 4) panel4.setAlpha(1f);
        if (count >= 5) {
            panel5.setAlpha(1f);
            showCongratulationDialog(); // Show congratulation alert
        }
    }

    private void showCongratulationDialog() {
        // Show an alert dialog when user reaches 5 participations
        new AlertDialog.Builder(this)
                .setTitle("Congratulations!")
                .setMessage("You are eligible to claim a free iPhone 16 Pro Max for your hard work! We will e-mail you with the details!")
                .setPositiveButton("OK", null)
                .show();
    }
}
