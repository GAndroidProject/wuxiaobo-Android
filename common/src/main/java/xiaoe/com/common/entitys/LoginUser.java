package xiaoe.com.common.entitys;

import java.util.UUID;

public class LoginUser {

    private String rowId;
    private String id;
    private String wxOpenId;
    private String wxUnionId;
    private String api_token;

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
}
