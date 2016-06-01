package com.example.administrator.terrificweather.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.terrificweather.model.Province;
import com.example.administrator.terrificweather.model.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/24.
 */
public class WeatherDB {
    private Context context;

    public WeatherDB(Context context){
        this.context = context;
    }

    public List<Province> loadProvince(SQLiteDatabase db) {
        List<Province> list = new ArrayList<>();

        Cursor cursor = db.query("T_Province", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.ProSort = cursor.getInt(cursor.getColumnIndex("ProSort"));
                province.ProName = cursor.getString(cursor.getColumnIndex("ProName"));
                list.add(province);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<City> loadCity(SQLiteDatabase db,int ProID){
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("T_City",null,"ProID = ?",new String[]{String.valueOf(ProID)}
        ,null,null,null);
        if(cursor.moveToFirst()){
            do {
                City city = new City();
                city.CityName = cursor.getString(cursor.getColumnIndex("CityName"));
                city.ProID = ProID;
//                city.ProID = cursor.getInt(cursor.getColumnIndex("ProID"));
                list.add(city);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

}
