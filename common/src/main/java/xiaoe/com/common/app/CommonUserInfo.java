package xiaoe.com.common.app;

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
