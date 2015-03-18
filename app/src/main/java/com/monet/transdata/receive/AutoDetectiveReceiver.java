package com.monet.transdata.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.monet.transdata.service.AutoDetectiveService;

/**
 * Created by Monet on 2015/3/17.
 */
public class AutoDetectiveReceiver extends BroadcastReceiver {
    /**
     * 在onReceive方法中再次启动AutoDetectiveService就可以实现后台定时检测网络状态功能了
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoDetectiveService.class);
        context.startService(i);
    }
}

