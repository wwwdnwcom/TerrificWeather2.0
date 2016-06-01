package com.example.administrator.terrificweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.terrificweather.db.DBHelper;
import com.example.administrator.terrificweather.model.Province;
import com.example.administrator.terrificweather.R;
import com.example.administrator.terrificweather.adapter.CityAdapter;
import com.example.administrator.terrificweather.db.WeatherDB;
import com.example.administrator.terrificweather.model.City;
import com.example.administrator.terrificweather.ui.setting.Setting;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/4/14.
 */
public class ChooseCityActivity extends BaseActivity{

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private WeatherDB weatherDB;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ArrayList<String> dataList = new ArrayList<String>();
    private Province selectedProvince;
    private City selectedCity;
    private List<Province> provinceList;
    private List<City> cityList;
    private CityAdapter cityAdapter;

    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    private int currentLevel;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_cities);
        dbHelper = new DBHelper(this);
        dbHelper.openDatabase();
        weatherDB = new WeatherDB(this);
        initView();
        initRecylerView();
        queryProvinces();
    }
    private void initView() {
        ImageView bannner = (ImageView) findViewById(R.id.small_city);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setStatusBarColorForKITKAT(R.color.colorSunrise);
        if(mSetting.getInt(Setting.HOUR,0)<6 || mSetting.getInt(Setting.HOUR,0) > 18){
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this,R.color.colorSunset));
            Glide.with(this).load(R.mipmap.city_night).diskCacheStrategy(DiskCacheStrategy.ALL).into(bannner);
            setStatusBarColorForKITKAT(R.color.colorSunset);
        }
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void initRecylerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        cityAdapter = new CityAdapter(this,dataList);
        recyclerView.setAdapter(cityAdapter);

        cityAdapter.setOnItemClickListener(new CityAdapter.OnRecyclerViewItemClickListener(){

            @Override
            public void onItemClick(View view , final int pos) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(pos);
                    recyclerView.scrollTo(0,0);
                    queryCities();
                } else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(pos);
                    Intent intent = new Intent();
                    String cityName = selectedCity.CityName;
                    intent.putExtra(Setting.CITY_NAME,cityName);
                    setResult(2,intent);
                    finish();
                }
            }
        });
    }

    private void queryProvinces() {
        collapsingToolbarLayout.setTitle("选择省份");
        Observer<Province> observer = new Observer<Province>() {
            @Override
            public void onCompleted() {
                currentLevel = LEVEL_PROVINCE;
                cityAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Province province) {
                dataList.add(province.ProName);
            }
        };

        Observable.defer(new Func0<Observable<Province>>() {
            @Override
            public Observable<Province> call() {
                provinceList = weatherDB.loadProvince(dbHelper.getDatabase());
                dataList.clear();
                return Observable.from(provinceList);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

    }
    private void queryCities() {
        dataList.clear();
        collapsingToolbarLayout.setTitle(selectedProvince.ProName);
        Observer<City> observer = new Observer<City>() {
            @Override
            public void onCompleted() {
                currentLevel = LEVEL_CITY;
                cityAdapter.notifyDataSetChanged();
//                定位到第一个item
                recyclerView.smoothScrollToPosition(0);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(City city) {
                dataList.add(city.CityName);

            }
        };

        Observable.defer(new Func0<Observable<City>>() {
            @Override
            public Observable<City> call() {
                cityList = weatherDB.loadCity(dbHelper.getDatabase() , selectedProvince.ProSort);

                return Observable.from(cityList);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(currentLevel == LEVEL_PROVINCE){
                finish();
            }else{
                queryProvinces();
                recyclerView.smoothScrollToPosition(0);
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.closeDatabase();
    }
}
