package com.example.lab_rest;

import android.content.Context;
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
import com.example.lab_rest.model.Book;
import com.example.lab_rest.model.DeleteResponse;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.BookService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookListActivity extends AppCompatActivity {

    private BookService bookService;
    private RecyclerView rvBookList;
    private BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get reference to the RecyclerView bookList
        rvBookList = findViewById(R.id.rvBookList);

        //register for context menu
        registerForContextMenu(rvBookList);

        // fetch and update book list
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        bookService = ApiUtils.getBookService();

        // execute the call. send the user token when sending the query
        bookService.getAllBooks(token).enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // Get list of book object from response
                    List<Book> books = response.body();

                    // initialize adapter
                    adapter = new BookAdapter(getApplicationContext(), books);

                    // set adapter to the RecyclerView
                    rvBookList.setAdapter(adapter);

                    // set layout to recycler view
                    rvBookList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvBookList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvBookList.addItemDecoration(dividerItemDecoration);
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
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    /**
     * Delete book record. Called by contextual menu "Delete"
     * @param selectedBook - book selected by user
     */
    private void doDeleteBook(Book selectedBook) {
        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // prepare REST API call
        BookService bookService = ApiUtils.getBookService();
        Call<DeleteResponse> call = bookService.deleteBook(user.getToken(), selectedBook.getId());

        // execute the call
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.code() == 200) {
                    // 200 means OK
                    displayAlert("Book successfully deleted");
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
        inflater.inflate(R.menu.book_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Book selectedBook = adapter.getSelectedItem();
        Log.d("MyApp", "selected "+selectedBook.toString());    // debug purpose

        if (item.getItemId() == R.id.menu_details) {
            // user clicked details contextual menu
            doViewDetails(selectedBook);
        }
        else if (item.getItemId() == R.id.menu_delete) {
            // user clicked the delete contextual menu
            doDeleteBook(selectedBook);
        } else if (item.getItemId() == R.id.menu_borrow) {
            // user clicked borrow book
            doBorrowBook(selectedBook);
        } else if (item.getItemId() == R.id.menu_update) {
            // user clicked the update contextual menu
            doUpdateBook(selectedBook);
        }

        return super.onContextItemSelected(item);
    }

    private void doBorrowBook(Book selectedBook) {
        Log.d("MyApp:", "borrowing book: " + selectedBook.toString());
        // forward user to BorrowBookActivity, passing the selected book id
        Intent intent = new Intent(getApplicationContext(), BorrowAddActivity.class);
        intent.putExtra("book_id", selectedBook.getId());
        startActivity(intent);
    }

    private void doUpdateBook(Book selectedBook) {
        Log.d("MyApp:", "updating book: " + selectedBook.toString());
        // forward user to UpdateBookActivity, passing the selected book id
        Intent intent = new Intent(getApplicationContext(), UpdateBookActivity.class);
        intent.putExtra("book_id", selectedBook.getId());
        startActivity(intent);
    }

    private void doViewDetails(Book selectedBook) {
        Log.d("MyApp:", "viewing details: " + selectedBook.toString());
        // forward user to BookDetailsActivity, passing the selected book id
        Intent intent = new Intent(getApplicationContext(), BookDetailsActivity.class);
        intent.putExtra("book_id", selectedBook.getId());
        startActivity(intent);
    }

    /**
     * Action handler for Add Book floating action button
     * @param view
     */
    public void floatingAddBookClicked(View view) {
        // forward user to NewBookActivity
        Intent intent = new Intent(getApplicationContext(), NewBookActivity.class);
        startActivity(intent);
    }
}