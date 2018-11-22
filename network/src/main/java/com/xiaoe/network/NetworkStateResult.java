package com.xiaoe.network;

/**
 * @author flynnWang
 * @date 2018/11/22
 * <p>
 * 描述：网络状态
 */
public class NetworkStateResult {
    /**
     * 网络异常
     */
    public static final int ERROR_NETWORK = 111111;

    private int code = ERROR_NETWORK;
    private String msg;
    private String networkType;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }
}
