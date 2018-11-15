package xiaoe.com.common.app;

import java.util.List;

import xiaoe.com.common.db.LoginSQLiteCallback;
import xiaoe.com.common.db.SQLiteUtil;
import xiaoe.com.common.entitys.LoginUser;

public class CommonUserInfo {

    private static String apiToken;
    private static String phone;
    private static String userId;
    private static String wxNickname;
    private static String wxAvatar;
    private static String shopId;
    // 是否为超级会员
    private static boolean isSuperVip;
    // 超级会员是否可用
    private static boolean isSuperVipAvailable;

    private static CommonUserInfo commonUserInfo = null;
    private static LoginUser loginUser = null;

    public static CommonUserInfo getInstance(){
        if(commonUserInfo == null){
            synchronized (CommonUserInfo.class){
                if(commonUserInfo == null){
                    commonUserInfo = new CommonUserInfo();
                    SQLiteUtil.init(XiaoeApplication.getmContext(), new LoginSQLiteCallback());
                    if(!SQLiteUtil.tabIsExist(LoginSQLiteCallback.TABLE_NAME_USER)){
                        SQLiteUtil.execSQL(LoginSQLiteCallback.TABLE_SCHEMA_USER);
                    }
                }
            }
        }
        if(loginUser == null){
            setUserInfo();
        }
        return commonUserInfo;
    }

    public void clearUserInfo(){
        commonUserInfo = null;
    }
    private static void setUserInfo(){
        List<LoginUser> list = SQLiteUtil.query(LoginSQLiteCallback.TABLE_NAME_USER, "select * from " + LoginSQLiteCallback.TABLE_NAME_USER, null);
        if(list != null && list.size() > 0){
            loginUser = list.get(0);
        }
    }
    public static void setShopId(String shopId) {
        CommonUserInfo.shopId = shopId;
    }

    public static String getShopId() {
        return shopId;
    }

    public static void setApiToken(String apiToken) {
        CommonUserInfo.apiToken = apiToken;
    }

    public static void setPhone(String phone) {
        CommonUserInfo.phone = phone;
    }

    public static void setUserId(String userId) {
        CommonUserInfo.userId = userId;
    }

    public static void setWxNickname(String wxNickname) {
        CommonUserInfo.wxNickname = wxNickname;
    }

    public static void setWxAvatar(String wxAvatar) {
        CommonUserInfo.wxAvatar = wxAvatar;
    }

    public static String getApiToken() {
        return apiToken;
    }

    public static String getApiTokenByDB(){
        //双层判断
        if(loginUser == null){
            setUserInfo();
        }
        if(loginUser != null){
            return loginUser.getApi_token();
        }
        return null;
    }

    public static String getPhone() {
        return phone;
    }

    public static String getUserId() {
        return userId;
    }

    public static String getWxNickname() {
        return wxNickname;
    }

    public static String getWxAvatar() {
        return wxAvatar;
    }

    public static void setIsSuperVip(boolean isSuperVip) {
        CommonUserInfo.isSuperVip = isSuperVip;
    }

    public static boolean isIsSuperVip() {
        return isSuperVip;
    }

    public static void setIsSuperVipAvailable(boolean isSuperVipAvailable) {
        CommonUserInfo.isSuperVipAvailable = isSuperVipAvailable;
    }

    public static boolean isIsSuperVipAvailable() {
        return isSuperVipAvailable;
    }
}
