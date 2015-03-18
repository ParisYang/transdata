package com.monet.transdata.util;

/**
 * Created by Monet on 2015/3/17.
 */
public interface NetworkTypeCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
