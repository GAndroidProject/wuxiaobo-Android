package com.xiaoe.shop.wxb.business.login.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.db.LoginSQLiteCallback;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.LoginBindRequest;
import com.xiaoe.network.requests.LoginCheckRegisterRequest;
import com.xiaoe.network.requests.LoginDoRegisterRequest;
import com.xiaoe.network.requests.LoginPhoneCodeRequest;
import com.xiaoe.network.requests.LoginRegisterCodeVerifyRequest;
import com.xiaoe.network.requests.LoginRequest;
import com.xiaoe.network.requests.SettingPseronMsgRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.setting.presenter.SettingPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.login.LoginPresenter;
import com.xiaoe.shop.wxb.utils.ActivityCollector;
import com.xiaoe.shop.wxb.utils.JudgeUtil;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginNewActivity extends XiaoeActivity {

    private static final String TAG = "LoginNewActivity";

    protected static final String LOGIN_OBTAIN_CODE = "obtain_code"; // 获取验证码
    protected static final String LOGIN_VERIFY_CODE = "verify_code"; // 确认验证码
    protected static final String LOGIN_BIND_WX = "bind_wx"; // 绑定微信

    Unbinder unbinder;
    // 软键盘
    InputMethodManager imm;

    @BindView(R.id.login_header_new)
    FrameLayout loginHeader;
    @BindView(R.id.login_back_new)
    ImageView loginBack;
    @BindView(R.id.login_title_new)
    TextView loginTitle;

    protected String mainFragmentTitle;
    private Fragment currentFragment;

    LoginPresenter loginPresenter;
    SettingPresenter settingPresenter;

    String phoneNum; // 输入的手机号
    String smsCode; // 输入的 smsCode
    boolean hasRegister; // 已注册
    private LoginUser loginUser;
    private SQLiteUtil loginSQLiteUtil;
    boolean isLoginPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_login_new);
        unbinder = ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        loginHeader.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        loginSQLiteUtil = SQLiteUtil.init(this, new LoginSQLiteCallback());

        mainFragmentTitle = getIntent().getStringExtra("login_title");
        if (!TextUtils.isEmpty(mainFragmentTitle)) {
            if (mainFragmentTitle.equals(getString(R.string.login_main_phone))) { // 输入手机号页面
                isLoginPhone = true;
            } else if (mainFragmentTitle.equals(getString(R.string.login_bind_phone_title))) { // 绑定手机号页面
                isLoginPhone = false;
            }
            loginTitle.setText(mainFragmentTitle);
        } else {
            Log.d(TAG, "onCreate: title 为空，异常情况");
            return;
        }
        currentFragment = LoginNewFragment.newInstance(R.layout.fragment_login_main_new);
        getSupportFragmentManager().beginTransaction().add(R.id.login_container_new, currentFragment, LOGIN_OBTAIN_CODE).commit();

        // 网络请求
        loginPresenter = new LoginPresenter(this, this);
        settingPresenter = new SettingPresenter(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String apiToken = CommonUserInfo.getApiToken();
        if (!TextUtils.isEmpty(apiToken)) { // 有用户登录信息，直接去主页
            if (getDialog().isShowing()) {
                getDialog().dismissDialog();
            }
            ActivityCollector.finishAll();
            JumpDetail.jumpMain(this, true);
            return;
        }
        String code = SharedPreferencesUtil.getData("wx_code", "").toString();
        String accessToken = SharedPreferencesUtil.getData("accessToken", "").toString();
        if (!TextUtils.isEmpty(code) && !TextUtils.isEmpty(accessToken)) {
            loginPresenter.bindWeChat(accessToken, code); // 进行绑定微信
            // code 如果有，都要清空
            SharedPreferencesUtil.putData("wx_code", ""); // 清空 code
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick(R.id.login_back_new)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_back_new:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getDialog().isShowing()) {
            getDialog().dismissDialog();
        }
        toggleSoftKeyboard();
        if (currentFragment != null) {
            switch (currentFragment.getTag()) {
                case LOGIN_OBTAIN_CODE:
                    super.onBackPressed();
                    break;
                case LOGIN_VERIFY_CODE:
                    loginTitle.setText(mainFragmentTitle);
                    replaceFragment(LOGIN_OBTAIN_CODE);
                    break;
                case LOGIN_BIND_WX:
                    loginTitle.setText(getString(R.string.login_code_new_title));
                    replaceFragment(LOGIN_OBTAIN_CODE);
                    break;
            }
        }
    }

    protected void replaceFragment(String tag) {
        if (getDialog().isShowing()) {
            getDialog().dismissDialog();
        }
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(currentFragment).commit();
        }
        currentFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (currentFragment == null) {
            switch (tag) {
                case LOGIN_OBTAIN_CODE:
                    loginTitle.setText(mainFragmentTitle);
                    currentFragment = LoginNewFragment.newInstance(R.layout.fragment_login_main_new);
                    break;
                case LOGIN_VERIFY_CODE:
                    loginTitle.setText(getString(R.string.login_code_new_title));
                    currentFragment = LoginNewFragment.newInstance(R.layout.fragment_login_code_new);
                    break;
                case LOGIN_BIND_WX:
                    loginTitle.setText(getString(R.string.login_we_chat_title));
                    currentFragment = LoginNewFragment.newInstance(R.layout.fragment_login_we_chat_new);
                    break;
                default:
                    break;
            }
            if (currentFragment != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.login_container_new, currentFragment, tag).commit();
            }
        } else {
            getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof LoginRequest) { // 登录回调
                handleLoginCallback(result);
            } else if (iRequest instanceof LoginCheckRegisterRequest) { // 检测是否注册回调
                handleHasRegisterCallback(result);
            } else if (iRequest instanceof LoginPhoneCodeRequest) { // 发送验证码回调
                handleSendCodeCallback(result);
            } else if (iRequest instanceof LoginDoRegisterRequest) { // 注册操作回调
                handleRegisterCallback(result);
            } else if (iRequest instanceof LoginBindRequest) { // 绑定手机操作回调
                handleBindCallback(result);
            } else if (iRequest instanceof SettingPseronMsgRequest) { // 获取个人信息回调
                handlePersonMsgCallback(result);
            } else if (iRequest instanceof LoginRegisterCodeVerifyRequest) { // 注册验证码确认回调
                handleVerifyCodeCallback(result);
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: 请求失败..");
            if (getDialog().isShowing()) {
                getDialog().dismissDialog();
            }
            if (((LoginNewFragment) currentFragment).getDialog().isShowing()) {
                ((LoginNewFragment) currentFragment).getDialog().dismissDialog();
            }
            ToastUtils.show(this, getString(R.string.network_error_text));
        }
    }

    /**
     * 处理校验验证码回调
     * @param result 返回结果
     */
    private void handleVerifyCodeCallback(JSONObject result) {
        int code = result.getInteger("code");
        if (code == NetworkCodes.CODE_SUCCEED) { // 验证成功
            Log.d(TAG, "onMainThreadResponse: msg --- " + result.getString("msg"));
            ((LoginNewFragment) currentFragment).loginTimeCount.cancel();
            if (((LoginNewFragment) currentFragment).codeTip.getVisibility() == View.VISIBLE) {
                JudgeUtil.hideCodeErrorView(this, ((LoginNewFragment)currentFragment).codeTip, ((LoginNewFragment) currentFragment).codeObtainWrap);
            }
            toggleSoftKeyboard();
            if (hasRegister) { // 已经注册了
                loginPresenter.loginBySmsCode(phoneNum, smsCode);
            } else {
                if (isLoginPhone) { // 验证码登录，但未注册，执行注册操作
                    String pwd = String.valueOf(System.currentTimeMillis()).substring(0, 8);
                    toggleSoftKeyboard();
                    loginPresenter.doRegister(phoneNum, pwd, smsCode);
                } else { // 微信登录，但未注册，执行绑定手机号操作
                    String accessToken = SharedPreferencesUtil.getData("accessToken", "").toString();
                    if (!TextUtils.isEmpty(accessToken)) {
                        loginPresenter.bindPhone(accessToken, phoneNum, smsCode);
                        // 用完就清空
                        SharedPreferencesUtil.putData("accessToken", "");
                        toggleSoftKeyboard();
                    } else {
                        Log.d(TAG, "onComplete: 微信登录绑定手机异常...");
                    }
                }
            }
        } else if (code == NetworkCodes.CODE_LOGIN_FAIL) {
            Log.d(TAG, "onMainThreadResponse: msg --- " + result.getString("msg"));
            if (((LoginNewFragment) currentFragment).getDialog().isShowing()) {
                ((LoginNewFragment) currentFragment).getDialog().dismissDialog();
            }
            JudgeUtil.showCodeErrorView(this, getString(R.string.login_code_error_new), ((LoginNewFragment)currentFragment).codeTip, ((LoginNewFragment) currentFragment).codeObtainWrap);
            this.smsCode = "";
        }
    }

    /**
     * 处理个人信息回调
     * @param result 返回结果
     */
    private void handlePersonMsgCallback(JSONObject result) {
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
        } else if (code == NetworkCodes.CODE_NOT_LOAING) { // 未登录状态
            Log.d(TAG, "handlePersonMsgCallback: 返回 2009 啦...");
        }
    }

    /**
     * 处理绑定手机回调
     * @param result 返回结果
     */
    private void handleBindCallback(JSONObject result) {
        int code = result.getInteger("code");
        if (((LoginNewFragment)currentFragment).getDialog().isShowing()) {
            ((LoginNewFragment)currentFragment).getDialog().dismissDialog();
        }
        if (code == NetworkCodes.CODE_SUCCEED) { // 注册成功
            JSONObject data = (JSONObject) result.get("data");
            initUserInfo(data);
        } else if (code == NetworkCodes.CODE_PHONE_CODE_ERROR) {
            Log.d(TAG, "onMainThreadResponse: 验证码错误...");
            if (((LoginNewFragment)currentFragment).codeTip != null && ((LoginNewFragment) currentFragment).codeObtainWrap != null) {
                JudgeUtil.showCodeErrorView(this, getString(R.string.login_code_error_new), ((LoginNewFragment)currentFragment).codeTip, ((LoginNewFragment) currentFragment).codeObtainWrap);
            }
        } else if (code == NetworkCodes.CODE_REGISTER_FAIL) {
            Log.d(TAG, "onMainThreadResponse: 注册失败..");
            ToastUtils.show(mContext, getString(R.string.registration_failed));
        } else if (code == NetworkCodes.CODE_OBTAIN_ACCESS_TOKEN_FAIL) {
            Log.d(TAG, "onMainThreadResponse: 服务器访问失败");
        } else if (code == NetworkCodes.CODE_PARAMS_ERROR) {
            Log.d(TAG, "onMainThreadResponse: 参数错误");
        } else if (code == NetworkCodes.CODE_PHONE_HAD_BIND) {
            Log.d(TAG, "onMainThreadResponse: 手机已被绑定");
            ToastUtils.show(mContext, getString(R.string.phone_has_bundled));
        } else if (code == NetworkCodes.CODE_WX_HAD_BIND) {
            Log.d(TAG, "onMainThreadResponse: 微信号已被绑定");
            ToastUtils.show(mContext, R.string.wechat_id_is_bound);
        }
    }

    /**
     * 处理注册回调
     * @param result 返回结果
     */
    private void handleRegisterCallback(JSONObject result) {
        int code = result.getInteger("code");
        String msg = result.getString("msg");
        if (code == NetworkCodes.CODE_LIMIT_USER) { // 受限用户
            JSONObject data = (JSONObject) result.get("data");
            obtainLimitUserInfo(data);
            return;
        } else if (code == NetworkCodes.CODE_SUCCEED) {
            JSONObject data = (JSONObject) result.get("data");
            initUserInfo(data);
        } else if (code == NetworkCodes.CODE_PARAMS_ERROR) { // 参数错误
            Log.d(TAG, "onMainThreadResponse: register --- " + msg);
        } else if (code == NetworkCodes.CODE_REGISTER_FAIL) { // 注册失败
            Log.d(TAG, "onMainThreadResponse: register --- " + msg);
        } else if (code == NetworkCodes.CODE_PHONE_CODE_ERROR) { // 验证码错误
            Log.d(TAG, "onMainThreadResponse: register --- " + msg);
        }
        ToastUtils.show(mContext, msg);
    }

    /**
     * 处理发送验证码回调
     * @param result 返回结果
     */
    private void handleSendCodeCallback(JSONObject result) {
        int code = result.getInteger("code");
        if (code == NetworkCodes.CODE_SUCCEED) {
            Log.d(TAG, "onMainThreadResponse: msg --- " + result.getString("msg"));
            toggleSoftKeyboard();
            if (!currentFragment.getTag().equals(LOGIN_VERIFY_CODE)) {
                replaceFragment(LOGIN_VERIFY_CODE);
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: msg --- " + result.getString("msg"));
        }
    }

    /**
     * 处理验证码请求回调
     * @param result 返回结果
     */
    private void handleHasRegisterCallback(JSONObject result) {
        getDialog().showLoadDialog(false);
        int code = result.getInteger("code");
        hasRegister = code == NetworkCodes.CODE_HAD_REGISTER;
        loginPresenter.obtainPhoneCode(phoneNum);
    }

    /**
     * 处理登录请求回调
     * @param result 返回结果
     */
    private void handleLoginCallback(JSONObject result) {
        int code = result.getInteger("code");
        if (code == NetworkCodes.CODE_SUCCEED) {
            JSONObject data = (JSONObject) result.get("data");
            updateLoginMsg(data);
        } else if (code == NetworkCodes.CODE_PARAMS_ERROR) {
            Log.d(TAG, "onMainThreadResponse: 微信登录参数错误.");
        } else if (code == NetworkCodes.CODE_GOODS_GROUPS_NOT_FIND) {
            // 意思是没有完成微信、手机号绑定的用户
            Log.d(TAG, "onMainThreadResponse: 受限用户.");
            JSONObject data = (JSONObject) result.get("data");
            // 绑定手机
            obtainLimitUserInfo(data);
        } else if (code == NetworkCodes.CODE_LOGIN_FAIL) {
            if (((LoginNewFragment) currentFragment).getDialog().isShowing()) {
                ((LoginNewFragment) currentFragment).getDialog().dismissDialog();
            }
            ToastUtils.show(mContext, R.string.login_failure);
            Log.d(TAG, "onMainThreadResponse: " + result.getString("msg"));
        } else if (code == NetworkCodes.CODE_OBTAIN_ACCESS_TOKEN_FAIL) { // 获取 access token 失败
            Log.d(TAG, "onMainThreadResponse: 获取 access token 失败");
        } else if (code == NetworkCodes.CODE_LOGIN_PASSWORD_ERROR) {
            ToastUtils.show(mContext, R.string.wrong_password);
            Log.d(TAG, "onMainThreadResponse: " + result.getString("msg"));
        }
    }

    // 获取受限用户信息（绑定手机）
    private void obtainLimitUserInfo(JSONObject data) {

        String accessToken = data.getString("access_token");

        String oldAccessToken = SharedPreferencesUtil.getData("accessToken", "").toString();
        if (!TextUtils.isEmpty(oldAccessToken)) {
            SharedPreferencesUtil.putData("accessToken", "");
        }
        SharedPreferencesUtil.putData("accessToken", accessToken);

        if (((LoginNewFragment)currentFragment).getDialog().isShowing()) {
            ((LoginNewFragment)currentFragment).getDialog().dismissDialog();
        }
        replaceFragment(LOGIN_BIND_WX);
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

    // 注册成功初始化用户信息
    private void initUserInfo(JSONObject data) {
        getDialog().showLoadDialog(false);
        String id = data.getString("id");
        String wxOpenId = data.getString("wx_open_id");
        String wxUnionId = data.getString("wx_union_id");
        String apiToken = data.getString("api_token");

        loginUser = new LoginUser();

        loginUser.setId(id);
        loginUser.setWxOpenId(wxOpenId);
        loginUser.setWxUnionId(wxUnionId);
        loginUser.setApi_token(apiToken);

        List<LoginUser> userList = getLoginUserList();
        if (userList.size() == 1) {
            // 已经有用户注册过，此时需要先将已注册用户的信息删掉
            String tempId = userList.get(0).getId();
            loginSQLiteUtil.delete(LoginSQLiteCallback.TABLE_NAME_USER, "id = ?", new String[]{tempId});
        }
        // 存储用户信息
        loginSQLiteUtil.insert(LoginSQLiteCallback.TABLE_NAME_USER, loginUser);

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

        ToastUtils.show(mContext, getString(R.string.login_successfully));
        if (getDialog().isShowing()) {
            getDialog().dismissDialog();
        }
        ActivityCollector.finishAll();
        JumpDetail.jumpMain(this, true);
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
}
