package com.example.administrator.terrificweather.ui.setting;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.administrator.terrificweather.common.BaseApplication;

/**
 * Created by Administrator on 2016/4/24.
 */
public class Setting {
    public static final String CHANGE_ICONS = "change_icons";//切换图标
    public static final String CLEAR_CACHE = "clear_cache";//清除缓存
    public static final String AUTO_UPDATE = "change_update_time";//自动更新时长
    public static final String CITY_NAME = "城市";//选择城市
    public static final String HOUR = "小时";//当前小时
    public static final String NOTIFICATION_MODEL = "notification_model";//通知栏

    public static final String KEY = "6e8a0aa36c734a52b8f7184f24204f73";//和风天气apikey

    public static int ONE_HOUR = 3600;
    public static int TEN_HOUR = 36000;

    private static Setting sInstance;

    private SharedPreferences mPrefs;



    public static Setting  getsInstance(){
        if(sInstance == null){
            sInstance = new Setting(BaseApplication.mAppContext);
        }
        return sInstance;
    }

    public Setting(Context context) {
        mPrefs = context.getSharedPreferences("setting",Context.MODE_PRIVATE);
    }



    public Setting putBoolean(String key, boolean value) {
        mPrefs.edit().putBoolean(key, value).apply();
        return this;
    }

    public boolean getBoolean(String key, boolean def) {
        return mPrefs.getBoolean(key, def);
    }

    public Setting putInt(String key, int value) {
        mPrefs.edit().putInt(key, value).apply();
        return this;
    }

    public int getInt(String key, int defValue) {
        return mPrefs.getInt(key, defValue);
    }

    public Setting putString(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
        return this;
    }

    public String getString(String key, String defValue) {
        return mPrefs.getString(key, defValue);
    }

    // 图标种类相关
    public void setIconType(int type) {
        mPrefs.edit().putInt(Setting.CHANGE_ICONS, type).apply();
    }

    public int getIconType() {
        return mPrefs.getInt(Setting.CHANGE_ICONS, 0);
    }

    // 自动更新时间 hours
    public void setAutoUpdate(int t) {
        mPrefs.edit().putInt(Setting.AUTO_UPDATE, t).apply();
    }

    public int getAutoUpdate() {
        return mPrefs.getInt(Setting.AUTO_UPDATE, 3);
    }

    //当前城市
    public void setCityName(String name) {
        mPrefs.edit().putString(Setting.CITY_NAME, name).apply();
    }

    public String getCityName() {
        return mPrefs.getString(Setting.CITY_NAME, "北京");
    }
//通知兰
//    public void setNotificationModel(int t){
//        mPrefs.edit().putInt(NOTIFICATION_MODEL,t).apply();
//    }
//    public int getNotificationModel(){
//        return mPrefs.getInt(NOTIFICATION_MODEL, Notification.FLAG_AUTO_CANCEL);
//    }
}
