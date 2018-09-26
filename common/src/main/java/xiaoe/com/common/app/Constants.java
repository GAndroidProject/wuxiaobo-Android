package xiaoe.com.common.app;

import android.Manifest;

/**
 * Created by Administrator on 2017/7/17.
 */

public class Constants {
    //应用appid 测试环境
    private static final String testAppid = "apppcHqlTPT3482";
    //应用appid 正式环境
    private static final String formalAppid = "appuAhZGRFx3075";

    //微信登录测试环境
//    private static final String wxLoginAppIdTest = "wx3514f0d221328921";
    private static final String wxLoginAppIdTest = "wxfe3c46716c7587ee";
    //微信登录正式环境
    private static final String wxLoginAppIdFormal = "wx0b41f790886f724e";

    public static final String AUDIO_PLAY_RESOURCE_ID = "audioPlayResourceid";
    //上一次购买资源的id
    public static final String LAST_BUY_RESOURCE_ID = "lastBuyResourceId";
    //最近一次购买资源的id
    public static final String RECENTLY_BUY_RESOURCE_ID = "recentlyBuyResourceId";
    //支付成功
    public static final String SUCCEED_PAY = "succeedPay";
    //缓存有效持续时间(秒)
    public static final int CACHE_DURATION = 432000;//5天
    //Bugly测试环境
    private static final String TEST_BUGLY_ID = "ff632cf71b";
    private static final String FORMAL_BUGLY_ID = "ff632cf71b";
//    Bugly正式环境
//    private static final String FORMAL_BUGLY_ID = "05b0bd6eec";
    //一小时秒数
    public static final int HOUR_SECOND = 3600;
    //一小时毫秒数
    public static final int HOUR_MSEC = HOUR_SECOND * 1000;
    //一天秒数
    public static final int DAY_SECOND = HOUR_SECOND * 24;
    //一天毫秒数
    public static final int DAY_MSEC = DAY_SECOND * 1000;
    public static final int USE_TYPE = 14;
    //权限常量集
    public static String[] permissions = {Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //读写权限
    public static String[] READ_WRITE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String[] mTabNames = {"首页", "已购", "我的"};
    //获取微信登录appid
    public static String getWxLoginAppId(){
        if(XiaoeApplication.isFormalCondition()){
            return wxLoginAppIdFormal;
        }
        return wxLoginAppIdTest;
    }
    //获取应用appid
    public static String getAppId(){
        if(XiaoeApplication.isFormalCondition()){
            return formalAppid;
        }
        return testAppid;
    }
    //获取bugly应用appid
    public static String getBuglyAppId(){
        if(XiaoeApplication.isFormalCondition()){
            return FORMAL_BUGLY_ID;
        }
        return TEST_BUGLY_ID;
    }
}
