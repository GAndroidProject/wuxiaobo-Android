package com.xiaoe.shop.wxb.business.setting.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.CacheManagerUtil;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.LoginCodeVerifyRequest;
import com.xiaoe.network.requests.LoginNewCodeVerifyRequest;
import com.xiaoe.network.requests.LoginPhoneCodeRequest;
import com.xiaoe.network.requests.ResetPasswordRequest;
import com.xiaoe.network.requests.UpdatePhoneRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseResult;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.common.db.LoginSQLiteCallback;
import com.xiaoe.shop.wxb.business.upgrade.AppUpgradeHelper;
import com.xiaoe.shop.wxb.common.login.LoginPresenter;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;

public class SettingAccountActivity extends XiaoeActivity {

    private static final String TAG = "SettingAccountActivity";

    // 需要切换的 fragment tag
    protected static final String MAIN = "main"; // 设置主页
    protected static final String ACCOUNT = "account"; // 账号设置
    protected static final String MESSAGE = "message"; // 推送消息设置
    protected static final String CACHE = "cache"; // 清除缓存
    protected static final String VERSION = "version"; // 版本更新
    protected static final String ABOUT = "about"; // 关于
//    protected static final String SUGGESTION = "suggestion"; // 建议
    protected static final String SERVICE = "service";

    protected static final String CURRENT_PHONE = "current_phone"; // 更换手机号界面
    protected static final String PHONE_CODE = "phone_code"; // 更换手机获取验证码
    protected static final String PWD_PHONE_CODE = "pwd_phone_code"; // 修改密码获取验证码
    protected static final String PWD_NEW = "password_new"; // 设置新的密码
    protected static final String COMPLETE = "complete"; // 完成修改页面

    @BindView(R.id.account_toolbar)
    Toolbar accountToolbar;
    @BindView(R.id.setting_account_edit_back)
    ImageView accountBack;
    @BindView(R.id.setting_account_edit_title)
    TextView accountTitle;

    private Fragment currentFragment;
    // 软键盘
    InputMethodManager imm;

    LoginPresenter loginPresenter;

    String smsCode = ""; // 原来手机的验证码
    String newSmsCode = ""; // 新手机的验证码

    String apiToken = ""; // api_token
    String localPhone = ""; // 本地存的电话
    boolean inputNewPhone = false; // 输入新的手机号
    String newPhone = ""; // 新手机号
    boolean isUpdatePassword = false; // 是否修改密码

    boolean isHasUpgradeCurrentApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_setting_account);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        accountToolbar.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);

        // 网络请求
        // SettingPresenter settingPresenter = new SettingPresenter(this);
        // settingPresenter.requestSearchResult();
        apiToken = CommonUserInfo.getApiToken();
        localPhone = CommonUserInfo.getPhone();
        currentFragment = new MainAccountFragment();
        loginPresenter = new LoginPresenter(this, this);
        getSupportFragmentManager().beginTransaction().add(R.id.account_container, currentFragment, MAIN).commit();
        init();
        isHasUpgradeCurrentApp = AppUpgradeHelper.getInstance().isHasUpgradeCurrentApp();
    }

    private void init() {
        accountTitle.setText("设置");
    }

    @Override
    public void onBackPressed() {
        toggleSoftKeyboard();
        if (currentFragment != null) {
            switch (currentFragment.getTag()) {
                case MAIN:
                    super.onBackPressed();
                    break;
                case ACCOUNT:
                case MESSAGE:
                case ABOUT:
//                case SUGGESTION:
                case COMPLETE:
                    accountTitle.setText("设置");
                    replaceFragment(MAIN);
                    break;
                case CURRENT_PHONE:
                case PWD_PHONE_CODE:
                case PHONE_CODE:
                    accountTitle.setText("账号设置");
                    replaceFragment(ACCOUNT);
                    break;
                case PWD_NEW:
                    accountTitle.setText("设置新密码");
                    replaceFragment(PWD_PHONE_CODE);
                    break;
                case SERVICE:
                    accountTitle.setText("关于我们");
                    replaceFragment(ABOUT);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof LoginPhoneCodeRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) { // 发送成功
                    replaceFragment(PHONE_CODE);
                    if (inputNewPhone) {
                        // 拿到新手机号之后修改验证码 title
                        ((SettingAccountFragment) currentFragment).phoneNumTitle.setText(String.format(getResources().getString(R.string.setting_phone_num_title), newPhone));
                    }
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) { // 发送失败
                    Toast("获取验证码失败");
                    Log.d(TAG, "onMainThreadResponse: 发送失败");
                }
            } else if (iRequest instanceof LoginCodeVerifyRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    if (isUpdatePassword) {
                        replaceFragment(PWD_NEW);
                        return;
                    }
                    replaceFragment(COMPLETE);
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) {
                    Toast("验证码错误");
                    Log.d(TAG, "onMainThreadResponse: 旧手机号验证失败...");
                }
            } else if (iRequest instanceof LoginNewCodeVerifyRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    loginPresenter.updatePhone(apiToken, smsCode, newPhone, newSmsCode);
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) {
                    Toast("验证码错误");
                    Log.d(TAG, "onMainThreadResponse: 新手机验证失败...");
                }
            } else if (iRequest instanceof UpdatePhoneRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    updateLocalUserInfo();
                    Toast("更换成功");
                    replaceFragment(MAIN);
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) {
                    Toast("更换失败");
                    Log.d(TAG, "onMainThreadResponse: 更换失败");
                }
            } else if (iRequest instanceof ResetPasswordRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    Toast("修改成功");
                    accountTitle.setText("设置");
                    replaceFragment(SettingAccountActivity.MAIN);
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) {
                    Log.d(TAG, "onMainThreadResponse: 修改密码失败...");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail, params error may be...");
        }
    }

    // 设置主页的转换
    protected String int2Str(int position) {
        switch (position) {
            case 0: // 账号设置
                return ACCOUNT;
            case 1: // 推送消息设置
                return MESSAGE;
            case 2: // 消除缓存
                return CACHE;
            case 3: // 版本更新
                return VERSION;
            case 4: // 关于我们
                return ABOUT;
//            case 5: // 意见反馈
//                return SUGGESTION;
            default:
                return null;
        }
    }

    // 切换显示的 fragment
    protected void replaceFragment(String tag) {
        if (tag.equals(CACHE) || tag.equals(VERSION)) {
            switch (tag) {
                case CACHE:
                    final AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("")
                        .setMessage("确定清除")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CacheManagerUtil.clearAllCache(SettingAccountActivity.this);
                                MainAccountFragment mainAccountFragment = (MainAccountFragment) currentFragment;
                                mainAccountFragment.itemInfoList.get(2).setItemContent("");
                                mainAccountFragment.settingRecyclerAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                    alertDialog.show();
                    break;
                case VERSION: // 版本更新
//                    final AlertDialog versionDialog = new AlertDialog.Builder(this)
//                            .setTitle("")
//                            .setMessage("最新版本更新了功能，是否下载")
//                            .setPositiveButton("下载", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .setNegativeButton("不了", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            }).create();
//                    versionDialog.show();

                    if (isHasUpgradeCurrentApp){
                        AppUpgradeHelper.getInstance().checkUpgrade(true,this);
                    }
                    break;
                default:
                    break;
            }
            return;
        }
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(currentFragment).commit();
        }
        currentFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (currentFragment == null) {
            switch (tag) {
                case ACCOUNT: // 账号设置
                    accountTitle.setText("账号设置");
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_account);
                    break;
                case MESSAGE: // 推送消息设置
                    accountTitle.setText("推送消息设置");
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_message);
                    break;
                case ABOUT: // 关于我们
                    accountTitle.setText("关于我们");
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_about);
                    break;
//                case SUGGESTION: // 意见反馈
//                    accountTitle.setText("意见反馈");
//                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_suggestion);
//                    break;
                case CURRENT_PHONE:
                    accountTitle.setText("更换手机号");
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_current_phone);
                    break;
                case PWD_PHONE_CODE:
                    accountTitle.setText("修改密码");
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_pwd_obtain_phone_code);
                    break;
                case PWD_NEW:
                    accountTitle.setText("设置新密码");
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_pwd_new);
                    break;
                case PHONE_CODE:
                    accountTitle.setText("输入验证码");
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_phone_code);
                    break;
                case COMPLETE:
                    accountTitle.setText("修改手机号");
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_change_phone_complete);
                case SERVICE:
                    accountTitle.setText("服务协议");
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_service);
                default:
                    break;
            }
            if (currentFragment != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.account_container, currentFragment, tag).commit();
            }
        } else {
            switch (currentFragment.getTag()) {
                case ACCOUNT: // 账号设置
                    accountTitle.setText("账号设置");
                    break;
                case MESSAGE: // 推送消息设置
                    accountTitle.setText("推送消息设置");
                    break;
                case ABOUT: // 关于我们
                    accountTitle.setText("关于我们");
                    break;
//                case SUGGESTION: // 意见反馈
//                    accountTitle.setText("意见反馈");
//                    break;
                case CURRENT_PHONE:
                    accountTitle.setText("更换手机号");
                    break;
                case PWD_PHONE_CODE:
                    accountTitle.setText("设置新密码");
                    break;
                case PWD_NEW:
                    accountTitle.setText("设置新密码");
                    break;
                case PHONE_CODE:
                    accountTitle.setText("输入验证码");
                    break;
                case COMPLETE:
                    accountTitle.setText("修改手机号");
                    break;
                case SERVICE:
                    accountTitle.setText("服务协议");
                    break;
                default:
                    break;
            }
            getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        }
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

    private void updateLocalUserInfo() {
        LoginUser userInfo = getLoginUserList().get(0);
        userInfo.setPhone(newPhone);

        SQLiteUtil.update(LoginSQLiteCallback.TABLE_NAME_USER, userInfo, "user_id = ?", new String[]{ userInfo.getUserId() });

        // TODO: 更新 recyclerView
    }

    @Override
    public void onHeadLeftButtonClick(View v) {
        toggleSoftKeyboard();
        if (currentFragment != null) {
            switch (currentFragment.getTag()) {
                case MAIN:
                    super.onHeadLeftButtonClick(v);
                    break;
                case ACCOUNT:
                case MESSAGE:
                case ABOUT:
//                case SUGGESTION:
                case COMPLETE:
                    accountTitle.setText("设置");
                    replaceFragment(MAIN);
                    break;
                case CURRENT_PHONE:
                case PWD_PHONE_CODE:
                case PHONE_CODE:
                    accountTitle.setText("账号设置");
                    replaceFragment(ACCOUNT);
                    break;
                case PWD_NEW:
                    accountTitle.setText("设置新密码");
                    replaceFragment(PWD_PHONE_CODE);
                    break;
                default:
                    break;
            }
        }
    }
}
