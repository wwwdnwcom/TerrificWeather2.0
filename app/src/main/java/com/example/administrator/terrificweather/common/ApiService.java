package com.example.administrator.terrificweather.common;

import com.example.administrator.terrificweather.model.WeatherAPI;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;


/**
 * Created by Administrator on 2016/4/24.
 */
public interface ApiService {
    String baseURL = "https://api.heweather.com/x3/";

    @GET("weather")
    Observable<WeatherAPI> mWeatherAPI(@Query("city") String city, @Query("key") String key);
}
