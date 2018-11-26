package com.xiaoe.shop.wxb.common.login;

import android.content.Context;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import com.xiaoe.common.app.Constants;
import com.xiaoe.common.utils.MD5Utils;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.LoginBindRequest;
import com.xiaoe.network.requests.LoginCheckRegisterRequest;
import com.xiaoe.network.requests.LoginCodeVerifyRequest;
import com.xiaoe.network.requests.LoginDoRegisterRequest;
import com.xiaoe.network.requests.LoginFindPwdCodeVerifyRequest;
import com.xiaoe.network.requests.LoginNewCodeVerifyRequest;
import com.xiaoe.network.requests.LoginPhoneCodeRequest;
import com.xiaoe.network.requests.LoginRegisterCodeVerifyRequest;
import com.xiaoe.network.requests.LoginRequest;
import com.xiaoe.network.requests.ResetPasswordRequest;
import com.xiaoe.network.requests.TouristsShopIdRequest;
import com.xiaoe.network.requests.UpdatePhoneRequest;

/**
 * @author zak
 * @date 2018/10/19
 */
public class LoginPresenter implements IBizCallback {

    private INetworkResponse inr;
    private Context mContext;
    private IWXAPI iwxapi;

    public LoginPresenter(Context context, INetworkResponse inr) {
        this.mContext = context;
        this.inr = inr;
        regToWx(context);
    }

    /**
     * 将应用注册到微信
     *
     * @param context
     */
    private void regToWx(Context context) {
        // 获取 WXAPIFactory 实例
        iwxapi = WXAPIFactory.createWXAPI(context, Constants.getWXAppId(), true);
        // 将应用的 appId 注册到微信
        iwxapi.registerApp(Constants.getWXAppId());
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    /**
     * 调起微信登录
     */
    public void reqWXLogin() {
        if (!iwxapi.isWXAppInstalled()) {
            // 没装微信
            Toast.makeText(mContext, "需要安装微信", Toast.LENGTH_SHORT).show();
        } else {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            iwxapi.sendReq(req);
        }
    }

    /**
     * 首页检测手机号是否已经注册
     *
     * @param phoneNum
     */
    public void checkRegister(String phoneNum) {
        LoginCheckRegisterRequest loginCheckRegisterRequest = new LoginCheckRegisterRequest(NetworkEngine.LOGIN_BASE_URL + "check_phone", this);

//        loginCheckRegisterRequest.addHeaderParam("app-id", Constants.getWXAppId());
        loginCheckRegisterRequest.addRequestParam("phone", phoneNum);

        NetworkEngine.getInstance().sendRequest(loginCheckRegisterRequest);
    }

    /**
     * 获取手机验证码
     *
     * @param phoneNum
     */
    public void obtainPhoneCode(String phoneNum) {
        LoginPhoneCodeRequest loginPhoneCodeRequest = new LoginPhoneCodeRequest(NetworkEngine.LOGIN_BASE_URL + "send_msg", this);

//        loginPhoneCodeRequest.addHeaderParam("app-id", Constants.getWXAppId());
        loginPhoneCodeRequest.addRequestParam("phone", phoneNum);

        NetworkEngine.getInstance().sendRequest(loginPhoneCodeRequest);
    }

    /**
     * 旧手机验证码
     *
     * @param phoneNum
     * @param code
     */
    public void verifyCode(String phoneNum, String code) {
        LoginCodeVerifyRequest loginCodeVerifyRequest = new LoginCodeVerifyRequest(NetworkEngine.LOGIN_BASE_URL + "verify_code", this);

//        loginCodeVerifyRequest.addHeaderParam("app-id", Constants.getWXAppId());
        loginCodeVerifyRequest.addRequestParam("phone", phoneNum);
        loginCodeVerifyRequest.addRequestParam("sms_code", code);

        NetworkEngine.getInstance().sendRequest(loginCodeVerifyRequest);
    }

    /**
     * 新手机验证码
     *
     * @param phoneNum
     * @param code
     */
    public void verifyNewCode(String phoneNum, String code) {
        LoginNewCodeVerifyRequest loginNewCodeVerifyRequest = new LoginNewCodeVerifyRequest(NetworkEngine.LOGIN_BASE_URL + "verify_code", this);

//        loginNewCodeVerifyRequest.addHeaderParam("app-id", Constants.getWXAppId());
        loginNewCodeVerifyRequest.addRequestParam("phone", phoneNum);
        loginNewCodeVerifyRequest.addRequestParam("sms_code", code);

        NetworkEngine.getInstance().sendRequest(loginNewCodeVerifyRequest);
    }

    /**
     * 确认手机验证码（注册页面要去设置密码页）
     *
     * @param phoneNum
     * @param code
     */
    public void verifyRegisterCode(String phoneNum, String code) {
        LoginRegisterCodeVerifyRequest loginRegisterCodeVerifyRequest = new LoginRegisterCodeVerifyRequest(NetworkEngine.LOGIN_BASE_URL + "verify_code", this);

//        loginRegisterCodeVerifyRequest.addHeaderParam("app-id", Constants.getWXAppId());
        loginRegisterCodeVerifyRequest.addRequestParam("phone", phoneNum);
        loginRegisterCodeVerifyRequest.addRequestParam("sms_code", code);

        NetworkEngine.getInstance().sendRequest(loginRegisterCodeVerifyRequest);
    }

    /**
     * 找回密码验证码
     *
     * @param phoneNum
     * @param code
     */
    public void verifyFindPwdCode(String phoneNum, String code) {
        LoginFindPwdCodeVerifyRequest loginFindPwdCodeVerifyRequest = new LoginFindPwdCodeVerifyRequest(NetworkEngine.LOGIN_BASE_URL + "verify_code", this);

//        loginFindPwdCodeVerifyRequest.addHeaderParam("app-id", Constants.getWXAppId());
        loginFindPwdCodeVerifyRequest.addRequestParam("phone", phoneNum);
        loginFindPwdCodeVerifyRequest.addRequestParam("sms_code", code);

        NetworkEngine.getInstance().sendRequest(loginFindPwdCodeVerifyRequest);
    }

    /**
     * 执行注册操作
     *
     * @param phoneNum
     * @param passWord
     * @param smsCode
     */
    public void doRegister(String phoneNum, String passWord, String smsCode) {
        LoginDoRegisterRequest loginDoRegisterRequest = new LoginDoRegisterRequest(NetworkEngine.LOGIN_BASE_URL + "auth/register", this);

//        loginDoRegisterRequest.addHeaderParam("app-id", Constants.getWXAppId());
        loginDoRegisterRequest.addRequestParam("phone", phoneNum);
        loginDoRegisterRequest.addRequestParam("password", passWord);
        loginDoRegisterRequest.addRequestParam("sms_code", smsCode);

        NetworkEngine.getInstance().sendRequest(loginDoRegisterRequest);
    }

    /**
     * 绑定手机
     *
     * @param accessToken
     * @param phoneNum
     * @param smsCode
     */
    public void bindPhone(String accessToken, String phoneNum, String smsCode) {
        LoginBindRequest loginBindRequest = new LoginBindRequest(NetworkEngine.LOGIN_BASE_URL + "auth/bind", this);

//        loginBindRequest.addHeaderParam("app-id", Constants.getWXAppId());
        loginBindRequest.addRequestParam("access_token", accessToken);
        loginBindRequest.addRequestParam("phone", phoneNum);
        loginBindRequest.addRequestParam("sms_code", smsCode);

        NetworkEngine.getInstance().sendRequest(loginBindRequest);
    }

    /**
     * 绑定微信
     *
     * @param accessToken
     * @param code
     */
    public void bindWeChat(String accessToken, String code) {
        LoginBindRequest loginBindRequest = new LoginBindRequest(NetworkEngine.LOGIN_BASE_URL + "auth/bind", this);

//        loginBindRequest.addHeaderParam("app-id", Constants.getWXAppId());
        loginBindRequest.addRequestParam("access_token", accessToken);
        loginBindRequest.addRequestParam("code", code);

        NetworkEngine.getInstance().sendRequest(loginBindRequest);
    }

    /**
     * 使用微信登录
     *
     * @param code
     */
    public void loginByWeChat(String code) {
        LoginRequest loginRequest = new LoginRequest(NetworkEngine.LOGIN_BASE_URL + "auth/login", this);

//        loginRequest.addHeaderParam("app-id", Constants.getWXAppId());
        loginRequest.addRequestParam("code", code);

        NetworkEngine.getInstance().sendRequest(loginRequest);
    }

    /**
     * 使用密码登录
     *
     * @param phoneNum
     * @param password
     */
    public void loginByPassword(String phoneNum, String password) {
        LoginRequest loginRequest = new LoginRequest(NetworkEngine.LOGIN_BASE_URL + "auth/login", this);

//        loginRequest.addHeaderParam("app-id", Constants.getWXAppId());
        loginRequest.addRequestParam("phone", phoneNum);
        loginRequest.addRequestParam("password", MD5Utils.encrypt(password));

        NetworkEngine.getInstance().sendRequest(loginRequest);
    }

    /**
     * 使用验证码登录
     *
     * @param phoneNum
     * @param smsCode
     */
    public void loginBySmsCode(String phoneNum, String smsCode) {
        LoginRequest loginRequest = new LoginRequest(NetworkEngine.LOGIN_BASE_URL + "auth/login", this);

//        loginRequest.addHeaderParam("app-id", Constants.getWXAppId());
        loginRequest.addRequestParam("phone", phoneNum);
        loginRequest.addRequestParam("sms_code", smsCode);

        NetworkEngine.getInstance().sendRequest(loginRequest);
    }

    /**
     * 验证码方式重置密码
     *
     * @param phoneNum
     * @param smsCode
     * @param password
     */
    public void resetPasswordBySms(String phoneNum, String smsCode, String password) {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(NetworkEngine.LOGIN_BASE_URL + "auth/reset_password", this);

//        resetPasswordRequest.addHeaderParam("app-id", Constants.getWXAppId());
        resetPasswordRequest.addRequestParam("phone", phoneNum);
        resetPasswordRequest.addRequestParam("sms_code", smsCode);
        resetPasswordRequest.addRequestParam("new_password", MD5Utils.encrypt(password));

        NetworkEngine.getInstance().sendRequest(resetPasswordRequest);
    }

    /**
     * 更新手机号
     *
     * @param apiToken
     * @param smsCode
     * @param newPhone
     * @param newSmsCode
     */
    public void updatePhone(String apiToken, String smsCode, String newPhone, String newSmsCode) {
        UpdatePhoneRequest updatePhoneRequest = new UpdatePhoneRequest(NetworkEngine.LOGIN_BASE_URL + "reset_phone", this);

//        updatePhoneRequest.addHeaderParam("app-id", Constants.getWXAppId());
        updatePhoneRequest.addRequestParam("api_token", apiToken);
        updatePhoneRequest.addRequestParam("sms_code", smsCode);
        updatePhoneRequest.addRequestParam("new_phone", newPhone);
        updatePhoneRequest.addRequestParam("new_sms_code", newSmsCode);

        NetworkEngine.getInstance().sendRequest(updatePhoneRequest);
    }

    // 游客登录模式请求店铺 id
    public void requestTouristsShopId() {
        TouristsShopIdRequest touristsShopIdRequest = new TouristsShopIdRequest(NetworkEngine.LOGIN_BASE_URL + "shop", this);

//        touristsShopIdRequest.addHeaderParam("app-id", Constants.getWXAppId());

        NetworkEngine.getInstance().sendRequest(touristsShopIdRequest);
    }
}
