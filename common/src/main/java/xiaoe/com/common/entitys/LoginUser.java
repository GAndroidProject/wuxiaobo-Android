package xiaoe.com.common.entitys;

import java.util.UUID;

public class LoginUser {

    private String rowId;
    private String id;
    private String wxOpenId;
    private String wxUnionId;
    private String api_token;

    private String userId;
    private String wxNickname;
    private String wxAvatar;
    private String phone;
    private String shopId;

    public LoginUser() {
        this.rowId = UUID.randomUUID().toString();
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public void setWxUnionId(String wxUnionId) {
        this.wxUnionId = wxUnionId;
    }

    public void setApi_token(String api_token) {
        this.api_token = api_token;
    }

    public String getRowId() {
        return rowId;
    }

    public String getId() {
        return id;
    }

    public String getWxOpenId() {
        return wxOpenId;
    }

    public String getWxUnionId() {
        return wxUnionId;
    }

    public String getApi_token() {
        return api_token;
    }

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
