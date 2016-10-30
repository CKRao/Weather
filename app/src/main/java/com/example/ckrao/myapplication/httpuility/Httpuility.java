package com.example.ckrao.myapplication.httpuility;

import android.content.Context;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


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
    }
}
