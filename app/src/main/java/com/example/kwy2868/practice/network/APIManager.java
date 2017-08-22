package com.example.kwy2868.practice.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIManager {
    private static final String BASE_URL = "http://203.252.166.230";

    private static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    private static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(getInterceptor()).build();

    public static HttpLoggingInterceptor getInterceptor() {
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();

    private static UserService userService = retrofit.create(UserService.class);
    public static UserService getUserService() {
        return userService;
    }

    private static MusicService musicService = retrofit.create(MusicService.class);
    public static MusicService getMusicService() {
        return musicService;
    }

}
