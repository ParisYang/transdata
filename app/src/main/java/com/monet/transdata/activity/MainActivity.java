package com.monet.transdata.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.monet.transdata.R;
import com.monet.transdata.service.AutoAdaptService;
import com.monet.transdata.service.AutoDetectiveService;
import com.monet.transdata.util.Utility;
import com.monet.transdata.util.WifiAdmin;


public class MainActivity extends Activity implements View.OnClickListener{

    /**
     * 定义颜色
     */
    final static String lighted = "#003366";
    final static String greyed = "#CCCCCC";
    /**
     * 按钮
     */
    private Button startHere;
    /**
     * 显示收到的信息
     */
    private TextView revMessageText;
    /**
     * 显示连接的网络
     */
    private TextView networkTypeNameText;
    /**
     * 显示现在的时间
     */
    private TextView currentTimeText;
    /**
     * 按钮-wifi
     */
    private Button switchWifiButton;
    /**
     * 按钮-mobile_data
     */
    private Button switchMobileDataButton;
    /**
     * 一个广播接收器，用来改变UI
     */
    private UiUpdateReceiver uiUpdateReceiver;
    /**
     * 试验一个接收系统网络状态改变的广播接收器
     */
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //初始化各控件
        startHere = (Button) findViewById(R.id.start_here);
        revMessageText = (TextView) findViewById(R.id.rev_message);
        networkTypeNameText = (TextView) findViewById(R.id.network_type_name);
        currentTimeText = (TextView) findViewById(R.id.current_time);
        switchWifiButton = (Button) findViewById(R.id.switch_wifi);
        switchMobileDataButton = (Button) findViewById(R.id.switch_mobile_data);
        //还要记得注册点击事件监听
        startHere.setOnClickListener(this);
        switchWifiButton.setOnClickListener(this);
        switchMobileDataButton.setOnClickListener(this);
        //激活AutoDetectiveSevice服务
        Intent intent = new Intent(this, AutoDetectiveService.class);
        startService(intent);
        //showRequest();
        showNetwork();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_here:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            WifiAdmin wifiAdmin = new WifiAdmin(MainActivity.this);
                            wifiAdmin.openWifi();
                            Log.i("test",Integer.toString(wifiAdmin.checkState()));
                            Log.i("test", wifiAdmin.lookUpScan());
                            Log.i("test", wifiAdmin.lookUpConfig());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.switch_wifi:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switchTo("wifi", lighted, greyed);
                        Toast.makeText(MainActivity.this, "Using [wifi] Now", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.switch_mobile_data:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switchTo("3g", greyed, lighted);
                        Toast.makeText(MainActivity.this, "Using [3g] Now", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册广播接收器，因依赖activity，故不能在Manifest里注册
        uiUpdateReceiver = new UiUpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("UI_UPDATE_ACTION");
        registerReceiver(uiUpdateReceiver, filter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    private void showRequest() {
        //激活AutoAdaptservice服务
        Intent intent = new Intent(this, AutoAdaptService.class);
        startService(intent);
    }

    private void showNetwork(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        networkTypeNameText.setText(prefs.getString("network_type_name","kong"));
        currentTimeText.setText(prefs.getString("current_time","kong"));

    }

    private void switchTo(String switchType,String wifiColor,String egColor) {
        switchWifiButton.setBackgroundColor(Color.parseColor(wifiColor));
        switchMobileDataButton.setBackgroundColor(Color.parseColor(egColor));
        WifiAdmin wifiAdmin = new WifiAdmin(MainActivity.this);
        if ("wifi".equals(switchType)) {
            wifiAdmin.openWifi();
            Utility.setMobileDataEnabled(MainActivity.this, false);
        }else if ("3g".equals(switchType)) {
            wifiAdmin.closeWifi();
            Utility.setMobileDataEnabled(MainActivity.this, true);
        }
    }

    public class UiUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            showNetwork();
        }
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "network changes", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(uiUpdateReceiver);
        unregisterReceiver(networkChangeReceiver);
        Intent intent = new Intent(this, AutoDetectiveService.class);
        stopService(intent);
    }
}
