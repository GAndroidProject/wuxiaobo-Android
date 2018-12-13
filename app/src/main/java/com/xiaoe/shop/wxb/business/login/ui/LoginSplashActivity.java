package com.xiaoe.shop.wxb.business.login.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.db.LoginSQLiteCallback;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.ChangeLoginIdentityEvent;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.LoginRequest;
import com.xiaoe.network.requests.SettingPseronMsgRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.setting.presenter.SettingPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.login.LoginPresenter;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginSplashActivity extends XiaoeActivity {

    private static final String TAG = "LoginSplashActivity";

    Unbinder unbinder;

    @BindView(R.id.login_splash_wrap)
    FrameLayout loginSplashWrap;
    @BindView(R.id.login_splash_bg)
    ImageView loginSplashBg;
    @BindView(R.id.login_splash_we_chat)
    FrameLayout loginSplashWx;
    @BindView(R.id.login_splash_phone)
    TextView loginSplashPhone;
    @BindView(R.id.login_splash_protocol)
    TextView loginSplashProtocol;

    LoginPresenter loginPresenter;
    private SQLiteUtil loginSQLiteUtil;
    private LoginUser loginUser;
    protected SettingPresenter settingPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setStatusBarWithWhite();
        setContentView(R.layout.activity_login_splash);
        unbinder = ButterKnife.bind(this);

        loginSQLiteUtil = SQLiteUtil.init(this, new LoginSQLiteCallback());
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        toggleSoftKeyboard();
        initView();

        loginPresenter = new LoginPresenter(this, this);
        settingPresenter = new SettingPresenter(this);
    }

    private void initView() {
        String htmlTextStart = "<font color='#BCA16B'>";
        String htmlTextEnd = "</font>";
        String result = String.format(getString(R.string.login_protocol_tips), htmlTextStart, htmlTextEnd);
        loginSplashProtocol.setText(Html.fromHtml(result));
    }

    @OnClick({R.id.login_splash_we_chat, R.id.login_splash_phone, R.id.login_splash_protocol})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_splash_we_chat: // 微信登录
                Log.d(TAG, "onClick: wx ------- ");
                getDialog().showLoadDialog(false);
                loginPresenter.reqWXLogin();
                break;
            case R.id.login_splash_phone: // 手机号登录
                JumpDetail.jumpLoingNew(this, getString(R.string.login_main_phone));
                break;
            case R.id.login_splash_protocol: // 跳转服务协议
                JumpDetail.jumpProtocol(this);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String apiToken = CommonUserInfo.getApiToken();
        if (apiToken != null && !apiToken.equals("")) { // 有用户登录信息，直接去主页
            finish();
            JumpDetail.jumpMain(this, true);
            return;
        }
        String code = SharedPreferencesUtil.getData("wx_code", "").toString();
        if (!TextUtils.isEmpty(code)) {
            // 从直接点击微信登录
            loginPresenter.loginByWeChat(code);
            // code 如果有，都要清空
            SharedPreferencesUtil.putData("wx_code", ""); // 清空 code
        }
        if (getDialog().isShowing()) {
            getDialog().dismissDialog();
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof LoginRequest) { // 微信登录回调
                handleLoginCallBack(result);
            } else if (iRequest instanceof SettingPseronMsgRequest) {
                handlePersonMsgCallBack(result);
            }
        } else {
            if (getDialog().isShowing()) {
                getDialog().dismissDialog();
            }
            Log.d(TAG, "onMainThreadResponse: request fail");
        }
    }

    /**
     * 处理个人信息请求回调
     * @param result 返回结果
     */
    private void handlePersonMsgCallBack(JSONObject result) {
        int code = result.getInteger("code");
        if (code == NetworkCodes.CODE_SUCCEED) {
            JSONObject data = (JSONObject) result.get("data");
            initMineMsg(data);
        } else if (code == NetworkCodes.CODE_PERSON_PARAM_LOSE) {
            Log.d(TAG, "onMainThreadResponse: 必选字段缺失");
        } else if (code == NetworkCodes.CODE_PERSON_PARAM_UNUSEFUL) {
            Log.d(TAG, "onMainThreadResponse: 字段格式无效");
        } else if (code == NetworkCodes.CODE_PERSON_NOT_FOUND) {
            Log.d(TAG, "onMainThreadResponse: 当前用户不存在");
        }
    }

    /**
     * 处理登录请求回调
     * @param result 返回结果
     */
    private void handleLoginCallBack(JSONObject result) {
        int code = result.getInteger("code");
        if (code == NetworkCodes.CODE_SUCCEED) {
            JSONObject data = (JSONObject) result.get("data");
            updateLoginMsg(data);
        } else if (code == NetworkCodes.CODE_GOODS_GROUPS_NOT_FIND) {
            // 意思是没有完成微信、手机号绑定的用户
            Log.d(TAG, "onMainThreadResponse: 受限用户.");
            JSONObject data = (JSONObject) result.get("data");
            // 绑定手机
            obtainLimitUserInfo(data);
        } else if (code == NetworkCodes.CODE_LOGIN_FAIL) {
            ToastUtils.show(mContext, R.string.login_failure);
            Log.d(TAG, "onMainThreadResponse: " + result.getString("msg"));
        } else if (code == NetworkCodes.CODE_LOGIN_PASSWORD_ERROR) {
            ToastUtils.show(mContext, R.string.wrong_password);
            Log.d(TAG, "onMainThreadResponse: " + result.getString("msg"));
        } else if (code == NetworkCodes.CODE_PARAMS_ERROR) {
            Log.d(TAG, "onMainThreadResponse: 微信登录参数错误.");
        } else if (code == NetworkCodes.CODE_OBTAIN_ACCESS_TOKEN_FAIL) { // 获取 access token 失败
            Log.d(TAG, "onMainThreadResponse: 获取 access token 失败");
        }
    }

    // 登录成功之后，拿到的登录信息更新到本地数据库
    private void updateLoginMsg(JSONObject data) {
        String id = data.getString("id");
        String wxOpenId = data.getString("wx_open_id");
        String wxUnionId = data.getString("wx_union_id");
//        String phone = data.getString("phone");
        String apiToken = data.getString("api_token");

        List<LoginUser> list = loginSQLiteUtil.query(LoginSQLiteCallback.TABLE_NAME_USER, "select * from " + LoginSQLiteCallback.TABLE_NAME_USER, null);

        if (list.size() > 0) {
            loginUser = list.get(0);
        } else {
            loginUser = new LoginUser();
        }

        loginUser.setId(id);
        loginUser.setWxOpenId(wxOpenId);
        loginUser.setWxUnionId(wxUnionId);
        loginUser.setApi_token(apiToken);

        List<LoginUser> userList = getLoginUserList();

        if (userList.size() == 1) { // 证明有登录或者注册过的用户
            String localId = userList.get(0).getId();
            if (localId.equals(id)) { // 同一个用户
                loginSQLiteUtil.update(LoginSQLiteCallback.TABLE_NAME_USER, loginUser, "id = ?", new String[]{id}); // 更新该用户信息
            } else { // 不同一个用户
                loginSQLiteUtil.delete(LoginSQLiteCallback.TABLE_NAME_USER, "id = ?", new String[]{localId});
                loginSQLiteUtil.insert(LoginSQLiteCallback.TABLE_NAME_USER, loginUser); // 删掉原来的用户，插入新用户
            }
        } else { // 表中没有用户登录记录
            loginSQLiteUtil.insert(LoginSQLiteCallback.TABLE_NAME_USER, loginUser);
        }
        CommonUserInfo.setApiToken(apiToken);

        settingPresenter.requestPersonData(apiToken, false);
    }

    // 初始化用户信息
    private void initMineMsg(JSONObject data) {

        String wxNickname = data.getString("wx_nickname");
        String wxAvatar = data.getString("wx_avatar");
        String shopId = data.getString("app_id");
        String userId = data.getString("user_id");
        String phone = data.getString("phone");

        List<LoginUser> list = loginSQLiteUtil.query(LoginSQLiteCallback.TABLE_NAME_USER, "select * from " + LoginSQLiteCallback.TABLE_NAME_USER, null);

        LoginUser localLoginUser = list.get(0);

        localLoginUser.setId(loginUser.getId());
        localLoginUser.setApi_token(loginUser.getApi_token());
        localLoginUser.setWxUnionId(loginUser.getWxUnionId());
        localLoginUser.setWxOpenId(loginUser.getWxOpenId());
        localLoginUser.setUserId(userId);
        localLoginUser.setWxNickname(wxNickname);
        localLoginUser.setWxAvatar(wxAvatar);
        localLoginUser.setPhone(phone);
        localLoginUser.setShopId(shopId);
        loginSQLiteUtil.deleteFrom(LoginSQLiteCallback.TABLE_NAME_USER);
        CommonUserInfo.getInstance().clearUserInfo();
        loginSQLiteUtil.insert(LoginSQLiteCallback.TABLE_NAME_USER, localLoginUser);

        if (getDialog().isShowing()) {
            getDialog().dismissDialog();
        }
        ToastUtils.show(mContext, getString(R.string.login_successfully));
        finish();
        JumpDetail.jumpMain(this, true);
    }

    // 获取受限用户信息（绑定手机）
    private void obtainLimitUserInfo(JSONObject data) {

        String accessToken = data.getString("access_token");

        SharedPreferencesUtil.putData("accessToken", accessToken);

        JumpDetail.jumpLoingNew(this, getString(R.string.login_bind_phone_title));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    /**
     * 弹出或关闭软键盘
     */
    protected void toggleSoftKeyboard() {
        if (imm != null && imm.isActive()) {
            View view = getCurrentFocus();
            if (view != null) {
                IBinder iBinder = view.getWindowToken();
                imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
