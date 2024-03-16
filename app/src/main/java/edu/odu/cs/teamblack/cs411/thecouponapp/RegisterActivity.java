package edu.odu.cs.teamblack.cs411.thecouponapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import edu.odu.cs.teamblack.cs411.thecouponapp.network.ApiService;
import edu.odu.cs.teamblack.cs411.thecouponapp.network.RegistrationRequest;
import edu.odu.cs.teamblack.cs411.thecouponapp.network.RegistrationResponse;
import edu.odu.cs.teamblack.cs411.thecouponapp.network.RetrofitClientInstance;
import edu.odu.cs.teamblack.cs411.thecouponapp.network.UserResponse;
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
        Button btn=findViewById(R.id.register_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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
                    RegistrationResponse registrationResponse = response.body();
                    // TODO: Handle successful registration
                } else {
                    // Handle registration failure
                    // For example, display an error message to the user

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