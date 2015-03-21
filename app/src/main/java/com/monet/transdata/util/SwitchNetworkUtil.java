package com.monet.transdata.util;

import android.content.Context;
import android.util.Log;

/**
 * Created by Monet on 2015/3/20.
 */
public class SwitchNetworkUtil {
    public static void handleSwitch(final Context context, final String failType, final int mobileSignalVale, final SwitchCallbackListener switchListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建一个wifi管理类
                    WifiUtil wifiUtil = new WifiUtil(context);
                    if (failType.equals("disconnect")) {
                        //把wifi和mobile data都打开，当然wifi的优先级在Android中比较高
                        wifiUtil.openWifi();
                        Utility.setMobileDataEnabled(context, true);
                        Log.e("test", "2-1 step disconnect");

                    } else if (failType.equalsIgnoreCase("wifibutfail")) {
                        //更换下一个wifi
                        Log.e("test", "2-2-1 step wifibutfail");
                        wifiUtil.switchToNextWifi();
                        TelephoneUtil telephoneUtil = new TelephoneUtil(context);
                        //此处已经打开另一个wifi了,然后和mobile data的信号强度进行比较
                        if (wifiUtil.getRSSI() < mobileSignalVale) {
                            Log.e("test", "2-2-2 step " + wifiUtil.getRSSI() + " and " + mobileSignalVale);
                            wifiUtil.closeWifi();
                            Utility.setMobileDataEnabled(context, true);
                        }
                    } else if (failType.equalsIgnoreCase("egbutfail")) {
                        //mobiledata无法连接，切换至wifi
                        wifiUtil.openWifi();
                        //Utility.setMobileDataEnabled(context, false);
                        Log.e("test", "2-3 step egbutfail");
                    } else {
                        wifiUtil.closeWifi();
                        Utility.setMobileDataEnabled(context, true);
                        Log.e("test", "2-4 step Enable mobile data");
                    }
                    if (switchListener != null) {
//                       回调onFinish()方法
                        switchListener.onFinish();
                    }
                } catch (Exception e) {
                    if (switchListener != null) {
//                       回调onError()方法
                        switchListener.onError(e);
                    }
                }
            }
        }).start();
    }
}
