package com.example.ckrao.myapplication;

/**
 * Created by clark on 2016/10/24.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectActivity extends Activity {
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog, mTvTitle;
    private SortAdapter adapter;
    private EditTextWithDel mEtCityName;
    private List<CitySortModel> SourceDateList;
    private ImageView back;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                back.setVisibility(View.GONE);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        back = (ImageView) findViewById(R.id.id_background);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);

            }
        }).start();
        initViews();
    }

    private void initViews() {
        mEtCityName = (EditTextWithDel) findViewById(R.id.et_search);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        initDatas();
        initEvents();
        setAdapter();

    }

    private void setAdapter() {
        AssetsDatabaseManager.initManager(getApplication());
        AssetsDatabaseManager am = AssetsDatabaseManager.getManager();
        final SQLiteDatabase db = am.getDatabase("cityid.db");
        final List<String> arrayList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = db.query("city_id", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("city_area"));
                        arrayList.add(name);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SourceDateList = filledData(arrayList);
                        Collections.sort(SourceDateList, new PinyinComparator());
                        adapter = new SortAdapter(SelectActivity.this, SourceDateList);
//                        sortListView.addHeaderView(initHeadView());
                        sortListView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    private void initEvents() {
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position + 1);
                }
            }
        });


        //ListView的点击事件
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Toast.makeText(getApplication(), ((CitySortModel) adapter.getItem(position - 1)).getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                String dizhi = ((CitySortModel) adapter.getItem(position - 1)).getName();
                intent.putExtra("dizhi", dizhi);
                setResult(0, intent);
//                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                //判断隐藏软键盘是否弹出
                if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
                    //隐藏软键盘
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
                finish();
            }
        });
        //根据输入框输入值的改变来过滤搜索
        mEtCityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initDatas() {
        sideBar.setTextView(dialog);
    }

//    private View initHeadView() {
//        View headView = getLayoutInflater().inflate(R.layout.headview, null);
//        GridView mGvCity = (GridView) headView.findViewById(R.id.gv_hot_city);
//
//        String[] datas = getResources().getStringArray(R.array.city);
//        ArrayList<String> cityList = new ArrayList<>();
//        for (int i = 0; i < datas.length; i++) {
//            cityList.add(datas[i]);
//        }
//
//        CityAdapter cAdapter = new CityAdapter(getApplicationContext(), R.layout.gridview_item, cityList);
//        mGvCity.setAdapter(cAdapter);
//        return headView;
//    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<CitySortModel> mSortList = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            mSortList = SourceDateList;
        } else {
            mSortList.clear();
            for (CitySortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.toUpperCase().indexOf(filterStr.toString().toUpperCase()) != -1 || PinyinUtils.getPingYin(name).toUpperCase().startsWith(filterStr.toString().toUpperCase())) {
                    mSortList.add(sortModel);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(mSortList, new PinyinComparator());
        adapter.updateListView(mSortList);
    }

    private List<CitySortModel> filledData(final List<String> date) {
        final List<CitySortModel> mSortList = new ArrayList<>();
        final ArrayList<String> indexString = new ArrayList<>();
        for (int i = 0; i < date.size(); i++) {
            CitySortModel sortModel = new CitySortModel();
            sortModel.setName(date.get(i));
            String pinyin = PinyinUtils.getPingYin(date.get(i));
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
                if (!indexString.contains(sortString)) {
                    indexString.add(sortString);
                }
            }
            mSortList.add(sortModel);
        }
        Collections.sort(indexString);
        sideBar.setIndexText(indexString);
        return mSortList;
    }
//    public class DBManager {
//        private String DB_NAME = "cityid.db";
//        private Context mContext;
//
//        public DBManager(Context mContext) {
//            this.mContext = mContext;
//        }
//
//        //把assets目录下的db文件复制到dbpath下
//        public SQLiteDatabase DBManager(String packName) {
//            String dbPath = "/data/data/" + packName
//                    + "/databases/" + DB_NAME;
//            if (!new File(dbPath).exists()) {
//                try {
//                    FileOutputStream out = new FileOutputStream(dbPath);
//                    InputStream in = mContext.getAssets().open("cityid.db");
//                    byte[] buffer = new byte[1024];
//                    int readBytes = 0;
//                    while ((readBytes = in.read(buffer)) != -1)
//                        out.write(buffer, 0, readBytes);
//                    in.close();
//                    out.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return SQLiteDatabase.openOrCreateDatabase(dbPath, null);
//        }
//    }
}