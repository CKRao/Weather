package com.example.ckrao.myapplication.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.ckrao.myapplication.Model.DataBean;
import com.example.ckrao.myapplication.MainActivity;
import com.example.ckrao.myapplication.HttpUtility.HttpCallBackListener;
import com.example.ckrao.myapplication.HttpUtility.Httpuility;
import com.example.ckrao.myapplication.Utility.GetWeekOfDate;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by clark on 2016/11/5.
 */
public class AutoUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 2 * 60 * 60 * 1000;//4小时毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
//        Log.i("myck","request");
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String city = prefs.getString("city", "");
        try {
            String address = "https://free-api.heweather.com/v5/weather?city=" + URLEncoder.encode(city, "UTF-8")
                    + "&key=b727f217188c4e8a91ecba4d349c73ff";
            new Httpuility(address, new HttpCallBackListener() {
                @Override
                public void onFinish(String response) {
                    Gson gson = new Gson();
                    DataBean bean = gson.fromJson(response, DataBean.class);
                    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    Date date04 =null;
                    Date date05 =null;
                    Date date06 =null;
                    Date date07 =null;
                    try {
                        date = fmt.parse(bean.getHeWeather5().get(0).getDaily_forecast().get(0).getDate());
                        date04 = fmt.parse(bean.getHeWeather5().get(0).getDaily_forecast().get(3).getDate());
                        date05 = fmt.parse( bean.getHeWeather5().get(0).getDaily_forecast().get(4).getDate());
                        date06 = fmt.parse( bean.getHeWeather5().get(0).getDaily_forecast().get(5).getDate());
                        date07 = fmt.parse(  bean.getHeWeather5().get(0).getDaily_forecast().get(6).getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String CityID = bean.getHeWeather5().get(0).getBasic().getId();
                    String img = "img" + bean.getHeWeather5().get(0).getNow().getCond().getCode();
                    String mCity = bean.getHeWeather5().get(0).getBasic().getCity();
                    String mWeek = GetWeekOfDate.getWeekOfDate(date);
                    String week04 = GetWeekOfDate.getWeekOfDate(date04);
                    String week05 = GetWeekOfDate.getWeekOfDate(date05);
                    String week06 = GetWeekOfDate.getWeekOfDate(date06);
                    String week07 = GetWeekOfDate.getWeekOfDate(date07);
                    String mTemp = bean.getHeWeather5().get(0).getNow().getTmp();
                    String mWeather = bean.getHeWeather5().get(0).getNow().getCond().getTxt();
                    String air_conditioning_tx = bean.getHeWeather5().get(0).getSuggestion().getFlu().getBrf();
                    String air_conditioning_content = bean.getHeWeather5().get(0).getSuggestion().getFlu().getTxt();
                    String sport_tx = bean.getHeWeather5().get(0).getSuggestion().getSport().getBrf();
                    String sport_content = bean.getHeWeather5().get(0).getSuggestion().getSport().getTxt();
                    String ultraviolet_radiation_content = bean.getHeWeather5().get(0).getSuggestion().getUv().getTxt();
                    String ultraviolet_radiation_tx = bean.getHeWeather5().get(0).getSuggestion().getUv().getBrf();
                    String cloth_content = bean.getHeWeather5().get(0).getSuggestion().getDrsg().getTxt();
                    String cloth_tx = bean.getHeWeather5().get(0).getSuggestion().getDrsg().getBrf();
                    String img01 = "img" + bean.getHeWeather5().get(0).getDaily_forecast().get(0).getCond().getCode_d();
                    String img02 = "img" + bean.getHeWeather5().get(0).getDaily_forecast().get(1).getCond().getCode_d();
                    String img03 = "img" + bean.getHeWeather5().get(0).getDaily_forecast().get(2).getCond().getCode_d();
                    String img04 = "img" + bean.getHeWeather5().get(0).getDaily_forecast().get(3).getCond().getCode_d();
                    String img05 = "img" + bean.getHeWeather5().get(0).getDaily_forecast().get(4).getCond().getCode_d();
                    String img06 = "img" + bean.getHeWeather5().get(0).getDaily_forecast().get(5).getCond().getCode_d();
                    String img07 = "img" + bean.getHeWeather5().get(0).getDaily_forecast().get(6).getCond().getCode_d();
                    String temp01 = bean.getHeWeather5().get(0).getDaily_forecast().get(0).getTmp().getMin() +
                            "º～" + bean.getHeWeather5().get(0).getDaily_forecast().get(0).getTmp().getMax() + "º";
                    String temp02 = bean.getHeWeather5().get(0).getDaily_forecast().get(1).getTmp().getMin() +
                            "º～" + bean.getHeWeather5().get(0).getDaily_forecast().get(1).getTmp().getMax() + "º";
                    String temp03 = bean.getHeWeather5().get(0).getDaily_forecast().get(2).getTmp().getMin() +
                            "º～" + bean.getHeWeather5().get(0).getDaily_forecast().get(2).getTmp().getMax() + "º";
                    String temp04 = bean.getHeWeather5().get(0).getDaily_forecast().get(3).getTmp().getMin() +
                            "º～" + bean.getHeWeather5().get(0).getDaily_forecast().get(3).getTmp().getMax() + "º";
                    String temp05 = bean.getHeWeather5().get(0).getDaily_forecast().get(4).getTmp().getMin() +
                            "º～" + bean.getHeWeather5().get(0).getDaily_forecast().get(4).getTmp().getMax() + "º";
                    String temp06 = bean.getHeWeather5().get(0).getDaily_forecast().get(5).getTmp().getMin() +
                            "º～" + bean.getHeWeather5().get(0).getDaily_forecast().get(5).getTmp().getMax() + "º";
                    String temp07 = bean.getHeWeather5().get(0).getDaily_forecast().get(6).getTmp().getMin() +
                            "º～" + bean.getHeWeather5().get(0).getDaily_forecast().get(6).getTmp().getMax() + "º";
                    String rainfall = bean.getHeWeather5().get(0).getDaily_forecast().get(0).getPcpn();
                    String humidity = bean.getHeWeather5().get(0).getDaily_forecast().get(0).getHum();
                    String winddirection = bean.getHeWeather5().get(0).getDaily_forecast().get(0).getWind().getSpd() + "km/h";
                    MainActivity.saveMessage(getApplicationContext(), air_conditioning_tx, air_conditioning_content, sport_tx,
                            sport_content, ultraviolet_radiation_tx, ultraviolet_radiation_content, cloth_tx, cloth_content,
                            mCity, mWeek, mTemp, mWeather, img, img01,img02,img03,temp01, temp02, temp03,
                            CityID, rainfall, humidity, winddirection,week04,week05,week06,week07,img04,img05,img06,img07,
                            temp04,temp05,temp06,temp07);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
