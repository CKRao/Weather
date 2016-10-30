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
    private ImageView img, img_01, img_02, img_03;
    private TextView week_01, week_02, week_03;
    private TextView temp_01, temp_02, temp_03;
    private TextView mSport_tx, mSport_content;
    private Httpuility mHttpuility;
    private List<DataBean> mDataBeanList;
    private Handler mHandler;
    private static boolean show = false;
    private ImageButton mSearchButton;
    private PullToRefreshView mPullToRefreshView;
    /**
     * aqi : {"city":{"aqi":"16","co":"0","no2":"15","o3":"51","pm10":"13","pm25":"10","qlty":"优","so2":"2"}}
     * basic : {"city":"北京","cnty":"中国","id":"CN101010100","lat":"39.904000","lon":"116.391000","update":{"loc":"2016-10-30 20:54","utc":"2016-10-30 12:54"}}
     * daily_forecast : [{"astro":{"sr":"06:41","ss":"17:14"},"cond":{"code_d":"104","code_n":"101","txt_d":"阴","txt_n":"多云"},"date":"2016-10-30","hum":"42","pcpn":"0.0","pop":"0","pres":"1028","tmp":{"max":"13","min":"0"},"vis":"10","wind":{"deg":"342","dir":"北风","sc":"4-5","spd":"17"}},{"astro":{"sr":"06:42","ss":"17:13"},"cond":{"code_d":"100","code_n":"100","txt_d":"晴","txt_n":"晴"},"date":"2016-10-31","hum":"24","pcpn":"0.0","pop":"0","pres":"1038","tmp":{"max":"6","min":"-4"},"vis":"10","wind":{"deg":"354","dir":"无持续风向","sc":"微风","spd":"5"}},{"astro":{"sr":"06:43","ss":"17:12"},"cond":{"code_d":"101","code_n":"100","txt_d":"多云","txt_n":"晴"},"date":"2016-11-01","hum":"47","pcpn":"0.0","pop":"0","pres":"1034","tmp":{"max":"8","min":"-2"},"vis":"10","wind":{"deg":"204","dir":"无持续风向","sc":"微风","spd":"2"}},{"astro":{"sr":"06:44","ss":"17:11"},"cond":{"code_d":"100","code_n":"100","txt_d":"晴","txt_n":"晴"},"date":"2016-11-02","hum":"58","pcpn":"0.0","pop":"0","pres":"1025","tmp":{"max":"12","min":"2"},"vis":"10","wind":{"deg":"165","dir":"无持续风向","sc":"微风","spd":"0"}},{"astro":{"sr":"06:45","ss":"17:10"},"cond":{"code_d":"100","code_n":"100","txt_d":"晴","txt_n":"晴"},"date":"2016-11-03","hum":"40","pcpn":"0.0","pop":"0","pres":"1024","tmp":{"max":"14","min":"3"},"vis":"10","wind":{"deg":"197","dir":"无持续风向","sc":"微风","spd":"5"}},{"astro":{"sr":"06:47","ss":"17:08"},"cond":{"code_d":"502","code_n":"502","txt_d":"霾","txt_n":"霾"},"date":"2016-11-04","hum":"44","pcpn":"0.0","pop":"0","pres":"1018","tmp":{"max":"15","min":"4"},"vis":"10","wind":{"deg":"6","dir":"无持续风向","sc":"微风","spd":"1"}},{"astro":{"sr":"06:48","ss":"17:07"},"cond":{"code_d":"100","code_n":"101","txt_d":"晴","txt_n":"多云"},"date":"2016-11-05","hum":"64","pcpn":"0.0","pop":"0","pres":"1015","tmp":{"max":"16","min":"5"},"vis":"10","wind":{"deg":"167","dir":"无持续风向","sc":"微风","spd":"4"}}]
     * hourly_forecast : [{"date":"2016-10-30 22:00","hum":"31","pop":"0","pres":"1037","tmp":"4","wind":{"deg":"6","dir":"北风","sc":"3-4","spd":"17"}}]
     * now : {"cond":{"code":"101","txt":"多云"},"fl":"4","hum":"26","pcpn":"0","pres":"1031","tmp":"8","vis":"10","wind":{"deg":"340","dir":"北风","sc":"6-7","spd":"35"}}
     * status : ok
     * suggestion : {"comf":{"brf":"较舒适","txt":"白天天气晴好，早晚会感觉偏凉，午后舒适、宜人。"},"cw":{"brf":"较不宜","txt":"较不宜洗车，未来一天无雨，风力较大，如果执意擦洗汽车，要做好蒙上污垢的心理准备。"},"drsg":{"brf":"冷","txt":"天气冷，建议着棉服、羽绒服、皮夹克加羊毛衫等冬季服装。年老体弱者宜着厚棉衣、冬大衣或厚羽绒服。"},"flu":{"brf":"极易发","txt":"将有一次强降温过程，天气寒冷，极易发生感冒，请特别注意增加衣服保暖防寒。"},"sport":{"brf":"较不宜","txt":"天气较好，但考虑风力较大，天气寒冷，推荐您进行室内运动，若在户外运动须注意保暖。"},"trav":{"brf":"一般","txt":"天气较好，温度稍低，加之风稍大，让人感觉有点凉，会对外出有一定影响，外出注意防风保暖。"},"uv":{"brf":"中等","txt":"属中等强度紫外线辐射天气，外出时建议涂擦SPF高于15、PA+的防晒护肤品，戴帽子、太阳镜。"}}
     */

    private List<HeWeatherdataserviceBean> HeWeatherdataservice;


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

                Intent intent = new Intent(MainActivity.this,SelectActivity.class);
                startActivityForResult(intent,0);
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
                mCloth_tx.setText("穿衣指数——" + bean.getCloth_tx());
                mCloth_content.setText(bean.getCloth_content());
                mSport_tx.setText("运动指数——" + bean.getSport_tx());
                mSport_content.setText(bean.getSport_content());
                mAir_conditioning_tx.setText("空调指数——" + bean.getAir_conditioning_tx());
                mAir_conditioning_content.setText(bean.getAir_conditioning_content());
                mUltraviolet_radiation_tx.setText("紫外线指数——" + bean.getUltraviolet_radiation_tx());
                mUltraviolet_radiation_content.setText(bean.getUltraviolet_radiation_content());
                img.setImageResource(getResource(bean.getImg()));
                img_01.setImageResource(getResource(bean.getImg_01()));
                img_02.setImageResource(getResource(bean.getImg_02()));
                img_03.setImageResource(getResource(bean.getImg_03()));
                week_01.setText(bean.getWeek_01());
                week_02.setText(bean.getWeek_02());
                week_03.setText(bean.getWeek_03());
                temp_01.setText(bean.getTemp_01());
                temp_02.setText(bean.getTemp_02());
                temp_03.setText(bean.getTemp_03());
            }
        };

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

        week_01 = (TextView) findViewById(R.id.week_01);
        week_02 = (TextView) findViewById(R.id.week_02);
        week_03 = (TextView) findViewById(R.id.week_03);

        temp_01 = (TextView) findViewById(R.id.temp_01);
        temp_02 = (TextView) findViewById(R.id.temp_02);
        temp_03 = (TextView) findViewById(R.id.temp_03);

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
                    String img = "img" + result.getString("img");
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

                    String img03 = null;
                    String img02 = null;
                    String img01 = null;
                    String week01 = null;
                    String week02 = null;
                    String week03 = null;
                    String temp01 = null;
                    String temp02 = null;
                    String temp03 = null;
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
                    JSONArray daily = result.getJSONArray("daily");
                    for (int i = 1; i <= 3; i++) {
                        JSONObject jsonObject = daily.getJSONObject(i);
                        String date = jsonObject.getString("date").substring(5);
                        JSONObject night = jsonObject.getJSONObject("night");
                        JSONObject day = jsonObject.getJSONObject("day");
                        switch (i) {
                            case 1:
                                week01 = date;
                                img01 = "img" + day.getString("img");
                                temp01 = night.getString("templow") + "º～" + day.getString("temphigh")+"º";
                                break;
                            case 2:
                                week02 = date;
                                img02 = "img" + day.getString("img");
                                temp02 = night.getString("templow") + "º～" + day.getString("temphigh")+"º";
                                break;
                            case 3:
                                week03 = date;
                                img03 = "img" + day.getString("img");
                                temp03 = night.getString("templow") + "º～" + day.getString("temphigh")+"º";
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
                    bean.setImg(img);
                    bean.setImg_01(img01);
                    bean.setImg_02(img02);
                    bean.setImg_03(img03);
                    bean.setWeek_01(week01);
                    bean.setWeek_02(week02);
                    bean.setWeek_03(week03);
                    bean.setTemp_01(temp01);
                    bean.setTemp_02(temp02);
                    bean.setTemp_03(temp03);
                    showMessage(getApplicationContext(), air_conditioning_tx, air_conditioning_content, sport_tx,
                            sport_content, ultraviolet_radiation_tx, ultraviolet_radiation_content, cloth_tx, cloth_content,
                            mCity, mWeek, mTemp, mWeather, img, img01, img02, img03, week01, week02, week03, temp01, temp02, temp03);
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
        img.setImageResource(getResource(prefs.getString("img","img99")));
        img_01.setImageResource(getResource(prefs.getString("img01","img99")));
        img_02.setImageResource(getResource(prefs.getString("img02","img99")));
        img_03.setImageResource(getResource(prefs.getString("img03","img99")));
        week_01.setText(prefs.getString("week01","未知"));
        week_02.setText(prefs.getString("week02","未知"));
        week_03.setText(prefs.getString("week03","未知"));
        temp_01.setText(prefs.getString("temp01",""));
        temp_02.setText(prefs.getString("temp02",""));
        temp_03.setText(prefs.getString("temp03",""));
    }

    private void showMessage(Context context, String air_conditioning_tx,
                             String air_conditioning_content, String sport_tx,
                             String sport_content, String ultraviolet_radiation_tx,
                             String ultraviolet_radiation_content, String cloth_tx,
                             String cloth_content,String mCity, String mWeek, String mTemp,
                             String mWeather, String img, String img01, String img02,
                             String img03, String week01, String week02, String week03,
                             String temp01, String temp02, String temp03) {
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
        editor.putString("img", img);
        editor.putString("img01", img01);
        editor.putString("img02", img02);
        editor.putString("img03", img03);
        editor.putString("week01", week01);
        editor.putString("week02", week02);
        editor.putString("week03", week03);
        editor.putString("temp01", temp01);
        editor.putString("temp02", temp02);
        editor.putString("temp03", temp03);
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data!=null){
            dizhi = String.valueOf(data.getStringExtra("dizhi"));
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
                            String img = "img" + result.getString("img");
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

                            String img03 = null;
                            String img02 = null;
                            String img01 = null;
                            String week01 = null;
                            String week02 = null;
                            String week03 = null;
                            String temp01 = null;
                            String temp02 = null;
                            String temp03 = null;
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
                            JSONArray daily = result.getJSONArray("daily");
                            for (int i = 1; i <= 3; i++) {
                                JSONObject jsonObject = daily.getJSONObject(i);
                                String date = jsonObject.getString("date").substring(5);
                                JSONObject night = jsonObject.getJSONObject("night");
                                JSONObject day = jsonObject.getJSONObject("day");
                                switch (i) {
                                    case 1:
                                        week01 = date;
                                        img01 = "img" + day.getString("img");
                                        temp01 = night.getString("templow") + "º～" + day.getString("temphigh") + "º";
                                        break;
                                    case 2:
                                        week02 = date;
                                        img02 = "img" + day.getString("img");
                                        temp02 = night.getString("templow") + "º～" + day.getString("temphigh") + "º";
                                        break;
                                    case 3:
                                        week03 = date;
                                        img03 = "img" + day.getString("img");
                                        temp03 = night.getString("templow") + "º～" + day.getString("temphigh") + "º";
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
                            bean.setImg(img);
                            bean.setImg_01(img01);
                            bean.setImg_02(img02);
                            bean.setImg_03(img03);
                            bean.setWeek_01(week01);
                            bean.setWeek_02(week02);
                            bean.setWeek_03(week03);
                            bean.setTemp_01(temp01);
                            bean.setTemp_02(temp02);
                            bean.setTemp_03(temp03);
                            showMessage(getApplicationContext(), air_conditioning_tx, air_conditioning_content, sport_tx,
                                    sport_content, ultraviolet_radiation_tx, ultraviolet_radiation_content, cloth_tx, cloth_content,
                                    mCity, mWeek, mTemp, mWeather, img, img01, img02, img03, week01, week02, week03, temp01, temp02, temp03);
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
    }

    public List<HeWeatherdataserviceBean> getHeWeatherdataservice() {
        return HeWeatherdataservice;
    }

    public void setHeWeatherdataservice(List<HeWeatherdataserviceBean> HeWeatherdataservice) {
        this.HeWeatherdataservice = HeWeatherdataservice;
    }

    public static class HeWeatherdataserviceBean {
        /**
         * city : {"aqi":"16","co":"0","no2":"15","o3":"51","pm10":"13","pm25":"10","qlty":"优","so2":"2"}
         */

        private AqiBean aqi;
        /**
         * city : 北京
         * cnty : 中国
         * id : CN101010100
         * lat : 39.904000
         * lon : 116.391000
         * update : {"loc":"2016-10-30 20:54","utc":"2016-10-30 12:54"}
         */

        private BasicBean basic;
        /**
         * cond : {"code":"101","txt":"多云"}
         * fl : 4
         * hum : 26
         * pcpn : 0
         * pres : 1031
         * tmp : 8
         * vis : 10
         * wind : {"deg":"340","dir":"北风","sc":"6-7","spd":"35"}
         */

        private NowBean now;
        private String status;
        /**
         * comf : {"brf":"较舒适","txt":"白天天气晴好，早晚会感觉偏凉，午后舒适、宜人。"}
         * cw : {"brf":"较不宜","txt":"较不宜洗车，未来一天无雨，风力较大，如果执意擦洗汽车，要做好蒙上污垢的心理准备。"}
         * drsg : {"brf":"冷","txt":"天气冷，建议着棉服、羽绒服、皮夹克加羊毛衫等冬季服装。年老体弱者宜着厚棉衣、冬大衣或厚羽绒服。"}
         * flu : {"brf":"极易发","txt":"将有一次强降温过程，天气寒冷，极易发生感冒，请特别注意增加衣服保暖防寒。"}
         * sport : {"brf":"较不宜","txt":"天气较好，但考虑风力较大，天气寒冷，推荐您进行室内运动，若在户外运动须注意保暖。"}
         * trav : {"brf":"一般","txt":"天气较好，温度稍低，加之风稍大，让人感觉有点凉，会对外出有一定影响，外出注意防风保暖。"}
         * uv : {"brf":"中等","txt":"属中等强度紫外线辐射天气，外出时建议涂擦SPF高于15、PA+的防晒护肤品，戴帽子、太阳镜。"}
         */

        private SuggestionBean suggestion;
        /**
         * astro : {"sr":"06:41","ss":"17:14"}
         * cond : {"code_d":"104","code_n":"101","txt_d":"阴","txt_n":"多云"}
         * date : 2016-10-30
         * hum : 42
         * pcpn : 0.0
         * pop : 0
         * pres : 1028
         * tmp : {"max":"13","min":"0"}
         * vis : 10
         * wind : {"deg":"342","dir":"北风","sc":"4-5","spd":"17"}
         */

        private List<DailyForecastBean> daily_forecast;
        /**
         * date : 2016-10-30 22:00
         * hum : 31
         * pop : 0
         * pres : 1037
         * tmp : 4
         * wind : {"deg":"6","dir":"北风","sc":"3-4","spd":"17"}
         */

        private List<HourlyForecastBean> hourly_forecast;

        public AqiBean getAqi() {
            return aqi;
        }

        public void setAqi(AqiBean aqi) {
            this.aqi = aqi;
        }

        public BasicBean getBasic() {
            return basic;
        }

        public void setBasic(BasicBean basic) {
            this.basic = basic;
        }

        public NowBean getNow() {
            return now;
        }

        public void setNow(NowBean now) {
            this.now = now;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public SuggestionBean getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(SuggestionBean suggestion) {
            this.suggestion = suggestion;
        }

        public List<DailyForecastBean> getDaily_forecast() {
            return daily_forecast;
        }

        public void setDaily_forecast(List<DailyForecastBean> daily_forecast) {
            this.daily_forecast = daily_forecast;
        }

        public List<HourlyForecastBean> getHourly_forecast() {
            return hourly_forecast;
        }

        public void setHourly_forecast(List<HourlyForecastBean> hourly_forecast) {
            this.hourly_forecast = hourly_forecast;
        }

        public static class AqiBean {
            /**
             * aqi : 16
             * co : 0
             * no2 : 15
             * o3 : 51
             * pm10 : 13
             * pm25 : 10
             * qlty : 优
             * so2 : 2
             */

            private CityBean city;

            public CityBean getCity() {
                return city;
            }

            public void setCity(CityBean city) {
                this.city = city;
            }

            public static class CityBean {
                private String aqi;
                private String co;
                private String no2;
                private String o3;
                private String pm10;
                private String pm25;
                private String qlty;
                private String so2;

                public String getAqi() {
                    return aqi;
                }

                public void setAqi(String aqi) {
                    this.aqi = aqi;
                }

                public String getCo() {
                    return co;
                }

                public void setCo(String co) {
                    this.co = co;
                }

                public String getNo2() {
                    return no2;
                }

                public void setNo2(String no2) {
                    this.no2 = no2;
                }

                public String getO3() {
                    return o3;
                }

                public void setO3(String o3) {
                    this.o3 = o3;
                }

                public String getPm10() {
                    return pm10;
                }

                public void setPm10(String pm10) {
                    this.pm10 = pm10;
                }

                public String getPm25() {
                    return pm25;
                }

                public void setPm25(String pm25) {
                    this.pm25 = pm25;
                }

                public String getQlty() {
                    return qlty;
                }

                public void setQlty(String qlty) {
                    this.qlty = qlty;
                }

                public String getSo2() {
                    return so2;
                }

                public void setSo2(String so2) {
                    this.so2 = so2;
                }
            }
        }

        public static class BasicBean {
            private String city;
            private String cnty;
            private String id;
            private String lat;
            private String lon;
            /**
             * loc : 2016-10-30 20:54
             * utc : 2016-10-30 12:54
             */

            private UpdateBean update;

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCnty() {
                return cnty;
            }

            public void setCnty(String cnty) {
                this.cnty = cnty;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLon() {
                return lon;
            }

            public void setLon(String lon) {
                this.lon = lon;
            }

            public UpdateBean getUpdate() {
                return update;
            }

            public void setUpdate(UpdateBean update) {
                this.update = update;
            }

            public static class UpdateBean {
                private String loc;
                private String utc;

                public String getLoc() {
                    return loc;
                }

                public void setLoc(String loc) {
                    this.loc = loc;
                }

                public String getUtc() {
                    return utc;
                }

                public void setUtc(String utc) {
                    this.utc = utc;
                }
            }
        }

        public static class NowBean {
            /**
             * code : 101
             * txt : 多云
             */

            private CondBean cond;
            private String fl;
            private String hum;
            private String pcpn;
            private String pres;
            private String tmp;
            private String vis;
            /**
             * deg : 340
             * dir : 北风
             * sc : 6-7
             * spd : 35
             */

            private WindBean wind;

            public CondBean getCond() {
                return cond;
            }

            public void setCond(CondBean cond) {
                this.cond = cond;
            }

            public String getFl() {
                return fl;
            }

            public void setFl(String fl) {
                this.fl = fl;
            }

            public String getHum() {
                return hum;
            }

            public void setHum(String hum) {
                this.hum = hum;
            }

            public String getPcpn() {
                return pcpn;
            }

            public void setPcpn(String pcpn) {
                this.pcpn = pcpn;
            }

            public String getPres() {
                return pres;
            }

            public void setPres(String pres) {
                this.pres = pres;
            }

            public String getTmp() {
                return tmp;
            }

            public void setTmp(String tmp) {
                this.tmp = tmp;
            }

            public String getVis() {
                return vis;
            }

            public void setVis(String vis) {
                this.vis = vis;
            }

            public WindBean getWind() {
                return wind;
            }

            public void setWind(WindBean wind) {
                this.wind = wind;
            }

            public static class CondBean {
                private String code;
                private String txt;

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getTxt() {
                    return txt;
                }

                public void setTxt(String txt) {
                    this.txt = txt;
                }
            }

            public static class WindBean {
                private String deg;
                private String dir;
                private String sc;
                private String spd;

                public String getDeg() {
                    return deg;
                }

                public void setDeg(String deg) {
                    this.deg = deg;
                }

                public String getDir() {
                    return dir;
                }

                public void setDir(String dir) {
                    this.dir = dir;
                }

                public String getSc() {
                    return sc;
                }

                public void setSc(String sc) {
                    this.sc = sc;
                }

                public String getSpd() {
                    return spd;
                }

                public void setSpd(String spd) {
                    this.spd = spd;
                }
            }
        }

        public static class SuggestionBean {
            /**
             * brf : 较舒适
             * txt : 白天天气晴好，早晚会感觉偏凉，午后舒适、宜人。
             */

            private ComfBean comf;
            /**
             * brf : 较不宜
             * txt : 较不宜洗车，未来一天无雨，风力较大，如果执意擦洗汽车，要做好蒙上污垢的心理准备。
             */

            private CwBean cw;
            /**
             * brf : 冷
             * txt : 天气冷，建议着棉服、羽绒服、皮夹克加羊毛衫等冬季服装。年老体弱者宜着厚棉衣、冬大衣或厚羽绒服。
             */

            private DrsgBean drsg;
            /**
             * brf : 极易发
             * txt : 将有一次强降温过程，天气寒冷，极易发生感冒，请特别注意增加衣服保暖防寒。
             */

            private FluBean flu;
            /**
             * brf : 较不宜
             * txt : 天气较好，但考虑风力较大，天气寒冷，推荐您进行室内运动，若在户外运动须注意保暖。
             */

            private SportBean sport;
            /**
             * brf : 一般
             * txt : 天气较好，温度稍低，加之风稍大，让人感觉有点凉，会对外出有一定影响，外出注意防风保暖。
             */

            private TravBean trav;
            /**
             * brf : 中等
             * txt : 属中等强度紫外线辐射天气，外出时建议涂擦SPF高于15、PA+的防晒护肤品，戴帽子、太阳镜。
             */

            private UvBean uv;

            public ComfBean getComf() {
                return comf;
            }

            public void setComf(ComfBean comf) {
                this.comf = comf;
            }

            public CwBean getCw() {
                return cw;
            }

            public void setCw(CwBean cw) {
                this.cw = cw;
            }

            public DrsgBean getDrsg() {
                return drsg;
            }

            public void setDrsg(DrsgBean drsg) {
                this.drsg = drsg;
            }

            public FluBean getFlu() {
                return flu;
            }

            public void setFlu(FluBean flu) {
                this.flu = flu;
            }

            public SportBean getSport() {
                return sport;
            }

            public void setSport(SportBean sport) {
                this.sport = sport;
            }

            public TravBean getTrav() {
                return trav;
            }

            public void setTrav(TravBean trav) {
                this.trav = trav;
            }

            public UvBean getUv() {
                return uv;
            }

            public void setUv(UvBean uv) {
                this.uv = uv;
            }

            public static class ComfBean {
                private String brf;
                private String txt;

                public String getBrf() {
                    return brf;
                }

                public void setBrf(String brf) {
                    this.brf = brf;
                }

                public String getTxt() {
                    return txt;
                }

                public void setTxt(String txt) {
                    this.txt = txt;
                }
            }

            public static class CwBean {
                private String brf;
                private String txt;

                public String getBrf() {
                    return brf;
                }

                public void setBrf(String brf) {
                    this.brf = brf;
                }

                public String getTxt() {
                    return txt;
                }

                public void setTxt(String txt) {
                    this.txt = txt;
                }
            }

            public static class DrsgBean {
                private String brf;
                private String txt;

                public String getBrf() {
                    return brf;
                }

                public void setBrf(String brf) {
                    this.brf = brf;
                }

                public String getTxt() {
                    return txt;
                }

                public void setTxt(String txt) {
                    this.txt = txt;
                }
            }

            public static class FluBean {
                private String brf;
                private String txt;

                public String getBrf() {
                    return brf;
                }

                public void setBrf(String brf) {
                    this.brf = brf;
                }

                public String getTxt() {
                    return txt;
                }

                public void setTxt(String txt) {
                    this.txt = txt;
                }
            }

            public static class SportBean {
                private String brf;
                private String txt;

                public String getBrf() {
                    return brf;
                }

                public void setBrf(String brf) {
                    this.brf = brf;
                }

                public String getTxt() {
                    return txt;
                }

                public void setTxt(String txt) {
                    this.txt = txt;
                }
            }

            public static class TravBean {
                private String brf;
                private String txt;

                public String getBrf() {
                    return brf;
                }

                public void setBrf(String brf) {
                    this.brf = brf;
                }

                public String getTxt() {
                    return txt;
                }

                public void setTxt(String txt) {
                    this.txt = txt;
                }
            }

            public static class UvBean {
                private String brf;
                private String txt;

                public String getBrf() {
                    return brf;
                }

                public void setBrf(String brf) {
                    this.brf = brf;
                }

                public String getTxt() {
                    return txt;
                }

                public void setTxt(String txt) {
                    this.txt = txt;
                }
            }
        }

        public static class DailyForecastBean {
            /**
             * sr : 06:41
             * ss : 17:14
             */

            private AstroBean astro;
            /**
             * code_d : 104
             * code_n : 101
             * txt_d : 阴
             * txt_n : 多云
             */

            private CondBean cond;
            private String date;
            private String hum;
            private String pcpn;
            private String pop;
            private String pres;
            /**
             * max : 13
             * min : 0
             */

            private TmpBean tmp;
            private String vis;
            /**
             * deg : 342
             * dir : 北风
             * sc : 4-5
             * spd : 17
             */

            private WindBean wind;

            public AstroBean getAstro() {
                return astro;
            }

            public void setAstro(AstroBean astro) {
                this.astro = astro;
            }

            public CondBean getCond() {
                return cond;
            }

            public void setCond(CondBean cond) {
                this.cond = cond;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getHum() {
                return hum;
            }

            public void setHum(String hum) {
                this.hum = hum;
            }

            public String getPcpn() {
                return pcpn;
            }

            public void setPcpn(String pcpn) {
                this.pcpn = pcpn;
            }

            public String getPop() {
                return pop;
            }

            public void setPop(String pop) {
                this.pop = pop;
            }

            public String getPres() {
                return pres;
            }

            public void setPres(String pres) {
                this.pres = pres;
            }

            public TmpBean getTmp() {
                return tmp;
            }

            public void setTmp(TmpBean tmp) {
                this.tmp = tmp;
            }

            public String getVis() {
                return vis;
            }

            public void setVis(String vis) {
                this.vis = vis;
            }

            public WindBean getWind() {
                return wind;
            }

            public void setWind(WindBean wind) {
                this.wind = wind;
            }

            public static class AstroBean {
                private String sr;
                private String ss;

                public String getSr() {
                    return sr;
                }

                public void setSr(String sr) {
                    this.sr = sr;
                }

                public String getSs() {
                    return ss;
                }

                public void setSs(String ss) {
                    this.ss = ss;
                }
            }

            public static class CondBean {
                private String code_d;
                private String code_n;
                private String txt_d;
                private String txt_n;

                public String getCode_d() {
                    return code_d;
                }

                public void setCode_d(String code_d) {
                    this.code_d = code_d;
                }

                public String getCode_n() {
                    return code_n;
                }

                public void setCode_n(String code_n) {
                    this.code_n = code_n;
                }

                public String getTxt_d() {
                    return txt_d;
                }

                public void setTxt_d(String txt_d) {
                    this.txt_d = txt_d;
                }

                public String getTxt_n() {
                    return txt_n;
                }

                public void setTxt_n(String txt_n) {
                    this.txt_n = txt_n;
                }
            }

            public static class TmpBean {
                private String max;
                private String min;

                public String getMax() {
                    return max;
                }

                public void setMax(String max) {
                    this.max = max;
                }

                public String getMin() {
                    return min;
                }

                public void setMin(String min) {
                    this.min = min;
                }
            }

            public static class WindBean {
                private String deg;
                private String dir;
                private String sc;
                private String spd;

                public String getDeg() {
                    return deg;
                }

                public void setDeg(String deg) {
                    this.deg = deg;
                }

                public String getDir() {
                    return dir;
                }

                public void setDir(String dir) {
                    this.dir = dir;
                }

                public String getSc() {
                    return sc;
                }

                public void setSc(String sc) {
                    this.sc = sc;
                }

                public String getSpd() {
                    return spd;
                }

                public void setSpd(String spd) {
                    this.spd = spd;
                }
            }
        }

        public static class HourlyForecastBean {
            private String date;
            private String hum;
            private String pop;
            private String pres;
            private String tmp;
            /**
             * deg : 6
             * dir : 北风
             * sc : 3-4
             * spd : 17
             */

            private WindBean wind;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getHum() {
                return hum;
            }

            public void setHum(String hum) {
                this.hum = hum;
            }

            public String getPop() {
                return pop;
            }

            public void setPop(String pop) {
                this.pop = pop;
            }

            public String getPres() {
                return pres;
            }

            public void setPres(String pres) {
                this.pres = pres;
            }

            public String getTmp() {
                return tmp;
            }

            public void setTmp(String tmp) {
                this.tmp = tmp;
            }

            public WindBean getWind() {
                return wind;
            }

            public void setWind(WindBean wind) {
                this.wind = wind;
            }

            public static class WindBean {
                private String deg;
                private String dir;
                private String sc;
                private String spd;

                public String getDeg() {
                    return deg;
                }

                public void setDeg(String deg) {
                    this.deg = deg;
                }

                public String getDir() {
                    return dir;
                }

                public void setDir(String dir) {
                    this.dir = dir;
                }

                public String getSc() {
                    return sc;
                }

                public void setSc(String sc) {
                    this.sc = sc;
                }

                public String getSpd() {
                    return spd;
                }

                public void setSpd(String spd) {
                    this.spd = spd;
                }
            }
        }
    }
}
