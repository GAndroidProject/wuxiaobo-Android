package com.xiaoe.shop.wxb.business.launch.ui;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.animation.drawable.AnimatedDrawable2;
import com.facebook.imagepipeline.image.ImageInfo;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.downloadUtil.DownloadFileConfig;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.downloadUtil.DownloadSQLiteUtil;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.UnReadMsgRequest;
import com.xiaoe.network.utils.ThreadPoolUtils;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.launch.presenter.UnReadMsgPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 开机闪屏（启动）页面
 */
public class LaunchActivity extends XiaoeActivity {

    @BindView(R.id.launch_gif)
    SimpleDraweeView launchGif;

    private static final String TAG = "LaunchActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initData() {
        String apiToken = CommonUserInfo.getInstance().getApiTokenByDB();
        if (apiToken != null) {
            UnReadMsgPresenter unReadMsgPresenter = new UnReadMsgPresenter(this);
            unReadMsgPresenter.requestUnReadCouponMsg();
        }
        ThreadPoolUtils.runTaskOnThread(() -> {
            //下载列表中，可能有正在下载状态，但是退出是还是正在下载状态，所以启动时将之前的状态置为暂停
            if (SQLiteUtil.tabIsExist(DownloadFileConfig.TABLE_NAME)) {
                DownloadManager.getInstance().setDownloadPause();
                DownloadManager.getInstance().getAllDownloadList();
            } else {
                DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
                downloadSQLiteUtil.execSQL(DownloadFileConfig.CREATE_TABLE_SQL);
            }
        });
    }

    private void initView() {
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, final Animatable animatable) {
                Log.d(TAG, "onFinalImageSet: animatable - " + animatable);
                if (animatable == null) {
                    return;
                }
                int duration = 0;
                try {
                    // mTotalLoops mLoopCount
                    Field field = AnimatedDrawable2.class.getDeclaredField("mLoopCount");
                    field.setAccessible(true);
                    // 设置循环次数为 1
                    field.set(animatable, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                animatable.start();
                if (animatable instanceof AnimatedDrawable2) {
                    duration = (int) ((AnimatedDrawable2) animatable).getLoopDurationMs();
                    Log.d(TAG, "onFinalImageSet: loopCount - " + ((AnimatedDrawable2) animatable).getLoopCount());
                }
                if (duration > 0) {
                    launchGif.postDelayed(() -> {
                        if (animatable.isRunning()) {
                            animatable.stop();
                            JumpDetail.jumpLogin(mContext);
                            finish();
                        }
                    }, duration);
                }
                Log.d(TAG, "onFinalImageSet: duration " + duration);
            }
        };

        Uri uri = Uri.parse("res:///" + R.drawable.launch);
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(controllerListener)
                // 设置加载图片完成后是否直接进行播放
                .setAutoPlayAnimations(true)
                .build();
        launchGif.setController(draweeController);
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if (success) {
            JSONObject result = (JSONObject) entity;
            int code = result.getInteger("code");
            if (code == NetworkCodes.CODE_SUCCEED) {
                JSONObject data = (JSONObject) result.get("data");
                boolean hasUnread = data.getBoolean("has_unread") == null ? false : data.getBoolean("has_unread");
                CommonUserInfo.getInstance().setHasUnreadMsg(hasUnread);
            }
        }
    }

}
