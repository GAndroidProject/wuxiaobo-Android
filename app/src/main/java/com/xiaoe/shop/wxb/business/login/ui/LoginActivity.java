package com.xiaoe.shop.wxb.business.login.ui;

import android.content.Context;
import android.content.Intent;
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
import com.xiaoe.common.entitys.ChangeLoginIdentityEvent;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.LoginBindRequest;
import com.xiaoe.network.requests.LoginCheckRegisterRequest;
import com.xiaoe.network.requests.LoginCodeVerifyRequest;
import com.xiaoe.network.requests.LoginDoRegisterRequest;
import com.xiaoe.network.requests.LoginFindPwdCodeVerifyRequest;
import com.xiaoe.network.requests.LoginPhoneCodeRequest;
import com.xiaoe.network.requests.LoginRegisterCodeVerifyRequest;
import com.xiaoe.network.requests.LoginRequest;
import com.xiaoe.network.requests.ResetPasswordRequest;
import com.xiaoe.network.requests.SettingPseronMsgRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.setting.presenter.SettingPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.login.LoginPresenter;
import com.xiaoe.shop.wxb.utils.JudgeUtil;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;

/**
 * @deprecated 使用 LoginNewActivity
 */
public class LoginActivity extends XiaoeActivity {

    @BindView(R.id.login_header)
    FrameLayout loginHeader;
    @BindView(R.id.login_back)
    ImageView loginBack;
    @BindView(R.id.login_title)
    TextView loginTitleText;
    @BindView(R.id.login_register)
    TextView loginRegister;

    private static final String TAG = "LoginActivity";

    public static final String REGISTER_ERROR_TIP = "register_error_tip"; // 手机没有注册则显示没有注册 tip

    // 需要切换的 fragment tag
    protected static final String MAIN = "login_main"; // 登录主页
    protected static final String CODE = "login_code"; // 验证码页
    protected static final String BIND_PHONE = "login_bind"; // 绑定手机号页
    protected static final String PWD = "login_pwd"; // 密码登录页
    protected static final String FIND_PWD = "login_find_pwd"; // 找回密码页
    protected static final String SET_PWD = "login_set_pwd"; // 设置密码页
    protected static final String REGISTER = "login_register"; // 注页面
    protected static final String BIND_WE_CHAT = "bind_we_chat"; // 绑定微信页
    protected static final String SERVICE = "service"; // 服务协议

    private Fragment currentFragment;
    // 软键盘
    InputMethodManager imm;

    Unbinder unbinder;

    // 手机号
    protected String phoneNum;
    // 验证码
    protected String smsCode;
    protected boolean isRegister; // 是否为注册流程
    protected boolean isPwdLogin; // 是否为密码登录流程

    protected LoginPresenter loginPresenter;
    protected SettingPresenter settingPresenter;
    protected List<LoginUser> loginUserList;
    private LoginUser loginUser;
    Intent intent;
    private boolean isTouristClick; // 是否为游客点击
    private SQLiteUtil loginSQLiteUtil;
    protected String password; // 密码登录流程 password

    protected String getPhoneNum() {
        return phoneNum;
    }

    protected void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    protected String getSmsCode() {
        return smsCode;
    }

    protected void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setStatusBar();
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);

        loginSQLiteUtil = SQLiteUtil.init(this, new LoginSQLiteCallback());

        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        intent = getIntent();
        isTouristClick = intent.getBooleanExtra("isTouristClick", false);

        loginHeader.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);

        currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_main);
        getSupportFragmentManager().beginTransaction().add(R.id.login_container, currentFragment, MAIN).commit();

        loginUserList = getLoginUserList();

        // 网络请求
        loginPresenter = new LoginPresenter(this, this);
        settingPresenter = new SettingPresenter(this);

        initData();
        initView();
        initListener();
    }

    private void initData() {

    }

    private void initView() {
        switch (currentFragment.getTag()) {
            case MAIN:
                loginRegister.setVisibility(View.VISIBLE);
                loginBack.setVisibility(View.GONE);
                loginTitleText.setVisibility(View.GONE);
                break;
            case SERVICE:
                loginRegister.setVisibility(View.GONE);
                loginBack.setVisibility(View.VISIBLE);
                loginTitleText.setVisibility(View.VISIBLE);
                break;
            default:
                loginRegister.setVisibility(View.GONE);
                loginBack.setVisibility(View.VISIBLE);
                loginTitleText.setVisibility(View.GONE);
                break;
        }
    }

    private void initListener() {

        loginBack.setOnClickListener(new DebouncingOnClickListener() {

            @Override
            public void doClick(View v) {
                onBackPressed();
            }
        });

        loginRegister.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                // 如果软件盘在的话，就把软键盘隐藏
                toggleSoftKeyboard();
                isRegister = true;
                replaceFragment(REGISTER);
            }
        });
    }

    @Override
    public void onBackPressed() {
        toggleSoftKeyboard();
        if (currentFragment != null) {
            switch (currentFragment.getTag()) {
                case REGISTER:
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    isRegister = false;
                    replaceFragment(MAIN);
                    break;
                case CODE:
                    ((LoginPageFragment) currentFragment).loginCodeContent.clearAllEditText();
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    if (isRegister) {
                        replaceFragment(REGISTER);
                    } else {
                        replaceFragment(MAIN);
                    }
                    break;
                case PWD:
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    isPwdLogin = false; // 回到首页之前，结束密码登录流程
                    replaceFragment(MAIN);
                    break;
                case FIND_PWD:
                    isPwdLogin = true; // 从找回密码回来，开始密码登录流程
                    replaceFragment(PWD);
                    break;
                case SET_PWD:
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    if (isRegister) {
                        replaceFragment(REGISTER);
                    } else {
                        replaceFragment(MAIN);
                    }
                    break;
                case BIND_WE_CHAT:
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    SharedPreferencesUtil.putData("accessToken", ""); // 离开绑定微信页面的时候清空 accessToken
                    replaceFragment(MAIN);
                    break;
                case BIND_PHONE:
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    replaceFragment(MAIN);
                    break;
                case SERVICE:
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    loginTitleText.setVisibility(View.GONE);
                    replaceFragment(REGISTER);
                    break;
                default:
                    super.onBackPressed();
            }
        }
    }

    // 上一个 fragment 的 tag
    protected String preTag;
    protected void replaceFragment(String tag) {
        if (currentFragment != null) {
            if (MAIN.equals(tag)) {
                // 回到首页之前清空 preTag 以及处理初始化操作
                preTag = null;
                isRegister = false;
                isPwdLogin = false;
                phoneNum = "";
                smsCode = "";
            } else {
                preTag = currentFragment.getTag();
            }
            getSupportFragmentManager().beginTransaction().hide(currentFragment).commit();
        }
        currentFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (currentFragment == null) { // 第一次创建
            switch (tag) {
                case MAIN: // 登录主页
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_main);
                    break;
                case CODE: // 验证码页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_code, phoneNum);
                    break;
                case BIND_PHONE: // 绑定手机页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_bind_phone);
                    break;
                case PWD: // 密码登录页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_pwd);
                    break;
                case FIND_PWD: // 找回密码页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_find);
                    break;
                case SET_PWD: // 设置密码页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_set_pwd, phoneNum, smsCode);
                    break;
                case REGISTER: // 注册页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_register);
                    break;
                case BIND_WE_CHAT: // 微信登录页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_we_chat);
                    break;
                case SERVICE: // 服务页面
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_service);
                default:
                    break;
            }
            if (currentFragment != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.login_container, currentFragment, tag).commit();
            }
        } else {
            switch (tag) {
                case MAIN:
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    break;
                case CODE:
                case REGISTER:
                case PWD:
                case FIND_PWD:
                case SET_PWD:
                case BIND_WE_CHAT:
                case BIND_PHONE:
                case SERVICE:
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof LoginRequest) { // 微信登录回调
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
                    obtainLimitUserInfo(data, false);
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) {
                    loginPresenter.checkRegister(phoneNum);
                    ToastUtils.show(mContext, R.string.login_failure);
                    Log.d(TAG, "onMainThreadResponse: " + result.getString("msg"));
                } else if (code == NetworkCodes.CODE_OBTAIN_ACCESS_TOKEN_FAIL) { // 获取 access token 失败
                    Log.d(TAG, "onMainThreadResponse: 获取 access token 失败");
                } else if (code == NetworkCodes.CODE_LOGIN_PASSWORD_ERROR) {
                    ToastUtils.show(mContext, R.string.wrong_password);
                    Log.d(TAG, "onMainThreadResponse: " + result.getString("msg"));
                }
            } else if (iRequest instanceof LoginPhoneCodeRequest) { // 获取验证码回调
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) { // 获取成功
                    Log.d(TAG, "onMainThreadResponse: msg --- " + result.getString("msg"));
                    toggleSoftKeyboard();
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) { // 获取失败
                    Log.d(TAG, "onMainThreadResponse: msg --- " + result.getString("msg"));
                    this.smsCode = "";
                    ((LoginPageFragment) currentFragment).loginCodeContent.setErrorBg(R.drawable.cv_error_bg);
                }
            } else if (iRequest instanceof LoginCodeVerifyRequest) { // 登录验证码验证
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    Log.d(TAG, "onMainThreadResponse: msg --- " + result.getString("msg"));
                    loginPresenter.loginBySmsCode(phoneNum, smsCode);
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) {
                    ToastUtils.show(mContext, R.string.verification_code_error);
                    Log.d(TAG, "onMainThreadResponse: msg --- " + result.getString("msg"));
                }
            } else if (iRequest instanceof LoginRegisterCodeVerifyRequest) { // 注册验证码确认回调
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) { // 验证成功
                    Log.d(TAG, "onMainThreadResponse: msg --- " + result.getString("msg"));
                    ((LoginPageFragment) currentFragment).loginTimeCount.cancel();
                    ((LoginPageFragment) currentFragment).loginCodeContent.clearAllEditText();
                    toggleSoftKeyboard();
                    // 注册流程去到设置密码页
                    replaceFragment(LoginActivity.SET_PWD);
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) {
                    Log.d(TAG, "onMainThreadResponse: msg --- " + result.getString("msg"));
                    this.smsCode = "";
                    ((LoginPageFragment) currentFragment).loginCodeContent.setErrorBg(R.drawable.cv_error_bg);
                }
            } else if (iRequest instanceof LoginFindPwdCodeVerifyRequest) { // 找回密码验证码回调
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) { // 验证成功
                    Log.d(TAG, "onMainThreadResponse: msg --- " + result.getString("msg"));
                    ((LoginPageFragment) currentFragment).loginTimeCount.cancel();
                    ((LoginPageFragment) currentFragment).loginCodeContent.clearAllEditText();
                    toggleSoftKeyboard();
                    replaceFragment(LoginActivity.SET_PWD);
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) { // 验证失败
                    Log.d(TAG, "onMainThreadResponse: msg --- " + result.getString("msg"));
                    this.smsCode = "";
                    ((LoginPageFragment) currentFragment).loginCodeContent.setErrorBg(R.drawable.cv_error_bg);
                }
            } else if (iRequest instanceof LoginCheckRegisterRequest) { // 验证码检测
                int code = result.getInteger("code");
                if (isPwdLogin) { // 密码登录流程
                    if (code == NetworkCodes.CODE_HAD_REGISTER) {
                        loginPresenter.loginByPassword(iRequest.getFormBody().get("phone").toString(), password);
                    } else {
                        ToastUtils.show(mContext, getString(R.string.phone_not_registered));
                    }
                    return;
                }
                if (isRegister) { // 注册流程
                    if (code == NetworkCodes.CODE_HAD_REGISTER) {
                        ((LoginPageFragment) currentFragment).phoneObtainCode.setEnabled(false);
                        ((LoginPageFragment) currentFragment).phoneErrorTip.setText(R.string.phone_had_reg_text);
                        JudgeUtil.showErrorViewIfNeed(this, REGISTER_ERROR_TIP, ((LoginPageFragment) currentFragment).phoneErrorTip, ((LoginPageFragment) currentFragment).phoneObtainCode);
                    } else {
                        ((LoginPageFragment) currentFragment).phoneObtainCode.setEnabled(true);
//                        ((LoginPageFragment) currentFragment).phoneObtainCode.setAlpha(1);
//                        ((LoginPageFragment) currentFragment).phoneObtainCode.setBackground(getResources().getDrawable(R.drawable.person_submit_bg));
                        replaceFragment(CODE);
                        // 发送请求验证码接口
                        loginPresenter.obtainPhoneCode(phoneNum);
                    }
                } else { // 登录流程
                    if (code == NetworkCodes.CODE_HAD_REGISTER) { // 已经注册
                        if (((LoginPageFragment) currentFragment).phoneObtainCode != null) {
                            ((LoginPageFragment) currentFragment).phoneObtainCode.setEnabled(true);
//                            ((LoginPageFragment) currentFragment).phoneObtainCode.setAlpha(1);
//                            ((LoginPageFragment) currentFragment).phoneObtainCode.setBackground(getResources().getDrawable(R.drawable.person_submit_bg));
                        }
                        replaceFragment(CODE);
                        // 发送请求验证码接口
                        loginPresenter.obtainPhoneCode(phoneNum);
                    } else if (code == NetworkCodes.CODE_NO_REGISTER) { // 没有注册
                        ((LoginPageFragment) currentFragment).phoneObtainCode.setEnabled(false);
                        ((LoginPageFragment) currentFragment).phoneErrorTip.setText(R.string.phone_not_reg_text);
                        JudgeUtil.showErrorViewIfNeed(this, REGISTER_ERROR_TIP, ((LoginPageFragment) currentFragment).phoneErrorTip, ((LoginPageFragment) currentFragment).phoneObtainCode);
                    }
                }
            } else if (iRequest instanceof LoginDoRegisterRequest) { // 执行注册操作
                int code = result.getInteger("code");
                String msg = result.getString("msg");
                if (code == NetworkCodes.CODE_LIMIT_USER) { // 受限用户
                    JSONObject data = (JSONObject) result.get("data");
                    obtainLimitUserInfo(data, true);
                    return;
                } else if (code == NetworkCodes.CODE_PARAMS_ERROR) { // 参数错误
                    Log.d(TAG, "onMainThreadResponse: register --- " + msg);
                } else if (code == NetworkCodes.CODE_REGISTER_FAIL) { // 注册失败
                    Log.d(TAG, "onMainThreadResponse: register --- " + msg);
                } else if (code == NetworkCodes.CODE_PHONE_CODE_ERROR) { // 验证码错误
                    Log.d(TAG, "onMainThreadResponse: register --- " + msg);
                }
                ToastUtils.show(mContext, msg);
            } else if (iRequest instanceof LoginBindRequest) { // 绑定操作回调
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) { // 注册成功
                    JSONObject data = (JSONObject) result.get("data");
                    initUserInfo(data);
                } else if (code == NetworkCodes.CODE_PHONE_CODE_ERROR) {
                    Log.d(TAG, "onMainThreadResponse: 验证码错误...");
                    ToastUtils.show(mContext, R.string.verification_code_error);
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
            } else if (iRequest instanceof ResetPasswordRequest) { // 重置密码回调
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) { // 修改成功
                    ToastUtils.show(mContext, R.string.modify_successfully);
                    isPwdLogin = true; // 回到密码登录流程
                    replaceFragment(PWD);
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) { // 修改失败
                    Log.d(TAG, "onMainThreadResponse: 修改失败");
                    ToastUtils.show(mContext, R.string.modification_failed);
                }
            } else if (iRequest instanceof SettingPseronMsgRequest) {
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
            } /*else if (iRequest instanceof TouristsShopIdRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    String touristsShopId = ((JSONObject) result.get("data")).getString("shop_id");
                    SharedPreferencesUtil.putData("touristsShopId", touristsShopId);
                     // 游客登录
                    JumpDetail.jumpMain(this, false);
                } else {
                    Log.d(TAG, "onMainThreadResponse: 游客模式获取店铺 id 失败..");
                }
            }*/
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail");
        }
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
        if (!isTouristClick) {
            JumpDetail.jumpMain(this, true);
        } else {
            ChangeLoginIdentityEvent changeLoginIdentityEvent = new ChangeLoginIdentityEvent();
            changeLoginIdentityEvent.setChangeSuccess(true);
            EventBus.getDefault().post(changeLoginIdentityEvent);
            finish();
        }
    }

    // 获取受限用户信息（此时需要去绑定微信或者手机）
    private void obtainLimitUserInfo(JSONObject data, boolean needBindWx) {

        String accessToken = data.getString("access_token");

        SharedPreferencesUtil.putData("accessToken", accessToken);

        if (needBindWx) {
            // 受限用户绑定微信
            replaceFragment(BIND_WE_CHAT);
        } else {
            // 受限用户绑定手机
            replaceFragment(BIND_PHONE);
        }
    }

    // 注册成功初始化用户信息
    private void initUserInfo(JSONObject data) {
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

    /**
     * 如果软键盘弹出，就关闭软键盘
     */
    private void toggleSoftKeyboard() {
        if (imm != null && imm.isActive()) {
            View view = getCurrentFocus();
            if (view != null) {
                IBinder iBinder = view.getWindowToken();
                imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String apiToken = CommonUserInfo.getApiToken();
        if (apiToken != null && !apiToken.equals("")) { // 有用户登录信息，直接去主页
            JumpDetail.jumpMain(this, true);
            return;
        }
        String code = SharedPreferencesUtil.getData("wx_code", "").toString();
        // 获取注册所需要的信息
        String accessToken = SharedPreferencesUtil.getData("accessToken", "").toString();
        if (!TextUtils.isEmpty(code)) {
            // 注册流程，拉起微信后，进行绑定操作
            if (!TextUtils.isEmpty(accessToken)) { // 表示有操作过
                if (preTag != null && preTag.equals(SET_PWD)) { // 从设置密码点击绑定微信进行注册
                    loginPresenter.bindWeChat(accessToken, code); // 进行绑定微信
                }
            }
            if (preTag == null) { // 从首页直接点击微信登录
                loginPresenter.loginByWeChat(code);
            }
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
}
