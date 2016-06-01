package com.example.administrator.terrificweather.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.terrificweather.common.AnimRecyclerViewAdapter;
import com.example.administrator.terrificweather.model.Weather;
import com.example.administrator.terrificweather.ui.setting.Setting;
import com.example.administrator.terrificweather.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2016/4/24.
 */
public class WeatherAdapter extends AnimRecyclerViewAdapter<RecyclerView.ViewHolder> {

    private Context context;
    private final int TYPE_ONE = 0;
    private final int TYPE_TWO = 1;
    private final int TYPE_THREE = 2;
    private final int TYPE_FOUR = 3;


    private Weather weatherData;
    private Setting setting;

    public WeatherAdapter(Context context,Weather weatherData){
        this.context = context;
        this.weatherData = weatherData;

        setting = Setting.getsInstance();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == TYPE_ONE){
            return TYPE_ONE;
        }
        if(position == TYPE_TWO){
            return TYPE_TWO;
        }
        if(position == TYPE_THREE){
            return TYPE_THREE;
        }
        if(position == TYPE_FOUR){
            return TYPE_FOUR;
        }

        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ONE){
            return new NowWeatherViewHolder(LayoutInflater.from(context).inflate(R.layout.item_temperature,parent,false));

        }
        if(viewType == TYPE_TWO){
            return new ForecastViewHolder(LayoutInflater.from(context).inflate(R.layout.item_forecast, parent, false));

        }
        if(viewType == TYPE_THREE){
            return new SuggestionViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.item_suggesstion, parent, false));
        }
        if(viewType == TYPE_FOUR){
            return new HoursWeatherViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.item_hour_info, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NowWeatherViewHolder){
            ((NowWeatherViewHolder) holder).tempFlu.setText(weatherData.now.tmp + "℃");
            ((NowWeatherViewHolder) holder).maxTemp.setText("↑ " + weatherData.dailyForecast.get(0).tmp.max + "°");
            ((NowWeatherViewHolder) holder).minTemp.setText("↓ " + weatherData.dailyForecast.get(0).tmp.min + "°" );
            if(weatherData.aqi != null){
                ((NowWeatherViewHolder) holder).tempPm.setText("PM25:" + weatherData.aqi.city.pm25);
                ((NowWeatherViewHolder) holder).tempQuality.setText("空气质量:" + weatherData.aqi.city.qlty);
            }
            Glide.with(context).load(setting.getInt(weatherData.now.cond.txt,R.mipmap.none))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(((NowWeatherViewHolder) holder).weatherIcon);


        }
        if(holder instanceof HoursWeatherViewHolder){
            for(int i=0;i<weatherData.hourlyForecast.size();i++){
                String mDate = weatherData.hourlyForecast.get(i).date;
                ((HoursWeatherViewHolder) holder).mClock[i].setText(mDate.substring(mDate.length()-5,mDate.length()));
                ((HoursWeatherViewHolder) holder).mTemp[i].setText(weatherData.hourlyForecast.get(i).tmp + "°");
                ((HoursWeatherViewHolder) holder).mHumidity[i].setText(weatherData.hourlyForecast.get(i).hum+"%");
                ((HoursWeatherViewHolder) holder).mWind[i].setText(weatherData.hourlyForecast.get(i).wind.spd + "Km");

            }
        }

        if(holder instanceof SuggestionViewHolder){
            ((SuggestionViewHolder) holder).clothBrief.setText("穿衣指数---" + weatherData.suggestion.drsg.brf);
            ((SuggestionViewHolder) holder).clothTxt.setText(weatherData.suggestion.drsg.txt);

            ((SuggestionViewHolder) holder).sportBrief.setText("运动指数---" + weatherData.suggestion.sport.brf);
            ((SuggestionViewHolder) holder).sportTxt.setText(weatherData.suggestion.sport.txt);

            ((SuggestionViewHolder) holder).travelBrief.setText("旅游指数---" + weatherData.suggestion.trav.brf);
            ((SuggestionViewHolder) holder).travelTxt.setText(weatherData.suggestion.trav.txt);

            ((SuggestionViewHolder) holder).fluBrief.setText("感冒指数---" + weatherData.suggestion.flu.brf);
            ((SuggestionViewHolder) holder).fluTxt.setText(weatherData.suggestion.flu.txt);

            ((SuggestionViewHolder) holder).washcarBrief.setText("洗车指数---" + weatherData.suggestion.cw.brf);
            ((SuggestionViewHolder) holder).washcarTxt.setText(weatherData.suggestion.cw.txt);

            ((SuggestionViewHolder) holder).lightBrief.setText("紫外线指数---" + weatherData.suggestion.uv.brf);
            ((SuggestionViewHolder) holder).lightTxt.setText(weatherData.suggestion.uv.txt);
        }

        if(holder instanceof ForecastViewHolder){
            ((ForecastViewHolder) holder).forecastDate[0].setText("今日");
            ((ForecastViewHolder) holder).forecastDate[1].setText("明日");
            for(int i=0;i<weatherData.dailyForecast.size();i++){
                if(i > 1){
                    try {
                        ((ForecastViewHolder) holder).forecastDate[i].setText(dayForWeek(weatherData.dailyForecast.get(i).date));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Glide.with(context).load(setting.getInt(weatherData.dailyForecast.get(i).cond.txtD,R.mipmap.none))
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(((ForecastViewHolder) holder).forecastIcon[i]);

                ((ForecastViewHolder) holder).forecastTemp[i].setText(weatherData.dailyForecast.get(i).tmp.min + "°" +
                weatherData.dailyForecast.get(i).tmp.max+ "°");
                ((ForecastViewHolder) holder).forecastTxt[i].setText(weatherData.dailyForecast.get(i).cond.txtD + "。 最高" +
                weatherData.dailyForecast.get(i).tmp.max + "℃。 " +
                weatherData.dailyForecast.get(i).wind.sc + " " +
                weatherData.dailyForecast.get(i).wind.dir + " "+
                weatherData.dailyForecast.get(i).wind.spd + "km/h." + "降水几率" + "" +
                weatherData.dailyForecast.get(i).pop + "%.");
            }
        }
        showCardAnim(holder.itemView,position);

    }

    @Override
    public int getItemCount() {
        return 4;
    }

//    判断星期几

    public static String dayForWeek(String pTime) throws Exception{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(simpleDateFormat.parse(pTime));
        int dayForWeek = 0;
        String week = "";
        dayForWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayForWeek){
            case 1:
                week = "星期日";
                break;
            case 2:
                week = "星期一";
                break;
            case 3:
                week = "星期二";
                break;
            case 4:
                week = "星期三";
                break;
            case 5:
                week = "星期四";
                break;
            case 6:
                week = "星期五";
                break;
            case 7:
                week = "星期六";
                break;
        }
        return week;
    }

//    当天天气情况

    class NowWeatherViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private ImageView weatherIcon;
        private TextView tempFlu;
        private TextView maxTemp;
        private TextView minTemp;

        private TextView tempPm;
        private TextView tempQuality;


        public NowWeatherViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
            weatherIcon = (ImageView) itemView.findViewById(R.id.weather_icon);
            tempFlu = (TextView) itemView.findViewById(R.id.temp_flu);
            maxTemp = (TextView) itemView.findViewById(R.id.temp_max);
            minTemp = (TextView) itemView.findViewById(R.id.temp_min);

            tempPm = (TextView) itemView.findViewById(R.id.temp_pm);
            tempQuality = (TextView) itemView.findViewById(R.id.temp_quality);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(listener != null){
//                        listener.onItemClick(weatherData);
//                    }
//                }
//            });

        }

    }
//    当日小时预告
    class HoursWeatherViewHolder extends RecyclerView.ViewHolder{

    private LinearLayout itemHourInfoLinearlayout;
    private TextView[] mClock = new TextView[weatherData.hourlyForecast.size()];
    private TextView[] mTemp = new TextView[weatherData.hourlyForecast.size()];
    private TextView[] mHumidity = new TextView[weatherData.hourlyForecast.size()];
    private TextView[] mWind = new TextView[weatherData.hourlyForecast.size()];

    public HoursWeatherViewHolder(View itemView) {
        super(itemView);
        itemHourInfoLinearlayout = (LinearLayout) itemView.findViewById(R.id.item_hour_info_linearlayout);

        for(int i=0;i<weatherData.hourlyForecast.size();i++){
            View view = View.inflate(context,R.layout.item_hour_info_line,null);
            mClock[i] = (TextView) view.findViewById(R.id.icon_clock);
            mTemp[i] = (TextView) view.findViewById(R.id.icon_temp);
            mHumidity[i] = (TextView) view.findViewById(R.id.icon_humidity);
            mWind[i] = (TextView) view.findViewById(R.id.icon_wind);
            itemHourInfoLinearlayout.addView(view);
        }


    }
}
//建议
    class SuggestionViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;
        private TextView clothBrief;
        private TextView clothTxt;
        private TextView sportBrief;
        private TextView sportTxt;
        private TextView travelBrief;
        private TextView travelTxt;
        private TextView fluBrief;
        private TextView fluTxt;
        private TextView washcarBrief;
        private TextView washcarTxt;
        private TextView lightBrief;
        private TextView lightTxt;

        public SuggestionViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardview);
            clothBrief = (TextView) itemView.findViewById(R.id.cloth_wear);
            clothTxt = (TextView) itemView.findViewById(R.id.cloth_txt);
            sportBrief = (TextView) itemView.findViewById(R.id.sport_brief);
            sportTxt = (TextView) itemView.findViewById(R.id.sport_txt);
            travelBrief = (TextView) itemView.findViewById(R.id.travel_brief);
            travelTxt = (TextView) itemView.findViewById(R.id.travel_txt);
            fluBrief = (TextView) itemView.findViewById(R.id.flu_brief);
            fluTxt = (TextView) itemView.findViewById(R.id.flu_txt);
            washcarBrief = (TextView) itemView.findViewById(R.id.washcar_brief);
            washcarTxt = (TextView) itemView.findViewById(R.id.washcar_txt);
            lightBrief = (TextView) itemView.findViewById(R.id.light_brief);
            lightTxt = (TextView) itemView.findViewById(R.id.light_txt);
        }
    }

//    未来几天的天气
class ForecastViewHolder extends RecyclerView.ViewHolder {
    private LinearLayout forecastLinear;
    private TextView[] forecastDate = new TextView[weatherData.dailyForecast.size()];
    private TextView[] forecastTemp = new TextView[weatherData.dailyForecast.size()];
    private TextView[] forecastTxt = new TextView[weatherData.dailyForecast.size()];
    private ImageView[] forecastIcon = new ImageView[weatherData.dailyForecast.size()];


    public ForecastViewHolder(View itemView) {
        super(itemView);
        forecastLinear = (LinearLayout) itemView.findViewById(R.id.forecast_linear);
        for (int i = 0; i < weatherData.dailyForecast.size(); i++) {
            View view = View.inflate(context, R.layout.item_forcast_line, null);
            forecastDate[i] = (TextView) view.findViewById(R.id.forecast_date);
            forecastTemp[i] = (TextView) view.findViewById(R.id.forecast_temp);
            forecastTxt[i] = (TextView) view.findViewById(R.id.forecast_text);
            forecastIcon[i] = (ImageView) view.findViewById(R.id.forecast_image);
            forecastLinear.addView(view);
        }

    }
}



}
