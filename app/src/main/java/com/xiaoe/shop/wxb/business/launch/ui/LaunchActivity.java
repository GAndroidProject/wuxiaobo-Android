package com.xiaoe.shop.wxb.business.launch.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;

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
        ImageView launchGIF = (ImageView) findViewById(R.id.launch_gif);

        duration = 0;

        Glide.with(this)
                .load(R.drawable.launch)
                .listener(new RequestListener<Integer, GlideDrawable>() {

                    @Override
                    public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        // 计算动画时长
                        GifDrawable drawable = (GifDrawable) resource;
                        GifDecoder decoder = drawable.getDecoder();
                        for (int i = 0; i < drawable.getFrameCount(); i++) {
                            duration += decoder.getDelay(i);
                        }
                        //发送延时消息，通知动画结束
                        mHandler.sendEmptyMessageDelayed(MESSAGE_SUCCESS,
                                duration+500);
                        return false;
                    }

                }) //仅仅加载一次gif动画
                .into(new GlideDrawableImageViewTarget(launchGIF, 1));
    }
}
