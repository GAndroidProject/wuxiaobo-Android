package com.xiaoe.shop.wxb.common.login;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
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
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.utils.ToastUtils;

import java.util.List;

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
        boolean isRegisterSuccess = iwxapi.registerApp(Constants.getWXAppId());
        if (!isRegisterSuccess) { // 注册不成功
            Toast.makeText(mContext, mContext.getResources().getString(R.string.we_chat_error_tip), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    /**
     * 调起微信登录
     */
    public void reqWXLogin() {
        if (!isWeChatAppInstalled(mContext)) {
            // 没装微信
            ToastUtils.show(mContext, R.string.need_install_wechat);
        } else {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            iwxapi.sendReq(req);
            Log.d("sssssssssssssss", "reqWXLogin: " + iwxapi.sendReq(req));
        }
    }

    private boolean isWeChatAppInstalled(Context context) {
        Log.d("sssssssssssssss", "isWeChatAppInstalled: " + iwxapi.isWXAppInstalled());
        if (iwxapi.isWXAppInstalled()) {
            return true;
        } else {
            final PackageManager packageManager = context.getPackageManager();// 获取packageManager
            List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
            if (pInfo != null) {
                for (int i = 0; i < pInfo.size(); i++) {
                    String pn = pInfo.get(i).packageName;
                    Log.d("sssssssssssssss", "isWeChatAppInstalled: " + pn);
                    if (pn.equalsIgnoreCase("com.tencent.mm")) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 首页检测手机号是否已经注册
     *
     * @param phoneNum
     */
    public void checkRegister(String phoneNum) {
        LoginCheckRegisterRequest loginCheckRegisterRequest = new LoginCheckRegisterRequest(this);

        loginCheckRegisterRequest.addRequestParam("phone", phoneNum);

        loginCheckRegisterRequest.sendRequest();
    }

    /**
     * 获取手机验证码
     *
     * @param phoneNum
     */
    public void obtainPhoneCode(String phoneNum) {
        LoginPhoneCodeRequest loginPhoneCodeRequest = new LoginPhoneCodeRequest(this);

        loginPhoneCodeRequest.addRequestParam("phone", phoneNum);

        loginPhoneCodeRequest.sendRequest();
    }

    /**
     * 旧手机验证码
     *
     * @param phoneNum
     * @param code
     */
    public void verifyCode(String phoneNum, String code) {
        LoginCodeVerifyRequest loginCodeVerifyRequest = new LoginCodeVerifyRequest(this);

        loginCodeVerifyRequest.addRequestParam("phone", phoneNum);
        loginCodeVerifyRequest.addRequestParam("sms_code", code);

        loginCodeVerifyRequest.sendRequest();
    }

    /**
     * 新手机验证码
     *
     * @param phoneNum
     * @param code
     */
    public void verifyNewCode(String phoneNum, String code) {
        LoginNewCodeVerifyRequest loginNewCodeVerifyRequest = new LoginNewCodeVerifyRequest(this);

        loginNewCodeVerifyRequest.addRequestParam("phone", phoneNum);
        loginNewCodeVerifyRequest.addRequestParam("sms_code", code);

        loginNewCodeVerifyRequest.sendRequest();
    }

    /**
     * 确认手机验证码（注册页面要去设置密码页）
     *
     * @param phoneNum
     * @param code
     */
    public void verifyRegisterCode(String phoneNum, String code) {
        LoginRegisterCodeVerifyRequest loginRegisterCodeVerifyRequest = new LoginRegisterCodeVerifyRequest(this);

        loginRegisterCodeVerifyRequest.addRequestParam("phone", phoneNum);
        loginRegisterCodeVerifyRequest.addRequestParam("sms_code", code);

        loginRegisterCodeVerifyRequest.sendRequest();
    }

    /**
     * 找回密码验证码
     *
     * @param phoneNum
     * @param code
     */
    public void verifyFindPwdCode(String phoneNum, String code) {
        LoginFindPwdCodeVerifyRequest loginFindPwdCodeVerifyRequest = new LoginFindPwdCodeVerifyRequest(this);

        loginFindPwdCodeVerifyRequest.addRequestParam("phone", phoneNum);
        loginFindPwdCodeVerifyRequest.addRequestParam("sms_code", code);

        loginFindPwdCodeVerifyRequest.sendRequest();
    }

    /**
     * 执行注册操作
     *
     * @param phoneNum
     * @param passWord
     * @param smsCode
     */
    public void doRegister(String phoneNum, String passWord, String smsCode) {
        LoginDoRegisterRequest loginDoRegisterRequest = new LoginDoRegisterRequest(this);

        loginDoRegisterRequest.addRequestParam("phone", phoneNum);
        loginDoRegisterRequest.addRequestParam("password", passWord);
        loginDoRegisterRequest.addRequestParam("sms_code", smsCode);

        loginDoRegisterRequest.sendRequest();
    }

    /**
     * 绑定手机
     *
     * @param accessToken
     * @param phoneNum
     * @param smsCode
     */
    public void bindPhone(String accessToken, String phoneNum, String smsCode) {
        LoginBindRequest loginBindRequest = new LoginBindRequest(this);

        loginBindRequest.addRequestParam("access_token", accessToken);
        loginBindRequest.addRequestParam("phone", phoneNum);
        loginBindRequest.addRequestParam("sms_code", smsCode);

        loginBindRequest.sendRequest();
    }

    /**
     * 绑定微信
     *
     * @param accessToken
     * @param code
     */
    public void bindWeChat(String accessToken, String code) {
        LoginBindRequest loginBindRequest = new LoginBindRequest(this);

        loginBindRequest.addRequestParam("access_token", accessToken);
        loginBindRequest.addRequestParam("code", code);

        loginBindRequest.sendRequest();
    }

    /**
     * 使用微信登录
     *
     * @param code
     */
    public void loginByWeChat(String code) {
        LoginRequest loginRequest = new LoginRequest(this);

        loginRequest.addRequestParam("code", code);

        loginRequest.sendRequest();
    }

    /**
     * 使用密码登录
     *
     * @param phoneNum
     * @param password
     */
    public void loginByPassword(String phoneNum, String password) {
        LoginRequest loginRequest = new LoginRequest(this);

        loginRequest.addRequestParam("phone", phoneNum);
        loginRequest.addRequestParam("password", MD5Utils.encrypt(password));

        loginRequest.sendRequest();
    }

    /**
     * 使用验证码登录
     *
     * @param phoneNum
     * @param smsCode
     */
    public void loginBySmsCode(String phoneNum, String smsCode) {
        LoginRequest loginRequest = new LoginRequest(this);

        loginRequest.addRequestParam("phone", phoneNum);
        loginRequest.addRequestParam("sms_code", smsCode);

        loginRequest.sendRequest();
    }

    /**
     * 验证码方式重置密码
     *
     * @param phoneNum
     * @param smsCode
     * @param password
     */
    public void resetPasswordBySms(String phoneNum, String smsCode, String password) {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(this);

        resetPasswordRequest.addRequestParam("phone", phoneNum);
        resetPasswordRequest.addRequestParam("sms_code", smsCode);
        resetPasswordRequest.addRequestParam("new_password", MD5Utils.encrypt(password));

        resetPasswordRequest.sendRequest();
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
        UpdatePhoneRequest updatePhoneRequest = new UpdatePhoneRequest(this);

        updatePhoneRequest.addRequestParam("api_token", apiToken);
        updatePhoneRequest.addRequestParam("sms_code", smsCode);
        updatePhoneRequest.addRequestParam("new_phone", newPhone);
        updatePhoneRequest.addRequestParam("new_sms_code", newSmsCode);

        updatePhoneRequest.sendRequest();
    }

    // 游客登录模式请求店铺 id
    public void requestTouristsShopId() {
        TouristsShopIdRequest touristsShopIdRequest = new TouristsShopIdRequest(this);

        touristsShopIdRequest.sendRequest();
    }
}
