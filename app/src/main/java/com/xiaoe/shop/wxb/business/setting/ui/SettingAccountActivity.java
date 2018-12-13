package com.xiaoe.shop.wxb.business.setting.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.LoginSQLiteCallback;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.CacheManagerUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.LoginCodeVerifyRequest;
import com.xiaoe.network.requests.LoginNewCodeVerifyRequest;
import com.xiaoe.network.requests.LoginPhoneCodeRequest;
import com.xiaoe.network.requests.ResetPasswordRequest;
import com.xiaoe.network.requests.SetPushStateRequest;
import com.xiaoe.network.requests.UpdatePhoneRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.setting.presenter.SettingPresenter;
import com.xiaoe.shop.wxb.business.upgrade.AppUpgradeHelper;
import com.xiaoe.shop.wxb.common.login.LoginPresenter;
import com.xiaoe.shop.wxb.interfaces.OnCustomDialogListener;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.utils.ToastUtils;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingAccountActivity extends XiaoeActivity {

    private static final String TAG = "SettingAccountActivity";

    private static final int CLEAN_SUCCESS = 0X001;
    private static final int CLEAN_FAIL = 0X002;

    // 需要切换的 fragment tag
    protected static final String MAIN = "main"; // 设置主页
    // protected static final String ACCOUNT = "account"; // 账号设置
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
    SettingPresenter settingPresenter;

    String smsCode = ""; // 原来手机的验证码
    String newSmsCode = ""; // 新手机的验证码

    String apiToken = ""; // api_token
    String localPhone = ""; // 本地存的电话
    boolean inputNewPhone = false; // 输入新的手机号
    String newPhone = ""; // 新手机号
    boolean isUpdatePassword = false; // 是否修改密码

    boolean isHasUpgradeCurrentApp;

    private Handler cacheHandler = new CacheHandler(this);

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

        // 网络请求
        // SettingPresenter settingPresenter = new SettingPresenter(this);
        // settingPresenter.requestSearchResult();
        apiToken = CommonUserInfo.getApiToken();
        localPhone = CommonUserInfo.getPhone();
        currentFragment = new MainAccountFragment();
        loginPresenter = new LoginPresenter(this, this);
        settingPresenter = new SettingPresenter(this);
        getSupportFragmentManager().beginTransaction().add(R.id.account_container, currentFragment, MAIN).commit();
        init();
        isHasUpgradeCurrentApp = AppUpgradeHelper.getInstance().isHasUpgradeCurrentApp();
    }

    private void init() {
        accountTitle.setText(getString(R.string.setting_account_title));
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
                    ToastUtils.show(mContext, getString(R.string.failed_get_verification_code));
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
                    ToastUtils.show(mContext, R.string.verification_code_error);
                    Log.d(TAG, "onMainThreadResponse: 旧手机号验证失败...");
                }
            } else if (iRequest instanceof LoginNewCodeVerifyRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    loginPresenter.updatePhone(apiToken, smsCode, newPhone, newSmsCode);
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) {
                    ToastUtils.show(mContext, R.string.verification_code_error);
                    Log.d(TAG, "onMainThreadResponse: 新手机验证失败...");
                }
            } else if (iRequest instanceof UpdatePhoneRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    updateLocalUserInfo();
                    ToastUtils.show(mContext, getString(R.string.replace_success));
                    replaceFragment(MAIN);
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) {
                    ToastUtils.show(mContext, getString(R.string.replace_failed));
                    Log.d(TAG, "onMainThreadResponse: 更换失败");
                }
            } else if (iRequest instanceof ResetPasswordRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    ToastUtils.show(mContext, R.string.modify_successfully);
                    accountTitle.setText(getString(R.string.setting_account_title));
                    replaceFragment(SettingAccountActivity.MAIN);
                } else if (code == NetworkCodes.CODE_LOGIN_FAIL) {
                    Log.d(TAG, "onMainThreadResponse: 修改密码失败...");
                }
//            } else if (iRequest instanceof GetPushStateRequest) {
//                int code = result.getInteger("code");
//                if (code == NetworkCodes.CODE_SUCCEED) {
//                    JSONObject data = result.getJSONObject("data");
//                    String isPushState = data.getString("is_push_state");
//                    SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_JPUSH_STATE_CODE, isPushState);
//                    if (currentFragment instanceof SettingAccountFragment) {
//                        ((SettingAccountFragment) currentFragment).updateMessageFragment(true);
//                    }
//                } else if (code == NetworkCodes.CODE_FAILED) {
//                    Log.d(TAG, "onMainThreadResponse: 获取推送消息状态失败...");
//                }
            } else if (iRequest instanceof SetPushStateRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    if (currentFragment instanceof SettingAccountFragment) {
                        ((SettingAccountFragment) currentFragment).updateMessageFragment(false);
                    }
                } else if (code == NetworkCodes.CODE_FAILED) {
                    Log.d(TAG, "onMainThreadResponse: 设置推送消息状态失败...");
                    ToastUtils.show(mContext, getString(R.string.setup_failed));
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail, params error may be...");
        }
    }

    // 设置主页的转换
    protected String int2Str(int position) {
        switch (position) {
//            case 0: // 账号设置
//                return ACCOUNT;
            case 0: // 推送消息设置
                return MESSAGE;
            case 1: // 消除缓存
                return CACHE;
            case 2: // 版本更新
                return VERSION;
            case 3: // 关于我们
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
                    getDialog().setTitleVisibility(View.VISIBLE);
                    getDialog().setTitle(getString(R.string.sure_to_clear));
                    getDialog().setMessageVisibility(View.GONE);
                    getDialog().setConfirmText(getResources().getString(R.string.confirm_title));
                    getDialog().setCancelText(getResources().getString(R.string.cancel_title));
                    getDialog().setOnCustomDialogListener(new OnCustomDialogListener() {
                        @Override
                        public void onClickCancel(View view, int tag) {
                            getDialog().dismissDialog();
                        }

                        @Override
                        public void onClickConfirm(View view, int tag) {
                            clearCache();
                            getDialog().dismissDialog();
                        }

                        @Override
                        public void onDialogDismiss(DialogInterface dialog, int tag, boolean backKey) {

                        }
                    });
                    getDialog().showDialog(1);
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
//                case ACCOUNT: // 账号设置
//                    accountTitle.setText(getString(R.string.account_settings));
//                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_account);
//                    break;
                case MESSAGE: // 推送消息设置
                    accountTitle.setText(getString(R.string.message_settings));
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_message);
                    break;
                case ABOUT: // 关于我们
                    accountTitle.setText(getString(R.string.setting_about_us));
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_about);
                    break;
//                case SUGGESTION: // 意见反馈
//                    accountTitle.setText("意见反馈");
//                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_suggestion);
//                    break;
                case CURRENT_PHONE:
                    accountTitle.setText(R.string.change_phone_number);
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_current_phone);
                    break;
                case PWD_PHONE_CODE:
                    accountTitle.setText(R.string.change_password);
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_pwd_obtain_phone_code);
                    break;
                case PWD_NEW:
                    accountTitle.setText(getString(R.string.login_set_pwd_title));
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_pwd_new);
                    break;
                case PHONE_CODE:
                    accountTitle.setText(getString(R.string.login_code_title));
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_phone_code);
                    break;
                case COMPLETE:
                    accountTitle.setText(R.string.modify_phone_number);
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_change_phone_complete);
                    break;
                case SERVICE:
                    accountTitle.setText(getString(R.string.self_service_title));
                    currentFragment = SettingAccountFragment.newInstance(R.layout.fragment_service);
                    break;
                default:
                    break;
            }
            if (currentFragment != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.account_container, currentFragment, tag).commit();
            }
        } else {
            switch (currentFragment.getTag()) {
//                case ACCOUNT: // 账号设置
//                    accountTitle.setText(getString(R.string.account_settings));
//                    break;
                case MESSAGE: // 推送消息设置
                    accountTitle.setText(getString(R.string.message_settings));
                    break;
                case ABOUT: // 关于我们
                    accountTitle.setText(getString(R.string.setting_about_us));
                    break;
//                case SUGGESTION: // 意见反馈
//                    accountTitle.setText("意见反馈");
//                    break;
                case CURRENT_PHONE:
                    accountTitle.setText(getString(R.string.change_phone_number));
                    break;
                case PWD_PHONE_CODE:
                    accountTitle.setText(getString(R.string.login_set_pwd_title));
                    break;
                case PWD_NEW:
                    accountTitle.setText(getString(R.string.login_set_pwd_title));
                    break;
                case PHONE_CODE:
                    accountTitle.setText(getString(R.string.login_code_title));
                    break;
                case COMPLETE:
                    accountTitle.setText(getString(R.string.modify_phone_number));
                    break;
                case SERVICE:
                    accountTitle.setText(getString(R.string.self_service_title));
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

        SQLiteUtil.init(this, new LoginSQLiteCallback()).update(LoginSQLiteCallback.TABLE_NAME_USER, userInfo, "user_id = ?", new String[]{ userInfo.getUserId() });

        // TODO: 更新 recyclerView
    }

    @Override
    public void onHeadLeftButtonClick(View v) {
        toggleSoftKeyboard();
        if (currentFragment == null) {
            return;
        }
        String tag = currentFragment.getTag();
        if (MAIN.equals(tag)) {
            super.onHeadLeftButtonClick(v);
        } else {
            onBackAction(tag);
        }
    }

    @Override
    public void onBackPressed() {
        toggleSoftKeyboard();
        if (currentFragment == null) {
            return;
        }
        String tag = currentFragment.getTag();
        if (MAIN.equals(tag)) {
            super.onBackPressed();
        } else {
            onBackAction(tag);
        }
    }

    private void onBackAction(String fragmentTag) {
        switch (fragmentTag) {
//            case ACCOUNT:
            case MESSAGE:
            case ABOUT:
//                case SUGGESTION:
            case COMPLETE:
                accountTitle.setText(getString(R.string.setting_account_title));
                replaceFragment(MAIN);
                break;
            case CURRENT_PHONE:
            case PWD_PHONE_CODE:
//            case PHONE_CODE:
//                accountTitle.setText(getString(R.string.account_settings));
//                replaceFragment(ACCOUNT);
//                break;
            case PWD_NEW:
                accountTitle.setText(getString(R.string.login_set_pwd_title));
                replaceFragment(PWD_PHONE_CODE);
                break;
            case SERVICE:
                accountTitle.setText(getString(R.string.setting_about_us));
                replaceFragment(ABOUT);
                break;
            default:
                break;
        }
    }

    // 在子线程中清除缓存
    private void clearCache() {
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    CacheManagerUtil.clearAllCache(getApplicationContext());
                    msg.what = CLEAN_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = CLEAN_FAIL;
                }
                cacheHandler.sendMessage(msg);
            }
        }.start();
    }

    static class CacheHandler extends Handler {

        WeakReference<SettingAccountActivity> wrf;

        CacheHandler(SettingAccountActivity activity) {
            wrf = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == CLEAN_SUCCESS){
                try {
                    MainAccountFragment mainAccountFragment = (MainAccountFragment) wrf.get().currentFragment;
                    mainAccountFragment.itemInfoList.get(1).setItemContent(CacheManagerUtil.getTotalCacheSize(wrf.get().getApplicationContext()));
                    mainAccountFragment.settingRecyclerAdapter.notifyDataSetChanged();
                    ToastUtils.show(XiaoeApplication.applicationContext, R.string.clear_success);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(msg.what == CLEAN_FAIL){
                ToastUtils.show(XiaoeApplication.applicationContext, R.string.removal_failure);
            }
        }

    }
}
