package edu.odu.cs.teamblack.cs411.thecouponapp.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        // Log the request details
        System.out.println("Request: " + request.url());

        // Log the request body if present
        if (request.body() != null) {
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            System.out.println("Request Body: " + buffer.readUtf8());
        }

        // Proceed with the request
        Response response = chain.proceed(request);

        // Log the response details
        System.out.println("Response: " + response.code());

        // Log the response body if present
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            String responseBodyString = responseBody.string();
            System.out.println("Response Body: " + responseBodyString);
            // Re-create the response body since it can be read only once
            response = response.newBuilder()
                    .body(ResponseBody.create(responseBody.contentType(), responseBodyString))
                    .build();
        }

        return response;
    }
}
