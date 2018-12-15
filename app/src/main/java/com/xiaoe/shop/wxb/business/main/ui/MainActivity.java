package com.xiaoe.shop.wxb.business.main.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.ScholarshipEntity;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.requests.BindJgPushRequest;
import com.xiaoe.network.requests.CouponHandleRequest;
import com.xiaoe.network.requests.GetPushStateRequest;
import com.xiaoe.network.requests.GetUnreadMessageRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.IsSuperVipRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.main.MainFragmentStatePagerAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.ui.MiniAudioPlayControllerLayout;
import com.xiaoe.shop.wxb.business.launch.presenter.CouponHandlePresenter;
import com.xiaoe.shop.wxb.business.main.presenter.MessagePushPresenter;
import com.xiaoe.shop.wxb.business.setting.presenter.SettingPresenter;
import com.xiaoe.shop.wxb.business.super_vip.presenter.SuperVipPresenter;
import com.xiaoe.shop.wxb.business.upgrade.AppUpgradeHelper;
import com.xiaoe.shop.wxb.common.datareport.EventReportManager;
import com.xiaoe.shop.wxb.common.datareport.MobclickEvent;
import com.xiaoe.shop.wxb.common.jpush.ExampleUtil;
import com.xiaoe.shop.wxb.common.jpush.LocalBroadcastManager;
import com.xiaoe.shop.wxb.common.jpush.entity.JgPushSaveInfo;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;
import com.xiaoe.shop.wxb.events.OnUnreadMsgEvent;
import com.xiaoe.shop.wxb.interfaces.OnBottomTabSelectListener;
import com.xiaoe.shop.wxb.utils.OSUtils;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.utils.ToastUtils;
import com.xiaoe.shop.wxb.widget.BottomTabBar;
import com.xiaoe.shop.wxb.widget.CustomDialog;
import com.xiaoe.shop.wxb.widget.ScrollViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
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
    private boolean firstShow = true;

    Intent intent;
    public boolean isFormalUser;

    SuperVipPresenter superVipPresenter;
    private SettingPresenter settingPresenter;
    public String expireAt;
    /**
     * 正在获取未读消息数
     */
    private boolean isGetUnreadMsg;
    CouponHandlePresenter couponHandlePresenter;
    boolean needShowScholarship;

    boolean isExit = false;
    Runnable exitAppRunnable = () -> isExit = false;
    /**
     * 当前页面索引
     */
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        Log.d(TAG,"onCreate -- before");
        // 设置状态栏需要在 setContentView 之前进行
        setStatusBar();

        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

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

        MessagePushPresenter messagePushPresenter = new MessagePushPresenter(this);
        messagePushPresenter.requestBindJgPush();

        // 一直显示奖学金 tab
        needShowScholarship = !ScholarshipEntity.getInstance().isTaskExist();
        initView();
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
        settingPresenter = new SettingPresenter(this);
        settingPresenter.getPushState();
//        getUnreadMsg();
        couponHandlePresenter = new CouponHandlePresenter(this);
        couponHandlePresenter.requestSendCoupon();
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        Log.d(TAG,"onResume");
//        if(!isApplyPermission){
            Log.d(TAG, "onResume: --------");
        //申请权限
            requestPermission(getUnauthorizedPermission(firstShow),getHideUnauthorizedPermission());
            firstShow = false;
//        }
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

    public int getCurrentPosition() {
        return currentPosition;
    }

    private void initView() {
        RelativeLayout mainActivityRootView = (RelativeLayout) findViewById(R.id.main_activity_root_view);
        mainActivityRootView.setBackgroundColor(Color.parseColor(Global.g().getGlobalColor()));
        bottomTabBar = (BottomTabBar) findViewById(R.id.bottom_tab_bar_layout);
        bottomTabBar.setBottomTabBarOrientation(LinearLayout.HORIZONTAL);
        if (!needShowScholarship) {
            bottomTabBar.setTabBarWeightSum(3);
        } else {
            bottomTabBar.setTabBarWeightSum(4);
        }
        bottomTabBar.setBottomTabSelectListener(this);
        List<String> buttonNames = new ArrayList<String>();
        buttonNames.add(getString(R.string.tab_today));
        buttonNames.add(getString(R.string.micro_fragment_title));
        if (needShowScholarship) {
            buttonNames.add(getString(R.string.scholarship_title));
        }
        buttonNames.add(getString(R.string.tab_mine));
        List<Integer> buttonCheckedIcons = new ArrayList<Integer>();
        buttonCheckedIcons.add(R.mipmap.today_selected);
        buttonCheckedIcons.add(R.mipmap.class_selected);
        if (needShowScholarship) {
            buttonCheckedIcons.add(R.mipmap.scholarship_select);
        }
        buttonCheckedIcons.add(R.mipmap.profile_selected);
        List<Integer> buttonIcons = new ArrayList<Integer>();
        buttonIcons.add(R.mipmap.today_default);
        buttonIcons.add(R.mipmap.class_default);
        if (needShowScholarship) {
            buttonIcons.add(R.mipmap.scholarship_default);
        }
        buttonIcons.add(R.mipmap.profile_default);
        bottomTabBar.addTabButton(buttonNames, buttonIcons, buttonCheckedIcons, getResources().getColor(R.color.secondary_button_text_color), getResources().getColor(R.color.high_title_color));

        mainViewPager = (ScrollViewPager) findViewById(R.id.main_view_pager);
        mainViewPager.setScroll(false);
        MainFragmentStatePagerAdapter adapter = new MainFragmentStatePagerAdapter(getSupportFragmentManager(), needShowScholarship);
        mainViewPager.setAdapter(adapter);
        mainViewPager.setOffscreenPageLimit(3);
        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (1 == position){
//                    if (!StatusBarUtil.setStatusBarDarkTheme(MainActivity.this, false)) {
//                        // 如果不支持设置深色风格，可以设置状态栏为半透明 0x55000000
//                        StatusBarUtil.setStatusBarColor(MainActivity.this, 0x55000000);
//                    }
//                }else {
//                    if (!StatusBarUtil.setStatusBarDarkTheme(MainActivity.this, true)) {
//                        // 如果不支持设置深色风格，可以设置状态栏为半透明 0x55000000
//                        StatusBarUtil.setStatusBarColor(MainActivity.this, 0x55000000);
//                    }
//                }
                if (!StatusBarUtil.setStatusBarDarkTheme(MainActivity.this, true)) {
                    // 如果不支持设置深色风格，可以设置状态栏为半透明 0x55000000
                    StatusBarUtil.setStatusBarColor(MainActivity.this, 0x55000000);
                }
            }
            @Override
            public void onPageSelected(int position) {
//                Log.e("EventReportManager", "onPageSelected: position " + position);
                ((BaseFragment) adapter.getItem(position)).refreshReportDuration();
                if (currentPosition != position) {
                    ((BaseFragment) adapter.getItem(currentPosition)).eventReportDuration();
                }
                currentPosition = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        miniAudioPlayController = (MiniAudioPlayControllerLayout) findViewById(R.id.mini_audio_play_controller);
        setMiniAudioPlayController(miniAudioPlayController);
        setMiniPlayerAnimHeight(Dp2Px2SpUtil.dp2px(this, 76));
    }


    @Override
    public void onCheckedTab(int index) {
        Log.d(TAG, "onCheckedTab: "+index);
        mainViewPager.setCurrentItem(index);
        Log.d(TAG, "onCheckedTab: I "+mainViewPager.getCurrentItem());
        if (!needShowScholarship) {
            if (2 == index && !TextUtils.isEmpty(CommonUserInfo.getInstance().getApiTokenByDB())) { // 有登录态才请求
                getUnreadMsg();
            }
            if (2 == index && bottomTabBar.getBottomBarButtonByIndex(2).getRedPointVisibility() == View.VISIBLE) {
                bottomTabBar.getBottomBarButtonByIndex(2).setRedPointVisibility(View.GONE);
            }
        } else {
            if (3 == index && !TextUtils.isEmpty(CommonUserInfo.getInstance().getApiTokenByDB())) { // 有登录态才请求
                getUnreadMsg();
            }
            if (3 == index && bottomTabBar.getBottomBarButtonByIndex(3).getRedPointVisibility() == View.VISIBLE) {
                bottomTabBar.getBottomBarButtonByIndex(3).setRedPointVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null) {
            return;
        }
        boolean needChange = intent.getBooleanExtra("needChange", false);
        int tabIndex = intent.getIntExtra("tabIndex", 0);
        if (needChange) {
            if (!needShowScholarship && tabIndex >= 3) {
                tabIndex = 2;
            }
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
                miniAudioPlayController.setIsClose(false);
                miniAudioPlayController.setAudioTitle(playEntity.getTitle());
                miniAudioPlayController.setAudioImage(playEntity.getImgUrlCompressed());
                miniAudioPlayController.setColumnTitle(playEntity.getProductsTitle());
                miniAudioPlayController.setPlayButtonEnabled(false);
                miniAudioPlayController.setPlayState(AudioPlayEvent.PAUSE);
                break;
            case AudioPlayEvent.PLAY:
                miniAudioPlayController.setVisibility(View.VISIBLE);
                miniAudioPlayController.setIsClose(false);
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
            case AudioPlayEvent.CLOSE:
                miniAudioPlayController.close();
                break;
            default:
                break;
        }
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
//                    setBindJPushSP(true);
                    Log.d(TAG, "onMainThreadResponse: 绑定极光推送成功...");
                } else {
//                    setBindJPushSP(false);
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
            } else if (iRequest instanceof GetUnreadMessageRequest) {
                isGetUnreadMsg = false;
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = result.getJSONObject("data");
                    int messageCount = data.getInteger("message_count");
                    EventBus.getDefault().post(new OnUnreadMsgEvent(0, messageCount, OnUnreadMsgEvent.MessageOrigin.HTTP));
                    Log.d(TAG, "onMainThreadResponse: 未读消息数 " + messageCount);
                } else if (code == NetworkCodes.CODE_FAILED) {
                    Log.d(TAG, "onMainThreadResponse: 获取未读消息失败...");
                }
            } else if (iRequest instanceof CouponHandleRequest) {
                int code = result.getInteger("code");
                if ((NetworkEngine.API_THIRD_BASE_URL + "xe.user.coupon.grant/1.0.0").equals(iRequest.getCmd())) {
                    if (code == NetworkCodes.CODE_SUCCEED) {
                        couponHandlePresenter.requestUnReadCouponMsg();
                    } else if (code == NetworkCodes.CODE_NO_COUPON) {
                        Log.d(TAG, "onMainThreadResponse: 店铺无优惠券功能");
                    } else if (code == NetworkCodes.CODE_USER_NO_COUPON) {
                        Log.d(TAG, "onMainThreadResponse: 用户无待领优惠券");
                    } else {
                        Log.d(TAG, "onMainThreadResponse: 发放优惠券未知错误");
                    }
                } else if ((NetworkEngine.API_THIRD_BASE_URL + "xe.user.coupon.unread/1.0.0").equals(iRequest.getCmd())) {
                    if (code == NetworkCodes.CODE_SUCCEED) {
                        JSONObject data = (JSONObject) result.get("data");
                        boolean hasUnread = data.getBoolean("has_unread") == null ? false : data.getBoolean("has_unread");
                        int unreadCount = data.getInteger("unread_coupon_count") == null ? 0 : data.getInteger("unread_coupon_count");
                        CommonUserInfo.getInstance().setHasUnreadMsg(hasUnread);
                        CommonUserInfo.getInstance().setUnreadMsgCount(unreadCount);
                        if (hasUnread) {
                            if (!needShowScholarship) {
                                bottomTabBar.getBottomBarButtonByIndex(2).setRedPointVisibility(View.VISIBLE);
                            } else {
                                bottomTabBar.getBottomBarButtonByIndex(3).setRedPointVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail...");
            isGetUnreadMsg = false;
        }
    }

    //-----start----- for receive customer msg from jpush server ------

    private void setBindJPushSP(boolean bindSuccess) {
        JgPushSaveInfo jgPushSaveInfo = new JgPushSaveInfo();
        jgPushSaveInfo.setFormalUser(isFormalUser);
        jgPushSaveInfo.setBindRegIdSuccess(bindSuccess);
        jgPushSaveInfo.setRegistrationID(JPushInterface.getRegistrationID(mContext));

        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_BIND_JPUSH_USER_CODE, JSON.toJSONString(jgPushSaveInfo));
        Log.d(TAG, "setBindJPushSP: " + JSON.toJSONString(jgPushSaveInfo));
    }

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
            } catch (Exception ignored) {
            }
        }
    }

    private void setCustomMsg(String msg) {
//        if (null != msgText) {
//            msgText.setText(msg);
//            msgText.setVisibility(android.view.View.VISIBLE);
//        }
    }

    //-----end----- for receive customer msg from jpush server ------

    // 初始化超级会员信息
    public void initSuperVipMsg(JSONObject data) {
        boolean isSuperVip = data.getBoolean("is_svip");
        boolean isCanBuy = data.getBoolean("is_can_buy");
        int effectiveRange = data.getInteger("effactive_range");
        boolean hasYearBuy = false; // 是否有一年规格
        JSONArray vipType = (JSONArray) data.get("svip_type");
        if (vipType != null) {
            for (int i = 0; i < vipType.size(); i++) {
                int item = vipType.getInteger(i);
                if (item == 5) { // 有一年规格
                    hasYearBuy = true;
                }
            }
        }

        String expire = data.getString("expire_at");
        if (!TextUtils.isEmpty(expire)) {
            expireAt = expire.split(" ")[0];
        }

        CommonUserInfo.setIsSuperVip(isSuperVip);
        CommonUserInfo.setIsSuperVipAvailable(isCanBuy && hasYearBuy);
        CommonUserInfo.setSuperVipEffective(effectiveRange);
    }

    public void getUnreadMsg() {
        if (!isGetUnreadMsg) {
            settingPresenter.requestUnreadMessage();
            isGetUnreadMsg = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (mainViewPager == null || isExit){
            finish();
            return;
        } else {
            ToastUtils.show(mContext, R.string.again_back_pressed_exit);
        }
        isExit = true;
        mainViewPager.removeCallbacks(exitAppRunnable);
        mainViewPager.postDelayed(exitAppRunnable,1500);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int grantResult : grantResults) {
            if (grantResult != 0) {
                //用户拒绝后再次弹起授权
                requestPermission(getUnauthorizedPermission(false), getHideUnauthorizedPermission());
                break;
            }
        }
    }

    /**
     * 获取未授权的权限，可以直接申请弹窗授权
     * @return
     */
    private List<String> getUnauthorizedPermission(boolean first){
        ArrayList<String> permissionList = new ArrayList<String>();
        if(Build.VERSION.SDK_INT < 23){
            return null;
        }
        for(int i = 0; i < Constants.permissions.length ; i++){
            String permissions = Constants.permissions[i];
            boolean showPermission = shouldShowRequestPermissionRationale(permissions);
            if (ContextCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED) {
                if(showPermission || first){
                    //可以弹出选择允许权限
                    permissionList.add(permissions);
                }
            }
        }
        return permissionList;
    }

    /**
     * 获取未授权的权限，拒绝后不在提示的权限
     * @return
     */
    private List<String> getHideUnauthorizedPermission(){
        ArrayList<String> hidePermissionList = new ArrayList<String>();
        if(Build.VERSION.SDK_INT < 23){
            return hidePermissionList;
        }
        for(int i = 0; i < Constants.permissions.length ; i++){
            String permissions = Constants.permissions[i];
            boolean showPermission = shouldShowRequestPermissionRationale(permissions);
            if (ContextCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED) {
                if(!showPermission){
                    //不可以弹出选择允许权限
                    hidePermissionList.add(permissions);
                }
            }
        }
        return hidePermissionList;
    }

    private void requestPermission(List<String> permissionList, List<String> hidePermissionList) {
        if(Build.VERSION.SDK_INT < 23){
            return;
        }
        String[] permission = permissionList == null ? new String[]{} : permissionList.toArray(new String[permissionList.size()]);
        String[] hidePermission = hidePermissionList == null ? new String[]{} : hidePermissionList.toArray(new String[hidePermissionList.size()]);
        if(permission.length > 0){
            ActivityCompat.requestPermissions(this,permission,1);
        }else if(hidePermission.length > 0){
            getDialog().dismissDialog();
            getDialog().setTitleVisibility(View.GONE);
            getDialog().setMessageVisibility(View.VISIBLE);
            getDialog().setMessageTextColor(getColor(R.color.main_title_color));
            getDialog().setCancelable(false);
            getDialog().setHideCancelButton(true);
            if(Arrays.binarySearch(hidePermission, Manifest.permission.READ_PHONE_STATE) >= 0 && (Arrays.binarySearch(hidePermission, Manifest.permission.READ_EXTERNAL_STORAGE) >= 0 || Arrays.binarySearch(hidePermission, Manifest.permission.WRITE_EXTERNAL_STORAGE) >= 0)){
                getDialog().setHintMessage(getString(R.string.request_permissions));
            }else if(Arrays.binarySearch(hidePermission, Manifest.permission.READ_PHONE_STATE) >= 0){
                getDialog().setHintMessage(getString(R.string.request_permissions_phone));
            }else if(Arrays.binarySearch(hidePermission, Manifest.permission.READ_EXTERNAL_STORAGE) >= 0 || Arrays.binarySearch(hidePermission, Manifest.permission.WRITE_EXTERNAL_STORAGE) >= 0) {
                getDialog().setHintMessage(getString(R.string.request_permissions_storage));
            }else{
                getDialog().setHintMessage(getString(R.string.request_permissions));
            }
            getDialog().setConfirmText(getString(R.string.to_permit));
            getDialog().showDialog(CustomDialog.REQUEST_PERMISSIONS_TAG);
        }else{
            getDialog().dismissDialog();
        }
    }

}
