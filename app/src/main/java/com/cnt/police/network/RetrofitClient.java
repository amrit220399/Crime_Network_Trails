package com.cnt.police.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String base_url = "https://api-us.faceplusplus.com/facepp/v3/";//base url
    private static final String fcm_url = "https://fcm.googleapis.com/fcm/";//fcm url
    private static final String news_url = "https://newsapi.org/v2/";//fcm url
    private static Retrofit retrofit = null;
    private static Retrofit fcmRetrofit = null;
    private static Retrofit newsRetrofit = null;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder().baseUrl(base_url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getFCMInstance() {
        if (fcmRetrofit == null) {
            fcmRetrofit = new Retrofit.Builder()
                    .baseUrl(fcm_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return fcmRetrofit;
    }

    public static Retrofit getNewsInstance() {
        if (newsRetrofit == null) {
            newsRetrofit = new Retrofit.Builder()
                    .baseUrl(news_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return newsRetrofit;
    }
}
