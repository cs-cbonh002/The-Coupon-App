package edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.api.ApiService;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.requests.RegistrationRequest;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.responses.RegistrationResponse;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.api.RetrofitClientInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText userNameField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userNameField = findViewById(R.id.userNameField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        Button registerBtn = findViewById(R.id.register_button);
        Button loginBtn = findViewById(R.id.login_button);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegistration(); // Call the performRegistration method when the button is clicked.
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void performRegistration() {
        String username = userNameField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        if (!password.equals(confirmPassword)) {
            // Handle password mismatch
            return;
        }

        RegistrationRequest registrationRequest = new RegistrationRequest(username, password);
        ApiService apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        Call<RegistrationResponse> call = apiService.registerUser(registrationRequest);
        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.isSuccessful()) {
                    // Assuming the server response includes a token in successful registration
                    RegistrationResponse registrationResponse = response.body();
                    // Navigate to HomeActivity or LoginActivity, depending on your flow
                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                    finish();
                } else {
                    // Server responded with some error
                    String errorMessage = "Registration failed.";
                    if(response.errorBody() != null) {
                        try {
                            // Parse error response body if it has a structured format
                            String errorJson = response.errorBody().string();
                            // Convert this JSON string to a suitable error message
                            // errorMessage = parseErrorJson(errorJson);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                // Handle network errors
                // For example, display a message indicating network failure
            }
        });
    }
}