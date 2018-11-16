package com.xiaoe.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * Created by Administrator on 2017/8/17.
 */

public class NetworkState {

    /**
     * 判断是否有网络连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    /**
     * 判断WIFI网络是否可用
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static String getNewContent(String htmltext){
        if(TextUtils.isEmpty(htmltext)){
            return "";
        }
        String style = "<style type='text/css'>img { max-width: 100%; width: 100%; height: auto; } section {max-width: 100%; width: 100%; }</style>";
        try {
            String doc = style+htmltext;
            //下面代码找到对应的img标签，然后更改样式
//            Document doc= Jsoup.parse(htmltext);
//            Elements elements=doc.getElementsByTag("img");
//            for (Element element : elements) {
//                element.attr("style","");
//                element.attr("width","100%").attr("height","auto");
//            }
//            elements=doc.getElementsByTag("a");
//            for (Element element : elements) {
//                element.attr("style","");
//            }
            return doc.toString();
        } catch (Exception e) {
            return htmltext;
        }
    }
}
