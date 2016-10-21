package com.example.ckrao.myapplication;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ckrao.myapplication.httpuility.HttpCallBackListener;
import com.example.ckrao.myapplication.httpuility.Httpuility;
import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private String address;
    private String dizhi;
    private TextView city, week, temp, weather;
    private TextView mCloth_tx, mCloth_content;
    private TextView mAir_conditioning_content, mAir_conditioning_tx;
    private TextView mUltraviolet_radiation_content, mUltraviolet_radiation_tx;
    private TextView mSport_tx, mSport_content;
    private Httpuility mHttpuility;
    private List<DataBean> mDataBeanList;
    private Handler mHandler;
    private static boolean show = false;
    private EditText mText;
    private Button mSearchButton;
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
                dizhi = String.valueOf(mText.getText());
                mText.setText("");
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                try {
                    address = "http://api.jisuapi.com/weather/query?appkey=b968e6d10975a8c5&city="
                            + URLEncoder.encode(dizhi, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mHttpuility = new Httpuility(address, new HttpCallBackListener() {


                    @Override
                    public void onFinish(String response) {
                        try {
                            JSONObject object = new JSONObject(response);

                            JSONObject result = object.getJSONObject("result");
                            String mCity = result.getString("city");
                            String mWeek = result.getString("week");
                            String mTemp = result.getString("temp");
                            String mWeather = result.getString("weather");
                            String air_conditioning_tx = null;
                            String air_conditioning_content = null;
                            String sport_tx = null;
                            String sport_content = null;
                            String ultraviolet_radiation_content = null;
                            String ultraviolet_radiation_tx = null;
                            String cloth_content = null;
                            String cloth_tx = null;
                            JSONArray index = result.getJSONArray("index");
                            for (int i = 0; i < index.length(); i++) {
                                JSONObject object1 = index.getJSONObject(i);
                                switch (object1.getString("iname")) {
                                    case "空调指数":
                                        air_conditioning_tx = object1.getString("ivalue");
                                        air_conditioning_content = object1.getString("detail");
                                        break;
                                    case "运动指数":
                                        sport_tx = object1.getString("ivalue");
                                        sport_content = object1.getString("detail");
                                        break;
                                    case "紫外线指数":
                                        ultraviolet_radiation_tx = object1.getString("ivalue");
                                        ultraviolet_radiation_content = object1.getString("detail");
                                        break;
                                    case "穿衣指数":
                                        cloth_tx = object1.getString("ivalue");
                                        cloth_content = object1.getString("detail");
                                        break;
                                    default:
                                        break;
                                }
                            }
                            CurrentWeatherBean bean = new CurrentWeatherBean();
                            bean.setCity(mCity);
                            bean.setWeek(mWeek);
                            bean.setTemp(mTemp);
                            bean.setWeather(mWeather);
                            bean.setAir_conditioning_tx(air_conditioning_tx);
                            bean.setAir_conditioning_content(air_conditioning_content);
                            bean.setSport_tx(sport_tx);
                            bean.setSport_content(sport_content);
                            bean.setUltraviolet_radiation_tx(ultraviolet_radiation_tx);
                            bean.setUltraviolet_radiation_content(ultraviolet_radiation_content);
                            bean.setCloth_tx(cloth_tx);
                            bean.setCloth_content(cloth_content);
                            showMessage(getApplicationContext(), air_conditioning_tx, air_conditioning_content, sport_tx,
                                    sport_content, ultraviolet_radiation_tx, ultraviolet_radiation_content, cloth_tx, cloth_content,
                                    mCity, mWeek, mTemp, mWeather);
                            Message message = new Message();
                            message.obj = bean;
                            mHandler.sendMessage(message);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                CurrentWeatherBean bean = (CurrentWeatherBean) msg.obj;
                city.setText(bean.getCity());
                week.setText(bean.getWeek());
                temp.setText(bean.getTemp() + "℃");
                weather.setText(bean.getWeather());
                mCloth_tx.setText("穿衣指数——"+bean.getCloth_tx());
                mCloth_content.setText(bean.getCloth_content());
                mSport_tx.setText("运动指数——"+bean.getSport_tx());
                mSport_content.setText(bean.getSport_content());
                mAir_conditioning_tx.setText("空调指数——"+bean.getAir_conditioning_tx());
                mAir_conditioning_content.setText(bean.getAir_conditioning_content());
                mUltraviolet_radiation_tx.setText("紫外线指数——"+bean.getUltraviolet_radiation_tx());
                mUltraviolet_radiation_content.setText(bean.getUltraviolet_radiation_content());
            }
        };


    }

    private void initUI() {
        city = (TextView) findViewById(R.id.city);
        temp = (TextView) findViewById(R.id.temp);
        week = (TextView) findViewById(R.id.week);
        weather = (TextView) findViewById(R.id.weather);
        mSearchButton = (Button) findViewById(R.id.search);
        mText = (EditText) findViewById(R.id.editText);
        mCloth_tx = (TextView) findViewById(R.id.id_cloth_tx);
        mCloth_content = (TextView) findViewById(R.id.id_cloth_content);
        mSport_tx = (TextView) findViewById(R.id.id_sport_tx);
        mSport_content = (TextView) findViewById(R.id.id_sport_content);
        mUltraviolet_radiation_tx = (TextView) findViewById(R.id.id_ultraviolet_radiation_tx);
        mUltraviolet_radiation_content = (TextView) findViewById(R.id.id_ultraviolet_radiation_content);
        mAir_conditioning_tx = (TextView) findViewById(R.id.id_air_conditioning_tx);
        mAir_conditioning_content = (TextView) findViewById(R.id.id_air_conditioning_content);
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pullToRefreshView);
    }

    private void refreshData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        dizhi = prefs.getString("city", "");
        try {
            address = "http://api.jisuapi.com/weather/query?appkey=b968e6d10975a8c5&city="
                    + URLEncoder.encode(dizhi, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mHttpuility = new Httpuility(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject object = new JSONObject(response);

                    JSONObject result = object.getJSONObject("result");
                    String mCity = result.getString("city");
                    String mWeek = result.getString("week");
                    String mTemp = result.getString("temp");
                    String mWeather = result.getString("weather");
                    String air_conditioning_tx = null;
                    String air_conditioning_content = null;
                    String sport_tx = null;
                    String sport_content = null;
                    String ultraviolet_radiation_content = null;
                    String ultraviolet_radiation_tx = null;
                    String cloth_content = null;
                    String cloth_tx = null;
                    JSONArray index = result.getJSONArray("index");
                    for (int i = 0; i < index.length(); i++) {
                        JSONObject object1 = index.getJSONObject(i);
                        switch (object1.getString("iname")) {
                            case "空调指数":
                                air_conditioning_tx = object1.getString("ivalue");
                                air_conditioning_content = object1.getString("detail");
                                break;
                            case "运动指数":
                                sport_tx = object1.getString("ivalue");
                                sport_content = object1.getString("detail");
                                break;
                            case "紫外线指数":
                                ultraviolet_radiation_tx = object1.getString("ivalue");
                                ultraviolet_radiation_content = object1.getString("detail");
                                break;
                            case "穿衣指数":
                                cloth_tx = object1.getString("ivalue");
                                cloth_content = object1.getString("detail");
                                break;
                            default:
                                break;
                        }
                    }
                    CurrentWeatherBean bean = new CurrentWeatherBean();
                    bean.setCity(mCity);
                    bean.setWeek(mWeek);
                    bean.setTemp(mTemp);
                    bean.setWeather(mWeather);
                    bean.setAir_conditioning_tx(air_conditioning_tx);
                    bean.setAir_conditioning_content(air_conditioning_content);
                    bean.setSport_tx(sport_tx);
                    bean.setSport_content(sport_content);
                    bean.setUltraviolet_radiation_tx(ultraviolet_radiation_tx);
                    bean.setUltraviolet_radiation_content(ultraviolet_radiation_content);
                    bean.setCloth_tx(cloth_tx);
                    bean.setCloth_content(cloth_content);
                    showMessage(getApplicationContext(), air_conditioning_tx, air_conditioning_content, sport_tx,
                            sport_content, ultraviolet_radiation_tx, ultraviolet_radiation_content, cloth_tx, cloth_content,
                            mCity, mWeek, mTemp, mWeather);
                    Message message = new Message();
                    message.obj = bean;
                    mHandler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        city.setText(prefs.getString("city", ""));
        week.setText(prefs.getString("week", ""));
        temp.setText(prefs.getString("temp", "") + "℃");
        weather.setText(prefs.getString("weather", ""));
        mCloth_tx.setText("穿衣指数——"+prefs.getString("cloth_tx",""));
        mCloth_content.setText(prefs.getString("cloth_content",""));
        mSport_tx.setText("运动指数——"+prefs.getString("sport_tx",""));
        mSport_content.setText(prefs.getString("sport_content",""));
        mAir_conditioning_tx.setText("空调指数——"+prefs.getString("air_conditioning_tx",""));
        mAir_conditioning_content.setText(prefs.getString("air_conditioning_content",""));
        mUltraviolet_radiation_tx.setText("紫外线指数——"+prefs.getString("ultraviolet_radiation_tx",""));
        mUltraviolet_radiation_content.setText(prefs.getString("ultraviolet_radiation_content",""));

    }

    private void showMessage(Context context, String air_conditioning_tx,
                             String air_conditioning_content, String sport_tx,
                             String sport_content, String ultraviolet_radiation_tx,
                             String ultraviolet_radiation_content, String cloth_tx, String cloth_content,
                             String mCity, String mWeek, String mTemp, String mWeather) {
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(context).edit();
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
        editor.commit();
    }


}
