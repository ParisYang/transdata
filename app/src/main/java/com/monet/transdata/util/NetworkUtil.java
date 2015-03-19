package com.monet.transdata.util;

import android.content.Context;

/**
 * Created by Monet on 2015/3/17.
 */
public class NetworkUtil {
    public static void handleNetwork(final Context context,final NetworkTypeCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response= Utility.getNetworkTypeName(context);
                    if (listener != null) {
//                       回调onFinish()方法
                        listener.onFinish(response);
                    }
                } catch (Exception e) {
                    if (listener != null) {
//                       回调onError()方法
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }

}

