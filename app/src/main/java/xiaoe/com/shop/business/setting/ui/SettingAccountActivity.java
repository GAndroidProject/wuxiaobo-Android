package xiaoe.com.shop.business.setting.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.app.Global;
import xiaoe.com.common.utils.CacheManagerUtil;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.setting.presenter.SettingPresenter;
import xiaoe.com.shop.utils.StatusBarUtil;
import xiaoe.com.shop.widget.CodeVerifyView;

public class SettingAccountActivity extends XiaoeActivity {

    private static final String TAG = "SettingAccountActivity";

    // 需要切换的 fragment tag
    protected static final String MAIN = "main"; // 设置主页
    protected static final String ACCOUNT = "account"; // 账号设置
    protected static final String MESSAGE = "message"; // 推送消息设置
    protected static final String CACHE = "cache"; // 清除缓存
    protected static final String VERSION = "version"; // 版本更新
    protected static final String ABOUT = "about"; // 关于
    protected static final String SUGGESTION = "suggestion"; // 建议

    protected static final String PHONE = "phone"; // 手机
    protected static final String PHONE_NUM = "phone_num"; // 手机号
    protected static final String PWD_NOW = "password_now"; // 输入现在的密码
    protected static final String PWD_NEW = "password_new"; // 设置新的密码
    protected static final String WE_CHAT = "we_chat"; // 绑定微信

    @BindView(R.id.setting_account_edit_back)
    ImageView accountBack;
    @BindView(R.id.setting_account_edit_title)
    TextView accountTitle;

    private Fragment currentFragment;
    // 软键盘
    InputMethodManager imm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            Global.g().setGlobalColor("#000000");
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_VISIBLE);
        }

        // 网络请求
        // SettingPresenter settingPresenter = new SettingPresenter(this);
        // settingPresenter.requestSearchResult();

        currentFragment = new MainAccountFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.account_container, currentFragment, MAIN).commit();
        init();
    }

    private void init() {
        accountBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (imm != null && imm.isActive()) {
            View view = this.getCurrentFocus();
            if (view != null) {
                IBinder iBinder = view.getWindowToken();
                imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        if (currentFragment != null) {
            switch (currentFragment.getTag()) {
                case MAIN:
                    super.onBackPressed();
                    break;
                case ACCOUNT:
                case MESSAGE:
                case ABOUT:
                case SUGGESTION:
                case WE_CHAT:
                    accountTitle.setText("设置");
                    replaceFragment(MAIN);
                    break;
                case PHONE:
                case PWD_NOW:
                case PHONE_NUM:
                    accountTitle.setText("账号设置");
                    // 返回也要清空
                    CodeVerifyView verifyView = ((EditDataFragment) currentFragment).phoneNumContent;
                    if (verifyView != null && verifyView.hasContent()){
                        verifyView.clearAllEditText();
                    }
                    replaceFragment(ACCOUNT);
                    break;
                case PWD_NEW:
                    accountTitle.setText("设置新密码");
                    replaceFragment(PWD_NOW);
                    break;
            }
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
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
            case 5: // 意见反馈
                return SUGGESTION;
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
                    final AlertDialog versionDialog = new AlertDialog.Builder(this)
                            .setTitle("")
                            .setMessage("最新版本更新了功能，是否下载")
                            .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("不了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    versionDialog.show();
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
                    currentFragment = EditDataFragment.newInstance(R.layout.fragment_account);
                    break;
                case MESSAGE: // 推送消息设置
                    accountTitle.setText("推送消息设置");
                    currentFragment = EditDataFragment.newInstance(R.layout.fragment_message);
                    break;
                case ABOUT: // 关于我们
                    accountTitle.setText("关于我们");
                    currentFragment = EditDataFragment.newInstance(R.layout.fragment_about);
                    break;
                case SUGGESTION: // 意见反馈
                    accountTitle.setText("意见反馈");
                    currentFragment = EditDataFragment.newInstance(R.layout.fragment_suggestion);
                    break;
                case PHONE:
                    accountTitle.setText("更换手机号");
                    currentFragment = EditDataFragment.newInstance(R.layout.fragment_phone);
                    break;
                case PWD_NOW:
                    accountTitle.setText("设置新密码");
                    currentFragment = EditDataFragment.newInstance(R.layout.fragment_pwd_now);
                    break;
                case PWD_NEW:
                    accountTitle.setText("设置新密码");
                    currentFragment = EditDataFragment.newInstance(R.layout.fragment_pwd_new);
                    break;
                case PHONE_NUM:
                    accountTitle.setText("修改密码");
                    currentFragment = EditDataFragment.newInstance(R.layout.fragment_phone_num);
                    break;
                case WE_CHAT:
                    accountTitle.setText("绑定微信");
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
                case SUGGESTION: // 意见反馈
                    accountTitle.setText("意见反馈");
                    break;
                case PHONE:
                    accountTitle.setText("更换手机号");
                    break;
                case PWD_NOW:
                    accountTitle.setText("设置新密码");
                    break;
                case PWD_NEW:
                    accountTitle.setText("设置新密码");
                    break;
                case PHONE_NUM:
                    accountTitle.setText("修改密码");
                    break;
                case WE_CHAT:
                    accountTitle.setText("绑定微信");
                    break;
            }
            getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        }
    }
}
