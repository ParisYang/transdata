package com.monet.transdata.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

/**
 * Created by Monet on 2015/3/18.
 */
public class WifiUtil {
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mScanResult;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    WifiManager.WifiLock mWifiLock;

    // 构造器
    public WifiUtil(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
        //获取WifiList
        mScanResult = this.getScanResult();
        mWifiConfiguration = this.getConfiguration();
    }

    // 关键函数：添加一个网络并连接
    public boolean addNetwork(WifiConfiguration wcg) {
        int wcgNetworkID = mWifiManager.addNetwork(wcg);
        boolean success =  mWifiManager.enableNetwork(wcgNetworkID, true);
        Log.i("test--wcgNetworkID", Integer.toString(wcgNetworkID));
        Log.i("test--boolean_enable",Boolean.toString(success));
        return success;
    }

    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 检查当前WIFI状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }


    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        mScanResult = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    // 得到当前扫描的网络列表
    public List<ScanResult> getScanResult() {
        mWifiManager.startScan();
        mScanResult = mWifiManager.getScanResults();
        return mScanResult;
    }

    // 得到已保存的网络
    public List<WifiConfiguration> getConfiguration() {
        mWifiManager.startScan();
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
        return mWifiConfiguration;
    }

    // 查看扫描结果
    public String lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mScanResult.size(); i++) {
            String indexInfo = "Index_" + Integer.toString(i + 1) + ":";
            stringBuilder.append(indexInfo);
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((this.mScanResult.get(i)).toString());
            stringBuilder.append("\n\n");
        }
        return stringBuilder.toString().trim();
    }

    //查看保存的配置文件
    public String lookUpConfig() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiConfiguration.size(); i++) {
            String indexInfo = "Index_" + Integer.toString(i + 1) + ":";
            stringBuilder.append(indexInfo);
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiConfiguration.get(i)).toString());
            stringBuilder.append("\n\n");
        }
        return stringBuilder.toString().trim();
    }
    // 得到MAC地址
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    //得到接入点的SSID
    public String getSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    // 得到IP地址
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    //获取连接的RSSI
    public int getRSSI() {
        return  (mWifiInfo == null) ? 0 : mWifiInfo.getRssi();
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }



    // 断开指定ID的网络
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

//然后是一个实际应用方法，只验证过没有密码的情况：

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.isExists(SSID);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        //分为三种情况：1没有密码2用wep加密3用wpa加密
        if(Type == 1) //WIFICIPHER_NOPASS
        {
            //config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //config.wepTxKeyIndex = 0;
        }
        if(Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+Password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    public WifiConfiguration isExists(String SSID)
    {
        for (WifiConfiguration existingConfig : mWifiConfiguration){
            if (existingConfig.SSID.equals("\""+SSID+"\"")){
                return existingConfig;
            }
        }
        return null;
    }

    public boolean deleteExists(String SSID)
    {
        for (WifiConfiguration existingConfig : mWifiConfiguration)
        {
            if (existingConfig.SSID.equals("\""+SSID+"\"")) {
                WifiConfiguration tempConfig = existingConfig;
                mWifiManager.removeNetwork(tempConfig.networkId);
                mWifiManager.saveConfiguration();
                startScan();
                return true;
            }
        }
        return false;
    }

    //切换至下一个在扫描列表中的可用wifi
    public boolean switchToNextWifi() {
        this.openWifi();
        //注意此处得的getSSID是带引号的，如"CMCC"，和保存的配置一致，但和扫描的不同
        String currentSSID = this.getSSID();
        boolean tOrF = true;
        int rightPosition = 0;
        Log.e("test", "step 3.1 currentSSID" + currentSSID + mScanResult.size());
        //先找到当前wifi处于scanResult中的位置，然后从下一个开始查找
        for (int i = 0; i < mScanResult.size(); i++) {
            ScanResult scanResult = this.mScanResult.get(i);
            if (currentSSID.equals("\"" + scanResult.SSID + "\"") &&
                    !currentSSID.equals("\"" + this.mScanResult.get(i + 1) + "\"")) {
                rightPosition = i + 1;
                //结束整个循环过程，而continue是结束本次循环
                Log.e("test", "step 3.2 nextStartPosition" + rightPosition);
                break;
            }
        }
        //从当前连接wifi开始，遍历一次后结束循环
        for (int i = 0; i < mScanResult.size(); i++) {
            Log.e("test", "step 3.3 xunhuan  " + (i + rightPosition) % mScanResult.size());
            ScanResult scanResult = this.mScanResult.get((i + rightPosition) % mScanResult.size());
            Log.e("test", "step 3.4 scanResult " + scanResult.SSID);
            //如果是当前正在连接的wifi的话，就跳过
            if (!currentSSID.equals("\"" + scanResult.SSID + "\"")) {
                //如果在保存的配置中有该SSID
                for (WifiConfiguration config : mWifiConfiguration) {
                    if (config.SSID.equals("\"" + scanResult.SSID + "\"")) {
                        Log.e("test", "bingo  " + config.SSID);
                        return mWifiManager.enableNetwork(config.networkId, true);
                    }
                }
            }
        }
        Log.e("test", "step 3 oh,sorry");
        return tOrF;
    }
}

