package com.monet.transdata.util;

import android.content.Context;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by Monet on 2015/3/21.
 */
public class TelephoneUtil {
    /**
     * TelephonyManager类的对象
     */
    TelephonyManager telephonyManager;

    // 构造器
    public TelephoneUtil(Context context) {
        // 取得WifiManager对象
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public int getRSSI() {
        CellInfoGsm cellinfogsm = (CellInfoGsm)telephonyManager.getAllCellInfo().get(0);
        CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
        return cellSignalStrengthGsm.getDbm();
    }

    public void listenToEvent(PhoneStateListener phoneStateListener,int events) {
        telephonyManager.listen(phoneStateListener,events);
    }
}
