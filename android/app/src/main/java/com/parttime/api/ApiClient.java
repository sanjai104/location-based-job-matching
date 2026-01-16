package com.parttime.api;

import android.content.Context;
import com.parttime.utils.SharedPreferencesManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.68.54:8080/";

    private static Retrofit retrofit = null;
    private static Context appContext;
    
    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }
    
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                String token = SharedPreferencesManager.getInstance(appContext).getToken();
                Request.Builder requestBuilder = original.newBuilder();
                if (token != null && !token.isEmpty()) {
                    requestBuilder.header("Authorization", "Bearer " + token);
                }
                requestBuilder.header("Content-Type", "application/json");
                return chain.proceed(requestBuilder.build());
            });
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
    
    public static ApiService getApiService() {
        return getRetrofit().create(ApiService.class);
    }
}


