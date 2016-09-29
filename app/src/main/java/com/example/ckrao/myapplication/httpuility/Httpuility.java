package com.example.ckrao.myapplication.httpuility;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.example.ckrao.myapplication.DataBean;
import com.example.ckrao.myapplication.R;
import com.example.ckrao.myapplication.WeatherBean;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CKRAO on 2016/9/27.
 */

public class Httpuility {
    private Context mContext;
    public Httpuility(final String address, final HttpCallBackListener listener) {
       new Thread(new Runnable() {
           @Override
           public void run() {
               HttpURLConnection connection =null;
               try {
                   URL url = new URL(address);
                   connection = (HttpURLConnection) url.openConnection();
                   connection.setRequestMethod("GET");
                   connection.setReadTimeout(8000);
                   connection.setConnectTimeout(8000);
                   InputStream stream = connection.getInputStream();
                   BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                   StringBuilder response = new StringBuilder();
                   String line;
                   while ((line=reader.readLine())!=null){
                       response.append(line);
                   }
                   if (listener !=null){
                       listener.onFinish(response.toString());
                   }

               } catch (Exception e) {
                   e.printStackTrace();
                   if (listener!=null){
                       listener.onError(e);
                   }
               }finally {
                   if (connection!=null){
                       connection.disconnect();
                   }
               }
           }
       }).start();

//        mContext = context;
//        OkHttpClient okHttpClient = new OkHttpClient();
//        final Request request = new Request.
//                Builder().
//                url(address).
//                build();
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String weatherJson = response.body().toString();
//                Gson gson = new Gson();
//                DataBean bean =new DataBean();
//                 bean.setCity(gson.fromJson(weatherJson, WeatherBean.class).getResult().getCity().toString()) ;
//                Log.d("City",gson.fromJson(weatherJson, WeatherBean.class).getResult().getCity().toString());
//
//            }
//        });

    }
}
