package com.monet.transdata.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.monet.transdata.R;
import com.monet.transdata.service.AutoAdaptService;
import com.monet.transdata.service.AutoDetectiveService;


public class MainActivity extends Activity {

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
        //激活AutoDetectiveSevice服务
        Intent intent = new Intent(this, AutoDetectiveService.class);
        startService(intent);
        //showRequest();
        showNetwork();
    }

    @Override
    protected void onStart() {
        UiUpdateReceiver uiUpdateReceiver = new UiUpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("UI_UPDATE_ACTION");
        //注册广播接收器，因依赖activity，故不能在Manifest里注册
        registerReceiver(uiUpdateReceiver, filter);
        super.onStart();
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

    public class UiUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            showNetwork();
        }
    }


}
