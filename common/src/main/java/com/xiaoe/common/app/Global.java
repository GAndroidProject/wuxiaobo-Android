package com.xiaoe.common.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.UUID;

import com.xiaoe.common.entitys.DeviceInfo;

/**
 * Created by Administrator on 2017/5/25.
 */
public class Global
{
    private   Application application = null;

    private static Global ourInstance = new Global();

    public static Global g() {
        return ourInstance;
    }

    private Global() {
    }

    public void setApplication(Application application) {
        this.application = application;
        handler = new Handler(application.getMainLooper());
    }

    private  Handler handler;
    public void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    };
    public Point getDisplayPixel(){
        WindowManager windowManager = (WindowManager) application.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point;
    }
    public DeviceInfo getDeviceInfo(){
        Context context = application.getApplicationContext();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setVersionName(getVersionName());
        deviceInfo.setBuildLevel("Android "+getBuildLevel());
        deviceInfo.setBuildVersion("Android "+getBuildVersion());
        deviceInfo.setDeviceId(getDeviceId(context));
        deviceInfo.setPhoneBrand(getPhoneBrand());
        deviceInfo.setPhoneModel(getPhoneModel());

        return deviceInfo;
    }
    /**
     * 返回版本名字
     * 对应build.gradle中的versionName
     *
     * @return
     */
    public String getVersionName() {
        Context context = application.getApplicationContext();
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (Exception e) {
//            e.printStackTrace();
            return "";
        }
        return versionName;
    }
    /**
     * 获取设备的唯一标识，deviceId
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = null;
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            deviceId = tm.getDeviceId();
        }
        if (deviceId == null) {
            return "";
        } else {
            return deviceId;
        }
    }
    // 计算出该TextView中文字的长度(像素)
    public int getTextViewLine(TextView textView, String text, int maxWidth){
        if(textView == null || text ==null || TextUtils.isEmpty(text.trim())){
            return 1;
        }
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候,像素为多少
        float textLength = paint.measureText(text);
        int line = (int) (textLength/maxWidth) + 1;
        return line;
    }
    /**
     * 获取手机品牌
     *
     * @return
     */
    public static String getPhoneBrand() {
        return android.os.Build.BRAND;
    }
    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }
    /**
     * 获取手机Android API等级（22、23 ...）
     *
     * @return
     */
    public static int getBuildLevel() {
        return android.os.Build.VERSION.SDK_INT;
    }
    /**
     * 获取手机Android 版本（4.4、5.0、5.1 ...）
     *
     * @return
     */
    public static String getBuildVersion() {
        return android.os.Build.VERSION.RELEASE;
    }
    /**
     * 默认目录
     * @return
     */
    public String getDefaultDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "wxb" + File.separator;
    }
    public String getUUID(){
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        return uuid;
    }

    /**
     * 获取sd卡总大小
     * 单位字节B
     */
    public long getSDTotalSize() {
        long size = 0;
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            size = blockSize*blockCount;
        }
        return size;
    }

    /**
     * 获取可用大小
     * 单位字节B
     */
    public long getSDAvailableSize(){
        long size = 0;
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long availCount = sf.getAvailableBlocks();
            size = blockSize*availCount;
        }
        return size;
    }
    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }
    /**
     * 检查权限
     * @return
     */
    public boolean checkPermissions(String permission){
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(application.getApplicationContext(),
                permission);
        if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {
            //有权限。
            return true;
        }
        return false;
    }
    /**
     * 申请权限
     * @param activity
     * @param permissions 权限集
     */
    public void requestPermissions(Activity activity, String[] permissions){
        ActivityCompat.requestPermissions(activity,permissions,1);
    }

    //全局颜色，默认白色
    private String globalColor = "#ffffff";

    public String getGlobalColor() {
        return globalColor;
    }

    public void setGlobalColor(String globalColor) {
        this.globalColor = globalColor;
    }


    public  String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public  String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }



    //根据IP获取本地Mac
    public  String getLocalMacAddressFromIp(Context context) {
        String mac_s= "";
        try {
            byte[] mac;
            NetworkInterface ne=NetworkInterface.getByInetAddress(InetAddress.getByName(getIPAddress(context)));
            mac = ne.getHardwareAddress();
            mac_s = byte2hex(mac);
        } catch (Exception e) {
            // 获取 mac 地址失败，断网的情况下会这样 TODO 网络异常提示
            e.printStackTrace();
        }

        return mac_s;
    }

    public  String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                hs = hs.append(stmp);
            }
        }
        return String.valueOf(hs);
    }
}
