package edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.api;

import android.content.Context;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.interceptors.AuthInterceptor;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.interceptors.LoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;

public class RetrofitClientInstance {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context)) // Add the auth interceptor
                    .addInterceptor(new LoggingInterceptor()) // Add the logging interceptor
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // Set the OkHttpClient with the interceptors
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
