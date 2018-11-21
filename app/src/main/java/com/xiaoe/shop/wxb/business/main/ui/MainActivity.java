package com.xiaoe.shop.wxb.business.main.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.BindJgPushRequest;
import com.xiaoe.network.requests.GetPushStateRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.IsSuperVipRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.main.MainFragmentStatePagerAdapter;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.ui.MiniAudioPlayControllerLayout;
import com.xiaoe.shop.wxb.business.main.presenter.MessagePushPresenter;
import com.xiaoe.shop.wxb.business.setting.presenter.SettingPresenter;
import com.xiaoe.shop.wxb.business.super_vip.presenter.SuperVipPresenter;
import com.xiaoe.shop.wxb.business.upgrade.AppUpgradeHelper;
import com.xiaoe.shop.wxb.common.jpush.ExampleUtil;
import com.xiaoe.shop.wxb.common.jpush.LocalBroadcastManager;
import com.xiaoe.shop.wxb.common.jpush.entity.JgPushSaveInfo;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;
import com.xiaoe.shop.wxb.interfaces.OnBottomTabSelectListener;
import com.xiaoe.shop.wxb.utils.OSUtils;
import com.xiaoe.shop.wxb.widget.BottomTabBar;
import com.xiaoe.shop.wxb.widget.ScrollViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends XiaoeActivity implements OnBottomTabSelectListener {
    private static final String TAG = "MainActivity";
    private BottomTabBar bottomTabBar;
    private ScrollViewPager mainViewPager;
    private MiniAudioPlayControllerLayout miniAudioPlayController;

    public static final String MICRO_PAGE_MAIN = "app_home_page";
    public static final String MICRO_PAGE_COURSE = "app_course_page";

    public static boolean isForeground = false;
    private Intent audioPlayServiceIntent;

    Intent intent;
    public boolean isFormalUser;

    SuperVipPresenter superVipPresenter;
    public String expireAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate -- before");
        // 设置状态栏需要在 setContentView 之前进行
        setStatusBar();

        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

//        apiToken = CommonUserInfo.getApiToken();
//        loginUserList = getLoginUserInfoList();
//        settingPresenter = new SettingPresenter(this);
//        settingPresenter.requestPersonData(apiToken, false);
//        SharedPreferencesUtil.getInstance(this, SharedPreferencesUtil.FILE_NAME);
        intent = getIntent();

        isFormalUser = intent.getBooleanExtra("isFormalUser", false);

        if (!isFormalUser) {
            // 非正式用户，需要手动设置店铺 id 和默认登录 userId
            CommonUserInfo.setUserId(Constants.ANONYMOUS_USER_ID);
        }

        superVipPresenter = new SuperVipPresenter(this);
        superVipPresenter.requestSuperVip();

        // 请求绑定极光推送
        String jgPushRegId = JPushInterface.getRegistrationID(mContext);
        Log.d(TAG, "onCreate: JPush RegID " + jgPushRegId);

        String bindJPushSuccess = (String) SharedPreferencesUtil.getData(SharedPreferencesUtil.KEY_BIND_JPUSH_USER_CODE, "");
        JgPushSaveInfo jgPushSaveInfo = JSON.parseObject(bindJPushSuccess, JgPushSaveInfo.class);
        Log.d(TAG, "onCreate: " + jgPushSaveInfo);
        MessagePushPresenter messagePushPresenter = new MessagePushPresenter(this);
        if (jgPushSaveInfo != null) {
            if (!jgPushSaveInfo.isBindRegIdSuccess()) {
                messagePushPresenter.requestBindJgPush();
            } else {
                if (jgPushRegId != null && !jgPushRegId.equals(jgPushSaveInfo.getRegistrationID())) {
                    messagePushPresenter.requestBindJgPush();
                }
            }
        } else {
            if (jgPushRegId != null) {
                messagePushPresenter.requestBindJgPush();
            }
        }

        initView();
        initPermission();
        if (!OSUtils.isServiceRunning(this,AudioMediaPlayer.class.getName())) {
            audioPlayServiceIntent = new Intent(this, AudioMediaPlayer.class);
            startService(audioPlayServiceIntent);
        }

        registerMessageReceiver();  // used for receive msg

        //启动应用时自动检测版本更新
        AppUpgradeHelper.getInstance().registerEventBus();
        if (AppUpgradeHelper.getInstance().isShouldCheckUpgrade()) {//只有启动应用时候才检查更新
            AppUpgradeHelper.getInstance().checkUpgrade(false, this);
            AppUpgradeHelper.getInstance().setShouldCheckUpgrade(false);
        }
        // 获取消息接收状态
        new SettingPresenter(this).getPushState();
    }

    private void initView() {
        RelativeLayout mainActivityRootView = (RelativeLayout) findViewById(R.id.main_activity_root_view);
        mainActivityRootView.setBackgroundColor(Color.parseColor(Global.g().getGlobalColor()));
        bottomTabBar = (BottomTabBar) findViewById(R.id.bottom_tab_bar_layout);
        bottomTabBar.setBottomTabBarOrientation(LinearLayout.HORIZONTAL);
        bottomTabBar.setTabBarWeightSum(4);
        bottomTabBar.setBottomTabSelectListener(this);
        List<String> buttonNames = new ArrayList<String>();
        buttonNames.add("今日");
        buttonNames.add("课程");
        buttonNames.add("奖学金");
        buttonNames.add("我的");
        List<Integer> buttonCheckedIcons = new ArrayList<Integer>();
        buttonCheckedIcons.add(R.mipmap.today_selected);
        buttonCheckedIcons.add(R.mipmap.class_selected);
        buttonCheckedIcons.add(R.mipmap.task_selected);
        buttonCheckedIcons.add(R.mipmap.profile_selected);
        List<Integer> buttonIcons = new ArrayList<Integer>();
        buttonIcons.add(R.mipmap.today_default);
        buttonIcons.add(R.mipmap.class_default);
        buttonIcons.add(R.mipmap.task_default);
        buttonIcons.add(R.mipmap.profile_default);
        bottomTabBar.addTabButton(4, buttonNames, buttonIcons, buttonCheckedIcons, getResources().getColor(R.color.secondary_button_text_color), getResources().getColor(R.color.high_title_color));

        mainViewPager = (ScrollViewPager) findViewById(R.id.main_view_pager);
        mainViewPager.setScroll(false);
        mainViewPager.setAdapter(new MainFragmentStatePagerAdapter(getSupportFragmentManager()));
        mainViewPager.setOffscreenPageLimit(3);

//        boolean needChange = intent.getBooleanExtra("needChange", false);
//        int tabIndex = intent.getIntExtra("tabIndex", 0);
//        if (needChange) {
//            // 如果需要改变，就去到奖学金页面
//            replaceFragment(tabIndex);
//        }
//        Log.d(TAG, "initView: needChange " + needChange + " tabIndex " + tabIndex);

        miniAudioPlayController = (MiniAudioPlayControllerLayout) findViewById(R.id.mini_audio_play_controller);
        setMiniAudioPlayController(miniAudioPlayController);
//        miniAudioPlayController.setMiniPlayerAnimHeight(Dp2Px2SpUtil.dp2px(this, 76));
        setMiniPlayerAnimHeight(Dp2Px2SpUtil.dp2px(this, 76));
    }

    @Override
    public void initPermission() {
        super.initPermission();
    }

    @Override
    public void onCheckedTab(int index) {
        Log.d(TAG, "onCheckedTab: "+index);
        mainViewPager.setCurrentItem(index);
        Log.d(TAG, "onCheckedTab: I "+mainViewPager.getCurrentItem());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean needChange = intent.getBooleanExtra("needChange", false);
        int tabIndex = intent.getIntExtra("tabIndex", 0);
        if (needChange) {
            // 如果需要改变，就去到奖学金页面
            replaceFragment(tabIndex);
        }
        Log.d(TAG, "onNewIntent: needChange " + needChange + " tabIndex " + tabIndex);
    }

    protected void replaceFragment(int index) {
        mainViewPager.setCurrentItem(index);
        bottomTabBar.setCheckedButton(index);
    }

    @Subscribe
    public void onEventMainThread(AudioPlayEvent event) {
        AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        if(playEntity == null){
            return;
        }
        switch (event.getState()){
            case AudioPlayEvent.LOADING:
                miniAudioPlayController.setVisibility(View.VISIBLE);
                miniAudioPlayController.setAudioTitle(playEntity.getTitle());
                miniAudioPlayController.setColumnTitle(playEntity.getProductsTitle());
                miniAudioPlayController.setPlayButtonEnabled(false);
                miniAudioPlayController.setPlayState(AudioPlayEvent.PAUSE);
                break;
            case AudioPlayEvent.PLAY:
                miniAudioPlayController.setPlayButtonEnabled(true);
                miniAudioPlayController.setAudioTitle(playEntity.getTitle());
                miniAudioPlayController.setColumnTitle(playEntity.getProductsTitle());
                miniAudioPlayController.setPlayState(AudioPlayEvent.PLAY);
                miniAudioPlayController.setMaxProgress(AudioMediaPlayer.getDuration());
                break;
            case AudioPlayEvent.PAUSE:
                miniAudioPlayController.setPlayState(AudioPlayEvent.PAUSE);
                break;
            case AudioPlayEvent.STOP:
                miniAudioPlayController.setPlayState(AudioPlayEvent.PAUSE);
                break;
            case AudioPlayEvent.PROGRESS:
                miniAudioPlayController.setProgress(event.getProgress());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        Log.d(TAG,"onResume");
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(!AudioMediaPlayer.isStop()){
            if(!AudioMediaPlayer.isPlaying()){
                AudioMediaPlayer.release();
                if (OSUtils.isServiceRunning(this,AudioMediaPlayer.class.getName())){
                    if(audioPlayServiceIntent!= null){
                        stopService(audioPlayServiceIntent);
                    }
                }
            }
        }else{
            if (OSUtils.isServiceRunning(this,AudioMediaPlayer.class.getName()))
                if(audioPlayServiceIntent!= null){
                    stopService(audioPlayServiceIntent);
                }

        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        AppUpgradeHelper.getInstance().unregisterEventBus();
//        DownloadFileConfig.getInstance().dbClose();
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof IsSuperVipRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED || code == NetworkCodes.CODE_SUPER_VIP) { // 返回的 code 有 0 和 3011
                    JSONObject data = (JSONObject) result.get("data");
                    initSuperVipMsg(data);
                } else {
                    Log.d(TAG, "onMainThreadResponse: 获取超级会员信息失败...");
                }
            } else if (iRequest instanceof BindJgPushRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    setBindJPushSP(true);
                    Log.d(TAG, "onMainThreadResponse: 绑定极光推送成功...");
                } else {
                    setBindJPushSP(false);
                    Log.d(TAG, "onMainThreadResponse: 绑定极光推送失败...");
                }
            } else if (iRequest instanceof GetPushStateRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = result.getJSONObject("data");
                    String isPushState = data.getString("is_push_state");
                    SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_JPUSH_STATE_CODE, isPushState);
                    Log.d(TAG, "onMainThreadResponse: state " + isPushState);
                } else if (code == NetworkCodes.CODE_FAILED) {
                    Log.d(TAG, "onMainThreadResponse: 获取推送消息状态失败...");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail...");
        }
    }

    private void setBindJPushSP(boolean bindSuccess) {
        JgPushSaveInfo jgPushSaveInfo = new JgPushSaveInfo();
        jgPushSaveInfo.setFormalUser(isFormalUser);
        jgPushSaveInfo.setBindRegIdSuccess(bindSuccess);
        jgPushSaveInfo.setRegistrationID(JPushInterface.getRegistrationID(mContext));

        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_BIND_JPUSH_USER_CODE, JSON.toJSONString(jgPushSaveInfo));
        Log.d(TAG, "setBindJPushSP: " + JSON.toJSONString(jgPushSaveInfo));
    }

    // for receive customer msg from jpush server

    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.xiaoe.shop.wxb.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    setCustomMsg(showMsg.toString());
                }
            } catch (Exception ignored){
            }
        }
    }

    private void setCustomMsg(String msg){
//        if (null != msgText) {
//            msgText.setText(msg);
//            msgText.setVisibility(android.view.View.VISIBLE);
//        }
    }

    // 初始化超级会员信息
    public void initSuperVipMsg(JSONObject data) {
        boolean isSuperVip = data.getBoolean("is_svip");
        boolean isCanBuy = data.getBoolean("is_can_buy");

        String expire = data.getString("expire_at");
        if (expire != null) {
            expireAt = expire.split(" ")[0];
        }

        CommonUserInfo.setIsSuperVip(isSuperVip);
        CommonUserInfo.setIsSuperVipAvailable(isCanBuy);
    }
}
