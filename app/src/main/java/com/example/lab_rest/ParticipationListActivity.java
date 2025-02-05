package com.example.lab_rest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.lab_rest.adapter.BorrowAdapter;
//import com.example.lab_rest.model.Borrow;
import com.example.lab_rest.adapter.ParticipationAdapter;
import com.example.lab_rest.model.DeleteResponse;
import com.example.lab_rest.model.Event;
import com.example.lab_rest.model.Participation;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
//import com.example.lab_rest.remote.BorrowService;
import com.example.lab_rest.remote.EventService;
import com.example.lab_rest.remote.ParticipationService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParticipationListActivity extends AppCompatActivity {

    private ParticipationService participationService;
    private RecyclerView rvParticipationList;
    private ParticipationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_participation_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get reference to the RecyclerView rvBorrowedList
        rvParticipationList = findViewById(R.id.rvParticipationList);
        ImageView backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ParticipationListActivity.this, MainActivityUser.class);
            startActivity(intent);
            finish(); // Close the current activity
        });


        //register for context menu
        registerForContextMenu(rvParticipationList);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#FF1BA7"));
        }

        // fetch and update borrowed list
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        participationService = ApiUtils.getParticipationService();

        // execute the call. send the user token when sending the query
        participationService.getAllParticipationByUserId(token, user.getId()).enqueue(new Callback<List<Participation>>() {
            @Override
            public void onResponse(Call<List<Participation>> call, Response<List<Participation>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // Get list of borrow objects from response
                    List<Participation> participations = response.body();

                    // initialize adapter
                    adapter = new ParticipationAdapter(getApplicationContext(), participations);

                    // set adapter to the RecyclerView
                    rvParticipationList.setAdapter(adapter);

                    // set layout to recycler view
                    rvParticipationList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvParticipationList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvParticipationList.addItemDecoration(dividerItemDecoration);
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
            public void onFailure(Call<List<Participation>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.participation_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Participation selectedParticipation = adapter.getSelectedItem();
        Log.d("MyApp", "selected "+selectedParticipation.toString());    // debug purpose

        if (item.getItemId() == R.id.menu_details) {
            // user clicked details contextual menu
            doViewDetails(selectedParticipation);
        }
//        else if (item.getItemId() == R.id.menu_update) {
//            // user clicked the update contextual menu
//            doUpdateParticipation(selectedParticipation);
//        }
                else if (item.getItemId() == R.id.menu_delete) {
            // user clicked the update contextual menu
            doDeleteParticipation(selectedParticipation);
        }

        return super.onContextItemSelected(item);
    }

//    private void doUpdateParticipation(Participation selectedParticipation) {
//        Log.d("MyApp:", "updating borrow: " + selectedParticipation.toString());
//        // forward user to UpdateBorrowActivity, passing the selected borrow id
//        Intent intent = new Intent(getApplicationContext(), UpdateParticipationActivity.class);
//        intent.putExtra("participation_id", selectedParticipation.getParticipation_id());
//        startActivity(intent);
//    }

    private void doViewDetails(Participation selectedParticipation) {
        Log.d("MyApp:", "viewing details: " + selectedParticipation.toString());
        // forward user to BorrowDetailsActivity, passing the selected borrow id
        Intent intent = new Intent(getApplicationContext(), ParticipationDetailsActivity.class);
        intent.putExtra("participation_id", selectedParticipation.getParticipation_id());
        startActivity(intent);
    }
    private void doDeleteParticipation(Participation selectedParticipation) {
        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // prepare REST API call
        ParticipationService participationService = ApiUtils.getParticipationService();
        Call<DeleteResponse> call = participationService.deleteParticipation(user.getToken(), selectedParticipation.getParticipation_id());

        // execute the call
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.code() == 200) {
                    // 200 means OK
                    displayAlert("Event Participation has been withdrawn");
                    // update data in list view
                    updateRecyclerView();
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
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }

}