package com.example.administrator.terrificweather.common;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2016/3/4.
 */
public class BaseApplication extends Application {

    public static String cacheDir = "";
    public static Context mAppContext = null;


    @Override public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
        // 初始化 retrofit
        Retrofits.init(getApplicationContext());

        //Thread.setDefaultUncaughtExceptionHandler(new MyUnCaughtExceptionHandler());

        /**
         * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
         */

        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            //SDCard/Android/data/cpm/exam/admin/tw/cache/
            cacheDir = getApplicationContext().getExternalCacheDir().toString();
        }
        else {
            ///data/data/com/example/adminstrator/tw/cache/
            cacheDir = getApplicationContext().getCacheDir().toString();
        }
    }

    private boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean isNetworkConnected(Context context){
        if(context != null){
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if(mNetworkInfo != null){
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


}
