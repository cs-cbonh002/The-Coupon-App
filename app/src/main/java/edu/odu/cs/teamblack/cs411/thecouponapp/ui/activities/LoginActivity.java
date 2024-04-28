package edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.api.ApiService;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.responses.JwtResponse;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.requests.LoginRequest;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.api.RetrofitClientInstance;
import edu.odu.cs.teamblack.cs411.thecouponapp.utils.SecurePreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText userNameField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        userNameField = findViewById(R.id.userNameField);
        passwordField = findViewById(R.id.passwordField);

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });



        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "HomeFragment"); // Indicate which fragment to load
            startActivity(intent);
            finish();
        });


    }

    private void performLogin() {
        String username = userNameField.getText().toString();
        String password = passwordField.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Username or password cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(username, password);
        ApiService apiService = RetrofitClientInstance.getRetrofitInstance(LoginActivity.this).create(ApiService.class);

        Call<JwtResponse> call = apiService.getToken(loginRequest);
        call.enqueue(new Callback<JwtResponse>() {
            @Override
            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                if (response.isSuccessful() && response.body()!= null) {
                    JwtResponse jwtResponse = response.body();
                    SharedPreferences preferences = SecurePreferences.getEncryptedSharedPreferences(LoginActivity.this);
                    preferences.edit().putString("access_token", jwtResponse.getAccess()).apply();
                    Intent intent = new Intent(LoginActivity.this, FacadeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JwtResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "An error occurred during the network request", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
