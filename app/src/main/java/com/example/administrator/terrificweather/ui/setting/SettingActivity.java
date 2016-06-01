package com.example.administrator.terrificweather.ui.setting;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.administrator.terrificweather.activity.BaseActivity;
import com.example.administrator.terrificweather.R;

/**
 * Created by Administrator on 2016/4/25.
 */
public class SettingActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_32dpdp));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setStatusBarColor(R.color.colorPrimary);
        if(mSetting.getInt(Setting.HOUR, 0) < 6 || mSetting.getInt(Setting.HOUR, 0) > 18){
            toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorSunset));
            setStatusBarColor(R.color.colorSunset);
        }
        getFragmentManager().beginTransaction().replace(R.id.frame_layout,new SettingFragment()).commit();
    }
}
