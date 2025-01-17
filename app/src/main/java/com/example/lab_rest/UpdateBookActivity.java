package com.example.lab_rest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.lab_rest.model.Book;
import com.example.lab_rest.model.Category;
import com.example.lab_rest.model.FileInfo;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.BookService;
import com.example.lab_rest.remote.CategoryService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateBookActivity extends AppCompatActivity {

    // form fields
    private EditText txtTitle;
    private EditText txtDesc;
    private EditText txtAuthor;
    private EditText txtISBN;
    private EditText txtYear;
    private static TextView tvCreated;
    private Spinner spCategory;
    private ImageView imgBookCover;
    private ArrayAdapter<Category> adapter;

    private static Date createdAt;

    private Book book;  // current book to be updated

    // file upload feature
    private static final int PICK_IMAGE = 1;
    private static final int PERMISSION_REQUEST_STORAGE = 2;
    private Uri uri;

    /**
     * Date picker fragment class
     * Reference: https://developer.android.com/guide/topics/ui/controls/pickers
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            c.setTime(createdAt); // Use the current book created date as the default date in the picker
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            // create a date object from selected year, month and day
            createdAt = new GregorianCalendar(year, month, day).getTime();

            // display in the label beside the button with specific date format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            tvCreated.setText( sdf.format(createdAt) );
        }
    }

    /**
     * Called when pick date button is clicked. Display a date picker dialog
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // retrieve book id from intent
        // get book id sent by BookListActivity, -1 if not found
        Intent intent = getIntent();
        int id = intent.getIntExtra("book_id", -1);

        // initialize createdAt to today's date
        createdAt = new Date();

        // get references to the form fields in layout
        txtTitle = findViewById(R.id.txtTitle);
        txtDesc = findViewById(R.id.txtDesc);
        txtAuthor = findViewById(R.id.txtAuthor);
        txtISBN = findViewById(R.id.txtISBN);
        txtYear = findViewById(R.id.txtYear);
        tvCreated = findViewById(R.id.tvCreated);
        spCategory = findViewById(R.id.spCategory);
        imgBookCover = findViewById(R.id.imgBookCover);

        // retrieve book info from database using the book id
        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // fetch all book categories and set to spinner
        fetchAllBookCategories();

        // get book service instance
        BookService bookService = ApiUtils.getBookService();

        // execute the API query. send the token and book id
        bookService.getBook(user.getToken(), id).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                // for debug purpose
                Log.d("MyApp:", "Update Form Populate Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success
                    // get book object from response
                    book = response.body();

                    // set values into forms
                    txtTitle.setText(book.getName());
                    txtDesc.setText(book.getDescription());
                    txtAuthor.setText(book.getAuthor());
                    txtISBN.setText(book.getIsbn());
                    txtYear.setText(book.getYear());
                    tvCreated.setText(book.getCreatedAt());

                    // parse created_at date to date object
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        // parse created date string to date object
                        createdAt = sdf.parse(book.getCreatedAt());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // set spinner to select default category based on category of this book
                    for (int i=0; i<adapter.getCount(); i++)
                        if (adapter.getItem(i).getId() == book.getCategory_id())
                            spCategory.setSelection(i);

                    // Use Glide to load the image into the ImageView
                    Glide.with(getApplicationContext())
                            .load("http://178.128.220.20/fkexample/api/" + book.getImage()) // Make sure your Book object has a method to get the image URL
                            .placeholder(R.drawable.default_cover) // Placeholder image if the URL is empty
                            .error(R.drawable.default_cover) // Error image if there is a problem loading the image
                            .into(imgBookCover);

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
            public void onFailure(Call<Book> call, Throwable t) {
                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchAllBookCategories() {
        CategoryService catService = ApiUtils.getCategoryService();
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        Call<List<Category>> call = catService.getAllCategories(user.getToken());
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.code() == 200) {
                    // successfully fetch. get category list from response
                    List<Category> categories = response.body();
                    // prepare and set adapter to spinner
                    adapter = new ArrayAdapter<Category>(getApplicationContext(),
                            android.R.layout.simple_spinner_dropdown_item, categories);
                    spCategory.setAdapter(adapter);
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
            public void onFailure(Call<List<Category>> call, Throwable throwable) {
                Log.e("MyApp:", throwable.getMessage());
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

    /**
     * Update Book button action handler
     * Update book info in database when the user click Update Book button
     * @param view
     */
    public void updateBook(View view) {

        if (uri != null && !uri.getPath().isEmpty()) {
            // new image selected
            // Upload file first. if successful, call updateBookRecord in onResponse()
            uploadFile(uri);
        }
        else {
            // no new image selected
            // no need to upload file, update all other fields
            updateBookRecord();
        }




    }

    private void updateBookRecord() {

        // get values in form
        String title = txtTitle.getText().toString();
        String description = txtDesc.getText().toString();
        String author = txtAuthor.getText().toString();
        String isbn = txtISBN.getText().toString();
        String year = txtYear.getText().toString();

        // convert createdAt date to format required by Database - yyyy-MM-dd HH:mm:ss
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String created_at = sdf.format(createdAt);

        // set updated_at to current date and time
        String updated_at = sdf.format(new Date());

        Log.d("MyApp:", "Old Book info: " + book.toString());

        // update the book object retrieved in when populating the form with the new data.
        // update all fields excluding the id
        book.setName(title);
        book.setIsbn(isbn);
        book.setYear(year);
        book.setAuthor(author);
        book.setDescription(description);
        book.setCreatedAt(created_at);
        book.setUpdatedAt(updated_at);
        int catId = ((Category)spCategory.getSelectedItem()).getId();
        book.setCategory_id(catId);

        Log.d("MyApp:", "New Book info: " + book.toString());

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // send request to update the book record to the REST API
        BookService bookService = ApiUtils.getBookService();
        Call<Book> call = bookService.updateBook(user.getToken(), book.getId(), book.getIsbn(),
                book.getName(), book.getYear(), book.getAuthor(), book.getDescription(),
                book.getImage(), book.getCreatedAt(), book.getUpdatedAt(), book.getCategory_id());

        // execute
        call.enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {

                // for debug purpose
                Log.d("MyApp:", "Update Request Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success code for update request
                    // get updated book object from response
                    Book updatedBook = response.body();

                    // display message
                    displayUpdateSuccess(updatedBook.getName() + " updated successfully.");


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
            public void onFailure(Call<Book> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });
    }

    private void uploadFile(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = getBytesFromInputStream(inputStream);
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), fileBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", getFileName(uri), requestFile);

            // get token user info from shared preference in order to get token value
            SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
            User user = spm.getUser();

            BookService bookService = ApiUtils.getBookService();
            Call<FileInfo> call = bookService.uploadFile(user.getToken(), body);

            call.enqueue(new Callback<FileInfo>() {
                @Override
                public void onResponse(Call<FileInfo> call, Response<FileInfo> response) {
                    if (response.isSuccessful()) {
                        // file uploaded successfully
                        // Now add the book record with the uploaded file name
                        FileInfo fi = response.body();
                        String fileName = fi.getFile();

                        // update book object to newly uploaded image
                        book.setImage(fileName);

                        updateBookRecord();    // update book record with the new information
                    } else {
                        Toast.makeText(getApplicationContext(), "File upload failed", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<FileInfo> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error uploading file", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error preparing file for upload", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayUpdateSuccess(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // end this activity and forward user to BookListActivity
                        Intent intent = new Intent(getApplicationContext(), BookListActivity.class);
                        startActivity(intent);
                        finish();

                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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

    /**
     * Get image bytes from local disk. Used to upload the image bytes to Rest API
     * @param inputStream
     * @return
     * @throws IOException
     */
    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    /**
     * Get image file name from Uri
     * @param uri
     * @return
     */
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    }
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * Button browse action handler
     * @param view
     */
    public void choosePhoto(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+, require READ_MEDIA_IMAGES permission
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_STORAGE);
            } else {
                openGallery();
            }
        } else {
            // For Android 12 and below, require READ_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_STORAGE);
            } else {
                openGallery();
            }
        }
    }

    /**
     * User clicked deny or allow in permission request dialog
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Open Image Picker Activity
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    /**
     * Callback for Activity, in this case will handle the result of Image Picker Activity
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            // image picker activity return a value
            if(data != null) {
                // get file uri
                uri = data.getData();
                // set image to imageview
                imgBookCover.setImageURI(uri);
            }
        }
    }

}