package com.example.ckrao.myapplication.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by clark on 2016/11/5.
 */
public  class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context,AutoUpdateService.class);
        context.startService(intent1);
    }
}

