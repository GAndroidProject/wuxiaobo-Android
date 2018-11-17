package com.xiaoe.shop.wxb.business.launch.ui;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.network.downloadUtil.DownloadFileConfig;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.downloadUtil.DownloadSQLiteUtil;
import com.xiaoe.network.utils.ThreadPoolUtils;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.common.JumpDetail;

public class LaunchActivity extends XiaoeActivity {
    private static final String TAG = "LaunchActivity";

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            JumpDetail.jumpLogin(LaunchActivity.this);
            finish();
        }
    };
    private int duration;
    private int MESSAGE_SUCCESS = 0;
    private SimpleDraweeView launchGIF;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_launch);
        initView();
        initData();
    }

    private void initData() {
        ThreadPoolUtils.runTaskOnThread(new Runnable() {
            @Override
            public void run() {
                //下载列表中，可能有正在下载状态，但是退出是还是正在下载状态，所以启动时将之前的状态置为暂停
                if(SQLiteUtil.tabIsExist(DownloadFileConfig.TABLE_NAME)){
                    DownloadManager.getInstance().setDownloadPause();
                    DownloadManager.getInstance().getAllDownloadList();
                }else{
                    DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
                    downloadSQLiteUtil.execSQL(DownloadFileConfig.CREATE_TABLE_SQL);
                }
            }
        });

    }

    private void initView() {
        launchGIF = (SimpleDraweeView) findViewById(R.id.launch_gif);
        Uri uri = Uri.parse("res:///"+R.drawable.launch);
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                        .setUri(uri)
                        .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                        .build();
        launchGIF.setController(draweeController);
        mHandler.sendEmptyMessageDelayed(0, 2100);

    }
}
