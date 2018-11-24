package com.xiaoe.shop.wxb.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.xiaoe.common.utils.NetUtils;
import com.xiaoe.shop.wxb.interfaces.OnNetChangeListener;

public class NetBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "NetBroadcastReceiver";
    public OnNetChangeListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // 如果相等的话就说明网络状态发生了变化
        String netWorkState = NetUtils.getNetworkType(context);
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            // 当网络发生变化，判断当前网络状态，并通过NetEvent回调当前网络状态
            if (listener != null) {
                listener.onNetworkChangeListener(netWorkState);
            }
        }
    }

    public void setOnNetChangeListener(OnNetChangeListener listener) {
        this.listener = listener;
    }
}
