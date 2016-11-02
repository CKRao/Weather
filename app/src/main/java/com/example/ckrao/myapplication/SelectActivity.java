package com.example.ckrao.myapplication;

/**
 * Created by clark on 2016/10/24.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showProgressDialog();
        initViews();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("加载中...");
        }
        mProgressDialog.show();
//        Log.i("mProgressDialog","show()");
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
//            Log.i("mProgressDialog","dismiss()");
        }
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
        final List<CityModel> arrayList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = db.query("city_id", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("city_area"));
                        String id = cursor.getString(cursor.getColumnIndex("city_id"));
                        CityModel model = new CityModel();
                        model.setCityname(name);
                        model.setCityId(id);
                        arrayList.add(model);
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
                        closeProgressDialog();
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
                if (!PreferenceManager.getDefaultSharedPreferences(SelectActivity.this).getBoolean("isChoose", false)) {
                    SharedPreferences.Editor editor = PreferenceManager.
                            getDefaultSharedPreferences(SelectActivity.this).edit();
                    editor.putBoolean("isChoose", true);
                    editor.commit();
                }
                Toast.makeText(getApplication(), ((CitySortModel) adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                String city_id = ((CitySortModel) adapter.getItem(position)).getCityId();
                intent.putExtra("city_id", city_id);
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

    private List<CitySortModel> filledData(final List<CityModel> data) {
        final List<CitySortModel> mSortList = new ArrayList<>();
        final ArrayList<String> indexString = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            CitySortModel sortModel = new CitySortModel();
            sortModel.setName(data.get(i).getCityname());
            sortModel.setCityId(data.get(i).getCityId());
            String pinyin = PinyinUtils.getPingYin(data.get(i).getCityname());
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