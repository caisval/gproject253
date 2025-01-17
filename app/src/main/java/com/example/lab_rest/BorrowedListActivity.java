package com.example.lab_rest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import com.example.lab_rest.adapter.BookAdapter;
import com.example.lab_rest.adapter.BorrowAdapter;
import com.example.lab_rest.model.Book;
import com.example.lab_rest.model.Borrow;
import com.example.lab_rest.model.DeleteResponse;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.BookService;
import com.example.lab_rest.remote.BorrowService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BorrowedListActivity extends AppCompatActivity {

    private BorrowService borrowService;
    private RecyclerView rvBorrowedList;
    private BorrowAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_borrowed_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get reference to the RecyclerView rvBorrowedList
        rvBorrowedList = findViewById(R.id.rvBorrowedList);

        //register for context menu
        registerForContextMenu(rvBorrowedList);

        // fetch and update borrowed list
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        borrowService = ApiUtils.getBorrowService();

        // execute the call. send the user token when sending the query
        borrowService.getAllBorrowByUserId(token, user.getId()).enqueue(new Callback<List<Borrow>>() {
            @Override
            public void onResponse(Call<List<Borrow>> call, Response<List<Borrow>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // Get list of borrow objects from response
                    List<Borrow> borrows = response.body();

                    // initialize adapter
                    adapter = new BorrowAdapter(getApplicationContext(), borrows);

                    // set adapter to the RecyclerView
                    rvBorrowedList.setAdapter(adapter);

                    // set layout to recycler view
                    rvBorrowedList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvBorrowedList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvBorrowedList.addItemDecoration(dividerItemDecoration);
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
            public void onFailure(Call<List<Borrow>> call, Throwable t) {
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
        inflater.inflate(R.menu.borrow_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Borrow selectedBorrow = adapter.getSelectedItem();
        Log.d("MyApp", "selected "+selectedBorrow.toString());    // debug purpose

        if (item.getItemId() == R.id.menu_details) {
            // user clicked details contextual menu
            doViewDetails(selectedBorrow);
        }
        else if (item.getItemId() == R.id.menu_update) {
            // user clicked the update contextual menu
            doUpdateBorrow(selectedBorrow);
        }

        return super.onContextItemSelected(item);
    }

    private void doUpdateBorrow(Borrow selectedBorrow) {
        Log.d("MyApp:", "updating borrow: " + selectedBorrow.toString());
        // forward user to UpdateBorrowActivity, passing the selected borrow id
        Intent intent = new Intent(getApplicationContext(), UpdateBorrowActivity.class);
        intent.putExtra("borrow_id", selectedBorrow.getId());
        startActivity(intent);
    }

    private void doViewDetails(Borrow selectedBorrow) {
        Log.d("MyApp:", "viewing details: " + selectedBorrow.toString());
        // forward user to BorrowDetailsActivity, passing the selected borrow id
        Intent intent = new Intent(getApplicationContext(), BorrowDetailsActivity.class);
        intent.putExtra("borrow_id", selectedBorrow.getId());
        startActivity(intent);
    }

}