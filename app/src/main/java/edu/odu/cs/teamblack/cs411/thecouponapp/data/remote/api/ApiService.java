package edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.api;

import java.util.List;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.responses.JwtResponse;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.requests.LoginRequest;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.requests.RefreshTokenRequest;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.requests.RegistrationRequest;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.responses.RegistrationResponse;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.responses.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // User registration endpoint
    @POST("api/register/")
    Call<RegistrationResponse> registerUser(@Body RegistrationRequest registrationRequest);

    // JWT token retrieval endpoint
    @POST("api/token/")
    Call<JwtResponse> getToken(@Body LoginRequest loginRequest);

    // JWT token refresh endpoint
    @POST("api/token/refresh/")
    Call<JwtResponse> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);

    // Fetch current user profile endpoint
    @GET("api/users/me/")
    Call<UserResponse> getCurrentUserProfile();

    @POST("api/users/me/incident-logs/")
    Call<IncidentLog> createIncidentLog(
            @Body IncidentLog incidentLog
    );

    @GET("api/users/me/incident-logs/")
    Call<List<IncidentLog>> getUserIncidentLogs(@Header("Authorization") String authHeader);


    // Other endpoints as needed...probably emergency contacts
}
