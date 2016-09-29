package com.example.ckrao.myapplication;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ckrao.myapplication.httpuility.HttpCallBackListener;
import com.example.ckrao.myapplication.httpuility.Httpuility;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private MaterialSearchView searchView;
    private Toolbar mToolbar;
    private String address;
    private String dizhi;
    private TextView city, week, temp, weather;
    private Httpuility mHttpuility;
    private List<DataBean> mDataBeanList;
    private Handler mHandler;
    private static boolean show = false;
    private EditText mText;
    private Button mSearchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_main);

        city = (TextView) findViewById(R.id.city);
        temp = (TextView) findViewById(R.id.temp);
        week = (TextView) findViewById(R.id.week);
        weather = (TextView) findViewById(R.id.weather);
        mSearchButton = (Button) findViewById(R.id.search);
        mText = (EditText) findViewById(R.id.editText);
            showWeather();
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dizhi = String.valueOf(mText.getText());
                mText.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                            CurrentWeatherBean bean = new CurrentWeatherBean();
                            bean.setCity(mCity);
                            bean.setWeek(mWeek);
                            bean.setTemp(mTemp);
                            bean.setWeather(mWeather);
                            showMessage(getApplicationContext(), mCity, mWeek, mTemp, mWeather);
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
            }
        };




    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        city.setText(prefs.getString("city", ""));
        week.setText(prefs.getString("week", ""));
        temp.setText(prefs.getString("temp", "") + "℃");
        weather.setText(prefs.getString("weather", ""));
    }

    private void showMessage(Context context, String mCity,
                             String mWeek, String mTemp, String mWeather) {
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(context).edit();
        editor.putString("city", mCity);
        editor.putString("week", mWeek);
        editor.putString("temp", mTemp);
        editor.putString("weather", mWeather);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }


}
