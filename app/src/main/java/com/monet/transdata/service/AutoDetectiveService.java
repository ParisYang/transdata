package com.monet.transdata.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.monet.transdata.receive.AutoDetectiveReceiver;
import com.monet.transdata.util.NetworkTypeCallbackListener;
import com.monet.transdata.util.NetworkUtil;
import com.monet.transdata.util.Utility;

/**
 * Created by Monet on 2015/3/17.
 */
public class AutoDetectiveService  extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                detectiveNetwork();

            }
        }).start();
        Intent uiIntent = new Intent();
        uiIntent.setAction("UI_UPDATE_ACTION");
        sendBroadcast(uiIntent);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int aGap = 1000; //毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + aGap;
        Intent i = new Intent(this, AutoDetectiveReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void detectiveNetwork(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        NetworkUtil.handleNetwork(AutoDetectiveService.this,new NetworkTypeCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.saveNetworkInfo(AutoDetectiveService.this, response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }



}

