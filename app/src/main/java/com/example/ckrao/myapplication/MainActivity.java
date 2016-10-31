package com.example.ckrao.myapplication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ckrao.myapplication.httpuility.HttpCallBackListener;
import com.example.ckrao.myapplication.httpuility.Httpuility;
import com.google.gson.Gson;
import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private String address;
    private String myCity_id;
    private TextView city, week, temp, weather;
    private TextView mCloth_tx, mCloth_content;
    private TextView mAir_conditioning_content, mAir_conditioning_tx;
    private TextView mUltraviolet_radiation_content, mUltraviolet_radiation_tx;
    private ImageView img, img_01, img_02, img_03;
    private TextView temp_01, temp_02, temp_03;
    private TextView mSport_tx, mSport_content;
    private Httpuility mHttpuility;
    private List<DataBean> mDataBeanList;
    private Handler mHandler;
    private static boolean show = false;
    private ImageButton mSearchButton;
    private PullToRefreshView mPullToRefreshView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_main);
        initUI();
        showWeather();
        //设置刷新监听者
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                CurrentWeatherBean bean = (CurrentWeatherBean) msg.obj;
                setTheInfo(bean);
            }
        };

    }

    private void setTheInfo(CurrentWeatherBean bean) {
        city.setText(bean.getCity());
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
        img.setImageResource(getResource(bean.getImg()));
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

    private void initUI() {
        city = (TextView) findViewById(R.id.city);
        temp = (TextView) findViewById(R.id.temp);
        week = (TextView) findViewById(R.id.week);
        weather = (TextView) findViewById(R.id.weather);
        mSearchButton = (ImageButton) findViewById(R.id.search);
        mCloth_tx = (TextView) findViewById(R.id.id_cloth_tx);
        mCloth_content = (TextView) findViewById(R.id.id_cloth_content);
        mSport_tx = (TextView) findViewById(R.id.id_sport_tx);
        mSport_content = (TextView) findViewById(R.id.id_sport_content);
        mUltraviolet_radiation_tx = (TextView) findViewById(R.id.id_ultraviolet_radiation_tx);
        mUltraviolet_radiation_content = (TextView) findViewById(R.id.id_ultraviolet_radiation_content);
        mAir_conditioning_tx = (TextView) findViewById(R.id.id_air_conditioning_tx);
        mAir_conditioning_content = (TextView) findViewById(R.id.id_air_conditioning_content);

        img = (ImageView) findViewById(R.id.img);
        img_01 = (ImageView) findViewById(R.id.id_wea_01);
        img_02 = (ImageView) findViewById(R.id.id_wea_02);
        img_03 = (ImageView) findViewById(R.id.id_wea_03);

        temp_01 = (TextView) findViewById(R.id.temp_01);
        temp_02 = (TextView) findViewById(R.id.temp_02);
        temp_03 = (TextView) findViewById(R.id.temp_03);

        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pullToRefreshView);
    }

    private void refreshData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        myCity_id = prefs.getString("cityID","");
        try {
            address = "https://api.heweather.com/x3/weather?cityid=" + URLEncoder.encode(myCity_id, "UTF-8")
                    + "&key=b727f217188c4e8a91ecba4d349c73ff";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mHttpuility = new Httpuility(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                analysis(response);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

//    private void analysisJson(String response) throws JSONException {
//        JSONObject object = new JSONObject(response);
//        JSONObject result = object.getJSONObject("result");
//        String img = "img" + result.getString("img");
//        String mCity = result.getString("city");
//        String mWeek = result.getString("week");
//        String mTemp = result.getString("temp");
//        String mWeather = result.getString("weather");
//        String air_conditioning_tx = null;
//        String air_conditioning_content = null;
//        String sport_tx = null;
//        String sport_content = null;
//        String ultraviolet_radiation_content = null;
//        String ultraviolet_radiation_tx = null;
//        String cloth_content = null;
//        String cloth_tx = null;
//        String img03 = null;
//        String img02 = null;
//        String img01 = null;
//        String temp01 = null;
//        String temp02 = null;
//        String temp03 = null;
//        JSONArray index = result.getJSONArray("index");
//        for (int i = 0; i < index.length(); i++) {
//            JSONObject object1 = index.getJSONObject(i);
//            switch (object1.getString("iname")) {
//                case "空调指数":
//                    air_conditioning_tx = object1.getString("ivalue");
//                    air_conditioning_content = object1.getString("detail");
//                    break;
//                case "运动指数":
//                    sport_tx = object1.getString("ivalue");
//                    sport_content = object1.getString("detail");
//                    break;
//                case "紫外线指数":
//                    ultraviolet_radiation_tx = object1.getString("ivalue");
//                    ultraviolet_radiation_content = object1.getString("detail");
//                    break;
//                case "穿衣指数":
//                    cloth_tx = object1.getString("ivalue");
//                    cloth_content = object1.getString("detail");
//                    break;
//                default:
//                    break;
//            }
//        }
//        JSONArray daily = result.getJSONArray("daily");
//        for (int i = 1; i <= 3; i++) {
//            JSONObject jsonObject = daily.getJSONObject(i);
//            String date = jsonObject.getString("date").substring(5);
//            JSONObject night = jsonObject.getJSONObject("night");
//            JSONObject day = jsonObject.getJSONObject("day");
//        }
//        CurrentWeatherBean bean = new CurrentWeatherBean();
//        bean.setCity(mCity);
//        bean.setWeek(mWeek);
//        bean.setTemp(mTemp);
//        bean.setWeather(mWeather);
//        bean.setAir_conditioning_tx(air_conditioning_tx);
//        bean.setAir_conditioning_content(air_conditioning_content);
//        bean.setSport_tx(sport_tx);
//        bean.setSport_content(sport_content);
//        bean.setUltraviolet_radiation_tx(ultraviolet_radiation_tx);
//        bean.setUltraviolet_radiation_content(ultraviolet_radiation_content);
//        bean.setCloth_tx(cloth_tx);
//        bean.setCloth_content(cloth_content);
//        bean.setImg(img);
//        bean.setImg_01(img01);
//        bean.setImg_02(img02);
//        bean.setImg_03(img03);
//        bean.setTemp_01(temp01);
//        bean.setTemp_02(temp02);
//        bean.setTemp_03(temp03);
//        saveMessage(getApplicationContext(), air_conditioning_tx, air_conditioning_content, sport_tx,
//                sport_content, ultraviolet_radiation_tx, ultraviolet_radiation_content, cloth_tx, cloth_content,
//                mCity, mWeek, mTemp, mWeather, img, img01, img02, img03, temp01, temp02, temp03);
//        Message message = new Message();
//        message.obj = bean;
//        mHandler.sendMessage(message);
//    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        city.setText(prefs.getString("city", "未知"));
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
        img.setImageResource(getResource(prefs.getString("img", "img99")));
        img_01.setImageResource(getResource(prefs.getString("img01", "img99")));
        img_02.setImageResource(getResource(prefs.getString("img02", "img99")));
        img_03.setImageResource(getResource(prefs.getString("img03", "img99")));
        temp_01.setText(prefs.getString("temp01", ""));
        temp_02.setText(prefs.getString("temp02", ""));
        temp_03.setText(prefs.getString("temp03", ""));
    }

    private void saveMessage(Context context, String air_conditioning_tx,
                             String air_conditioning_content, String sport_tx,
                             String sport_content, String ultraviolet_radiation_tx,
                             String ultraviolet_radiation_content, String cloth_tx,
                             String cloth_content, String mCity, String mWeek, String mTemp,
                             String mWeather, String img, String img01, String img02,
                             String img03, String temp01, String temp02, String temp03,String cityID) {
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(context).edit();
        editor.putString("cityID",cityID);
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
            myCity_id = String.valueOf(data.getStringExtra("city_id"));
            try {
                address = "https://api.heweather.com/x3/weather?cityid=" + URLEncoder.encode(myCity_id, "UTF-8")
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

    private void analysis(String responce) {
        responce = responce.replace(" ", "").replace("3.0", "");
        Gson gson = new Gson();
        DataBean bean = gson.fromJson(responce, DataBean.class);
        String CityID = bean.getHeWeatherdataservice().get(0).getBasic().getId();
        String img = "img" + bean.getHeWeatherdataservice().get(0).getNow().getCond().getCode();
        String mCity = bean.getHeWeatherdataservice().get(0).getBasic().getCity();
        String mWeek = bean.getHeWeatherdataservice().get(0).getDaily_forecast().get(0).getDate().substring(5);
        String mTemp = bean.getHeWeatherdataservice().get(0).getNow().getTmp();
        String mWeather = bean.getHeWeatherdataservice().get(0).getNow().getCond().getTxt();
        String air_conditioning_tx = bean.getHeWeatherdataservice().get(0).getSuggestion().getFlu().getBrf();
        String air_conditioning_content = bean.getHeWeatherdataservice().get(0).getSuggestion().getFlu().getTxt();
        String sport_tx = bean.getHeWeatherdataservice().get(0).getSuggestion().getSport().getBrf();
        String sport_content = bean.getHeWeatherdataservice().get(0).getSuggestion().getSport().getTxt();
        String ultraviolet_radiation_content = bean.getHeWeatherdataservice().get(0).getSuggestion().getUv().getTxt();
        String ultraviolet_radiation_tx = bean.getHeWeatherdataservice().get(0).getSuggestion().getUv().getBrf();
        String cloth_content = bean.getHeWeatherdataservice().get(0).getSuggestion().getDrsg().getTxt();
        String cloth_tx = bean.getHeWeatherdataservice().get(0).getSuggestion().getDrsg().getBrf();
        String img03 = "img" + bean.getHeWeatherdataservice().get(0).getDaily_forecast().get(2).getCond().getCode_n();
        String img02 = "img" + bean.getHeWeatherdataservice().get(0).getDaily_forecast().get(1).getCond().getCode_n();
        String img01 = "img" + bean.getHeWeatherdataservice().get(0).getDaily_forecast().get(0).getCond().getCode_n();
        String temp01 = bean.getHeWeatherdataservice().get(0).getDaily_forecast().get(0).getTmp().getMin()+
                "º～"+bean.getHeWeatherdataservice().get(0).getDaily_forecast().get(0).getTmp().getMax()+"º";
        String temp02 = bean.getHeWeatherdataservice().get(0).getDaily_forecast().get(1).getTmp().getMin()+
                "º～"+bean.getHeWeatherdataservice().get(0).getDaily_forecast().get(1).getTmp().getMax()+"º";
        String temp03 = bean.getHeWeatherdataservice().get(0).getDaily_forecast().get(2).getTmp().getMin()+
                "º～"+bean.getHeWeatherdataservice().get(0).getDaily_forecast().get(2).getTmp().getMax()+"º";
        saveMessage(getApplicationContext(), air_conditioning_tx, air_conditioning_content, sport_tx,
                sport_content, ultraviolet_radiation_tx, ultraviolet_radiation_content, cloth_tx, cloth_content,
                mCity, mWeek, mTemp, mWeather, img, img01, img02, img03, temp01, temp02, temp03, CityID );
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
}
