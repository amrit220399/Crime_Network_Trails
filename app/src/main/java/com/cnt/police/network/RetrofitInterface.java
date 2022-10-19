package com.cnt.police.network;

import com.cnt.police.BuildConfig;
import com.cnt.police.network.requests.NotificationRequest;

import java.util.ArrayList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @POST("compare")
    @Multipart
    Call<ResponseBody> compareFaces(
            @Part("api_key") RequestBody api_key,
            @Part("api_secret") RequestBody api_secret,
            @Part("image_file1\"; filename=\"image_file1.png\" ") RequestBody image_file1,
            @Part("image_file2\"; filename=\"image_file2.png\" ") RequestBody image_file2
    );

    @Headers({"Authorization: key=" + BuildConfig.FCM_SERVER_KEY,
            "Content-Type: application/json"})
    @POST("send")
    Call<ResponseBody> sendNotification(@Body NotificationRequest request);

    @GET("everything")
    Call<ResponseBody> getLatestNews(@Query("apiKey") String apiKey, @Query("q") String query,
                                     @Query("sources") ArrayList<String> sources, @Query("page") int page);
}
