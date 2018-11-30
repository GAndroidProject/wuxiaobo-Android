package com.xiaoe.shop.wxb.business.audio.ui;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.umeng.socialize.UMShareAPI;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.entitys.HadSharedEvent;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.entitys.ScholarshipEntity;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.NetworkState;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.requests.AddCollectionRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.RemoveCollectionListRequest;
import com.xiaoe.network.requests.ScholarshipReceiveRequest;
import com.xiaoe.network.requests.ScholarshipSubmitRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.anim.ViewAnim;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPresenter;
import com.xiaoe.shop.wxb.business.main.presenter.ScholarshipPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;
import com.xiaoe.shop.wxb.events.HideVideoPlayListEvent;
import com.xiaoe.shop.wxb.events.MyCollectListRefreshEvent;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.interfaces.OnClickMoreMenuListener;
import com.xiaoe.shop.wxb.utils.CollectionUtils;
import com.xiaoe.shop.wxb.utils.NumberFormat;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;
import com.xiaoe.shop.wxb.utils.UpdateLearningUtils;
import com.xiaoe.shop.wxb.widget.CommonBuyView;
import com.xiaoe.shop.wxb.widget.ContentMenuLayout;
import com.xiaoe.shop.wxb.widget.CustomDialog;
import com.xiaoe.shop.wxb.widget.SpeedMenuLayout;
import com.xiaoe.shop.wxb.widget.StatusPagerView;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.List;

public class AudioActivity extends XiaoeActivity implements View.OnClickListener, OnClickMoreMenuListener {
    private static final String TAG = "AudioActivity";
    private SimpleDraweeView audioBG;
    private SimpleDraweeView audioRing;
    private ViewAnim mViewAnim;


    private RelativeLayout btnPageClose;
    private TextView audioTitle;
    private TextView playNum;
    private TextView btnSpeedPlay;
    private AudioPlayControllerView audioPlayController;
    private ObjectAnimator diskRotate;
    private AudioHoverControllerLayout audioHoverPlayController;
    private ContentMenuLayout contentMenuLayout;
    private SpeedMenuLayout mSpeedMenuLayout;
    private WebView detailContent;
    private CommonBuyView commonBuyView;
    private StatusPagerView statusPagerView;
    private AudioDetailsSwitchLayout pagerContentDetailLayout;
    private AudioPlayListLayout audioPlayList;
    private ImageView btnCollect;
    private boolean hasCollect = false;//是否收藏
    private CollectionUtils collectionUtils;
    private ImageView btnAudioDownload;

    List<LoginUser> loginUserList;

    private TouristDialog touristDialog;
    private ScholarshipPresenter scholarshipPresenter;
    private boolean hasEarnMoney;
    private String amount;
    Handler handler = new Handler();
    private Runnable runnable;
    Dialog dialog;
    private boolean mIsDownload = false;
    private SimpleDraweeView audioAdvertiseImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }
        EventBus.getDefault().register(this);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setStatusBar();
        setContentView(R.layout.activity_audio);
        loginUserList = getLoginUserList();

        if (loginUserList.size() == 0) {
            touristDialog = new TouristDialog(this);
            touristDialog.setDialogCloseClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touristDialog.dismissDialog();
                }
            });
            touristDialog.setDialogConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touristDialog.dismissDialog();
                    JumpDetail.jumpLogin(AudioActivity.this);
                }
            });
        }
        getDialog().setDialogTag(CustomDialog.PAGER_LOAD_TAG);
        getDialog().showLoadDialog(false);
        initViews();
        initDatas();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(AudioMediaPlayer.isPlaying()){
            setDiskRotateAnimator(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int code = getWXPayCode(true);
        if(code == 0){
            getDialog().setDialogTag(-1);
            getDialog().showLoadDialog(false);
            AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
            new AudioPresenter(null).requestDetail(playEntity.getResourceId());
        }
        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
    }

    private void initViews() {
        pagerContentDetailLayout = (AudioDetailsSwitchLayout) findViewById(R.id.pager_content_detail);

        mViewAnim = new ViewAnim();
        audioBG = (SimpleDraweeView) findViewById(R.id.audio_bg);
        audioBG.setImageURI("res:///"+R.mipmap.detail_bg_wave);
        audioRing = (SimpleDraweeView) findViewById(R.id.audio_ring);
//        audioRing.setImageURI("res:///"+R.mipmap.detail_disk);

        //页面关闭按钮
        btnPageClose = (RelativeLayout) findViewById(R.id.audio_page_close_btn);
        btnPageClose.setOnClickListener(this);
        //标题
        audioTitle = (TextView) findViewById(R.id.audio_title);
        //播放次数
        playNum = (TextView) findViewById(R.id.play_num);

        //倍速按钮
        btnSpeedPlay = (TextView) findViewById(R.id.audio_speed_play);
        btnSpeedPlay.setOnClickListener(this);
        updateSpeedPlayButtonView(AudioMediaPlayer.mPlaySpeed);
        //音频播放控制器
        audioPlayController = (AudioPlayControllerView) findViewById(R.id.audio_play_controller);
        //悬浮播放控制器
        audioHoverPlayController = (AudioHoverControllerLayout) findViewById(R.id.audio_hover_controller);
        audioHoverPlayController.setOnMenuListener(this);
        //菜单栏
        contentMenuLayout = (ContentMenuLayout) findViewById(R.id.content_menu_layout);
        contentMenuLayout.setButtonClickListener(this);
        //播放倍数菜单
        mSpeedMenuLayout = (SpeedMenuLayout) findViewById(R.id.speed_menu_layout);
        mSpeedMenuLayout.setButtonClickListener(this);

        //播放列表
        audioPlayList = (AudioPlayListLayout) findViewById(R.id.audio_play_list);
        audioPlayList.setVisibility(View.GONE);
        //播放列表按钮
        ImageView btnPlayList = (ImageView) findViewById(R.id.btn_play_list);
        btnPlayList.setOnClickListener(this);
        if(AudioPlayUtil.getInstance().isSingleAudio()){
            btnPlayList.setVisibility(View.GONE);
        }else {
            btnPlayList.setVisibility(View.VISIBLE);
            audioPlayList.addPlayData(AudioPlayUtil.getInstance().getAudioList());
        }

        ImageView btnAudioComment = (ImageView) findViewById(R.id.btn_audio_comment);
        btnAudioComment.setOnClickListener(this);
        //图文内容详细显示
        detailContent = (WebView) findViewById(R.id.audio_detail_content);
        //底部购买按钮
        commonBuyView = (CommonBuyView) findViewById(R.id.common_buy_view);
        commonBuyView.setOnVipBtnClickListener(this);
        commonBuyView.setOnBuyBtnClickListener(this);
        //状态页面
        statusPagerView = (StatusPagerView) findViewById(R.id.state_pager_view);
        statusPagerView.setVisibility(View.GONE);
        statusPagerView.setOnClickListener(this);
        //收藏按钮
        btnCollect = (ImageView) findViewById(R.id.btn_collect);
        btnCollect.setOnClickListener(this);
        //分享按钮
        ImageView btnShare = (ImageView) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);

        //下载按钮
        btnAudioDownload = (ImageView) findViewById(R.id.btn_audio_download);
        btnAudioDownload.setOnClickListener(this);

        // 广告位
        audioAdvertiseImg = (SimpleDraweeView) findViewById(R.id.audio_advertise_img);
        String imgUrl = "res:///" + R.mipmap.img_text_bg;
        SetImageUriUtil.setImgURI(audioAdvertiseImg, imgUrl, Dp2Px2SpUtil.dp2px(this, 375), Dp2Px2SpUtil.dp2px(this, 100));
        audioAdvertiseImg.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                JumpDetail.jumpMainTab(AudioActivity.this, true, true, 2);
            }
        });
    }
    private void initDatas() {
        collectionUtils = new CollectionUtils(this);
        refreshPager();
    }
    private void setDiskRotateAnimator(boolean play){
        if(diskRotate == null){
            diskRotate = ObjectAnimator.ofFloat(audioRing, "rotation", 0, 360);
            diskRotate.setInterpolator(new LinearInterpolator());
            diskRotate.setDuration(10000);
            diskRotate.setRepeatCount(-1);
        }
        if(diskRotate.isStarted()){
            if(play){
                diskRotate.resume();
            }else{
                diskRotate.pause();
            }
        }else{
            diskRotate.start();
        }
    }

    @Override
    public void onBackPressed() {
        setViewAnim(audioRing, 1, 0, 1, 0, 1, 0);
        setViewAnim(btnPageClose, 1, 0, 1, 0, 1, 0);
        if (AudioMediaPlayer.getAudio() != null && AudioMediaPlayer.getAudio().getHasBuy() == 1) {
            // 上报学习进度（买了才上报）
            UpdateLearningUtils updateLearningUtils = new UpdateLearningUtils(this);
            updateLearningUtils.updateLearningProgress(AudioMediaPlayer.getAudio().getResourceId(), 2, 10);
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim,R.anim.slide_bottom_out);
    }

    private void setViewAnim(final View fromView, float startScaleX, float finaScaleX,
                             float startScaleY, float finaScaleY,
                             float startAlpha, float finalAlpha){
        mViewAnim.startViewSimpleAnim(fromView, startScaleX, finaScaleX, startScaleY, finaScaleY, startAlpha, finalAlpha);
    }

    private void initViewState(int visible) {
        audioRing.setVisibility(visible);
        btnPageClose.setVisibility(visible);
        playNum.setVisibility(visible);
        btnSpeedPlay.setVisibility(visible);
        audioPlayController.setVisibility(visible);
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if(entity == null || !success){
            return;
        }
        JSONObject jsonObject = (JSONObject) entity;
        if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED){
            return;
        }
        if(iRequest instanceof AddCollectionRequest){
            addCollectionRequest(jsonObject);
        }else if(iRequest instanceof RemoveCollectionListRequest){
            removeCollectionRequest(jsonObject);
        } else if (iRequest instanceof ScholarshipSubmitRequest) { // 奖学金提交回调
            JSONObject data = (JSONObject) jsonObject.get("data");
            String taskDetailId = data.getString("task_detail_id");
            ScholarshipEntity.getInstance().setTaskDetailId(taskDetailId);
            scholarshipPresenter.queryReceiveResult(ScholarshipEntity.getInstance().getTaskId(), ScholarshipEntity.getInstance().getTaskDetailId());
        } else if (iRequest instanceof ScholarshipReceiveRequest) {
            // 获取成功之后查询领取结果接口
            JSONObject result = (JSONObject) jsonObject.get("data");
            obtainReceiveStatus(result);
        }
    }

    // 处理查询结果
    private void obtainReceiveStatus(JSONObject data) {
        int status = data.getInteger("status");
        JSONObject reward = (JSONObject) data.get("reward");
        if (status == 3 && reward != null) { // 已经完成并拿到数据
            ScholarshipEntity.getInstance().setIssueState(ScholarshipEntity.SCHOLARSHIP_ISSUED);

            // TODO: 禁止点击事件
            int type = reward.getInteger("type");
            int amount = reward.getInteger("amount");
            if (type == 1) { // 拿到钱
                hasEarnMoney = true;
                BigDecimal priceTop = new BigDecimal(amount);
                BigDecimal priceBottom = new BigDecimal(100);
                this.amount = priceTop.divide(priceBottom, 2, BigDecimal.ROUND_HALF_UP).toPlainString();
            } else if (type == 2) { // 拿到积分
                hasEarnMoney = false;
                this.amount = String.valueOf(amount);
            }
            showEarnDialog();
        } else if (status == 2) { // 处理中
            Toast("奖学金发放中");
            ScholarshipEntity.getInstance().setIssueState(ScholarshipEntity.SCHOLARSHIP_PROCESSING);
            runnable = new Runnable() {
                @Override
                public void run() {
                    scholarshipPresenter.queryReceiveResult(ScholarshipEntity.getInstance().getTaskId(), ScholarshipEntity.getInstance().getTaskDetailId());
                }
            };
            handler.postDelayed(runnable, 3000);
        } else if (status == 1) {
            ScholarshipEntity.getInstance().setIssueState(ScholarshipEntity.SCHOLARSHIP_FAIL);
            Toast.makeText(this, "领取失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 取消收藏
     * @param jsonObject
     */
    private void removeCollectionRequest(JSONObject jsonObject) {
        if(jsonObject.getIntValue("code") == NetworkCodes.CODE_SUCCEED ){
            toastCustom(getString(R.string.cancel_collect_succeed));
            AudioMediaPlayer.getAudio().setHasFavorite(0);
        }else{
            toastCustom(getResources().getString(R.string.cancel_collect_fail));
            setCollectState(true);
        }
    }

    /**
     * 添加收藏
     * @param jsonObject
     */
    private void addCollectionRequest(JSONObject jsonObject) {
        if(jsonObject.getIntValue("code") == NetworkCodes.CODE_SUCCEED ){
            toastCustom(getString(R.string.collect_succeed));
            AudioMediaPlayer.getAudio().setHasFavorite(1);
        }else{
            toastCustom(getResources().getString(R.string.collect_fail));
            setCollectState(false);
        }
    }


    private void setContentDetail(String detail){
        detailContent.loadDataWithBaseURL(null, NetworkState.getNewContent(detail), "text/html", "UFT-8", null);
    }
    /**
     *
     * @param state 0-正常的,1-请求失败,2-课程下架,-1： 加载，3-停售， 4-待上架，3004：商品已删除
     */
    public boolean setPagerState(int state){
        if(state == 1){
            pagerContentDetailLayout.setVisibility(View.GONE);
            statusPagerView.setPagerState(StatusPagerView.FAIL, getString(R.string.request_fail), StatusPagerView.DETAIL_NONE);
            return false;
        }else if(state == 2){
            pagerContentDetailLayout.setVisibility(View.GONE);
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_sold_out), R.mipmap.course_off);
            return false;
        }else if(state == 3){
            pagerContentDetailLayout.setVisibility(View.GONE);
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_sale_stop), R.mipmap.course_off);
            return false;
        }else if(state == 4){
            pagerContentDetailLayout.setVisibility(View.GONE);
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_stay_putaway), R.mipmap.course_off);
            return false;
        }else if(state == 3004){
            pagerContentDetailLayout.setVisibility(View.GONE);
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_delete), R.mipmap.course_off);
            return false;
        }else{
            pagerContentDetailLayout.setVisibility(View.VISIBLE);
            statusPagerView.setVisibility(View.GONE);
            statusPagerView.setLoadingState(View.GONE);
            statusPagerView.setHintStateVisibility(View.GONE);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.audio_page_close_btn:
                onBackPressed();
                break;
            case R.id.btn_audio_comment:
            case R.id.btn_comment:
                AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
                JumpDetail.jumpComment(this,playEntity.getResourceId(), 2, audioTitle.getText().toString());
                break;
            case R.id.btn_play_list:
                if(audioPlayList.getVisibility() == View.VISIBLE){
                    audioPlayList.setVisibility(View.GONE);
                }else{
                    audioPlayList.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.buy_course:
                if (loginUserList.size() == 1) {
                    buyResource();
                } else {
                    touristDialog.showDialog();
                }
                break;
            case R.id.buy_vip:
                if (loginUserList.size() == 1) {
                    JumpDetail.jumpSuperVip(this);
                } else {
                    touristDialog.showDialog();
                }
                break;
            case R.id.btn_collect:
            case R.id.btn_collect_item:
                if (loginUserList.size() == 1) {
                    collect();
                } else {
                    touristDialog.showDialog();
                }
                break;
            case R.id.btn_share:
            case R.id.btn_share_item:
                AudioPlayEntity audioPlayEntity = AudioMediaPlayer.getAudio();
                if(audioPlayEntity != null){
                    String imgUrl = TextUtils.isEmpty(audioPlayEntity.getImgUrlCompressed()) ? audioPlayEntity.getImgUrl() :  audioPlayEntity.getImgUrlCompressed();
                    umShare(audioPlayEntity.getTitle(), imgUrl, audioPlayEntity.getShareUrl(), "");
                }
                break;
            case R.id.audio_speed_play:
                if (AudioMediaPlayer.prepared) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (View.VISIBLE != mSpeedMenuLayout.getVisibility())
                            mSpeedMenuLayout.setVisibility(View.VISIBLE);
                    } else {
                        toastCustom(getString(R.string.speed_play_not_support));
                    }
                }
                break;
            case R.id.btn_speed_1:
                changePlaySpeed(0.7f);
                break;
            case R.id.btn_speed_2:
                changePlaySpeed(1f);
                break;
            case R.id.btn_speed_3:
                changePlaySpeed(1.5f);
                break;
            case R.id.btn_speed_4:
                changePlaySpeed(2.0f);
                break;
            case R.id.btn_audio_download:
            case R.id.btn_download:
                clickDownload();
                break;
            case R.id.state_pager_view:
                // TODO 添加刷新逻辑
//                if (statusPagerView.getCurrentLoadingStatus() == StatusPagerView.FAIL) {
//                    statusPagerView.setPagerState(StatusPagerView.LOADING, "", 0);
//                }
                break;
            default:
                break;
        }
    }

    private void clickDownload() {
        if(mIsDownload){
            //如果已经下载了，则不做任何操作
            return;
        }
        if(TextUtils.isEmpty(AudioMediaPlayer.getAudio().getPlayUrl())){
            toastCustom(getString(R.string.cannot_download));
        }else{
            AudioPlayEntity audioPlayEntity = AudioMediaPlayer.getAudio();
            boolean isDownload = DownloadManager.getInstance().isDownload(CommonUserInfo.getShopId(), audioPlayEntity.getResourceId());
            if(!isDownload){
                ColumnSecondDirectoryEntity download = new ColumnSecondDirectoryEntity();
                download.setApp_id(CommonUserInfo.getShopId());
                download.setResource_id(audioPlayEntity.getResourceId());
                download.setTitle(audioPlayEntity.getTitle());
                download.setResource_type(2);
                download.setImg_url(audioPlayEntity.getImgUrl());
                download.setAudio_url(audioPlayEntity.getPlayUrl());
                DownloadManager.getInstance().addDownload(null, null, download);
            }
            toastCustom(getString(R.string.add_download_list));
            setDownloadState(true);
        }
    }

    private void changePlaySpeed(float speed) {
        if (View.VISIBLE == mSpeedMenuLayout.getVisibility())
            mSpeedMenuLayout.setVisibility(View.GONE);
        boolean isChange = AudioMediaPlayer.changePlayerSpeed(speed);
        if (!isChange)   return;
        updateSpeedPlayButtonView(speed);
    }

    private void updateSpeedPlayButtonView(float speed) {
        String format = getString(R.string.speed_play_text);
        if (speed != 1) {
            format = "%.1f" + format;
            btnSpeedPlay.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            btnSpeedPlay.setPadding(0,0,0,0);
        }else {
            btnSpeedPlay.setCompoundDrawablesWithIntrinsicBounds(null,null,getDrawable(R.mipmap.icon_speed_play),null);
            btnSpeedPlay.setPadding(0,0,Dp2Px2SpUtil.dp2px(this,10),0);
        }
        btnSpeedPlay.setText(String.format(format,speed));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }

    private void collect() {
        hasCollect = !hasCollect;
        AudioPlayEntity audioPlayEntity = AudioMediaPlayer.getAudio();
        if(hasCollect){
            setCollectState(true);
            //添加收藏
            JSONObject collectionContent = new JSONObject();
            collectionContent.put("title",audioPlayEntity.getTitle());
            collectionContent.put("author","");
            collectionContent.put("img_url",audioPlayEntity.getImgUrl());
            collectionContent.put("img_url_compressed",audioPlayEntity.getImgUrl());
            String price = audioPlayEntity.getHasBuy() == 1 ? "" : ""+audioPlayEntity.getPrice();
            collectionContent.put("price", price);
            collectionUtils.requestAddCollection(audioPlayEntity.getResourceId(), "2", collectionContent);
        }else {
            setCollectState(false);
            //取消收藏
            collectionUtils.requestRemoveCollection(audioPlayEntity.getResourceId(), "2");
        }
    }

    private void buyResource() {
        AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        JumpDetail.jumpPay(this, playEntity.getResourceId(), 2, playEntity.getImgUrl(), playEntity.getTitle(), playEntity.getPrice(), null);
    }

    @Subscribe
    public void onEventMainThread(HideVideoPlayListEvent event){
        if (event != null && event.isHide() && audioPlayList.getVisibility() == View.VISIBLE){
            audioPlayList.postDelayed(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.btn_play_list).performClick();
                }
            },50);
        }
    }

    @Subscribe
    public void onEventMainThread(AudioPlayEvent event) {
        switch (event.getState()){
            case AudioPlayEvent.LOADING:
                audioPlayController.setPlayButtonEnabled(false);
                break;
            case AudioPlayEvent.PLAY:
                audioPlayController.setPlayButtonEnabled(true);
                audioPlayController.setPlayState(true);
                audioHoverPlayController.setPlayState(true);
                audioPlayController.setTotalDuration(AudioMediaPlayer.getDuration());
                setDiskRotateAnimator(true);
                break;
            case AudioPlayEvent.PAUSE:
                audioPlayController.setPlayButtonEnabled(true);
                audioPlayController.setPlayState(false);
                audioHoverPlayController.setPlayState(false);
                setDiskRotateAnimator(false);
                break;
            case AudioPlayEvent.STOP:
                audioPlayController.setPlayState(false);
                audioHoverPlayController.setPlayState(false);
                setDiskRotateAnimator(false);
                break;
            case AudioPlayEvent.PROGRESS:
                audioPlayController.setPlayDuration(event.getProgress());
                break;
            case AudioPlayEvent.REFRESH_PAGER:
                refreshPager();
                break;
            case AudioPlayEvent.NEXT:
            case AudioPlayEvent.LAST:
                refreshPager();
                audioPlayList.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(HadSharedEvent hadSharedEvent) {
        if (hadSharedEvent != null && hadSharedEvent.hadShared) {
            if (CommonUserInfo.isIsSuperVip() || (!AudioMediaPlayer.getAudio().isFree() && AudioMediaPlayer.getAudio().getHasBuy() == 1)) { // 不是免费然后已经分享并且买了
                if (scholarshipPresenter == null) {
                    scholarshipPresenter = new ScholarshipPresenter(
                            this,
                            AudioMediaPlayer.getAudio().getResourceId(),
                            "2",
                            CommonUserInfo.isIsSuperVip());
                }
                scholarshipPresenter.requestTaskList(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(detailContent != null){
            detailContent.destroy();
        }
        UMShareAPI.get(this).release();
        if (!hasCollect)
            EventBus.getDefault().post(new MyCollectListRefreshEvent(true,AudioMediaPlayer
                    .getAudio() == null ? "" : AudioMediaPlayer.getAudio().getResourceId()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onClickMoreMenu(View view) {
        if(view.getId() == R.id.hover_audio_more){
            contentMenuLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setButtonEnabled(boolean enabled){
        audioPlayController.setButtonEnabled(enabled);
        audioHoverPlayController.setButtonEnabled(enabled);
    }

    private void refreshPager(){
        if(activityDestroy){
            return;
        }

        AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        if(playEntity == null){
            getDialog().dismissDialog();
            setButtonEnabled(false);
            return;
        }
        int code = playEntity.getCode();

        if(!playEntity.isSingleBuy() && code == 0){
            JumpDetail.jumpColumn(this, playEntity.getProductId(), playEntity.getProductImgUrl(), playEntity.getProductType());
            finish();
            return;
        }
        if (code == 1){
            setPagerState(1);
            getDialog().dismissDialog();
            Toast(getString(R.string.network_error_text));
            return;
        }else if(code == -2){
            setButtonEnabled(false);
        }
        if(playEntity.getHasBuy() == 0 && code == 0){
            //未购买
            commonBuyView.setVisibility(playEntity.isCache() ? View.GONE : View.VISIBLE);
            commonBuyView.setBuyPrice(playEntity.getPrice());
            if (CommonUserInfo.isIsSuperVipAvailable()) { // 超级会员判断
                commonBuyView.setVipBtnVisibility(playEntity.isCache() ? View.GONE : View.VISIBLE);
            } else {
                commonBuyView.setVipBtnVisibility(View.GONE);
            }
            setButtonEnabled(false);
        }else{
            commonBuyView.setVisibility(View.GONE);
            setButtonEnabled(true);
        }
        if(code == 0){
            getDialog().dismissDialog();
            setContentDetail(playEntity.getContent());
            if(!setPagerState(playEntity.getResourceStateCode())){
                return;
            }
        }
        if(AudioMediaPlayer.isStop()){
            AudioMediaPlayer.setAudio(playEntity, true);
        }
        audioTitle.setText(playEntity.getTitle());
        int count = playEntity.getPlayCount();
        if(count > 0){
            playNum.setVisibility(View.VISIBLE);
            playNum.setText(NumberFormat.viewCountToString(count)+"次播放");
        }else{
            playNum.setVisibility(View.GONE);
        }
        String imageUrl = playEntity.getImgUrl();
        if(!TextUtils.isEmpty(imageUrl)){
            if("gif".equals(imageUrl.substring(imageUrl.lastIndexOf(".")+1)) || "GIF".equals(imageUrl.substring(imageUrl.lastIndexOf(".")+1))){
                setRoundAsCircle(Uri.parse(imageUrl));
            }else{
                audioRing.setImageURI(Uri.parse(imageUrl));
            }
            audioHoverPlayController.setAudioImage(imageUrl);
        }

        audioPlayList.setProductsTitle(playEntity.getProductsTitle());
        setCollectState(playEntity.getHasFavorite() == 1);
        //设置下载状态
        boolean isDownload = DownloadManager.getInstance().isDownload(playEntity.getAppId(), playEntity.getResourceId());
        setDownloadState(isDownload);
    }

    private void setRoundAsCircle(Uri uri){
        audioRing.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);

        ImageDecodeOptions imageDecodeOptions = new ImageDecodeOptionsBuilder()
                .setForceStaticImage(true)
                .build();

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
//                .setResizeOptions(new ResizeOptions(100, 100))
                .setCacheChoice(ImageRequest.CacheChoice.SMALL)
                .setImageDecodeOptions(imageDecodeOptions)
                .build();

        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(audioRing.getController())
                .setImageRequest(request);
        audioRing.setController(builder.build());
    }
    private void setDownloadState(boolean download){
        if(download){
            btnAudioDownload.setImageResource(R.mipmap.audio_alreadydownload);
        }else{
            btnAudioDownload.setImageResource(R.mipmap.audio_download);
        }
        contentMenuLayout.setDownloadState(download);
        mIsDownload = download;
    }
    /**
     * 设置收藏状态
     * @param collect 0-未收藏，1-已收藏
     */
    private void setCollectState(boolean collect){
        hasCollect = collect;
        if(collect){
            btnCollect.setImageResource(R.mipmap.audio_collect);
        }else{
            btnCollect.setImageResource(R.mipmap.video_collect);
        }
        contentMenuLayout.setCollectState(collect);
    }

    private void showEarnDialog() {
        dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        Window window = dialog.getWindow();
        View view = getLayoutInflater().inflate(R.layout.scholarship_dialog, null);
        ImageView earnClose = (ImageView) view.findViewById(R.id.scholarship_dialog_close);
        LinearLayout earnWrap = (LinearLayout) view.findViewById(R.id.scholarship_content_wrap);
        TextView earnTitle = (TextView) view.findViewById(R.id.scholarship_dialog_title);
        TextView earnContent = (TextView) view.findViewById(R.id.scholarship_dialog_content);
        TextView earnContentTail = (TextView) view.findViewById(R.id.scholarship_dialog_content_tail);
        TextView earnTip = (TextView) view.findViewById(R.id.scholarship_dialog_tip);
        TextView earnSubmit = (TextView) view.findViewById(R.id.scholarship_dialog_submit);

        if (hasEarnMoney) { // 拿到钱了
            earnTitle.setText("恭喜你，获得奖学金");
            earnWrap.setBackground(getResources().getDrawable(R.mipmap.scholarship_popup_bg));
            earnContent.setText(amount);
            earnContentTail.setVisibility(View.VISIBLE);
            earnTip.setVisibility(View.GONE);
            earnSubmit.setText(getString(R.string.scholarship_earn_btn));
            earnSubmit.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
                @Override
                public void singleClick(View v) {
                    JumpDetail.jumpScholarshipActivity(AudioActivity.this);
                    dialog.dismiss();
                }
            });
        } else {
            earnTitle.setText("差一点就瓜分到了");
            earnContentTail.setVisibility(View.GONE);
            earnWrap.setBackgroundColor(getResources().getColor(R.color.white));
            earnSubmit.setText(getString(R.string.scholarship_earn_integral));
            if (CommonUserInfo.isIsSuperVip()) { // 超级会员
                String content = "送你" + amount + "积分";
                earnContent.setText(content);
                earnTip.setVisibility(View.GONE);
            } else {
                String content = "送你" + amount + "积分";
                earnContent.setText(content);
                earnContent.setTextSize(20);
                earnContent.setTextColor(getResources().getColor(R.color.scholarship_btn_press));
                earnTip.setVisibility(View.GONE);
            }
            earnSubmit.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
                @Override
                public void singleClick(View v) {
                    JumpDetail.jumpIntegralActivity(AudioActivity.this);
                    dialog.dismiss();
                }
            });
        }
        earnClose.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                dialog.dismiss();
            }
        });
        // 先 show 后才会有宽高
        dialog.show();

        if (window != null) {
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            Point point = Global.g().getDisplayPixel();
            layoutParams.width = (int) (point.x * 0.8);
            window.setAttributes(layoutParams);
        }
        dialog.setContentView(view);
    }

    @Override
    public void onDialogDismiss(DialogInterface dialog, int tag, boolean backKey) {
        if(tag == CustomDialog.PAGER_LOAD_TAG && backKey){
            finish();
        }
    }
}
