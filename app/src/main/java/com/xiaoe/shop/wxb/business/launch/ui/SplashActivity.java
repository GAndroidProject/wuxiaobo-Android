package com.xiaoe.shop.wxb.business.launch.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.ScholarshipEntity;
import com.xiaoe.common.utils.CacheDataUtil;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.downloadUtil.DownloadFileConfig;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.downloadUtil.DownloadSQLiteUtil;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.utils.ThreadPoolUtils;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.main.presenter.ScholarshipPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;

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

    ScholarshipPresenter scholarshipPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        Log.d(TAG, "onCreate: ---- ");
        setStatusBar();
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        scholarshipPresenter = new ScholarshipPresenter(this, true);
        scholarshipPresenter.requestTaskList( false);
        initData();
        SharedPreferencesUtil.getInstance(this, SharedPreferencesUtil.FILE_NAME);
        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        // 加上网络请求的时间，减一秒
        ivGif.postDelayed(() -> {
            JumpDetail.jumpLogin(this);
            finish();
            }, 2000);
    }

    private void initData() {
        ThreadPoolUtils.runTaskOnThread(() -> {
            //下载列表中，可能有正在下载状态，但是退出是还是正在下载状态，所以启动时将之前的状态置为暂停
            DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
            if (downloadSQLiteUtil.tabIsExist(DownloadFileConfig.TABLE_NAME)) {
                DownloadManager.getInstance().setDownloadPause();
                DownloadManager.getInstance().getAllDownloadList();
            } else {
                downloadSQLiteUtil.execSQL(DownloadFileConfig.CREATE_TABLE_SQL);
            }
            SQLiteUtil sqLiteUtil = SQLiteUtil.init(this, new CacheDataUtil());
            if(!sqLiteUtil.tabIsExist(CacheDataUtil.TABLE_NAME)){
                //如果缓存表不存在，则创建
                sqLiteUtil.execSQL(CacheDataUtil.CREATE_TABLES_SQL);
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if (success) {
            ScholarshipEntity.getInstance().setTaskExist(true);
        } else {
            ScholarshipEntity.getInstance().setTaskExist(false);
        }
        // 需要拿到结果后再进行跳转
        initView();
    }
}
