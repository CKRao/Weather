package com.example.ckrao.myapplication;


import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ckrao.myapplication.Service.AutoUpdateService;
import com.example.ckrao.myapplication.HttpUtility.HttpCallBackListener;
import com.example.ckrao.myapplication.HttpUtility.Httpuility;
import com.google.gson.Gson;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView week;
    TextView temp;
    TextView weather;
    TextView mCloth_tx;
    TextView mCloth_content;
    TextView mAir_conditioning_content;
    TextView mAir_conditioning_tx;
    TextView mUltraviolet_radiation_content;
    TextView mUltraviolet_radiation_tx;
    ImageView img_01;
    ImageView img_02;
    ImageView img_03;
    TextView temp_01;
    TextView temp_02;
    TextView temp_03;
    TextView mSport_tx;
    TextView mSport_content;
    RelativeLayout layout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton mFloatingActionButton;
    ImageView bgImg;
    LocationClient mLocationClient;
    BDLocationListener mBDLocationListener = new MyLocationListener();
    Toolbar toolbar;
    private String address;
    private String myCity;
    private Httpuility mHttpuility;
    private List<DataBean> mDataBeanList;
    private Handler mHandler;
    private static boolean isDay;
//    private PullToRefreshView mPullToRefreshView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isChoose", false)) {
            Intent intent = new Intent(MainActivity.this, SelectActivity.class);
            startActivityForResult(intent, 0);
        }
        setContentView(R.layout.coordinatorlayout);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mBDLocationListener);
        determineTheTime();
        initUI();
        initLocation();
        setChange();
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
        showWeather();
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator.ofFloat(mFloatingActionButton, "rotation", 0F, 360F).
                        setDuration(800).
                        start();
                refreshData();
            }
        });
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                CurrentWeatherBean bean = (CurrentWeatherBean) msg.obj;
                setTheInfo(bean);
            }
        };
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (mLocationClient.isStarted()){
                   mLocationClient.stop();
               }
                mLocationClient.start();
            }
        });
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        Log.i("initLocation","init");
        mLocationClient.setLocOption(option);
        Log.i("initLocation"," mLocationClient.setLocOption(option);");
    }

    private void determineTheTime() {
        long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int am_pm = calendar.get(Calendar.AM_PM);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int minute_of_the_day = hour * 60 + minute;
        final int startTime = 6 * 60;
        final int endTime = 12 * 60;
        if (am_pm == 0) {
            if (minute_of_the_day >= startTime && minute_of_the_day <= endTime) {
                isDay = true;
            } else {
                isDay = false;
            }
        } else {
            if (minute_of_the_day >= startTime && minute_of_the_day <= endTime) {
                isDay = false;
            } else {
                isDay = true;
            }
        }

    }

    private void initUI() {
        bgImg = (ImageView) findViewById(R.id.id_bg_img);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.id_float_bt);
        layout = (RelativeLayout) findViewById(R.id.id_layout);
        temp = (TextView) findViewById(R.id.temp);
        week = (TextView) findViewById(R.id.week);
        weather = (TextView) findViewById(R.id.weather);
        mCloth_tx = (TextView) findViewById(R.id.id_cloth_tx);
        mCloth_content = (TextView) findViewById(R.id.id_cloth_content);
        mSport_tx = (TextView) findViewById(R.id.id_sport_tx);
        mSport_content = (TextView) findViewById(R.id.id_sport_content);
        mUltraviolet_radiation_tx = (TextView) findViewById(R.id.id_ultraviolet_radiation_tx);
        mUltraviolet_radiation_content = (TextView) findViewById(R.id.id_ultraviolet_radiation_content);
        mAir_conditioning_tx = (TextView) findViewById(R.id.id_air_conditioning_tx);
        mAir_conditioning_content = (TextView) findViewById(R.id.id_air_conditioning_content);

        img_01 = (ImageView) findViewById(R.id.id_wea_01);
        img_02 = (ImageView) findViewById(R.id.id_wea_02);
        img_03 = (ImageView) findViewById(R.id.id_wea_03);

        temp_01 = (TextView) findViewById(R.id.temp_01);
        temp_02 = (TextView) findViewById(R.id.temp_02);
        temp_03 = (TextView) findViewById(R.id.temp_03);
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.id_collapsing);
//        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pullToRefreshView);

    }

    private void setChange() {
        if (isDay) {
            bgImg.setImageResource(R.drawable.background1);
        } else {
            layout.setBackgroundColor(Color.DKGRAY);
            weather.setTextColor(Color.WHITE);
            temp.setTextColor(Color.WHITE);
            week.setTextColor(Color.WHITE);
        }

    }

    private void setTheInfo(CurrentWeatherBean bean) {
        collapsingToolbarLayout.setTitle(bean.getCity());
        week.setText(bean.getWeek());
        temp.setText(bean.getTemp() + "℃");
        weather.setText(bean.getWeather());
        mCloth_tx.setText("穿衣指数——" + bean.getCloth_tx());
        mCloth_content.setText(bean.getCloth_content());
        mSport_tx.setText("运动指数——" + bean.getSport_tx());
        mSport_content.setText(bean.getSport_content());
        mAir_conditioning_tx.setText("感冒指数——" + bean.getAir_conditioning_tx());
        mAir_conditioning_content.setText(bean.getAir_conditioning_content());
        mUltraviolet_radiation_tx.setText("紫外线指数——" + bean.getUltraviolet_radiation_tx());
        mUltraviolet_radiation_content.setText(bean.getUltraviolet_radiation_content());
        img_01.setImageResource(getResource(bean.getImg_01()));
        img_02.setImageResource(getResource(bean.getImg_02()));
        img_03.setImageResource(getResource(bean.getImg_03()));
        temp_01.setText(bean.getTemp_01());
        temp_02.setText(bean.getTemp_02());
        temp_03.setText(bean.getTemp_03());
    }

    //通过图片名获取资源ID
    private int getResource(String img) {
        Context ctx = getApplicationContext();
        int resId = getResources().getIdentifier(img, "drawable", ctx.getPackageName());
        return resId;
    }

    private void refreshData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        myCity = prefs.getString("city", "");
        try {
            address = "https://free-api.heweather.com/v5/weather?city=" + URLEncoder.encode(myCity, "UTF-8")
                    + "&key=b727f217188c4e8a91ecba4d349c73ff";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mHttpuility = new Httpuility(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                analysis(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                       ToastUtil.showToast(getApplicationContext(),"更新成功");
                        Snackbar.make(bgImg, "更新成功", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(bgImg, "更新失败", Snackbar.LENGTH_SHORT).show();
//                        ToastUtil.showToast(getApplicationContext(),"更新失败");
                    }
                });
            }
        });
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        collapsingToolbarLayout.setTitle(prefs.getString("city", "SpeedWeather"));
        week.setText(prefs.getString("week", "未知"));
        temp.setText(prefs.getString("temp", "0") + "℃");
        weather.setText(prefs.getString("weather", "未知"));
        mCloth_tx.setText("穿衣指数——" + prefs.getString("cloth_tx", ""));
        mCloth_content.setText(prefs.getString("cloth_content", ""));
        mSport_tx.setText("运动指数——" + prefs.getString("sport_tx", ""));
        mSport_content.setText(prefs.getString("sport_content", ""));
        mAir_conditioning_tx.setText("空调指数——" + prefs.getString("air_conditioning_tx", ""));
        mAir_conditioning_content.setText(prefs.getString("air_conditioning_content", ""));
        mUltraviolet_radiation_tx.setText("紫外线指数——" + prefs.getString("ultraviolet_radiation_tx", ""));
        mUltraviolet_radiation_content.setText(prefs.getString("ultraviolet_radiation_content", ""));
        img_01.setImageResource(getResource(prefs.getString("img01", "img99")));
        img_02.setImageResource(getResource(prefs.getString("img02", "img99")));
        img_03.setImageResource(getResource(prefs.getString("img03", "img99")));
        temp_01.setText(prefs.getString("temp01", ""));
        temp_02.setText(prefs.getString("temp02", ""));
        temp_03.setText(prefs.getString("temp03", ""));

    }

    public static void saveMessage(Context context, String air_conditioning_tx,
                                   String air_conditioning_content, String sport_tx,
                                   String sport_content, String ultraviolet_radiation_tx,
                                   String ultraviolet_radiation_content, String cloth_tx,
                                   String cloth_content, String mCity, String mWeek, String mTemp,
                                   String mWeather, String img, String img01, String img02,
                                   String img03, String temp01, String temp02, String temp03, String cityID) {
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(context).edit();
//        editor.putString("cityID", cityID);
        editor.putString("city", mCity);
        editor.putString("week", mWeek);
        editor.putString("temp", mTemp);
        editor.putString("weather", mWeather);
        editor.putString("city", mCity);
        editor.putString("air_conditioning_tx", air_conditioning_tx);
        editor.putString("air_conditioning_content", air_conditioning_content);
        editor.putString("sport_tx", sport_tx);
        editor.putString("sport_content", sport_content);
        editor.putString("ultraviolet_radiation_tx", ultraviolet_radiation_tx);
        editor.putString("ultraviolet_radiation_content", ultraviolet_radiation_content);
        editor.putString("cloth_tx", cloth_tx);
        editor.putString("cloth_content", cloth_content);
        editor.putString("img", img);
        editor.putString("img01", img01);
        editor.putString("img02", img02);
        editor.putString("img03", img03);
        editor.putString("temp01", temp01);
        editor.putString("temp02", temp02);
        editor.putString("temp03", temp03);
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            myCity = String.valueOf(data.getStringExtra("city_name"));
            Snackbar.make(layout, myCity,
                    Snackbar.LENGTH_LONG).show();
            try {
                address = "https://free-api.heweather.com/v5/weather?city=" + URLEncoder.encode(myCity, "UTF-8")
                        + "&key=b727f217188c4e8a91ecba4d349c73ff";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mHttpuility = new Httpuility(address, new HttpCallBackListener() {
                @Override
                public void onFinish(String response) {
                    try {
                        analysis(response);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
    }

    public void analysis(String response) {
        Gson gson = new Gson();
        DataBean bean = gson.fromJson(response, DataBean.class);
        String CityID = bean.getHeWeather5().get(0).getBasic().getId();
        String img = "img" + bean.getHeWeather5().get(0).getNow().getCond().getCode();
        String mCity = bean.getHeWeather5().get(0).getBasic().getCity();
        String mWeek = bean.getHeWeather5().get(0).getDaily_forecast().get(0).getDate().substring(5);
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
        String img03 = "img" + bean.getHeWeather5().get(0).getDaily_forecast().get(2).getCond().getCode_d();
        String img02 = "img" + bean.getHeWeather5().get(0).getDaily_forecast().get(1).getCond().getCode_d();
        String img01 = "img" + bean.getHeWeather5().get(0).getDaily_forecast().get(0).getCond().getCode_d();
        String temp01 = bean.getHeWeather5().get(0).getDaily_forecast().get(0).getTmp().getMin() +
                "º～" + bean.getHeWeather5().get(0).getDaily_forecast().get(0).getTmp().getMax() + "º";
        String temp02 = bean.getHeWeather5().get(0).getDaily_forecast().get(1).getTmp().getMin() +
                "º～" + bean.getHeWeather5().get(0).getDaily_forecast().get(1).getTmp().getMax() + "º";
        String temp03 = bean.getHeWeather5().get(0).getDaily_forecast().get(2).getTmp().getMin() +
                "º～" + bean.getHeWeather5().get(0).getDaily_forecast().get(2).getTmp().getMax() + "º";
        saveMessage(getApplicationContext(), air_conditioning_tx, air_conditioning_content, sport_tx,
                sport_content, ultraviolet_radiation_tx, ultraviolet_radiation_content, cloth_tx, cloth_content,
                mCity, mWeek, mTemp, mWeather, img, img01, img02, img03, temp01, temp02, temp03, CityID);
        CurrentWeatherBean bean1 = new CurrentWeatherBean();
        bean1.setCity(mCity);
        bean1.setWeek(mWeek);
        bean1.setTemp(mTemp);
        bean1.setWeather(mWeather);
        bean1.setAir_conditioning_tx(air_conditioning_tx);
        bean1.setAir_conditioning_content(air_conditioning_content);
        bean1.setSport_tx(sport_tx);
        bean1.setSport_content(sport_content);
        bean1.setUltraviolet_radiation_tx(ultraviolet_radiation_tx);
        bean1.setUltraviolet_radiation_content(ultraviolet_radiation_content);
        bean1.setCloth_tx(cloth_tx);
        bean1.setCloth_content(cloth_content);
        bean1.setImg(img);
        bean1.setImg_01(img01);
        bean1.setImg_02(img02);
        bean1.setImg_03(img03);
        bean1.setTemp_01(temp01);
        bean1.setTemp_02(temp02);
        bean1.setTemp_03(temp03);
        Message message = new Message();
        message.obj = bean1;
        mHandler.sendMessage(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, SelectActivity.class);
        switch (item.getItemId()) {
            case R.id.search:
                startActivityForResult(intent, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation( BDLocation location) {
           setTheLocalCity(location.getCity().replace("市",""));
            Log.i("clarkRao",location.getCity());

        }

    }


    private void setTheLocalCity(String city) {
        Snackbar.make(layout,"当前城市："+city,
                Snackbar.LENGTH_LONG).show();
        try {
            address = "https://free-api.heweather.com/v5/weather?city=" + URLEncoder.encode(city, "UTF-8")
                    + "&key=b727f217188c4e8a91ecba4d349c73ff";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mHttpuility = new Httpuility(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    analysis(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        mLocationClient.stop();
    }
}

