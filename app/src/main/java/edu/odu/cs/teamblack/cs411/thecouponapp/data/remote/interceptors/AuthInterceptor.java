package edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.interceptors;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import java.io.IOException;

import edu.odu.cs.teamblack.cs411.thecouponapp.utils.SecurePreferences;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        SharedPreferences preferences = SecurePreferences.getEncryptedSharedPreferences(context);
        String accessToken = preferences.getString("access_token", "");

        Request newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        return chain.proceed(newRequest);
    }
}
