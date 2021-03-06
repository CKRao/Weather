package com.example.ckrao.myapplication;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ckrao.myapplication.Adapter.MyViewPagerAdapter;
import com.example.ckrao.myapplication.Adapter.RecyclerAdapter;
import com.example.ckrao.myapplication.Model.CurrentWeatherBean;
import com.example.ckrao.myapplication.Model.DataBean;
import com.example.ckrao.myapplication.Model.MoreCityModel;
import com.example.ckrao.myapplication.Service.AutoUpdateService;
import com.example.ckrao.myapplication.HttpUtility.HttpCallBackListener;
import com.example.ckrao.myapplication.HttpUtility.Httpuility;
import com.example.ckrao.myapplication.Utility.GetWeekOfDate;
import com.example.ckrao.myapplication.Utility.ToastUtil;
import com.google.gson.Gson;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ImageView isnotadd;
    private TextView week;
    private TextView temp;
    private TextView weather;
    private TextView mCloth_tx;
    private TextView mCloth_content;
    private TextView mAir_conditioning_content;
    private TextView mAir_conditioning_tx;
    private TextView mUltraviolet_radiation_content;
    private TextView mUltraviolet_radiation_tx;
    private ImageView img_01;
    private ImageView img_02;
    private ImageView img_03;
    private ImageView img_04;
    private ImageView img_05;
    private ImageView img_06;
    private TextView temp_01;
    private TextView temp_02;
    private TextView temp_03;
    private TextView temp_04;
    private TextView temp_05;
    private TextView temp_06;
    private TextView week_04;
    private TextView week_05;
    private TextView week_06;
    private TextView mSport_tx;
    private TextView mSport_content;
    private TextView tip;
    private CardView cardView;
    private TextView mRainfall, mHumidity, mWinddirection;
    private RelativeLayout layout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton mFloatingActionButton;
    private ImageView bgImg;
    private LocationClient mLocationClient;
    private BDLocationListener mBDLocationListener = new MyLocationListener();
    private Toolbar toolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private String address;
    private String myCity;
    private Httpuility mHttpuility;
    private List<DataBean> mDataBeanList;
    private MyViewPager mViewPager;
    private TabLayout mTableLayout;
    private LayoutInflater mLayoutInflater;
    private View mView1, mView2;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout_view2;
    private MyViewPagerAdapter viewPagerAdapter;
    private RecyclerAdapter recyclerAdapter;
    private List<View> mViewList = new ArrayList<>();
    private static boolean isDay;
    private static final int BAIDU_GPS_OPEN_STATE = 100;
    private static final int REFRESH_COMPLETE = 101;
    private static final int REFRESH_RECYCLERVIEW = 102;
    private static final int REFRESH_MORECITY = 103;
    private static final int MORECITY_ISNULL = 104;
    private static final int MORECITY_ISEXIT = 105;
    private static final int START_SEARCH = 106;
    private static final int START_MORECITY = 107;
    private static final int REFRESH_LOCALCITY= 108;
    private boolean isExit = false;
    private RecyclerView mRecyclerView;
    private long exitTime = 0;
    private List<MoreCityModel> mMoreCityModels;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    refreshData();
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case REFRESH_RECYCLERVIEW:
                    recyclerAdapter.notifyDataSetChanged();
                    mRecyclerView.invalidate();
                    viewPagerAdapter.notifyDataSetChanged();
                    mViewPager.setCurrentItem(1);
                    break;
                case REFRESH_MORECITY:
                    refreshMorCity();
                    mSwipeRefreshLayout_view2.setRefreshing(false);
                    if (recyclerAdapter.getItemCount() > 0) {
                        Snackbar.make(bgImg, "更新成功", Snackbar.LENGTH_SHORT).show();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), "您尚未添加城市，无法获取数据");
                    }

                    break;
                case MORECITY_ISNULL:
                    isnotadd.setVisibility(View.VISIBLE);
                    tip.setVisibility(View.VISIBLE);
                    break;
                case MORECITY_ISEXIT:
                    isnotadd.setVisibility(View.GONE);
                    tip.setVisibility(View.GONE);
                    break;
                case START_MORECITY:
                    String data1 = "from more";
                    Intent intent1 = new Intent(MainActivity.this, SelectActivity.class);
                    intent1.putExtra("From", data1);
                    startActivityForResult(intent1, 1);
                    break;
                case START_SEARCH:
                    String data = "from search";
                    Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                    intent.putExtra("From", data);
                    startActivityForResult(intent, 0);
                    break;
                case REFRESH_LOCALCITY:
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                default:
                    CurrentWeatherBean bean = (CurrentWeatherBean) msg.obj;
                    setTheInfo(bean);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_main);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mBDLocationListener);
        mMoreCityModels = getDataFromList("moreCity");
        setDataForList("moreCity", mMoreCityModels);
        determineTheTime();
        initUI();
        initLocation();
        setChange();
        initEvent();
    }

    private void initEvent() {
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isChoose", false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_WIFI_STATE},
                            BAIDU_GPS_OPEN_STATE);
                }

            } else {
                mLocationClient.start();
            }
            SharedPreferences.Editor editor = PreferenceManager.
                    getDefaultSharedPreferences(this).edit();
            editor.putBoolean("isChoose", true);
            editor.commit();
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }
        showWeather();
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_item_local:
                                mDrawerLayout.closeDrawers();
                                if (mLocationClient.isStarted()) {
                                    mLocationClient.stop();
                                }
                                mLocationClient.start();
                                mViewPager.setCurrentItem(0);
                                break;
                            case R.id.navigation_sub_item_search:
                                mDrawerLayout.closeDrawers();
                                mViewPager.setCurrentItem(0);
                                mHandler.sendEmptyMessageDelayed(START_SEARCH, 200);
                                break;
                            case R.id.navigation_sub_item_morecity:
                                mDrawerLayout.closeDrawers();
                                mHandler.sendEmptyMessageDelayed(START_MORECITY, 200);
                                break;
                            case R.id.navigation_sub_item_about:
                                mDrawerLayout.closeDrawers();
                                Snackbar.make(layout, "The App is Powered by CR", Snackbar.LENGTH_LONG).show();
                                break;
                            case R.id.navigation_sub_item_exit:
                                finish();
                                break;
                        }
                        return false;
                    }
                });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE);

        mSwipeRefreshLayout_view2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(REFRESH_MORECITY, 1500);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        recyclerAdapter.setOnItemLongClickListener(new RecyclerAdapter.RecyclerViewOnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemLongClick(final View view, final int position) {
                view.animate().translationZ(15F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.animate().translationZ(1F).setDuration(500).start();
                    }
                }).start();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCusto);
                alertDialog.setMessage("是否删除此城市");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMoreCityModels.remove(position);
                        recyclerAdapter.notifyDataSetChanged();
                        setDataForList("moreCity", mMoreCityModels);
                        if (mMoreCityModels.size() <= 0) {
                            mHandler.sendEmptyMessage(MORECITY_ISNULL);
                        }
                    }
                });
                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mViewPager.getCurrentItem() == 0){
                            mTableLayout.getTabAt(0).setIcon(R.drawable.home1);
                            mTableLayout.getTabAt(1).setIcon(R.drawable.morecity);
                    }  else {
                    mTableLayout.getTabAt(0).setIcon(R.drawable.home2);
                    mTableLayout.getTabAt(1).setIcon(R.drawable.morecity1);
                }
                }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setDataForList(String tag, List<MoreCityModel> modelList) {
        if (modelList == null || modelList.size() <= 0) {
            modelList = new ArrayList<>();
        }
        Gson gson = new Gson();
        String result = gson.toJson(modelList);
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(tag, null);
        editor.putString(tag, result);
        editor.commit();

    }

    private List<MoreCityModel> getDataFromList(String tag) {
        List<MoreCityModel> models = new ArrayList<>();
        String strJson = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext()).getString(tag, null);
        if (strJson == null) {
            return models;
        } else {
            Gson gson = new Gson();
            models = gson.fromJson(strJson, new TypeToken<List<MoreCityModel>>() {
            }.getType());
            return models;
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        Log.i("initLocation", "init");
        mLocationClient.setLocOption(option);
        Log.i("initLocation", " mLocationClient.setLocOption(option);");
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

    private void refreshMorCity() {
        if (mMoreCityModels.size() > 0) {
            for (int i = 0; i < mMoreCityModels.size(); i++) {
                myCity = mMoreCityModels.get(i).getCity();
                try {
                    address = "https://free-api.heweather.com/v5/weather?city=" + URLEncoder.encode(myCity, "UTF-8")
                            + "&key=b727f217188c4e8a91ecba4d349c73ff";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                final int finalI = i;
                mHttpuility = new Httpuility(address, new HttpCallBackListener() {
                    @Override
                    public void onFinish(String response) {
                        Gson gson = new Gson();
                        DataBean bean = gson.fromJson(response, DataBean.class);

                        mMoreCityModels.get(finalI).setCity(bean.getHeWeather5().get(0).getBasic().getCity());
                        mMoreCityModels.get(finalI).setTemp(bean.getHeWeather5().get(0).getNow().getTmp());
                        mMoreCityModels.get(finalI).setWeather(getResource("img" + bean.getHeWeather5().get(0).getDaily_forecast().get(0).getCond().getCode_d()));
                        mMoreCityModels.get(finalI).setMax(bean.getHeWeather5().get(0).getDaily_forecast().get(0).getTmp().getMax());
                        mMoreCityModels.get(finalI).setMin(bean.getHeWeather5().get(0).getDaily_forecast().get(0).getTmp().getMin());
                        setDataForList("moreCity", mMoreCityModels);
                        mHandler.sendEmptyMessage(REFRESH_RECYCLERVIEW);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Snackbar.make(bgImg, "更新成功", Snackbar.LENGTH_SHORT).show();
//                            }
//                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(bgImg, "更新失败", Snackbar.LENGTH_SHORT).show();
                                ToastUtil.showToast(getApplicationContext(), "更新失败");
                            }
                        });
                    }
                });

            }
        }
    }

    private void initUI() {
        mLayoutInflater = LayoutInflater.from(this);
        mViewPager = (MyViewPager) findViewById(R.id.view_pager);
        mTableLayout = (TabLayout) findViewById(R.id.tabs);
        mView1 = mLayoutInflater.inflate(R.layout.show_layout, null);
        mView2 = mLayoutInflater.inflate(R.layout.more_city, null);

        mViewList.add(mView1);
        mViewList.add(mView2);

        mTableLayout.setTabMode(TabLayout.MODE_FIXED);
        mTableLayout.addTab(mTableLayout.newTab());
        mTableLayout.addTab(mTableLayout.newTab());


        tip = (TextView) mView2.findViewById(R.id.id_tip);
        isnotadd = (ImageView) mView2.findViewById(R.id.view2_notyetadd);
        bgImg = (ImageView) findViewById(R.id.id_bg_img);
        layout = (RelativeLayout) mView1.findViewById(R.id.id_layout);
        temp = (TextView) mView1.findViewById(R.id.temp);
        week = (TextView) mView1.findViewById(R.id.week);
        weather = (TextView) mView1.findViewById(R.id.weather);
        mCloth_tx = (TextView) mView1.findViewById(R.id.id_cloth_tx);
        mCloth_content = (TextView) mView1.findViewById(R.id.id_cloth_content);
        mSport_tx = (TextView) mView1.findViewById(R.id.id_sport_tx);
        mSport_content = (TextView) mView1.findViewById(R.id.id_sport_content);
        mUltraviolet_radiation_tx = (TextView) mView1.findViewById(R.id.id_ultraviolet_radiation_tx);
        mUltraviolet_radiation_content = (TextView) mView1.findViewById(R.id.id_ultraviolet_radiation_content);
        mAir_conditioning_tx = (TextView) mView1.findViewById(R.id.id_air_conditioning_tx);
        mAir_conditioning_content = (TextView) mView1.findViewById(R.id.id_air_conditioning_content);

        img_01 = (ImageView) mView1.findViewById(R.id.id_wea_01);
        img_02 = (ImageView) mView1.findViewById(R.id.id_wea_02);
        img_03 = (ImageView) mView1.findViewById(R.id.id_wea_03);
        img_04 = (ImageView) mView1.findViewById(R.id.id_wea_04);
        img_05 = (ImageView) mView1.findViewById(R.id.id_wea_05);
        img_06 = (ImageView) mView1.findViewById(R.id.id_wea_06);


        temp_01 = (TextView) mView1.findViewById(R.id.temp_01);
        temp_02 = (TextView) mView1.findViewById(R.id.temp_02);
        temp_03 = (TextView) mView1.findViewById(R.id.temp_03);
        temp_04 = (TextView) mView1.findViewById(R.id.temp_04);
        temp_05 = (TextView) mView1.findViewById(R.id.temp_05);
        temp_06 = (TextView) mView1.findViewById(R.id.temp_06);


        week_04 = (TextView) mView1.findViewById(R.id.week_04);
        week_05 = (TextView) mView1.findViewById(R.id.week_05);
        week_06 = (TextView) mView1.findViewById(R.id.week_06);


        mRainfall = (TextView) mView1.findViewById(R.id.rainfall);
        mHumidity = (TextView) mView1.findViewById(R.id.humidity);
        mWinddirection = (TextView) mView1.findViewById(R.id.winddirection);
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.id_collapsing);
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView1.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout_view2 = (SwipeRefreshLayout) mView2.findViewById(R.id.view2_swiperefresh);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.BLACK);

        mRecyclerView = (RecyclerView) mView2.findViewById(R.id.recycleview);
        if (mMoreCityModels.size() <= 0) {
            mHandler.sendEmptyMessage(MORECITY_ISNULL);
            recyclerAdapter = new RecyclerAdapter(mMoreCityModels);
        } else {
            mHandler.sendEmptyMessage(MORECITY_ISEXIT);
            mMoreCityModels = getDataFromList("moreCity");
            recyclerAdapter = new RecyclerAdapter(mMoreCityModels);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(recyclerAdapter);

        viewPagerAdapter = new MyViewPagerAdapter(mViewList);

        mViewPager.setAdapter(viewPagerAdapter);
        mTableLayout.setupWithViewPager(mViewPager);
        mTableLayout.setTabsFromPagerAdapter(viewPagerAdapter);
        mTableLayout.getTabAt(0).setIcon(R.drawable.home1);
        mTableLayout.getTabAt(1).setIcon(R.drawable.morecity);

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
        temp.setText(bean.getTemp() + "°");
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
        img_04.setImageResource(getResource(bean.getImg_04()));
        img_05.setImageResource(getResource(bean.getImg_05()));
        img_06.setImageResource(getResource(bean.getImg_06()));

        temp_01.setText(bean.getTemp_01());
        temp_02.setText(bean.getTemp_02());
        temp_03.setText(bean.getTemp_03());
        temp_04.setText(bean.getTemp_04());
        temp_05.setText(bean.getTemp_05());
        temp_06.setText(bean.getTemp_06());

        week_04.setText(bean.getWeek_04());
        week_05.setText(bean.getWeek_05());
        week_06.setText(bean.getWeek_06());

        mRainfall.setText(bean.getRainfall() + "%");
        mHumidity.setText(bean.getHumidity() + "%");
        mWinddirection.setText(bean.getWinddirection());
    }

    //通过图片名获取资源ID
    public  int getResource(String img) {
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
                    }
                });
            }
        });
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        collapsingToolbarLayout.setTitle(prefs.getString("city", "SpeedWeather"));
        week.setText(prefs.getString("week", "未知"));
        temp.setText(prefs.getString("temp", "0") + "°");
        weather.setText(prefs.getString("weather", "未知"));
        mCloth_tx.setText("穿衣指数——" + prefs.getString("cloth_tx", ""));
        mCloth_content.setText(prefs.getString("cloth_content", ""));
        mSport_tx.setText("运动指数——" + prefs.getString("sport_tx", ""));
        mSport_content.setText(prefs.getString("sport_content", ""));
        mAir_conditioning_tx.setText("空调指数——" + prefs.getString("air_conditioning_tx", ""));
        mAir_conditioning_content.setText(prefs.getString("air_conditioning_content", ""));
        mUltraviolet_radiation_tx.setText("紫外线指数——" + prefs.getString("ultraviolet_radiation_tx", ""));
        mUltraviolet_radiation_content.setText(prefs.getString("ultraviolet_radiation_content", ""));
        img_01.setImageResource(getResource(prefs.getString("img01", "img999")));
        img_02.setImageResource(getResource(prefs.getString("img02", "img999")));
        img_03.setImageResource(getResource(prefs.getString("img03", "img999")));
        img_04.setImageResource(getResource(prefs.getString("img04", "img999")));
        img_05.setImageResource(getResource(prefs.getString("img05", "img999")));
        img_06.setImageResource(getResource(prefs.getString("img06", "img999")));

        temp_01.setText(prefs.getString("temp01", ""));
        temp_02.setText(prefs.getString("temp02", ""));
        temp_03.setText(prefs.getString("temp03", ""));
        temp_04.setText(prefs.getString("temp04", ""));
        temp_05.setText(prefs.getString("temp05", ""));
        temp_06.setText(prefs.getString("temp06", ""));

        week_04.setText(prefs.getString("week04", ""));
        week_05.setText(prefs.getString("week05", ""));
        week_06.setText(prefs.getString("week06", ""));

        mRainfall.setText(prefs.getString("rainfall", "降雨量") + "%");
        mHumidity.setText(prefs.getString("humidity", "湿度") + "%");
        mWinddirection.setText(prefs.getString("winddirection", "None"));
    }

    public static void saveMessage(Context context, String air_conditioning_tx,
                                   String air_conditioning_content, String sport_tx,
                                   String sport_content, String ultraviolet_radiation_tx,
                                   String ultraviolet_radiation_content, String cloth_tx,
                                   String cloth_content, String mCity, String mWeek, String mTemp,
                                   String mWeather, String img, String img01, String img02,
                                   String img03, String temp01, String temp02, String temp03,
                                   String cityID, String rainfall, String humidity, String winddirection,
                                   String week04, String week05, String week06, String week07,
                                   String img04, String img05, String img06, String img07,
                                   String temp04, String temp05, String temp06, String temp07) {
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(context).edit();
//        editor.putString("cityID", cityID);
        editor.putString("rainfall", rainfall);
        editor.putString("humidity", humidity);
        editor.putString("winddirection", winddirection);
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
        editor.putString("img04", img04);
        editor.putString("img05", img05);
        editor.putString("img06", img06);
        editor.putString("img07", img07);
        editor.putString("temp01", temp01);
        editor.putString("temp02", temp02);
        editor.putString("temp03", temp03);
        editor.putString("temp04", temp04);
        editor.putString("temp05", temp05);
        editor.putString("temp06", temp06);
        editor.putString("temp07", temp07);
        editor.putString("week04", week04);
        editor.putString("week05", week05);
        editor.putString("week06", week06);
        editor.putString("week07", week07);
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (resultCode) {
                case 0:
                    myCity = data.getStringExtra("city_name");
                    Snackbar.make(layout, myCity,
                            Snackbar.LENGTH_LONG).show();
                    try {
                        address = "https://free-api.heweather.com/v5/weather?city=" + URLEncoder.encode(myCity, "UTF-8")
                                + "&key=b727f217188c4e8a91ecba4d349c73ff";
                        Log.i("Rao", address);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mHttpuility = new Httpuility(address, new HttpCallBackListener() {
                        @Override
                        public void onFinish(String response) {
                            try {
                                Log.i("Rao", response);
                                analysis(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                    break;
                case 1:
                    isExit = false;
                    myCity = data.getStringExtra("city_name");
                    for (int i = 0; i < mMoreCityModels.size(); i++) {
                        if (myCity.equals(mMoreCityModels.get(i).getCity())) {
                            Snackbar.make(layout, "此城市已存在",
                                    Snackbar.LENGTH_LONG).show();
                            isExit = true;
                            break;
                        }
                    }
                    if (!isExit) {
                        Snackbar.make(layout, "添加城市：" + myCity,
                                Snackbar.LENGTH_LONG).show();
                        try {
                            address = "https://free-api.heweather.com/v5/weather?city=" + URLEncoder.encode(myCity, "UTF-8")
                                    + "&key=b727f217188c4e8a91ecba4d349c73ff";
                            Log.i("Rao", address);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mHttpuility = new Httpuility(address, new HttpCallBackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    addMoreCity(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                            }
                        });
                    }
                    mViewPager.setCurrentItem(1);
                    break;
            }
        }
    }

    private void addMoreCity(String response) {
        Gson gson = new Gson();
        DataBean bean = gson.fromJson(response, DataBean.class);
        MoreCityModel moreCityModel = new MoreCityModel();
        moreCityModel.setCity(bean.getHeWeather5().get(0).getBasic().getCity());
        moreCityModel.setTemp(bean.getHeWeather5().get(0).getNow().getTmp());
        moreCityModel.setWeather(getResource("img" + bean.getHeWeather5().get(0).getDaily_forecast().get(0).getCond().getCode_d()));
        moreCityModel.setMax(bean.getHeWeather5().get(0).getDaily_forecast().get(0).getTmp().getMax());
        moreCityModel.setMin(bean.getHeWeather5().get(0).getDaily_forecast().get(0).getTmp().getMin());
        mMoreCityModels.add(moreCityModel);
        setDataForList("moreCity", mMoreCityModels);
        mHandler.sendEmptyMessage(REFRESH_RECYCLERVIEW);
        mHandler.sendEmptyMessage(MORECITY_ISEXIT);
    }

    public void analysis(String response) {
        Gson gson = new Gson();
        DataBean bean = gson.fromJson(response, DataBean.class);
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        Date date04 = null;
        Date date05 = null;
        Date date06 = null;
        Date date07 = null;
        try {
            date = fmt.parse(bean.getHeWeather5().get(0).getDaily_forecast().get(0).getDate());
            date04 = fmt.parse(bean.getHeWeather5().get(0).getDaily_forecast().get(3).getDate());
            date05 = fmt.parse(bean.getHeWeather5().get(0).getDaily_forecast().get(4).getDate());
            date06 = fmt.parse(bean.getHeWeather5().get(0).getDaily_forecast().get(5).getDate());
            date07 = fmt.parse(bean.getHeWeather5().get(0).getDaily_forecast().get(6).getDate());
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
        saveMessage(getApplicationContext(), air_conditioning_tx, air_conditioning_content, sport_tx,
                sport_content, ultraviolet_radiation_tx, ultraviolet_radiation_content, cloth_tx, cloth_content,
                mCity, mWeek, mTemp, mWeather, img, img01, img02, img03, temp01, temp02, temp03,
                CityID, rainfall, humidity, winddirection, week04, week05, week06, week07, img04, img05, img06, img07,
                temp04, temp05, temp06, temp07);
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
        bean1.setImg_04(img04);
        bean1.setImg_05(img05);
        bean1.setImg_06(img06);
        bean1.setImg_07(img07);
        bean1.setTemp_01(temp01);
        bean1.setTemp_02(temp02);
        bean1.setTemp_03(temp03);
        bean1.setTemp_04(temp04);
        bean1.setTemp_05(temp05);
        bean1.setTemp_06(temp06);
        bean1.setTemp_07(temp07);
        bean1.setWeek_04(week04);
        bean1.setWeek_05(week05);
        bean1.setWeek_06(week06);
        bean1.setWeek_07(week07);
        bean1.setRainfall(rainfall);
        bean1.setHumidity(humidity);
        bean1.setWinddirection(winddirection);
        Message message = new Message();
        message.obj = bean1;
        mHandler.sendMessage(message);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case BAIDU_GPS_OPEN_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理
                    mLocationClient.start();
                } else {
                    Snackbar.make(layout, "获取定位权限失败", Snackbar.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1500);
    }


    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
//            if (location.getDistrict()!= null) {
//                setTheLocalCity(location.getDistrict());
//            } else {
            setTheLocalCity(location.getCity()
                    .replace("市", "")
                    .replace("区", ""));
//            }
            Log.i("clarkRao", location.getCity());
            Log.i("clarkRao", location.getDistrict());
        }

    }


    private void setTheLocalCity(String city) {
        mSwipeRefreshLayout.setRefreshing(true);
        Snackbar.make(layout, "当前城市：" + city,
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
        mHandler.sendEmptyMessageDelayed(REFRESH_LOCALCITY,2000);
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.showToast(getApplicationContext(), "再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}

