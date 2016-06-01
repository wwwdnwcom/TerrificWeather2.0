package com.example.administrator.terrificweather.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.administrator.terrificweather.common.ACache;
import com.example.administrator.terrificweather.ui.setting.Setting;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by Administrator on 2016/5/23.
 */
public class BaseActivity extends AppCompatActivity{
    public ACache aCache;
    public Setting mSetting = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aCache = ACache.get(BaseActivity.this);
        mSetting = Setting.getsInstance();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    //设置沉浸式状态栏
    public void setStatusBarColor(int color){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(color);
        }
    }
    //Android 4.4与抽屉有冲突，会覆盖抽屉，因此专门重写此方法解决此问题
    public void setStatusBarColorForKITKAT(int color){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(color);
        }
    }
}
