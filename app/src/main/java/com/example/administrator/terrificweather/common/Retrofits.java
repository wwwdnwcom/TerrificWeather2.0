package com.example.administrator.terrificweather.common;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by Administrator on 2016/4/24.
 */
public class Retrofits {
    private static ApiService apiService = null;
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;
    public static Context context;

    public static void init(final Context context){
        Executor executor = Executors.newCachedThreadPool();

        Gson gson = new GsonBuilder().create();

        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(ApiService.baseURL)
                .callbackExecutor(executor)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        apiService =  retrofit.create(ApiService.class);
    }

    public static ApiService getApiService(Context context){
        if(apiService != null) return apiService;
        init(context);
        return getApiService(context);
    }

    public static Retrofit getRetrofit(Context context) {
        if (retrofit != null) return retrofit;
        init(context);
        return getRetrofit(context);
    }


    public static OkHttpClient getOkHttpClient(Context context) {
        if (okHttpClient != null) return okHttpClient;
        init(context);
        return getOkHttpClient(context);
    }



}
