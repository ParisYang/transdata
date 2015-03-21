package com.monet.transdata.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.monet.transdata.net.SocketCallback;
import com.monet.transdata.net.SocketConnect;

/**
 * Created by Monet on 2015/3/16.
 */
public class StableTcpService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //adaptNetwork()会生成线程
        adaptNetwork();
        return super.onStartCommand(intent, flags, startId);
    }

    private void adaptNetwork(){
        SocketConnect connect = new SocketConnect(new SocketCallback() {

            @Override
            public void receive(byte[] buffer) {
                Log.v("server","Server Message ：" + new String(buffer));
            }

            @Override
            public void disconnect() {
                Log.v("server", "disconnect!!!");
            }

            @Override
            public void connected() {
                Log.v("server", "connect!!!");
            }


        });
        connect.setRemoteAddress("localhost", 11011);
        new Thread(connect).start();
        for(int i=0; i<100; i++){
            connect.write("test".getBytes());
            try{
               Thread.sleep(1000);
            }catch(InterruptedException ie) {
                Log.i("cuowu", "thread interruptException1");
            }
        }

    }

}
