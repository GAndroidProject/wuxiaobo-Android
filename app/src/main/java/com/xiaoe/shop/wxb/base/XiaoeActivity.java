package com.xiaoe.shop.wxb.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.db.LoginSQLiteCallback;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.AudioPlayTable;
import com.xiaoe.common.entitys.DownloadResourceTableInfo;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.NetUtils;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.NetworkStateResult;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.anim.TranslationAnimator;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPresenter;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioSQLiteUtil;
import com.xiaoe.shop.wxb.business.audio.ui.MiniAudioPlayControllerLayout;
import com.xiaoe.shop.wxb.business.main.ui.MainActivity;
import com.xiaoe.shop.wxb.business.upgrade.AppUpgradeHelper;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.NetBroadcastReceiver;
import com.xiaoe.shop.wxb.common.pay.presenter.PayPresenter;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;
import com.xiaoe.shop.wxb.interfaces.OnCustomDialogListener;
import com.xiaoe.shop.wxb.interfaces.OnNetChangeListener;
import com.xiaoe.shop.wxb.utils.ActivityCollector;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.widget.CustomDialog;
import com.xiaoe.shop.wxb.widget.ShareDialog;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Administrator on 2017/7/17.
 */

public class XiaoeActivity extends AppCompatActivity implements INetworkResponse, OnCustomDialogListener,
        UMShareListener, OnNetChangeListener {
    private static final String TAG = "XiaoeActivity";
    private static final int DISMISS_POPUP_WINDOW = 1;
    private static final int DISMISS_TOAST = 2;
    private static final int DIALOG_TAG_LOADING = 4110;//登录弹窗类型
    private PopupWindow popupWindow;
    private Toast toast;
    private TextView mToastText;
    private boolean isInit = true;
    private int miniPlayerAnimHeight = 0;//音频迷你播放器动画滑动的高度
    private int actionDownY = 0;//手指按下屏幕Y轴坐标
    private MiniAudioPlayControllerLayout miniAudioPlayController;//音频迷你播放器
    private Handler mHandler = new XeHandler(this);
    private boolean mHasFocus = false;
    private boolean isActivityDestroy = false;
    private CustomDialog dialog;
    protected boolean activityDestroy = false;
    private PayPresenter payPresenter;

    // 登录的信息
    List<LoginUser> userList;

    // 用户登录信息
    LoginUser user = null;

    protected InputMethodManager imm;

    protected Context mContext;
    private ShareDialog mShareDialog;
    private NetBroadcastReceiver netBroadcastReceiver;

    private boolean isFrontActivity = true;
    private TranslationAnimator translationAnimator;

    private boolean hasToast;


    class XeHandler extends Handler {

        WeakReference<XiaoeActivity> wrf;

        XeHandler(XiaoeActivity activity) {
            wrf = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == DISMISS_POPUP_WINDOW){
                if(popupWindow != null){
                    popupWindow.dismiss();
                }
            }else if(msg.what == DISMISS_TOAST){
                if(toast != null){
                    toast.cancel();
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        ActivityCollector.addActivity(this);
        translationAnimator = new TranslationAnimator();
        popupWindow = new PopupWindow(this);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_custom_toast, null,false);
        mToastText = (TextView) view.findViewById(R.id.id_toast_text);
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        isActivityDestroy = false;

        dialog = new CustomDialog(this);
        dialog.setOnCustomDialogListener(XiaoeActivity.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // 初始化数据库
        SQLiteUtil sqLiteUtil = SQLiteUtil.init(this.getApplicationContext(), new LoginSQLiteCallback());
        // 如果表不存在，就去创建
        if (!sqLiteUtil.tabIsExist(LoginSQLiteCallback.TABLE_NAME_USER)) {
            sqLiteUtil.execSQL(LoginSQLiteCallback.TABLE_SCHEMA_USER);
        }

        userList = sqLiteUtil.query(LoginSQLiteCallback.TABLE_NAME_USER, "select * from " + LoginSQLiteCallback.TABLE_NAME_USER, null);
        if (userList.size() == 1) {
            user = userList.get(0);
            CommonUserInfo.setApiToken(user.getApi_token());
            CommonUserInfo.setWxAvatar(user.getWxAvatar());
            CommonUserInfo.setWxNickname(user.getWxNickname());
            CommonUserInfo.setPhone(user.getPhone());
            CommonUserInfo.setUserId(user.getUserId());
            CommonUserInfo.setShopId(user.getShopId());
        }
        AppUpgradeHelper.getInstance().setActivity(this);


        //实例化IntentFilter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        netBroadcastReceiver = new NetBroadcastReceiver();
        netBroadcastReceiver.setOnNetChangeListener(this);
        //注册广播接收
        registerReceiver(netBroadcastReceiver, filter);

    }
    // 状态栏设置
    protected void setStatusBar(){
        // 屏幕上方预留状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
//         设置透明状态栏
        StatusBarUtil.setTranslucentStatus(this);
//         设置深色文字图标风格
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            // 如果不支持设置深色风格，可以设置状态栏为半透明 0x55000000
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }

    protected void initStatusBar() {
        //设置深色文字图标风格
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            // 如果不支持设置深色风格，可以设置状态栏为半透明 0x55000000
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }

    // 获取登录用户登录信息集合
    protected List<LoginUser> getLoginUserList() {
        return userList;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isInit){
            isInit = false;
            if(this instanceof MainActivity){
                getAudioRecord();
            }
        }
        if(miniAudioPlayController != null && AudioMediaPlayer.getAudio() != null){
            if(AudioPlayUtil.getInstance().isCloseMiniPlayer()){
                miniAudioPlayController.setVisibility(View.GONE);
            }else{
                miniAudioPlayController.setVisibility(View.VISIBLE);
            }
            miniAudioPlayController.setAudioTitle(AudioMediaPlayer.getAudio().getTitle());
            miniAudioPlayController.setAudioImage(AudioMediaPlayer.getAudio().getImgUrlCompressed());
            miniAudioPlayController.setColumnTitle(AudioMediaPlayer.getAudio().getProductsTitle());
            miniAudioPlayController.setMaxProgress(AudioMediaPlayer.getDuration());
            miniAudioPlayController.setPlayState(AudioMediaPlayer.isPlaying() ? AudioPlayEvent.PLAY : AudioPlayEvent.PAUSE);
        }else if(miniAudioPlayController != null){
            miniAudioPlayController.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        isFrontActivity = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(dialog.getDialogTag() == ShareDialog.SHARE_DIALOG_TAG){
            dialog.dismissDialog();
        }
        isFrontActivity = false;
        MobclickAgent.onPause(this);
    }

    private void getAudioRecord() {
//        if(miniAudioPlayController == null){
//            return;
//        }else{
//            miniAudioPlayController.setVisibility(View.GONE);
//        }

        if(miniAudioPlayController == null){
            return;
        }
        SQLiteUtil sqLiteUtil = SQLiteUtil.init(this, new AudioSQLiteUtil());
        if(sqLiteUtil.tabIsExist(AudioPlayTable.TABLE_NAME)){
//            String sql = "select * from "+AudioPlayTable.TABLE_NAME+" where "+AudioPlayTable.getCurrentPlayState()+"=? limit 10";
//            List<AudioPlayEntity> entityList = SQLiteUtil.query(AudioPlayTable.TABLE_NAME,sql,new String[]{"1"});
            String sql = "select * from "+AudioPlayTable.TABLE_NAME+" where current_play_state=1 order by update_at desc limit 1";
            List<AudioPlayEntity> entityList = sqLiteUtil.query(AudioPlayTable.TABLE_NAME,sql,null);
            if(entityList.size() > 0){
                AudioPlayUtil.getInstance().setSingleAudio(true);
                AudioPlayEntity playEntity = entityList.get(0);
                playEntity.setPlay(false);
                playEntity.setCode(-1);

                AudioPlayUtil.getInstance().refreshAudio(playEntity);
                AudioPlayUtil.getInstance().setCloseMiniPlayer(false);

                //如果存在本地音频则播放本地是否
                DownloadResourceTableInfo download = DownloadManager.getInstance().getDownloadFinish(Constants.getAppId(), playEntity.getResourceId());
                if(download != null){
                    File file = new File(download.getLocalFilePath());
                    if(file.exists()){
                        String localAudioPath = download.getLocalFilePath();
                        playEntity.setPlayUrl(localAudioPath);
                        playEntity.setLocalResource(true);
                    }
                }
                AudioMediaPlayer.setAudio(playEntity, false);
                AudioPresenter audioPresenter = new AudioPresenter(null);
                audioPresenter.requestDetail(playEntity.getResourceId());
                String title = playEntity.getTitle();
                miniAudioPlayController.setAudioTitle(title);
                miniAudioPlayController.setAudioImage(playEntity.getImgUrlCompressed());
                miniAudioPlayController.setColumnTitle(playEntity.getProductsTitle());
                miniAudioPlayController.setVisibility(View.VISIBLE);
            }else{
                miniAudioPlayController.setVisibility(View.GONE);
            }
        }else{
            miniAudioPlayController.setVisibility(View.GONE);
        }
    }

    public void Toast(String msg){
        toast = Toast.makeText(this,msg,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        if(isActivityDestroy){
            return;
        }
        Global.g().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = (JSONObject) entity;
                if (success && entity != null) {
                    if(jsonObject.getIntValue("code") == NetworkCodes.CODE_NOT_LOAING){
                        if(!dialog.isShowing()){
                            dialog.getTitleView().setGravity(Gravity.START);
                            dialog.getTitleView().setPadding(Dp2Px2SpUtil.dp2px(XiaoeActivity.this, 22), 0, Dp2Px2SpUtil.dp2px(XiaoeActivity.this, 22), 0 );
                            dialog.getTitleView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            dialog.setMessageVisibility(View.GONE);
                            dialog.setCancelable(false);
                            dialog.setHideCancelButton(true);
                            dialog.setTitle(getString(R.string.login_invalid));
                            dialog.setConfirmText(getString(R.string.btn_again_login));
                            dialog.showDialog(DIALOG_TAG_LOADING);
                            // 往回传 null 关闭加载中
                            onMainThreadResponse(null, false, entity);
                        }
                    }else{
                        onMainThreadResponse(iRequest, true, entity);
                    }
                } else {
                    if (jsonObject != null) {
                        int code = jsonObject.getInteger("code");
                        if (NetworkStateResult.ERROR_NETWORK == code && !hasToast) {
                            hasToast = true;
                            Toast(getString(R.string.network_error_text));
                        }
                    }
                    onMainThreadResponse(iRequest, false, entity);
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mHasFocus = hasFocus;
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {

    }
    public void toastCustom(String msg){
        if(mHasFocus){
            mHandler.removeMessages(DISMISS_POPUP_WINDOW);
            mToastText.setText(msg);
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            mHandler.sendEmptyMessageDelayed(DISMISS_POPUP_WINDOW,1500);
        }
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (miniAudioPlayController != null && !miniAudioPlayController.isClose()) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    actionDownY = (int) ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (actionDownY - ev.getY() < -5) {
                        translationAnimator.setAnimator(miniAudioPlayController)
                                .brak(miniPlayerAnimHeight);
                    } else if(actionDownY - ev.getY() > 5) {
                        translationAnimator.setAnimator(miniAudioPlayController)
                                .remove(miniPlayerAnimHeight);
                    }
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 在弹出对话框时，显示popupWindow，则popupWindow会在对话框下面（被对话框盖住）
     * 是方法是让popupWindow显示在对话上面
     * view 是对话框中的view
     */
    public void toastCustomDialog(View view,String msg){
        mHandler.removeMessages(DISMISS_POPUP_WINDOW);
        mToastText.setText(msg);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        mHandler.sendEmptyMessageDelayed(DISMISS_POPUP_WINDOW,1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        activityDestroy = true;
        isActivityDestroy = true;
        if(toast != null){
            toast.cancel();
        }
        if(popupWindow != null){
            popupWindow.dismiss();
            popupWindow = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        unregisterReceiver(netBroadcastReceiver);
    }


    public void setMiniPlayerAnimHeight(int miniPlayerAnimHeight) {
        this.miniPlayerAnimHeight = miniPlayerAnimHeight;
        if (miniAudioPlayController != null){
            miniAudioPlayController.setMiniPlayerAnimHeight(miniPlayerAnimHeight);
        }
    }

    public void setMiniAudioPlayController(MiniAudioPlayControllerLayout miniAudioPlayController) {
        this.miniAudioPlayController = miniAudioPlayController;
        this.miniAudioPlayController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioPlayEntity audioPlayEntity = AudioMediaPlayer.getAudio();
                if(audioPlayEntity != null){
                    JumpDetail.jumpAudio(XiaoeActivity.this, audioPlayEntity.getResourceId(), audioPlayEntity.getHasBuy());
                }
//                Intent intent = new Intent(XiaoeActivity.this, AudioActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_bottom_in,R.anim.slide_top_out);
            }
        });
    }

    /**
     * 获取微信支付返回码
     * @param hint
     * @return
     */
    public int getWXPayCode(boolean hint){
        int code = (int) SharedPreferencesUtil.getData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
        //code = 0微信支付成功，-1支付失败（签名错误之类，未拉起微信支付），-2支付失败（用户主动取消），-100不是微信支付
        if(code == -1 && hint){
            getDialog().dismissDialog();
            getDialog().setHintMessage(getResources().getString(R.string.not_can_pay));
            getDialog().showDialog(-1);
        }else if(code == -2 && hint){
            getDialog().dismissDialog();
//            getDialog().setHintMessage(getResources().getString(R.string.pay_fail));
//            getDialog().showDialog(-2);
            toastCustom(getResources().getString(R.string.pay_fail));
        }
        return code;
    }
    //购买资源（下单）
    public void payOrder(String resourceId, int resourceType, int paymentType, String couponId) {
        if(payPresenter == null){
            payPresenter = new PayPresenter(this, this);
        }
        payPresenter.payOrder(paymentType, resourceType, resourceId, resourceId, couponId);
    }
    // 购买超级会员（下单）
    public void payOrder(String resourceId, String productId, int resourceType, int paymentType, String couponId) {
        if (payPresenter == null) {
            payPresenter = new PayPresenter(this, this);
        }
        payPresenter.payOrder(paymentType, resourceType, resourceId, productId, couponId);
    }
    //拉起微信支付
    public void pullWXPay(String appid, String partnerid, String prepayid, String noncestr, String timestamp, String packageValue, String sign){
        if(payPresenter == null){
            payPresenter = new PayPresenter(this, this);
        }
        payPresenter.pullWXPay(appid, partnerid, prepayid, noncestr, timestamp, packageValue, sign);
    }

    @Override
    public void onClickCancel(View view, int tag) {
        if(tag == CustomDialog.REQUEST_PERMISSIONS_TAG){
            finish();
        }
    }

    @Override
    public void onClickConfirm(View view, int tag) {
        if(tag == DIALOG_TAG_LOADING){
            SQLiteUtil liteUtil = SQLiteUtil.init(this,  new LoginSQLiteCallback());
            liteUtil.deleteFrom(LoginSQLiteCallback.TABLE_NAME_USER);
            CommonUserInfo.getInstance().clearUserInfo();
            CommonUserInfo.getInstance().clearLoginUserInfo();
            CommonUserInfo.setApiToken("");
            CommonUserInfo.setIsSuperVip(false);
            CommonUserInfo.setIsSuperVipAvailable(false);
            dialog.dismissDialog();
            // 点击重新登录，登录完之后要回到原来的页面
            JumpDetail.jumpLogin(this);
            // 登录后需要回到原来的页面，所以不做 finish 操作
//             finish();
            ActivityCollector.finishAll();
        }else if(tag == CustomDialog.NOT_WIFI_PLAY_TAG){
            AudioMediaPlayer.play();
        }else if(tag == CustomDialog.REQUEST_PERMISSIONS_TAG){
            goToAppSetting();
        }
    }
    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
//        startActivityForResult(intent, CustomDialog.REQUEST_PERMISSIONS_TAG);
    }

    @Override
    public void onDialogDismiss(DialogInterface dialog, int tag, boolean backKey) {
    }

    public CustomDialog getDialog(){
        return dialog;
    }


    /*↓↓↓↓↓↓↓  此处往下是友盟分享回调 ↓↓↓↓↓↓↓*/
    /**
     * 需要在调用的activity中实现方法
     * *     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     * *         super.onActivityResult(requestCode, resultCode, data);
     * *         UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
     * *     }
     *
     * @param shareTitle
     * @param imgUrl
     * @param shareUrl
     * @param desc
     */
    public void umShare(String shareTitle, String imgUrl, String shareUrl, String desc) {
        Log.d(TAG, "umShare: shareTitle - " + shareTitle + "\nimgUrl - " + imgUrl + "\nshareUrl - " + shareUrl + "\ndesc - " + desc);
        if (mShareDialog == null) {
            mShareDialog = new ShareDialog(this, this);
        }
        mShareDialog.showSharePanel(shareTitle, imgUrl, shareUrl, desc);
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {
        Log.d(TAG, "onStart: 友盟");
    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
        Log.d(TAG, "onResult: 友盟");
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        Log.d(TAG, "onError: 友盟");
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {
        Log.d(TAG, "onCancel: 友盟");
    }
    /* ↑↑↑↑↑↑↑ 此处往上是友盟分享回调 ↑↑↑↑↑↑↑↑*/

    /**
     * 点击左按钮(返回按钮)
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
    }

    /**
     *
     * @param netType netType : 2G，3G，4G，WIFI， unkonw network，no network
     */
    @Override
    public void onNetworkChangeListener(String netType) {
        Log.e(TAG, "onNetworkChangeListener: "+dialog.isShowing());
        if(!NetUtils.NETWORK_TYPE_WIFI.equals(netType) && DownloadManager.getInstance().isHasDownloadTask()){
            //如果不是wifi环境，且有下载任务则暂停下载任务
            DownloadManager.getInstance().allPaushDownload();
            Toast(getString(R.string.not_wifi_net_pause_download));
        }else if(!NetUtils.NETWORK_TYPE_WIFI.equals(netType) && AudioMediaPlayer.isPlaying() && isFrontActivity){
            AudioMediaPlayer.play();
            dialog.setMessageVisibility(View.GONE);
            dialog.getTitleView().setGravity(Gravity.START);
            dialog.getTitleView().setPadding(Dp2Px2SpUtil.dp2px(XiaoeActivity.this, 22), 0, Dp2Px2SpUtil.dp2px(XiaoeActivity.this, 22), 0 );
            dialog.getTitleView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            dialog.setCancelable(false);
            dialog.setHideCancelButton(false);
            dialog.setTitle(getString(R.string.not_wifi_net_play_hint));
            dialog.setConfirmText(getString(R.string.yes_text));
            dialog.setCancelText(getString(R.string.no_text));
            dialog.showDialog(CustomDialog.NOT_WIFI_PLAY_TAG);
        }
    }
}
