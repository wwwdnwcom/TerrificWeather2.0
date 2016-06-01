package com.example.administrator.terrificweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/24.
 */
public class WeatherAPI {
    @SerializedName("HeWeather data service 3.0")
    @Expose
    public List<Weather> mHeWeatherDataService30 = new ArrayList<>();
}
