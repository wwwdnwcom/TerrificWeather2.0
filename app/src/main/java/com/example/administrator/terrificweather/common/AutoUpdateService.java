package com.example.administrator.terrificweather.common;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.administrator.terrificweather.model.WeatherAPI;
import com.example.administrator.terrificweather.R;
import com.example.administrator.terrificweather.activity.MainActivity;
import com.example.administrator.terrificweather.model.Weather;
import com.example.administrator.terrificweather.ui.setting.Setting;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/4/10.
 */
public class AutoUpdateService extends Service{

    private Setting mSetting;
    private ACache mAcache;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mSetting = Setting.getsInstance();
        mAcache = ACache.get(this);
        Observable.interval(mSetting.getAutoUpdate(), TimeUnit.HOURS).subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                catchDataByNetwork();
            }
        });

        return START_REDELIVER_INTENT;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void normalStyleNotification(Weather weather){
        Intent intent = new Intent(AutoUpdateService.this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(AutoUpdateService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(AutoUpdateService.this);
        Notification notification = builder.setContentIntent(pendingIntent)
                .setContentTitle(weather.basic.city)
                .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.cond.txt, weather.now.tmp))
                .setSmallIcon(mSetting.getInt(weather.now.cond.txt, R.mipmap.none))
                .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);

    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    private void catchDataByNetwork(){
        String cityName = mSetting.getString(Setting.CITY_NAME,"北京");
        if(cityName != null){
            cityName = cityName.replace("市","")
                    .replace("省","")
                    .replace("自治区","")
                    .replace("特别行政区","")
                    .replace("地区","")
                    .replace("盟","");
        }
        Retrofits.getApiService(this)
                .mWeatherAPI(cityName,Setting.KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<WeatherAPI, Boolean>() {
                    @Override
                    public Boolean call(WeatherAPI weatherAPI) {
                        return weatherAPI.mHeWeatherDataService30.get(0).status.equals("ok");
                    }
                })
                .map(new Func1<WeatherAPI, Weather>() {

                    @Override
                    public Weather call(WeatherAPI weatherAPI) {
                        return weatherAPI.mHeWeatherDataService30.get(0);
                    }
                })
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Weather weather) {
                        mAcache.put("WeatherData",weather);
                        normalStyleNotification(weather);
                    }
                });
    }
}
