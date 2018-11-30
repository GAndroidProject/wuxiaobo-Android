package com.xiaoe.shop.wxb.business.launch.ui;

import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.utils.CacheDataUtil;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.downloadUtil.DownloadFileConfig;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.downloadUtil.DownloadSQLiteUtil;
import com.xiaoe.network.utils.ThreadPoolUtils;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.utils.FrameAnimation;
import com.xiaoe.shop.wxb.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Administrator
 * <p>
 * 描述：开机闪屏（启动）页面
 */
public class SplashActivity extends XiaoeActivity {

    @BindView(R.id.launch_gif)
    SimpleDraweeView launchGif;
    @BindView(R.id.iv_gif)
    ImageView ivGif;

    private static final String TAG = "SplashActivity";
    private boolean isApplyPermission = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ---- ");
        setStatusBar();
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initView();
        initData();
        SharedPreferencesUtil.getInstance(this, SharedPreferencesUtil.FILE_NAME);
        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isApplyPermission){
            Log.d(TAG, "onResume: --------");
            requestPermission(getUnauthorizedPermission(),getHideUnauthorizedPermission());
        }
    }

    private void initView() {
        ivGif.postDelayed(() -> requestPermission(getUnauthorizedPermission(), getHideUnauthorizedPermission()), 3000);
    }

    private void initData() {
        ThreadPoolUtils.runTaskOnThread(() -> {
            //下载列表中，可能有正在下载状态，但是退出是还是正在下载状态，所以启动时将之前的状态置为暂停
            DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
            if (downloadSQLiteUtil.tabIsExist(DownloadFileConfig.TABLE_NAME)) {
                DownloadManager.getInstance().setDownloadPause();
                DownloadManager.getInstance().getAllDownloadList();
            } else {
//                DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
                downloadSQLiteUtil.execSQL(DownloadFileConfig.CREATE_TABLE_SQL);
            }
            SQLiteUtil sqLiteUtil = SQLiteUtil.init(this, new CacheDataUtil());
            if(!sqLiteUtil.tabIsExist(CacheDataUtil.TABLE_NAME)){
                //如果缓存表不存在，则创建
                sqLiteUtil.execSQL(CacheDataUtil.CREATE_TABLES_SQL);
            }
        });
    }

    /**
     * 获取未授权的权限，可以直接申请弹窗授权
     * @return
     */
    public List<String> getUnauthorizedPermission(){
        ArrayList<String> permissionList = new ArrayList<String>();
        if(Build.VERSION.SDK_INT < 23){
            return null;
        }
        for(int i = 0; i < Constants.permissions.length ; i++){
            String permissions = Constants.permissions[i];
            boolean showPermission = shouldShowRequestPermissionRationale(permissions);
            if (ContextCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED) {
                if(showPermission){
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
    public List<String> getHideUnauthorizedPermission(){
        ArrayList<String> hidePermissionList = new ArrayList<String>();
        if(Build.VERSION.SDK_INT < 23){
            return hidePermissionList;
        }
        for(int i = 0; i < Constants.permissions.length ; i++){
            String permissions = Constants.permissions[i];
            boolean showPermission = shouldShowRequestPermissionRationale(permissions);
            if (ContextCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED) {
                if(!showPermission){
                    //可以弹出选择允许权限
                    hidePermissionList.add(permissions);
                }
            }
        }
        return hidePermissionList;
    }

    public void requestPermission(List<String> permissionList, List<String> hidePermissionList) {
        if(Build.VERSION.SDK_INT < 23){
            JumpDetail.jumpLogin(this);
            finish();
            return;
        }
        String[] permission = permissionList == null ? new String[]{} : permissionList.toArray(new String[permissionList.size()]);
        String[] hidePermission = hidePermissionList == null ? new String[]{} : hidePermissionList.toArray(new String[hidePermissionList.size()]);
        if(permission.length > 0){
            isApplyPermission = false;
            ActivityCompat.requestPermissions(this,permission,1);
        }else if(hidePermission.length > 0){
            getDialog().getTitleView().setGravity(Gravity.START);
            getDialog().getTitleView().setPadding(Dp2Px2SpUtil.dp2px(SplashActivity.this, 22), 0, Dp2Px2SpUtil.dp2px(SplashActivity.this, 22), 0 );
            getDialog().getTitleView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            getDialog().setMessageVisibility(View.GONE);
            getDialog().setCancelable(false);
            getDialog().setHideCancelButton(false);
            getDialog().setTitle(getString(R.string.request_permissions));
            getDialog().setConfirmText(getString(R.string.confirm_title));
            getDialog().showDialog(CustomDialog.REQUEST_PERMISSIONS_TAG);
            isApplyPermission = false;
        }else {
            isApplyPermission = true;
            JumpDetail.jumpLogin(this);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean obtainPermissions = true;
        for (int i = 0; i < grantResults.length; i++){
            if(grantResults[i] != 0){
                //用户拒绝后再次弹起授权
                obtainPermissions = false;
                requestPermission(getUnauthorizedPermission(), getHideUnauthorizedPermission());
                break;
            }
        }
        if(obtainPermissions && isApplyPermission){
            JumpDetail.jumpLogin(this);
            finish();
        }else{
            isApplyPermission = false;
        }
    }


}
