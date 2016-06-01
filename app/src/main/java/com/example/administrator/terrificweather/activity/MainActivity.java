package com.example.administrator.terrificweather.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.util.Util;
import com.example.administrator.terrificweather.common.ACache;
import com.example.administrator.terrificweather.common.AutoUpdateService;
import com.example.administrator.terrificweather.common.BaseApplication;
import com.example.administrator.terrificweather.common.HidingScrollListener;
import com.example.administrator.terrificweather.common.Retrofits;
import com.example.administrator.terrificweather.common.ShareDialog;
import com.example.administrator.terrificweather.model.Weather;
import com.example.administrator.terrificweather.model.WeatherAPI;
import com.example.administrator.terrificweather.ui.about.AboutActivity;
import com.example.administrator.terrificweather.ui.setting.Setting;
import com.example.administrator.terrificweather.ui.setting.SettingActivity;
import com.example.administrator.terrificweather.R;
import com.example.administrator.terrificweather.adapter.WeatherAdapter;

import java.util.Calendar;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.email.Email;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,AMapLocationListener,PlatformActionListener {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FloatingActionButton fab;
    private SwipeRefreshLayout refreshLayout;
    private ImageView imageView;
    private ProgressBar progressBar;
    private RelativeLayout headerBackground;
    private Weather weather;

    private RecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;
    private Observer<Weather> observer;

    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;

    public ShareDialog shareDialog;


    private boolean isLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ShareSDK.initSDK(this);
//        Log.d("11111111111111",weather.basic.city);
        initView();
        initDrawer();
        initIcon();
        if(BaseApplication.isNetworkConnected(this)){
            location();
            if(isLocation){
                onRefresh();
            }else{
                catchData();
            }

        }
        startService(new Intent(this, AutoUpdateService.class));

    }




    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setFitsSystemWindows(true);
        setSupportActionBar(toolbar);
        imageView = (ImageView) findViewById(R.id.head_image);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(this);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(" ");

        Calendar calendar = Calendar.getInstance();
        mSetting.putInt(Setting.HOUR, calendar.get(Calendar.HOUR_OF_DAY));

        setStatusBarColorForKITKAT(R.color.colorSunrise);
        if (mSetting.getInt(Setting.HOUR, 0) < 6 || mSetting.getInt(Setting.HOUR, 0) > 18) {
            Glide.with(this).load(R.mipmap.sunset).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
            setStatusBarColorForKITKAT(R.color.colorSunset);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fab:
                        shareDialog = new ShareDialog(MainActivity.this);
                        shareDialog.setCancelButtonOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareDialog.dismiss();
                            }
                        });

                        shareDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
                                if (item.get("ItemText").equals("新浪微博")) {
                                    SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
                                    weather = (Weather) aCache.getAsObject("WeatherData");
                                    String text = weather.basic.city + "当天天气情况为：空气质量类别是" + weather.aqi.city.qlty + "，当前温度为：" + weather.now.tmp + "度，最高温度和最低温度分别为：" + weather.dailyForecast.get(0).tmp.max + "度和" + weather.dailyForecast.get(0).tmp.min + "度，出门穿衣建议为：" + weather.suggestion.drsg.txt;
                                    sp.setText(text); //分享文本
//                                    sp.setImageUrl("http://7sby7r.com1.z0.glb.clouddn.com/CYSJ_02.jpg");//网络图片rul

                                    //3、非常重要：获取平台对象
                                    Platform sinaWeibo = ShareSDK.getPlatform(MainActivity.this, SinaWeibo.NAME);
//                                    sinaWeibo.SSOSetting(true);
                                    sinaWeibo.setPlatformActionListener(MainActivity.this); // 设置分享事件回调
                                    // 执行分享
                                    sinaWeibo.share(sp);
                                } else if (item.get("ItemText").equals("短信")) {
                                    ShortMessage.ShareParams sp = new ShortMessage.ShareParams();
                                    weather = (Weather) aCache.getAsObject("WeatherData");
                                    String text = weather.basic.city + "当天天气情况为：空气质量是" + weather.aqi.city.qlty + "，当前温度为：" + weather.now.tmp + "度，最高温度和最低温度分别为：" + weather.dailyForecast.get(0).tmp.max + "度和" + weather.dailyForecast.get(0).tmp.min + "度，出门穿衣建议为：" + weather.suggestion.drsg.txt;
                                    sp.setText(text);

                                    Platform shortMessage = ShareSDK.getPlatform(ShortMessage.NAME);
                                    shortMessage.setPlatformActionListener(MainActivity.this);
                                    shortMessage.share(sp);
                                } else if (item.get("ItemText").equals("微信好友")) {
                                    Wechat.ShareParams sp = new Wechat.ShareParams();
                                    sp.setShareType(Platform.SHARE_WEBPAGE);//非常重要：一定要设置分享属性

                                    sp.setText("1111111");   //分享文本//网络图片rul
//                                    sp.setUrl("http://sharesdk.cn");   //网友点进链接后，可以看到分享的详情

                                    //3、非常重要：获取平台对象
                                    Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                                    wechat.setPlatformActionListener(MainActivity.this); // 设置分享事件回调
                                    // 执行分享
                                    wechat.share(sp);
                                } else if (item.get("ItemText").equals("邮件")) {
                                    Email.ShareParams sp = new Email.ShareParams();
                                    weather = (Weather) aCache.getAsObject("WeatherData");
                                    String text = weather.basic.city + "当天天气情况为：空气质量是" + weather.aqi.city.qlty + "，当前温度为：" + weather.now.tmp + "度，最高温度和最低温度分别为：" + weather.dailyForecast.get(0).tmp.max + "度和" + weather.dailyForecast.get(0).tmp.min + "度，出门穿衣建议为：" + weather.suggestion.drsg.txt;
                                    sp.setText(text);


                                    //3、非常重要：获取平台对象
                                    Platform email = ShareSDK.getPlatform(Email.NAME);
                                    email.setPlatformActionListener(MainActivity.this); // 设置分享事件回调
                                    // 执行分享
                                    email.share(sp);
                                }
                                shareDialog.dismiss();
                            }
                        });

                        break;

                    default:

                        break;

                }
            }
        });


        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        final int fabBottomMargin = layoutParams.bottomMargin;

        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                fab.animate().translationY(fab.getHeight() + fabBottomMargin)
                        .setInterpolator(new AccelerateInterpolator(2))
                        .start();
            }

            @Override
            public void onShow() {
                fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        headerBackground = (RelativeLayout) headerLayout.findViewById(R.id.header_background);

        if(mSetting.getInt(Setting.HOUR,0) < 6 || mSetting.getInt(Setting.HOUR,0) > 18){
            headerBackground.setBackground(ContextCompat.getDrawable(this,R.mipmap.header_back_night));
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void initIcon() {
        if(mSetting.getIconType() == 0) {
            mSetting.putInt("未知", R.mipmap.none);
            mSetting.putInt("晴", R.mipmap.type_sunny_1);
            mSetting.putInt("阴", R.mipmap.type_cloudy_1);
            mSetting.putInt("多云", R.mipmap.type_cloudy_1);
            mSetting.putInt("少云", R.mipmap.type_cloudy_1);
            mSetting.putInt("晴间多云", R.mipmap.type_cloudytosunny_1);
            mSetting.putInt("小雨", R.mipmap.type_lightrain_1);
            mSetting.putInt("中雨", R.mipmap.type_lightrain_1);
            mSetting.putInt("大雨", R.mipmap.type_heavyrain_1);
            mSetting.putInt("阵雨", R.mipmap.type_thunderrain_1);
            mSetting.putInt("雷阵雨", R.mipmap.type_thunderrain_1);
            mSetting.putInt("霾", R.mipmap.type_fog_1);
            mSetting.putInt("雾", R.mipmap.type_fog_1);
        }else{
            mSetting.putInt("未知", R.mipmap.none);
            mSetting.putInt("晴", R.mipmap.type_sunny_2);
            mSetting.putInt("阴", R.mipmap.type_cloudy_2);
            mSetting.putInt("多云", R.mipmap.type_cloudy_2);
            mSetting.putInt("少云", R.mipmap.type_cloudy_2);
            mSetting.putInt("晴间多云", R.mipmap.type_cloudytosunny_2);
            mSetting.putInt("小雨", R.mipmap.type_lightrain_2);
            mSetting.putInt("中雨", R.mipmap.type_lightrain_2);
            mSetting.putInt("大雨", R.mipmap.type_heavyrain_2);
            mSetting.putInt("阵雨", R.mipmap.type_thunderrain_2);
            mSetting.putInt("雷阵雨", R.mipmap.type_thunderrain_2);
            mSetting.putInt("霾", R.mipmap.type_fog_2);
            mSetting.putInt("雾", R.mipmap.type_fog_2);
        }
    }

    private void catchData(){
        observer = new Observer<Weather>() {
            @Override
            public void onCompleted() {
                new RefreshHandler().sendEmptyMessage(2);
            }

            @Override
            public void onError(Throwable e) {
                Snackbar.make(fab,"获取失败",Snackbar.LENGTH_SHORT).show();
                new RefreshHandler().sendEmptyMessage(2);
            }

            @Override
            public void onNext(Weather weather) {
                progressBar.setVisibility(View.INVISIBLE);
                new RefreshHandler().sendEmptyMessage(2);
                collapsingToolbarLayout.setTitle(weather.basic.city);
                weatherAdapter = new WeatherAdapter(MainActivity.this,weather);
                recyclerView.setAdapter(weatherAdapter);

            }
        };
        catchDataByCache(observer);
    }

    private void catchDataByCache(Observer<Weather> observer) {
        Weather weather = null;
        //读取读取 Serializable数据key
        weather = (Weather) aCache.getAsObject("WeatherData");

        if(weather != null){
            Observable.just(weather).distinct().subscribe(observer);
        }else{
            catchDataByNetWork(observer);
    }
    }

    private void catchDataByNetWork(Observer<Weather> observer) {
        String cityName = mSetting.getString(Setting.CITY_NAME,"北京");
        if(cityName != null){
            cityName = cityName.replace("市","")
                    .replace("省", "")
                    .replace("自治区", "")
                    .replace("特别行政区", "")
                    .replace("地区", "")
                    .replace("盟", "");

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
                        public Weather call(WeatherAPI weatherAPI){
                             return weatherAPI.mHeWeatherDataService30.get(0);
                    }
            }).doOnNext(new Action1<Weather>() {
                @Override
                public void call(Weather weather) {
                    //保存 Serializable数据到 缓存中
                    aCache.put("WeatherData",weather,(mSetting.getInt(Setting.AUTO_UPDATE,0)+1)*Setting.ONE_HOUR);
                }
            }).subscribe(observer);
        }
    }
//分享
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        if(platform.getName().equals(SinaWeibo.NAME)){
            handler.sendEmptyMessage(1);
        }else if(platform.getName().equals(ShortMessage.NAME)){
            handler.sendEmptyMessage(2);
        }else if(platform.getName().equals(Wechat.NAME)){
            handler.sendEmptyMessage(3);
        }else if(platform.getName().equals(WechatMoments.NAME)){
            handler.sendEmptyMessage(4);
        }
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable)
    {
        throwable.printStackTrace();
        Message msg = new Message();
        msg.what = 6;
        msg.obj = throwable.getMessage();
        handler.sendMessage(msg);
    }
    @Override
    public void onCancel(Platform platform, int i) {
        handler.sendEmptyMessage(5);
    }
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Snackbar.make(fab, "微博分享成功", Snackbar.LENGTH_LONG).show();
                    break;

                case 2:
                    Snackbar.make(fab, "短信分享成功", Snackbar.LENGTH_LONG).show();
                    break;
                case 3:
                    Snackbar.make(fab, "微信分享成功", Snackbar.LENGTH_LONG).show();
                    break;
                case 4:
                    Snackbar.make(fab, "朋友圈分享成功", Snackbar.LENGTH_LONG).show();
                    break;

                case 5:
                    Snackbar.make(fab, "取消分享", Snackbar.LENGTH_LONG).show();
                    break;
                case 6:
                    Snackbar.make(fab, "分享失败", Snackbar.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        }

    };


    @SuppressLint("HandlerLeak")
    class RefreshHandler extends android.os.Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    refreshLayout.setRefreshing(true);
                    break;
                case 2:
                    if(refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                        if(BaseApplication.isNetworkConnected(MainActivity.this)){
                            Snackbar.make(fab,"加载完毕!",Snackbar.LENGTH_SHORT).show();
                        }else{
                            Snackbar.make(fab,"网络出了问题？",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }

//    private void showFabDialog(){
//        new AlertDialog.Builder(MainActivity.this).setTitle("鸣谢")
//                .setMessage("感谢使用本软件")
//                .setPositiveButton("确定",null)
//                .show();
//    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            if((System.currentTimeMillis() - exitTime) > 2000) {
                Snackbar.make(fab,"再按一次即可退出",Snackbar.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else{
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == 2){
            new RefreshHandler().sendEmptyMessage(1);
            mSetting.putString(Setting.CITY_NAME , data.getStringExtra(Setting.CITY_NAME));
            catchDataByNetWork(observer);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.nav_city:
                startActivityForResult(new Intent(MainActivity.this,ChooseCityActivity.class),1);
                break;
            case R.id.nav_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.nav_tool:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onRefresh() {
        catchDataByNetWork(observer);
    }

    private void location(){
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔时间，单位毫秒
        int tempTime = mSetting.getAutoUpdate();
        if(tempTime == 0){
            tempTime = 10000;
        }

        //设置定位间隔 单位毫秒
        mLocationOption.setInterval(tempTime * Setting.TEN_HOUR);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK();
        if(mLocationClient != null){
            mLocationClient.unRegisterLocationListener(this);
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation != null){
            if(aMapLocation.getErrorCode() == 0){
                //获取当前定位结果来源，详见定位类型表
                aMapLocation.getLocationType();
//                mSetting.putString(Setting.CITY_NAME, aMapLocation.getCity());
                mSetting.setCityName(aMapLocation.getCity());
                isLocation = true;

            }else{
                Log.e("AMapError",
                        "location Error,ErrorCode:"+aMapLocation.getErrorCode() +
                                ",errorInfo:" + aMapLocation.getErrorInfo());
                Snackbar.make(fab,"定位失败，加载默认城市",Snackbar.LENGTH_SHORT).show();
            }
            catchData();
        }
    }

    private void showShare(){
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("天气预报");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本，啦啦啦~");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(this);
    }


}
