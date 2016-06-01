package com.example.administrator.terrificweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by Administrator on 2016/2/27.
 */
public class FirstActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new switchHandler().sendEmptyMessageDelayed(1,1000);
    }

    class switchHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(FirstActivity.this,MainActivity.class);
            FirstActivity.this.startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            FirstActivity.this.finish();
        }
    }
}
