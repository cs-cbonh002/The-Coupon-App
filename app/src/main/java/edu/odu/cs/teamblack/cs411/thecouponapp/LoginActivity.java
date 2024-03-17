package edu.odu.cs.teamblack.cs411.thecouponapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import edu.odu.cs.teamblack.cs411.thecouponapp.network.ApiService;
import edu.odu.cs.teamblack.cs411.thecouponapp.network.JwtResponse;
import edu.odu.cs.teamblack.cs411.thecouponapp.network.LoginRequest;
import edu.odu.cs.teamblack.cs411.thecouponapp.network.RetrofitClientInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText userNameField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameField = findViewById(R.id.userNameField);
        passwordField = findViewById(R.id.passwordField);

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        Button settingsButton = findViewById(R.id.SettingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
            }
        });

        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }
        });

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = userNameField.getText().toString();
        String password = passwordField.getText().toString();

        LoginRequest loginRequest = new LoginRequest(username, password);
        ApiService apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        Call<JwtResponse> call = apiService.getToken(loginRequest);
        call.enqueue(new Callback<JwtResponse>() {
            @Override
            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                if (response.isSuccessful()) {
                    JwtResponse jwtResponse = response.body();
                    if (jwtResponse != null && jwtResponse.getAccess() != null) {
                        String accessToken = jwtResponse.getAccess();
                        // Save the access token using SharedPreferencesManager
                        SharedPreferencesManager.saveAccessToken(LoginActivity.this, accessToken);
                        // Transition to the next activity (e.g., HomeActivity)
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish(); // Finish the LoginActivity to prevent going back to it with back button
                    } else {
                        // Access token is null or the response body is null
                        Toast.makeText(LoginActivity.this, "Failed to retrieve access token from server.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JwtResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Failed to connect to server. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
