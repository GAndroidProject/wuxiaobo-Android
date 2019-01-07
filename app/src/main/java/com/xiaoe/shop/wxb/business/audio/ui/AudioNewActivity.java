package com.xiaoe.shop.wxb.business.audio.ui;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.socialize.UMShareAPI;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.HadSharedEvent;
import com.xiaoe.common.entitys.LearningRecord;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.entitys.ScholarshipEntity;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.NetUtils;
import com.xiaoe.common.utils.NetworkState;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.requests.AddCollectionRequest;
import com.xiaoe.network.requests.ColumnListRequst;
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
import com.xiaoe.shop.wxb.business.audio.presenter.CountDownTimerTool;
import com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper;
import com.xiaoe.shop.wxb.business.column.presenter.ColumnPresenter;
import com.xiaoe.shop.wxb.business.main.presenter.ScholarshipPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.datareport.EventReportManager;
import com.xiaoe.shop.wxb.common.datareport.MobclickEvent;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;
import com.xiaoe.shop.wxb.events.HideAudioPlayListEvent;
import com.xiaoe.shop.wxb.events.MyCollectListRefreshEvent;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.interfaces.OnClickMoreMenuListener;
import com.xiaoe.shop.wxb.interfaces.OnCustomScrollChangedListener;
import com.xiaoe.shop.wxb.utils.CollectionUtils;
import com.xiaoe.shop.wxb.utils.LogUtils;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.utils.UpdateLearningUtils;
import com.xiaoe.shop.wxb.widget.CommonBuyView;
import com.xiaoe.shop.wxb.widget.CustomDialog;
import com.xiaoe.shop.wxb.widget.CustomScrollView;
import com.xiaoe.shop.wxb.widget.StatusPagerView;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper.COUNT_DOWN_DURATION_10;
import static com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper.COUNT_DOWN_DURATION_20;
import static com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper.COUNT_DOWN_DURATION_30;
import static com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper.COUNT_DOWN_DURATION_60;
import static com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper.COUNT_DOWN_STATE_CLOSE;
import static com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper.COUNT_DOWN_STATE_CURRENT;
import static com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper.COUNT_DOWN_STATE_TIME;

public class AudioNewActivity extends XiaoeActivity implements View.OnClickListener, OnClickMoreMenuListener,
        OnCustomScrollChangedListener {
    private static final String TAG = "AudioActivity";
    private SimpleDraweeView audioBG;
    private SimpleDraweeView audioRing;
    private ViewAnim mViewAnim;

    private ImageView btnPageClose;
    private TextView audioTitle;
    private TextView playNum;
    private TextView btnSpeedPlay;
    private TextView btnCountDownPlay;
    private AudioPlayControllerView audioPlayController;
    private ObjectAnimator diskRotate;
    private WebView detailContent;
    private CommonBuyView commonBuyView;
    private StatusPagerView statusPagerView;
    private AudioPlayListDialog audioPlayList;
    private ImageView btnCollect;
    private boolean hasCollect = false;//是否收藏
    private CollectionUtils collectionUtils;
    private ImageView btnAudioDownload;
    private TextView tryAudioText;

    List<LoginUser> loginUserList;

    private TouristDialog touristDialog;
    private ScholarshipPresenter scholarshipPresenter;
    private boolean hasEarnMoney;
    private String amount;
    Handler handler = new Handler();
    private Runnable runnable;
    Dialog dialog;
    private boolean mIsDownload = false;
    private CustomScrollView audioScrollView;

    private int toolBarHeight;
    private View audioTitleBar;
    private TextView barTitle;
    private TextView mStatusBarBlank;
    private Dialog mPlaySpeedDialog,mCountDownPlayDialog;
    private View mPlaySpeedDialogView,mCountDownPlayDialogView;
    private String playNumStr;
    private List<CheckBox> mCheckBoxes;
    private List<TextView> mCountDownTexts;
    private ColumnPresenter mColumnPresenter;
    final String REQUEST_TAG = "AudioNewActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
//            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }
        EventBus.getDefault().register(this);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setStatusBar();
        setContentView(R.layout.activity_audio_new);
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
                    JumpDetail.jumpLogin(AudioNewActivity.this);
                }
            });
        }
        getDialog().setDialogTag(CustomDialog.PAGER_LOAD_TAG);
        getDialog().showLoadDialog(false);
        initViews();
        initDatas();
        AudioMediaPlayer.setCountDownCallBack(new CountDownTimerTool.CountDownCallBack() {
            @Override
            public void onTick(long millisUntilFinished) {
                updateCountDownPlayButtonView(false);
            }

            @Override
            public void onFinish() {
                updateCountDownPlayButtonView(false);
                updateCountDownCheckBox();
                updateCountDownText();
            }
        });
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

        audioScrollView = (CustomScrollView) findViewById(R.id.audio_scroll_view);
        audioScrollView.setScrollChanged(this);
//        audioScrollView.setLoadHeight(Dp2Px2SpUtil.dp2px(this, 40));

        mStatusBarBlank = (TextView) findViewById(R.id.status_bar_blank);
        audioTitleBar = findViewById(R.id.audio_title_bar);
        barTitle = (TextView) findViewById(R.id.title);

        mViewAnim = new ViewAnim();
        audioBG = (SimpleDraweeView) findViewById(R.id.audio_bg);
        audioBG.setImageURI("res:///"+R.mipmap.detail_bg_wave);
        audioRing = (SimpleDraweeView) findViewById(R.id.audio_ring);
//        audioRing.setImageURI("res:///"+R.mipmap.detail_disk);

        //页面关闭按钮
        btnPageClose = (ImageView) findViewById(R.id.audio_page_close_btn);
        btnPageClose.setOnClickListener(this);
        //标题
        audioTitle = (TextView) findViewById(R.id.audio_title);
        //播放次数
        playNum = (TextView) findViewById(R.id.play_num);

        //倍速按钮
        btnSpeedPlay = (TextView) findViewById(R.id.audio_speed_play);
        String phoneBrand = Global.getPhoneBrand().toUpperCase();
        if (isNotSupportSpeedChangePhone(phoneBrand))
            btnSpeedPlay.setVisibility(View.GONE);
        btnSpeedPlay.setOnClickListener(this);
        updateSpeedPlayButtonView(AudioMediaPlayer.mPlaySpeed);
        //定时
        btnCountDownPlay = (TextView) findViewById(R.id.audio_count_down);
        btnCountDownPlay.setOnClickListener(this);
        updateCountDownPlayButtonView();

        //音频播放控制器
        audioPlayController = (AudioPlayControllerView) findViewById(R.id.audio_play_controller);

        //播放列表
        audioPlayList = new AudioPlayListDialog(this);
        //播放列表按钮
        ImageView btnPlayList = (ImageView) findViewById(R.id.btn_play_list);
        btnPlayList.setOnClickListener(this);
        if(AudioPlayUtil.getInstance().isSingleAudio()){
            btnPlayList.setVisibility(View.GONE);
        }else {
            btnPlayList.setVisibility(View.VISIBLE);
//            audioPlayList.addPlayData(AudioPlayUtil.getInstance().getAudioList());
//            audioPlayList.addPlayListData(AudioPlayUtil.getInstance().getAudioList());
            mColumnPresenter = new ColumnPresenter(this);
            final String columnId = AudioMediaPlayer.getAudio().getColumnId();
            String playingColumnId = AudioMediaPlayer.mCurrentColumnId;
            if (!TextUtils.isEmpty(playingColumnId) && columnId == playingColumnId && AudioMediaPlayer.mCurrentPage > 0
                    && AudioPlayUtil.getInstance().getAudioListNew().size() > 0){
                audioPlayList.setPage(AudioMediaPlayer.mCurrentPage);
                audioPlayList.addPlayListData(AudioPlayUtil.getInstance().getAudioListNew(),true);
            }else   AudioMediaPlayer.mCurrentPage = -1;

            final String audioType = "2";
            audioPlayList.setLoadAudioListDataCallBack(new AudioPlayListDialog.LoadAudioListDataCallBack() {
                @Override
                public void onRefresh(int pageSize) {
                    if (mColumnPresenter != null){
                        LogUtils.d("onRefresh columnId = " + columnId);
                        mColumnPresenter.requestColumnList(columnId,audioType,1,
                                pageSize,true,REQUEST_TAG);
                    }
                }

                @Override
                public void onLoadMoreData(int page, int pageSize) {
                    if (mColumnPresenter != null){
                        mColumnPresenter.requestColumnList(columnId,audioType,page,
                                pageSize,true,REQUEST_TAG);
                    }
                }
            });
        }

        ImageView btnAudioComment = (ImageView) findViewById(R.id.btn_audio_comment);
        btnAudioComment.setOnClickListener(this);
        //图文内容详细显示
        detailContent = (WebView) findViewById(R.id.audio_detail_content);
        initWebView(detailContent);
        detailContent.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                JumpDetail.jumpAppBrowser(mContext, s, "");
                return true;
            }
        });
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

        //购买前试听
        tryAudioText = (TextView) findViewById(R.id.try_audio_text);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.price_color));
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        stringBuilder.append(getString(R.string.try_audio_hint));
        //购买前试听“免费试听”文字颜色
        stringBuilder.setSpan(colorSpan,0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tryAudioText.setText(stringBuilder);
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
            LearningRecord lr = new LearningRecord();
            lr.setLrId(AudioMediaPlayer.getAudio().getResourceId());
            lr.setLrType(DecorateEntityType.AUDIO);
            lr.setLrTitle(AudioMediaPlayer.getAudio().getTitle());
            lr.setLrImg(AudioMediaPlayer.getAudio().getImgUrl());
            lr.setLrDesc(playNumStr);
            UpdateLearningUtils.saveLr2Local(this, lr);
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
        if (isDestroyed())  return;
        if (iRequest instanceof ColumnListRequst){
            if (mColumnPresenter != null && audioPlayList != null){
                JSONObject jsonObject = (JSONObject) entity;
                if (!success || jsonObject == null){
                    audioPlayList.addPlayListData(null);
                    Toast(getString(R.string.network_error_text));
                    return;
                }
                Object dataObject = jsonObject.get("data");
                JSONArray data = null;
                if (dataObject != null){
                    data = (JSONArray) dataObject;
                }
                AudioPlayEntity audio = AudioMediaPlayer.getAudio();
                audioPlayList.addData(mColumnPresenter.formatSingleResourceEntity(data, audio == null ?
                                "" : audio.getTitle(), audio == null ? "" : audio.getColumnId(),
                        "", audio == null ? 0 : audio.getHasBuy()),audio == null ? 0 : audio.getHasBuy());
            }
        }
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

                EventReportManager.onEvent(mContext, MobclickEvent.SCHOLARSHIP_GETBURSE_SUCCESS_COUNT);
            } else if (type == 2) { // 拿到积分
                hasEarnMoney = false;
                this.amount = String.valueOf(amount);

                EventReportManager.onEvent(mContext, MobclickEvent.SCHOLARSHIP_GETGRADE_SUCCESS_COUNT);
            }
            showEarnDialog();
        } else if (status == 2) { // 处理中
            Toast(getString(R.string.scholarship_in_progress));
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
            Toast.makeText(this, R.string.failed_to_collect, Toast.LENGTH_SHORT).show();
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
            statusPagerView.setPagerState(StatusPagerView.FAIL, getString(R.string.request_fail), StatusPagerView.DETAIL_NONE);
            return false;
        }else if(state == 2){
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_sold_out), R.mipmap.course_off);
            return false;
        }else if(state == 3){
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_sale_stop), R.mipmap.course_off);
            return false;
        }else if(state == 4){
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_stay_putaway), R.mipmap.course_off);
            return false;
        }else if(state == 3004){
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_delete), R.mipmap.course_off);
            return false;
        }else{
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
                if (audioPlayList != null)
                    audioPlayList.showDialog();
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
            case R.id.audio_count_down:
                showCountDownPlayDialog();
                break;
            case R.id.btn_count_down_1:
                countDown(COUNT_DOWN_STATE_CLOSE,0,0);
                break;
            case R.id.btn_count_down_2:
                countDown(COUNT_DOWN_STATE_CURRENT,0,1);
                break;
            case R.id.btn_count_down_3:
                countDown(COUNT_DOWN_STATE_TIME,COUNT_DOWN_DURATION_10,2);
                break;
            case R.id.btn_count_down_4:
                countDown(COUNT_DOWN_STATE_TIME,COUNT_DOWN_DURATION_20,3);
                break;
            case R.id.btn_count_down_5:
                countDown(COUNT_DOWN_STATE_TIME,COUNT_DOWN_DURATION_30,4);
                break;
            case R.id.btn_count_down_6:
                countDown(COUNT_DOWN_STATE_TIME,COUNT_DOWN_DURATION_60,5);
                break;
            case R.id.audio_speed_play:
                if (AudioMediaPlayer.prepared) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String phoneBrand = Global.getPhoneBrand().toUpperCase();
                        //华为手机Android8.0倍速播放限制
                        if(isNotSupportSpeedChangePhone(phoneBrand)){
                            toastCustom(getString(R.string.speed_play_not_support));
                            return;
                        }
                        showPlaySpeedDialog();
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

    private void countDown(int state,int duration,int position) {
//        if (position == MediaPlayerCountDownHelper.INSTANCE.getMAudioSelectedPosition())//不能选择已经选中的项
//            return;
        AudioMediaPlayer.startCountDown(state,duration);
        updateCountDownPlayButtonView();
    }

    private boolean isNotSupportSpeedChangePhone(String phoneBrand) {
        return Build.VERSION.SDK_INT >= 26 && (Constants.PHONE_HONOR.equals(phoneBrand) || Constants.PHONE_HUAWEI.equals(phoneBrand));
    }

    private void showPlaySpeedDialog() {
        if (mPlaySpeedDialog == null){
            AlertDialog.Builder playSpeedBuilder = new AlertDialog.Builder(this);

            mPlaySpeedDialog = playSpeedBuilder.create();
            mPlaySpeedDialogView = LayoutInflater.from(this).inflate(R.layout.layout_speed_menu2, null, false);
            mPlaySpeedDialogView.findViewById(R.id.btn_speed_1).setOnClickListener(AudioNewActivity.this);
            mPlaySpeedDialogView.findViewById(R.id.btn_speed_2).setOnClickListener(AudioNewActivity.this);
            mPlaySpeedDialogView.findViewById(R.id.btn_speed_3).setOnClickListener(AudioNewActivity.this);
            mPlaySpeedDialogView.findViewById(R.id.btn_speed_4).setOnClickListener(AudioNewActivity.this);
            mPlaySpeedDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    if (mPlaySpeedDialog != null && mPlaySpeedDialog.isShowing())
                        mPlaySpeedDialog.dismiss();
                }
            });
        }
        mPlaySpeedDialog.show();
        Window window = mPlaySpeedDialog.getWindow();
        if (window == null) {
            return;
        }
        window.setWindowAnimations(R.style.ActionSheetDialogAnimation);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(null);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.BOTTOM);
        mPlaySpeedDialog.setContentView(mPlaySpeedDialogView);
    }

    private void showCountDownPlayDialog() {
        if (mCountDownPlayDialog == null){
            AlertDialog.Builder countDownPlayBuilder = new AlertDialog.Builder(this);

            mCountDownPlayDialog = countDownPlayBuilder.create();
            mCountDownPlayDialogView = LayoutInflater.from(this).inflate(R.layout.layout_audio_countdown_menu, null, false);
            mCountDownPlayDialogView.findViewById(R.id.btn_count_down_1).setOnClickListener(AudioNewActivity.this);
            mCountDownPlayDialogView.findViewById(R.id.btn_count_down_2).setOnClickListener(AudioNewActivity.this);
            mCountDownPlayDialogView.findViewById(R.id.btn_count_down_3).setOnClickListener(AudioNewActivity.this);
            mCountDownPlayDialogView.findViewById(R.id.btn_count_down_4).setOnClickListener(AudioNewActivity.this);
            mCountDownPlayDialogView.findViewById(R.id.btn_count_down_5).setOnClickListener(AudioNewActivity.this);
            mCountDownPlayDialogView.findViewById(R.id.btn_count_down_6).setOnClickListener(AudioNewActivity.this);
            mCheckBoxes = new ArrayList<>();
            mCheckBoxes.add((CheckBox) mCountDownPlayDialogView.findViewById(R.id.checkbox1));
            mCheckBoxes.add((CheckBox) mCountDownPlayDialogView.findViewById(R.id.checkbox2));
            mCheckBoxes.add((CheckBox) mCountDownPlayDialogView.findViewById(R.id.checkbox3));
            mCheckBoxes.add((CheckBox) mCountDownPlayDialogView.findViewById(R.id.checkbox4));
            mCheckBoxes.add((CheckBox) mCountDownPlayDialogView.findViewById(R.id.checkbox5));
            mCheckBoxes.add((CheckBox) mCountDownPlayDialogView.findViewById(R.id.checkbox6));
            mCountDownTexts = new ArrayList<>();
            mCountDownTexts.add((TextView) mCountDownPlayDialogView.findViewById(R.id.text_count_down_1));
            mCountDownTexts.add((TextView) mCountDownPlayDialogView.findViewById(R.id.text_count_down_2));
            mCountDownTexts.add((TextView) mCountDownPlayDialogView.findViewById(R.id.text_count_down_3));
            mCountDownTexts.add((TextView) mCountDownPlayDialogView.findViewById(R.id.text_count_down_4));
            mCountDownTexts.add((TextView) mCountDownPlayDialogView.findViewById(R.id.text_count_down_5));
            mCountDownTexts.add((TextView) mCountDownPlayDialogView.findViewById(R.id.text_count_down_6));
            mCountDownPlayDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    if (mCountDownPlayDialog != null && mCountDownPlayDialog.isShowing())
                        mCountDownPlayDialog.dismiss();
                }
            });
        }
        updateCountDownCheckBox();
        updateCountDownText();
        mCountDownPlayDialog.show();
        Window window = mCountDownPlayDialog.getWindow();
        if (window == null) {
            return;
        }
        window.setWindowAnimations(R.style.ActionSheetDialogAnimation);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(null);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.BOTTOM);
        mCountDownPlayDialog.setContentView(mCountDownPlayDialogView);
    }

    private void updateCountDownText() {
        if (mCountDownTexts == null)    return;
        for (int i = 0; i < mCountDownTexts.size(); i++) {
            TextView textView = mCountDownTexts.get(i);
            if (textView != null){
                int color = i == MediaPlayerCountDownHelper.INSTANCE.getMAudioSelectedPosition() ?
                        ContextCompat.getColor(this,R.color.high_title_color) :
                        ContextCompat.getColor(this,R.color.main_title_color);
                textView.setTextColor(color);
//                if (0 == i){//文案随选择项不一样变化(后来需求不需要变化)
//                    String text = COUNT_DOWN_STATE_TIME == MediaPlayerCountDownHelper.INSTANCE.getMCurrentState() ?
//                            getString(R.string.audio_count_down_item_1_close) : getString(R.string.audio_count_down_item_1);
//                    textView.setText(text);
//                }
            }
        }
    }

    private void updateCountDownCheckBox() {
        if (mCheckBoxes == null)    return;
        for (int i = 0; i < mCheckBoxes.size(); i++) {
            CheckBox checkBox = mCheckBoxes.get(i);
            if (checkBox != null) {
                checkBox.setClickable(false);
                checkBox.setEnabled(false);
                checkBox.setChecked(i == MediaPlayerCountDownHelper.INSTANCE.getMAudioSelectedPosition());
            }
        }
    }

    private void clickDownload() {
        if(mIsDownload){
            //如果已经下载了，则不做任何操作
            return;
        }
        AudioPlayEntity audioPlayEntity = AudioMediaPlayer.getAudio();
        if(TextUtils.isEmpty(audioPlayEntity.getPlayUrl()) || (audioPlayEntity.getIsTry() == 1 && audioPlayEntity.getHasBuy() == 0)){
            toastCustom(getString(R.string.cannot_download));
        }else{
            if(!NetUtils.NETWORK_TYPE_WIFI.equals(NetUtils.getNetworkType(this))){
                getDialog().setMessageVisibility(View.GONE);
                getDialog().getTitleView().setGravity(Gravity.START);
                getDialog().getTitleView().setPadding(Dp2Px2SpUtil.dp2px(this, 22), 0, Dp2Px2SpUtil.dp2px(this, 22), 0 );
                getDialog().getTitleView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                getDialog().setCancelable(false);
                getDialog().setHideCancelButton(false);
                getDialog().setTitle(getString(R.string.not_wifi_net_download_hint));
                getDialog().setConfirmText(getString(R.string.confirm_title));
                getDialog().setCancelText(getString(R.string.cancel_title));
                getDialog().showDialog(CustomDialog.NOT_WIFI_NET_DOWNLOAD_TAG);
            }else{
                downloadAudio();
            }
        }
    }

    /**
     * 下载音频
     */
    private void downloadAudio(){
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

    @Override
    public void onClickConfirm(View view, int tag) {
        super.onClickConfirm(view, tag);
        if(CustomDialog.NOT_WIFI_NET_DOWNLOAD_TAG == tag){
            downloadAudio();
        }
    }

    private void changePlaySpeed(float speed) {
        if (mPlaySpeedDialog != null && mPlaySpeedDialog.isShowing())
            mPlaySpeedDialog.dismiss();
        boolean isChange = AudioMediaPlayer.changePlayerSpeed(speed);
        if (!isChange) {
            Toast(getString(R.string.speed_play_fail));
            return;
        }
        updateSpeedPlayButtonView(speed);
    }

    private void updateCountDownPlayButtonView(){
        updateCountDownPlayButtonView(true);
    }

    private void updateCountDownPlayButtonView(boolean isDismissDialog) {
        if (isDestroyed() || btnCountDownPlay == null)  return;

        if (isDismissDialog && mCountDownPlayDialog != null && mCountDownPlayDialog.isShowing())
            mCountDownPlayDialog.dismiss();
        String text = getString(R.string.audio_count_down);
        int state = MediaPlayerCountDownHelper.INSTANCE.getMCurrentState();
        switch (state){
            case COUNT_DOWN_STATE_TIME:
                text = MediaPlayerCountDownHelper.INSTANCE.getCountText();
                break;
            case COUNT_DOWN_STATE_CURRENT:
                text = getString(R.string.audio_count_down_current);
                break;
            case COUNT_DOWN_STATE_CLOSE:
            default:
                break;
        }
        btnCountDownPlay.setPadding(0,0,Dp2Px2SpUtil.dp2px(this,COUNT_DOWN_STATE_TIME == state ? 5 : 10),0);
        btnCountDownPlay.setText(text);
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
    public void onEventMainThread(HideAudioPlayListEvent event){
        if (event != null && event.isHide() && audioPlayList != null){
            statusPagerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    audioPlayList.dismissDialog();
//                    findViewById(R.id.btn_play_list).performClick();
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
                audioPlayController.setTotalDuration(AudioMediaPlayer.getDuration());
                setDiskRotateAnimator(true);
                break;
            case AudioPlayEvent.PAUSE:
                audioPlayController.setPlayButtonEnabled(true);
                audioPlayController.setPlayState(false);
                setDiskRotateAnimator(false);
                break;
            case AudioPlayEvent.STOP:
                audioPlayController.setPlayState(false);
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
        AudioMediaPlayer.setCountDownCallBack(null);
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
        }
    }

    private void setButtonEnabled(boolean enabled){
        audioPlayController.setButtonEnabled(enabled);
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
            JumpDetail.jumpColumn(this, playEntity.getProductId(), playEntity.getProductImgUrl(),
                    playEntity.getProductType());
            finish();
            return;
        }
        if (code == 1 && !playEntity.isLocalResource()){
            setPagerState(1);
            getDialog().dismissDialog();
            Toast(getString(R.string.network_error_text));
            return;
        }else if(code == -2){
            setButtonEnabled(false);
        }else{
            getDialog().dismissDialog();
        }
        if(playEntity.getHasBuy() == 0 && code == 0){
            //未购买
            tryAudioText.setVisibility(playEntity.getIsTry() == 0 ? View.GONE : View.VISIBLE);
            commonBuyView.setVisibility(playEntity.isCache() ? View.GONE : View.VISIBLE);
            commonBuyView.setBuyPrice(playEntity.getPrice());
            if (CommonUserInfo.isIsSuperVipAvailable()) { // 超级会员判断
                if (CommonUserInfo.getSuperVipEffective() == 1) {
                    commonBuyView.setVipBtnVisibility(View.VISIBLE);
                } else {
                    commonBuyView.setVipBtnVisibility(View.GONE);
                }
            } else {
                commonBuyView.setVipBtnVisibility(View.GONE);
            }
            setButtonEnabled(playEntity.getIsTry() == 1);
        }else{
            commonBuyView.setVisibility(View.GONE);
            tryAudioText.setVisibility(View.GONE);
            setButtonEnabled(true);
        }
        int top = View.VISIBLE == commonBuyView.getVisibility() ? 45 : 15;
        detailContent.setPadding(0,0,0,Dp2Px2SpUtil.dp2px(this,top));
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
        barTitle.setText(playEntity.getTitle());
        int count = playEntity.getPlayCount();
        if(count > 0){
            playNum.setVisibility(View.VISIBLE);
            playNumStr = String.format(getString(R.string.learn_count), count);
            playNum.setText(String.format(getString(R.string.learn_count), count));
        }else{
            playNumStr = "";
            playNum.setVisibility(View.GONE);
        }
        String imageUrl = playEntity.getImgUrl();
        if(!TextUtils.isEmpty(imageUrl)){
            if("gif".equals(imageUrl.substring(imageUrl.lastIndexOf(".")+1)) || "GIF".equals(imageUrl.substring(imageUrl.lastIndexOf(".")+1))){
                SetImageUriUtil.setRoundAsCircle(audioRing, Uri.parse(imageUrl));
            }else{
                audioRing.setImageURI(Uri.parse(imageUrl));
            }
        }

        audioPlayList.setProductsTitle(playEntity.getProductsTitle());
        setCollectState(playEntity.getHasFavorite() == 1);
        //设置下载状态
        boolean isDownload = DownloadManager.getInstance().isDownload(playEntity.getAppId(), playEntity.getResourceId());
        setDownloadState(isDownload);
    }
    private void setDownloadState(boolean download){
        if(download){
            btnAudioDownload.setImageResource(R.mipmap.audio_alreadydownload);
        }else{
            btnAudioDownload.setImageResource(R.mipmap.audio_download);
        }
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
            earnTitle.setText(R.string.congratulations_winning_scholarship);
            earnWrap.setBackground(getResources().getDrawable(R.mipmap.scholarship_popup_bg));
            earnContent.setText(amount);
            earnContentTail.setVisibility(View.VISIBLE);
            earnTip.setVisibility(View.GONE);
            earnSubmit.setText(getString(R.string.scholarship_earn_btn));
            earnSubmit.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
                @Override
                public void singleClick(View v) {
                    JumpDetail.jumpScholarshipActivity(AudioNewActivity.this);
                    dialog.dismiss();
                }
            });
        } else {
            earnTitle.setText(R.string.almost_got_it);
            earnContentTail.setVisibility(View.GONE);
            earnWrap.setBackgroundColor(getResources().getColor(R.color.white));
            earnSubmit.setText(getString(R.string.scholarship_earn_integral));
            if (CommonUserInfo.isIsSuperVip()) { // 超级会员
                earnContent.setText(String.format(getString(R.string.giving_you_credits), amount));
                earnTip.setVisibility(View.GONE);
            } else {
                earnContent.setText(String.format(getString(R.string.giving_you_credits), amount));
                earnContent.setTextSize(20);
                earnContent.setTextColor(getResources().getColor(R.color.scholarship_btn_press));
                earnTip.setVisibility(View.GONE);
            }
            earnSubmit.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
                @Override
                public void singleClick(View v) {
                    JumpDetail.jumpIntegralActivity(AudioNewActivity.this);
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

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        int[] location = new int[2];
        audioTitle.getLocationOnScreen(location);
        int y = location[1];
        if (toolBarHeight < 1)
            toolBarHeight = y;
        float alpha = (1 - y /(toolBarHeight * 1.0f)) * 255;
        if(alpha > 200){
            alpha = 255;
        }else if(alpha < 0){
            alpha = 0;
        }
        int backgroundColor = Color.argb((int) alpha,255,255,255);
        mStatusBarBlank.setBackgroundColor(backgroundColor);
        mStatusBarBlank.setHeight(StatusBarUtil.getStatusBarHeight(mContext));
        barTitle.setVisibility(alpha > 30 ? View.VISIBLE : View.GONE);
        audioTitleBar.setBackgroundColor(backgroundColor);
        barTitle.setTextColor(Color.argb((int) alpha,0,0,0));
    }

    @Override
    public void onLoadState(int state) {

    }
}
