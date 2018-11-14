package xiaoe.com.common.app;

import android.Manifest;

/**
 * Created by Administrator on 2017/7/17.
 */

public class Constants {
    //应用appid 测试环境
//    private static final String testAppid = "appiOW1KfWe9943";
    private static final String testAppid = "apppcHqlTPT3482";
    //应用appid 正式环境
    private static final String formalAppid = "appuAhZGRFx3075";

    //微信测试环境
//    private static final String wxLoginAppIdTest = "wx3514f0d221328921";
    private static final String wxAppIdTest = "wx764341f522a6c929";
    //微信开放平台SECRET  测试
    private static final String WX_SECRET_TEST ="3232dcd861af17be187ca219d535323e";
    //微信正式环境
    private static final String wxAppIdFormal = "wx764341f522a6c929";
    //微信开放平台SECRET  正式
    private static final String WX_SECRET_FORMAL ="3232dcd861af17be187ca219d535323e";

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
    private static final String TEST_BUGLY_ID = "417b763f59";
    //Bugly正式环境
    private static final String FORMAL_BUGLY_ID = "417b763f59";
    //一小时秒数
    public static final int HOUR_SECOND = 3600;
    //一小时毫秒数
    public static final int HOUR_MSEC = HOUR_SECOND * 1000;
    //一天秒数
    public static final int DAY_SECOND = HOUR_SECOND * 24;
    //一天毫秒数
    public static final int DAY_MSEC = DAY_SECOND * 1000;
    public static final int USE_TYPE = 14;
    //友盟集成appId  测试
    private static final String U_MENG_APP_ID_TEST = "5bdac3fbf1f556303b0000ad";
    //友盟集成appId  正式
    private static final String U_MENG_APP_ID_FORMAL = "5bdac3fbf1f556303b0000ad";
    //权限常量集
    public static String[] permissions = {Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //读写权限
    public static String[] READ_WRITE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String[] mTabNames = {"首页", "已购", "我的"};

    // 奖学金和积分相关信息
    public static final String SCHOLARSHIP_ASSET_TYPE = "profit"; // 奖学金
    public static final String INTEGRAL_ASSET_TYPE = "credit";    // 积分
    public static final int NEED_FLOW = 1;
    public static final int NO_NEED_FLOW = 0;
    public static final int WITHDRAWAL_FLOW_TYPE = 0;   // 提现流水
    public static final int EARNING_FLOW_TYPE = 1;      // 入账记录

    //获取微信登录appid
    public static String getWXAppId(){
        if(XiaoeApplication.isFormalCondition()){
            return wxAppIdFormal;
        }
        return wxAppIdTest;
    }
    public static String getWxSecret(){
        if(XiaoeApplication.isFormalCondition()){
            return WX_SECRET_FORMAL;
        }
        return WX_SECRET_TEST;
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
    public static String getUMAppId(){
        if(XiaoeApplication.isFormalCondition()){
            return U_MENG_APP_ID_FORMAL;
        }
        return U_MENG_APP_ID_TEST;
    }
}
