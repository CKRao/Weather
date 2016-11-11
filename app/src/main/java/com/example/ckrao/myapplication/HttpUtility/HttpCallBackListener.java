package com.example.ckrao.myapplication.HttpUtility;

/**
 * Created by CKRAO on 2016/9/27.
 */

public interface HttpCallBackListener {
    void onFinish(String response);
    void onError(Exception e);
}
