package xiaoe.com.common.entitys;

import java.util.UUID;

public class LoginUserInfo {

    private String userId;
    private String wxNickname;
    private String wxAvatar;
    private String phone;
    private String shopId;

    public void setWxNickname(String wxNickname) {
        this.wxNickname = wxNickname;
    }

    public void setWxAvatar(String wxAvatar) {
        this.wxAvatar = wxAvatar;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getWxNickname() {
        return wxNickname;
    }

    public String getWxAvatar() {
        return wxAvatar;
    }

    public String getShopId() {
        return shopId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
