package com.monet.transdata.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Monet on 2015/3/16.
 */
public class Utility {
    public static final String NETWORK_TYPE_WIFI       = "wifi";
    public static final String NETWORK_TYPE_3G         = "eg";
    public static final String NETWORK_TYPE_2G         = "2g";
    public static final String NETWORK_TYPE_WAP        = "wap";
    public static final String NETWORK_TYPE_UNKNOWN    = "unknown";
    public static final String NETWORK_TYPE_DISCONNECT = "disconnect";
    public static final String NETWORK_TYPE_WIFIBUTFAIL = "wifibutfail";
    public static final String NETWORK_TYPE_EGBUTFAIL = "egbutfail";

    /**
     * Get network type name
     *
     * @param context
     * @return
     */
    public static String getNetworkTypeName(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        String type = NETWORK_TYPE_DISCONNECT;
        if (manager == null || networkInfo == null || !networkInfo.isAvailable() || !networkInfo.isConnected()) {
            //Log.v("test", type);
            return type;
        }else {
            String typeName = networkInfo.getTypeName();
            if(!checkConnectWithPing()) {
                if ("WIFI".equalsIgnoreCase(typeName)) {
                    return NETWORK_TYPE_WIFIBUTFAIL;
                }else {
                    return NETWORK_TYPE_EGBUTFAIL;
                }
            }else if ("WIFI".equalsIgnoreCase(typeName)) {
                type = NETWORK_TYPE_WIFI;
            }else if ("MOBILE".equalsIgnoreCase(typeName)) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                type = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NETWORK_TYPE_3G : NETWORK_TYPE_2G)
                        : NETWORK_TYPE_WAP;
            } else {
                type = NETWORK_TYPE_UNKNOWN;
            }
        }
        //Log.v("test", type);
        return type;
    }

    /**
     * Check if it can really connect to Internet
     * for example, some WiFis also need to be log in the web
     */
    private static boolean checkConnectWithPing() {
        try {
            String ip = "www.baidu.com";
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 5 " + ip);// ping网址1次 超时为10秒
            // ping的状态
            int status = p.waitFor();
            if (status != 0) {
                return false;
            }else {
                return true;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Whether is fast mobile network
     *
     * @param context
     * @return
     */
    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return false;
        }

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }


    /**
     * Get network type
     *
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
        return networkInfo == null ? -1 : networkInfo.getType();
    }


    public static void saveNetworkInfo(Context context, String networkTypeName) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("network_type_name", networkTypeName);
        editor.putString("current_time", sdf.format(new Date()));
        editor.commit();
    }

    public static void setMobileDataEnabled(Context context, boolean enabled) {
        try {
            final ConnectivityManager conman = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class
                    .forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass
                    .getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField
                    .get(conman);
            final Class iConnectivityManagerClass = Class
                    .forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}