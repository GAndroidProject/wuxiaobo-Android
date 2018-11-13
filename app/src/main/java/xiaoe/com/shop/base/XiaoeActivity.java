package xiaoe.com.shop.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.common.app.Constants;
import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.LoginUser;
import xiaoe.com.common.utils.SQLiteUtil;
import xiaoe.com.common.utils.SharedPreferencesUtil;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.anim.TranslationAnimator;
import xiaoe.com.shop.business.audio.ui.AudioActivity;
import xiaoe.com.shop.business.audio.ui.MiniAudioPlayControllerLayout;
import xiaoe.com.shop.business.login.presenter.LoginSQLiteCallback;
import xiaoe.com.shop.business.main.ui.MainActivity;
import xiaoe.com.shop.common.pay.presenter.PayPresenter;
import xiaoe.com.shop.interfaces.OnCancelListener;
import xiaoe.com.shop.interfaces.OnConfirmListener;
import xiaoe.com.shop.utils.ActivityCollector;
import xiaoe.com.shop.utils.StatusBarUtil;
import xiaoe.com.shop.widget.CustomDialog;

/**
 * Created by Administrator on 2017/7/17.
 */

public class XiaoeActivity extends AppCompatActivity implements INetworkResponse, OnCancelListener, OnConfirmListener, UMShareListener {
    private static final String TAG = "XiaoeActivity";
    private static final int DISMISS_POPUP_WINDOW = 1;
    private static final int DISMISS_TOAST = 2;
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
        ActivityCollector.addActivity(this);
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

//        SharedPreferencesUtil.getInstance(this, SharedPreferencesUtil.FILE_NAME);

        // 初始化数据库
        SQLiteUtil.init(this.getApplicationContext(), new LoginSQLiteCallback());
        // 如果表不存在，就去创建
        if (!SQLiteUtil.tabIsExist(LoginSQLiteCallback.TABLE_NAME_USER)) {
            SQLiteUtil.execSQL(LoginSQLiteCallback.TABLE_SCHEMA_USER);
        }

        userList = SQLiteUtil.query(LoginSQLiteCallback.TABLE_NAME_USER, "select * from " + LoginSQLiteCallback.TABLE_NAME_USER, null);
        if (userList.size() == 1) {
            user = userList.get(0);
            CommonUserInfo.setApiToken(user.getApi_token());
            CommonUserInfo.setWxAvatar(user.getWxAvatar());
            CommonUserInfo.setWxNickname(user.getWxNickname());
            CommonUserInfo.setPhone(user.getPhone());
            CommonUserInfo.setUserId(user.getUserId());
            CommonUserInfo.setShopId(user.getShopId());
        }
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

    // 获取登录用户登录信息集合
    protected List<LoginUser> getLoginUserList() {
        return userList;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(this instanceof MainActivity && isInit){
            isInit = false;
            getAudioRecord();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void getAudioRecord() {
        if(miniAudioPlayController == null){
            return;
        }else{
            miniAudioPlayController.setVisibility(View.GONE);
        }
//        SQLiteUtil.init(this, new AudioSQLiteUtil());
//        if(SQLiteUtil.tabIsExist(AudioPlayTable.TABLE_NAME)){
////            String sql = "select * from "+AudioPlayTable.TABLE_NAME+" where "+AudioPlayTable.getCurrentPlayState()+"=? limit 10";
////            List<AudioPlayEntity> entityList = SQLiteUtil.query(AudioPlayTable.TABLE_NAME,sql,new String[]{"1"});
//            String sql = "select * from "+AudioPlayTable.TABLE_NAME+" limit 10";
//            List<AudioPlayEntity> entityList = SQLiteUtil.query(AudioPlayTable.TABLE_NAME,sql,null);
//            if(entityList.size() > 0){
//                AudioPlayUtil.getInstance().setSingleAudio(true);
//                AudioPlayEntity playEntity = entityList.get(0);
//                playEntity.setPlay(false);
//                playEntity.setCode(-1);
//                AudioPlayUtil.getInstance().refreshAudio(playEntity);
//
//                AudioMediaPlayer.setAudio(playEntity, false);
//                AudioPresenter audioPresenter = new AudioPresenter(null);
//                audioPresenter.requestDetail(playEntity.getResourceId());
//                miniAudioPlayController.setAudioTitle(playEntity.getTitle());
//                miniAudioPlayController.setVisibility(View.GONE);
//            }else{
//                miniAudioPlayController.setVisibility(View.GONE);
//            }
//        }else{
//            miniAudioPlayController.setVisibility(View.GONE);
//        }
    }

    public void Toast(String msg){
        toast = Toast.makeText(this,msg,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        if(!success && entity instanceof String){
            if(((String)entity).equals("user login time out")){
            }
        }
        if(!isActivityDestroy){
            Global.g().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onMainThreadResponse(iRequest,success,entity);
                }
            });
        }
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
//        if (AudioMediaPlayer.isPlaying()) {
        if (miniAudioPlayController != null && !miniAudioPlayController.isClose()) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    actionDownY = (int) ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (actionDownY - ev.getY() < -5) {
                        TranslationAnimator.getInstance()
                                .setAnimator(miniAudioPlayController)
                                .brak(miniPlayerAnimHeight);
                    } else if(actionDownY - ev.getY() > 5) {
                        TranslationAnimator.getInstance()
                                .setAnimator(miniAudioPlayController)
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
    }

    public void initPermission() {
        ArrayList<String> permissionList = new ArrayList<String>();
        for(int i = 0; i < Constants.permissions.length ; i++){
            String permissions = Constants.permissions[i];
            if (ContextCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions);
            }
        }
        String[] permission = permissionList.toArray(new String[permissionList.size()]);
        if(Build.VERSION.SDK_INT > 22 && permission.length > 0){
            ActivityCompat.requestPermissions(this,permission,1);
        }
    }

    public void setMiniPlayerAnimHeight(int miniPlayerAnimHeight) {
        this.miniPlayerAnimHeight = miniPlayerAnimHeight;
    }

    public void setMiniAudioPlayController(MiniAudioPlayControllerLayout miniAudioPlayController) {
        this.miniAudioPlayController = miniAudioPlayController;
        this.miniAudioPlayController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(XiaoeActivity.this, AudioActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_bottom_in,R.anim.slide_top_out);
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
//            getDialog().dismissDialog();
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

    }

    @Override
    public void onClickConfirm(View view, int tag) {

    }
    public CustomDialog getDialog(){
        return dialog;
    }


    /*↓↓↓↓↓↓↓  此处往下是友盟分享回调 ↓↓↓↓↓↓↓*/

    /**
     * 需要在调用的activity中实现方法
     *     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     *         super.onActivityResult(requestCode, resultCode, data);
     *         UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
     *     }
     */
    public void umShare(String shareUrl){
        new ShareAction(this)
                .withText(shareUrl)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(this).open();
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
}
