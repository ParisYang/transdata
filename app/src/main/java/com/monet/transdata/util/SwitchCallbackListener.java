package com.monet.transdata.util;

/**
 * Created by Monet on 2015/3/20.
 */
public interface SwitchCallbackListener {
    void onFinish();

    void onError(Exception e);
}
