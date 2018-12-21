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
        String style = "<style type=\"text/css\"> img {" +
                "height: auto;"+
                "width:100%;" +//限定图片宽度填充屏幕
                "height:auto;}" +//限定图片高度自动
                " section {" +
                "max-width: 100%;"+
                "width: 100%;}"+
                "body {" +
                "word-wrap:break-word;"+//允许自动换行(汉字网页应该不需要这一属性,这个用来强制英文单词换行,类似于word/wps中的西文换行)
                "}" +
                "</style>" +
                "<style type=\"text/css\">iframe {display: block;max-width:100%;\n" +  //视频适应屏幕
                "margin:10px;}</style>" +
                "<style type=\"text/css\"> p { word-break:break-all; } </style>";

        try {
//            String doc = "<html><header>" + style +"</header>"+htmltext+"</html>";
            String doc = style +htmltext;
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
