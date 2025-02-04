package com.example.lab_rest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab_rest.model.FailLogin;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private ProgressDialog progressDialog;
    public static String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get references to form elements
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#FF1BA7"));
        }
    }

    /**
     * Login button action handler
     */
    public void loginClicked(View view) {

        // display loading progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // get username and password entered by user
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        // validate form, make sure it is not empty
        if (validateLogin(username, password)) {
            // if not empty, login using REST API
            doLogin(username, password);
        }

    }

    /**
     * Call REST API to login
     *
     * @param username username
     * @param password password
     */
    private void doLogin(String username, String password) {

        // get UserService instance
        UserService userService = ApiUtils.getUserService();

        // prepare the REST API call using the service interface
        Call<User> call = userService.login(username, password);

        // execute the REST API call
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call call, Response response) {

                // end progress dialog
                progressDialog.dismiss();

                if (response.isSuccessful()) {  // code 200
                    // parse response to POJO
                    User user = (User) response.body();
                    if (user != null && user.getToken() != null) {
                        // successful login. server replies a token value
                        displayToast("Login successful");
                        displayToast("Token: " + user.getToken());

                        userRole = user.getRole();

                        // store value in Shared Preferences
                        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                        spm.storeUser(user);

                        // forward user to MainActivity
                        finish();
                        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        if (user.getRole().equals("admin")){
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), MainActivityUser.class));
                        }
                    }
                    else {
                        // server return success but no user info replied
                        displayToast("Login error");
                    }
                }
                else {  // other than 200
                    // try to parse the response to FailLogin POJO
                    String errorResp = null;
                    try {
                        errorResp = response.errorBody().string();
                        FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
                        Snackbar.make(findViewById(R.id.login), e.getError().getMessage(), Snackbar.LENGTH_LONG).show();
                        //displayToast(e.getError().getMessage());
                    } catch (Exception e) {
                        Log.e("MyApp:", e.toString()); // print error details to error log
                        displayToast("Error");
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                // end progress dialog
                progressDialog.dismiss();

                displayToast("Error connecting to server.");
                displayToast(t.getMessage());
                Log.e("MyApp:", t.toString()); // print error details to error log
            }
        });
    }

    /**
     * Validate value of username and password entered. Client side validation.
     * @param username
     * @param password
     * @return
     */
    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            displayToast("Username is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    /**
     * Display a Toast message
     * @param message message to be displayed inside toast
     */
    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}