package com.xiaoe.shop.wxb.business.launch.ui;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.utils.CacheDataUtil;
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
import com.xiaoe.shop.wxb.utils.FrameAnimation;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initView();
        initData();
        SharedPreferencesUtil.getInstance(this, SharedPreferencesUtil.FILE_NAME);
        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
    }

    private void initView() {
        long preTime = System.currentTimeMillis();

        // 每50ms一帧，播放动画一次
        FrameAnimation frameAnimation = new FrameAnimation(ivGif, getRes(), 10, false);
        frameAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {
                Log.d(TAG, "start");
            }

            @Override
            public void onAnimationEnd() {
                Log.d(TAG, "end");
                Log.d(TAG, "onAnimationEnd: " + (System.currentTimeMillis() - preTime));
                JumpDetail.jumpLogin(mContext);
                finish();
            }

            @Override
            public void onAnimationRepeat() {
                Log.d(TAG, "repeat");
            }
        });

//        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
//            @Override
//            public void onFinalImageSet(String id, ImageInfo imageInfo, final Animatable animatable) {
//                Log.d(TAG, "onFinalImageSet: animatable - " + animatable);
//                if (animatable == null) {
//                    return;
//                }
//                int duration = 0;
//                try {
//                    // mTotalLoops mLoopCount
//                    Field field = AnimatedDrawable2.class.getDeclaredField("mLoopCount");
//                    field.setAccessible(true);
//                    // 设置循环次数为 1
//                    field.set(animatable, 1);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
////                animatable.start();
//                if (animatable instanceof AnimatedDrawable2) {
//                    duration = (int) ((AnimatedDrawable2) animatable).getLoopDurationMs();
//                    Log.d(TAG, "onFinalImageSet: loopCount - " + ((AnimatedDrawable2) animatable).getLoopCount());
//                }
//                if (duration > 0) {
//                    launchGif.postDelayed(() -> {
//                        if (animatable.isRunning()) {
//                            animatable.stop();
//                            JumpDetail.jumpLogin(mContext);
//                            finish();
//                        }
//                    }, duration);
//                }
//                Log.d(TAG, "onFinalImageSet: duration " + duration);
//            }
//        };
//
//        Uri uri = Uri.parse("res:///" + R.drawable.launch);
//        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
//                .setUri(uri)
//                .setControllerListener(controllerListener)
//                // 设置加载图片完成后是否直接进行播放
//                .setAutoPlayAnimations(true)
//                .build();
//        launchGif.setController(draweeController);
    }

    private void initData() {
        String apiToken = CommonUserInfo.getApiToken();
        if (apiToken != null) {
            UnReadMsgPresenter unReadMsgPresenter = new UnReadMsgPresenter(this);
            unReadMsgPresenter.requestUnReadCouponMsg();
        }
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

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if (success) {
            if (iRequest instanceof UnReadMsgRequest) {
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

    /**
     * 获取需要播放的动画资源
     */
    private int[] getRes() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.animation_logo);
        int len = typedArray.length();
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }

}
