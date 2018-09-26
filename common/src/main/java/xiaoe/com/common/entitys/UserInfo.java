package xiaoe.com.common.entitys;

/**
 * Created by Administrator on 2017/5/24.
 */

public class UserInfo {
    private static final String TAG = "UserInfo";


    /**
     * encryptData : xiao_eu_591d94ab8d
     * userId : u_1495110827_591d94ab8d270_14140226
     * headerImg : http://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTK8IQbpvT4b7ib2ggEUWkoCxN9Y362HLPX5iaKic5UK4SNIZYBN1bn31qvHH4Yo0yXC26ibRpI5FwF42w/0
     * userName : C zhan
     */

    private String encryptData;
    private String userId;
    private String headerImg;
    private String userName;

    public String getEncryptData() {
        return encryptData;
    }

    public void setEncryptData(String encryptData) {
        this.encryptData = encryptData;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHeaderImg() {
        return headerImg;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
