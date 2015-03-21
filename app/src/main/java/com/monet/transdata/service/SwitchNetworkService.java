package com.monet.transdata.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;

import com.monet.transdata.util.SwitchCallbackListener;
import com.monet.transdata.util.SwitchNetworkUtil;
import com.monet.transdata.util.TelephoneUtil;

/**
 * Created by Monet on 2015/3/19.
 */
public class SwitchNetworkService extends Service{
    private int countSwitchWifi;
    private static final int MAX_SWITCH_NUM = 3;
    private int mobileDataSignal;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        countSwitchWifi = 0;
        MobileDataRSSIListener mobileDataRSSIListener = new MobileDataRSSIListener();
        TelephoneUtil telephoneUtil = new TelephoneUtil(this);
        telephoneUtil.listenToEvent(mobileDataRSSIListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String failType = intent.getStringExtra("FAIL_TYPE");
        if(failType!=null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //限制wifi切换的次数，因为可能会在不可用wifi中循环切换
                    //这就要求保存的wifi配置最好至少有一个能够上网，避免保存不可用的wifi配置
                    if (countSwitchWifi < MAX_SWITCH_NUM) {
                        switchNetwork(failType, mobileDataSignal);
                    }else {
                        countSwitchWifi = 0;
                        switchNetwork("others", mobileDataSignal);
                    }
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void switchNetwork(String failType,int mobileSignalVale) {
        if (failType.equals("wifibutfail")) {
            countSwitchWifi++;
        }
        Log.e("test", "put mobileSignal with " + mobileSignalVale);
        SwitchNetworkUtil.handleSwitch(this, failType, mobileSignalVale, new SwitchCallbackListener() {
            @Override
            public void onFinish() {
                //留作以后动态更新检测时间？否则还真没想到用途。。。
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }

    private class MobileDataRSSIListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength){
            super.onSignalStrengthsChanged(signalStrength);//调用超类的该方法，在网络信号变化时得到回答信号
            /**
             * 还有一个方法是signalStrength.getGsmSignalStrength()，得到的是asu（alone signal unit 独立信号单元）
             * CDMA用getCdmaDbm,得到dbm
             * GSM用getGsmSignalStrenth,得到asu
             * 要用两种不同单位也是醉了
             * 而dbm是1毫瓦的分贝数，dBm =-113+2*asu
             * 接受电平>=-90dBm，就可以满足覆盖要求
             */
            mobileDataSignal = -113 + 2 * signalStrength.getGsmSignalStrength();
            Log.e("test", "Mobile signal change to:" + mobileDataSignal);
        }

    }
}


