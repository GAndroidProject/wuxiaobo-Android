package com.xiaoe.shop.wxb.business.video.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.umeng.socialize.UMShareAPI;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.entitys.DownloadResourceTableInfo;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.NetworkState;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.requests.AddCollectionRequest;
import com.xiaoe.network.requests.DetailRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.RemoveCollectionListRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.video.presenter.VideoPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.VideoPlayEvent;
import com.xiaoe.shop.wxb.interfaces.OnClickVideoButtonListener;
import com.xiaoe.shop.wxb.utils.ActivityCollector;
import com.xiaoe.shop.wxb.utils.CollectionUtils;
import com.xiaoe.shop.wxb.utils.NumberFormat;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;
import com.xiaoe.shop.wxb.utils.UpdateLearningUtils;
import com.xiaoe.shop.wxb.widget.CommonBuyView;
import com.xiaoe.shop.wxb.widget.StatusPagerView;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

public class VideoActivity extends XiaoeActivity implements View.OnClickListener, OnClickVideoButtonListener {
    private static final String TAG = "VideoActivity";
    private TextView playCount;
    private VideoPlayControllerView playControllerView;
    private ScrollView videoContentDetail;
    private CommonBuyView buyView;
    private StatusPagerView statusPagerView;
    private VideoPresenter videoPresenter;
    private WebView videoContentWebView;
    private TextView videoTitle;
    private Intent mIntent;
    private String mResourceId;
    private ImageView btnCollect;
    private boolean hasCollect = false;//是否收藏
    private CollectionUtils collectionUtils;
    private String collectTitle = "";
    private String collectImgUrl;
    private String collectImgUrlCompressed;
    private String collectPrice = "";
    private int resPrice = 0;
    private String mVideoUrl;
    private String mLocalVideoUrl;
    private boolean localResource = false;
    private SimpleDraweeView videoAdvertise;

    List<LoginUser> loginUserList;
    TouristDialog touristDialog;

    boolean hasBuy;
    String realSrcId;
    private String shareUrl = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }
        //如果音频播放中则停止音频播放
        if(AudioMediaPlayer.isPlaying()){
            AudioMediaPlayer.play();
        }
        setContentView(R.layout.activity_video);
        EventBus.getDefault().register(this);
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
                    JumpDetail.jumpLogin(VideoActivity.this, true);
                }
            });
        }

        videoPresenter = new VideoPresenter(this);
        collectionUtils = new CollectionUtils(this);
        mIntent = getIntent();
        initViews();
        initDatas();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int code = getWXPayCode(true);
        if(code == 0){
            getDialog().showLoadDialog(false);
            videoPresenter.requestDetail(mResourceId);
        }
        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
    }

    private void initViews() {
        String videoImageUrl = mIntent.getStringExtra("videoImageUrl");

        playControllerView = (VideoPlayControllerView) findViewById(R.id.video_play_controller);
        playControllerView.setPlayProgressWidgetVisibility(View.GONE);
        playControllerView.setOnClickVideoBackListener(this);
        playControllerView.setPreviewImage(videoImageUrl);

        videoContentDetail = (ScrollView) findViewById(R.id.video_content_detail);
        videoContentDetail.setVisibility(View.GONE);

        videoTitle = (TextView) findViewById(R.id.video_title);

        playCount = (TextView) findViewById(R.id.play_num);
        buyView = (CommonBuyView) findViewById(R.id.detail_buy);
        buyView.setVisibility(View.GONE);
        buyView.setOnBuyBtnClickListener(this);
        buyView.setOnVipBtnClickListener(this);
        statusPagerView = (StatusPagerView) findViewById(R.id.video_state_pager);
        statusPagerView.setVisibility(View.VISIBLE);
        statusPagerView.setLoadingState(View.VISIBLE);
        statusPagerView.setHintStateVisibility(View.GONE);
        //webView显示图文
        videoContentWebView = (WebView) findViewById(R.id.video_web_view);
        //收藏按钮
        btnCollect = (ImageView) findViewById(R.id.btn_collect);
        btnCollect.setOnClickListener(this);
        //分享按钮
        ImageView btnShare = (ImageView) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        // 广告图片
        videoAdvertise = (SimpleDraweeView) findViewById(R.id.video_advertise_img);
        String descImgUrl = "res:///" + R.mipmap.img_text_bg;
        SetImageUriUtil.setImgURI(videoAdvertise, descImgUrl, Dp2Px2SpUtil.dp2px(this, 375), Dp2Px2SpUtil.dp2px(this, 100));
        videoAdvertise.setOnClickListener(this);
    }

    private void initDatas() {
        mResourceId = mIntent.getStringExtra("resourceId");
        localResource = mIntent.getBooleanExtra("local_resource", false);
        videoPresenter.requestDetail(mResourceId);
    }
    @Override
    public void onBackPressed() {
        if(playControllerView.isFullScreen()){
            playControllerView.setFullScreen(false);
            setPlayScreen(VideoPlayConstant.VIDEO_LITTLE_SCREEN);
        }else{
            if (hasBuy) {
                // 上报学习进度(买了才上报)
                UpdateLearningUtils updateLearningUtils = new UpdateLearningUtils(this);
                updateLearningUtils.updateLearningProgress(realSrcId, 3, 10);
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buy_course:
                if (loginUserList.size() == 1) {
                    JumpDetail.jumpPay(this, mResourceId, 3, collectImgUrl, collectTitle, resPrice, null);
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
            case R.id.btn_collect:
                if (loginUserList.size() == 1) {
                    collect();
                } else {
                    touristDialog.showDialog();
                }
                break;
            case R.id.btn_share:
                umShare(collectTitle, TextUtils.isEmpty(collectImgUrlCompressed) ? collectImgUrl : collectImgUrlCompressed, shareUrl, "");
                break;
            case R.id.video_advertise_img:
                ActivityCollector.finishAll();
                if (loginUserList.size() == 1) {
                    JumpDetail.jumpMainScholarship(this, true, true);
                } else {
                    JumpDetail.jumpMainScholarship(this, false, true);
                }
                break;
            default:
                break;
        }
    }

    private void collect() {
        hasCollect = !hasCollect;
        if(hasCollect){
            //添加收藏
            JSONObject collectionContent = new JSONObject();
            collectionContent.put("title",collectTitle);
            collectionContent.put("author","");
            collectionContent.put("img_url",collectImgUrl);
            collectionContent.put("img_url_compressed",collectImgUrlCompressed);
            collectionContent.put("price",collectPrice);
            collectionUtils.requestAddCollection(mResourceId, "3", collectionContent);
        }else {
            //取消收藏
            collectionUtils.requestRemoveCollection(mResourceId, "3");
        }
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


    @Subscribe
    public void onEventMainThread(VideoPlayEvent event) {
        int state = event.getState();
        switch (state){
            case VideoPlayConstant.VIDEO_FULL_SCREEN:
                setPlayScreen(VideoPlayConstant.VIDEO_FULL_SCREEN);
                break;
            case VideoPlayConstant.VIDEO_LITTLE_SCREEN:
                setPlayScreen(VideoPlayConstant.VIDEO_LITTLE_SCREEN);
                break;
            case VideoPlayConstant.VIDEO_STATE_PLAY:
                playControllerView.setPlayProgressWidgetVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void setPlayScreen(int videoLittleScreen) {
        if(videoLittleScreen == VideoPlayConstant.VIDEO_FULL_SCREEN){
            videoContentDetail.setVisibility(View.GONE);

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


            RelativeLayout.LayoutParams playLayoutParam = (RelativeLayout.LayoutParams) playControllerView.getLayoutParams();
            playLayoutParam.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            playLayoutParam.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            playControllerView.setLayoutParams(playLayoutParam);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            videoContentDetail.setVisibility(View.VISIBLE);

            getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams playLayoutParam = (RelativeLayout.LayoutParams) playControllerView.getLayoutParams();
            playLayoutParam.height = Dp2Px2SpUtil.dp2px(this, 210);
            playControllerView.setLayoutParams(playLayoutParam);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        playControllerView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playControllerView.release();
        EventBus.getDefault().unregister(this);
        UMShareAPI.get(this).release();
    }

    @Override
    public void onVideoButton(View view, int type) {
        if(type == VideoPlayConstant.VIDEO_FULL_SCREEN){

        }else if(type == VideoPlayConstant.VIDEO_STATE_DOWNLOAD){
            if (loginUserList.size() == 1) {
                if(TextUtils.isEmpty(mVideoUrl)){
                    toastCustom(getString(R.string.cannot_download));
                    return;
                }
                boolean isDownload = DownloadManager.getInstance().isDownload(CommonUserInfo.getShopId(), mResourceId);
                if(!isDownload){
                    ColumnSecondDirectoryEntity download = new ColumnSecondDirectoryEntity();
                    download.setApp_id(CommonUserInfo.getShopId());
                    download.setResource_id(mResourceId);
                    download.setTitle(collectTitle);
                    download.setResource_type(3);
                    download.setImg_url(collectImgUrl);
                    download.setVideo_url(mVideoUrl);
                    DownloadManager.getInstance().addDownload(null, null, download);
                }
                toastCustom(getString(R.string.add_download_list));
            } else {
                touristDialog.showDialog();
            }
        }else{
            onBackPressed();
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if(activityDestroy){
            return;
        }

        if(entity == null || !success){
            setPagerState(true);
            return;
        }
        JSONObject jsonObject = (JSONObject) entity;
        if(iRequest instanceof DetailRequest){
            detailRequest(jsonObject);
        }else if(iRequest instanceof AddCollectionRequest){
            addCollectionRequest(jsonObject);
        }else if(iRequest instanceof RemoveCollectionListRequest){
            removeCollectionRequest(jsonObject);
        }

    }

    /**
     * 取消收藏
     * @param jsonObject
     */
    private void removeCollectionRequest(JSONObject jsonObject) {
        if(jsonObject.getIntValue("code") == NetworkCodes.CODE_SUCCEED ){
            toastCustom(getString(R.string.cancel_collect_succeed));
            setCollectState(false);
        }else{
            toastCustom(getResources().getString(R.string.cancel_collect_fail));
        }
    }

    /**
     * 添加收藏
     * @param jsonObject
     */
    private void addCollectionRequest(JSONObject jsonObject) {
        if(jsonObject.getIntValue("code") == NetworkCodes.CODE_SUCCEED ){
            toastCustom(getString(R.string.collect_succeed));
            setCollectState(true);
        }else{
            toastCustom(getResources().getString(R.string.collect_fail));
        }
    }

    private void detailRequest(JSONObject jsonObject) {
        getDialog().dismissDialog();
        try{
            JSONObject data = jsonObject.getJSONObject("data");
            if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED || data == null ){
                setPagerState(true);
                return;
            }
            //已购或者免费
            if(data.getBoolean("available")){
                setContent(data, true);
            }else{
                //未购
                setContent(data.getJSONObject("resource_info"), false);
            }
            JSONObject shareInfo = data.getJSONObject("share_info");
            if(shareInfo != null && shareInfo.getJSONObject("wx") != null){
                shareUrl = shareInfo.getJSONObject("wx").getString("share_url");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setContent(JSONObject data, boolean available) {
        hasBuy = available;
        String title = data.getString("title");
        videoTitle.setText(title);
        int count = data.getIntValue("view_count");
        if(count > 0){
            playCount.setVisibility(View.VISIBLE);
            playCount.setText(NumberFormat.viewCountToString(count)+"次播放");
        }else{
            playCount.setVisibility(View.GONE);
        }
        collectTitle = title;
        collectImgUrl = data.getString("img_url");
        collectImgUrlCompressed = data.getString("img_url_compressed");
        setCollectState(data.getIntValue("has_favorite") == 1);
        if(localResource){
            //如果存在本地视频则播放本地是否
            DownloadResourceTableInfo download = DownloadManager.getInstance().getDownloadFinish(CommonUserInfo.getShopId(), mResourceId);
            if(download != null){
                File file = new File(download.getLocalFilePath());
                if(file.exists()){
                   mLocalVideoUrl = download.getLocalFilePath();
                }
            }
        }
        if(available){
            collectPrice = "";
            buyView.setVisibility(View.GONE);
            String detail = data.getString("content");
            setContentDetail(detail);
            mVideoUrl = data.getString("video_mp4");
            if(TextUtils.isEmpty(mLocalVideoUrl)){
                playControllerView.setPlayUrl(mVideoUrl);
            }else{
                playControllerView.setPlayUrl(mLocalVideoUrl);
            }

            setPagerState(false);
            collectImgUrl = data.getString("img_url");
            realSrcId = data.getString("resource_id");
        }else{
            if(!TextUtils.isEmpty(mLocalVideoUrl)){
                playControllerView.setPlayUrl(mLocalVideoUrl);
            }
            buyView.setVisibility(View.VISIBLE);
            if (CommonUserInfo.isIsSuperVipAvailable() && !CommonUserInfo.isIsSuperVip()) { // 超级会员的判断
                buyView.setVipBtnVisibility(View.VISIBLE);
            } else {
                buyView.setVipBtnVisibility(View.GONE);
            }
            int price = data.getIntValue("price");
            resPrice = price;
            collectPrice = ""+price;
            buyView.setBuyPrice(price);
            String detail = data.getString("content");
            setContentDetail(detail);
            setPagerState(false);
        }

    }


    private void setContentDetail(String detail){
        videoContentWebView.loadDataWithBaseURL(null, NetworkState.getNewContent(detail), "text/html", "UFT-8", null);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    private void setPagerState(boolean error) {
        if(error){
            videoContentDetail.setVisibility(View.GONE);
            statusPagerView.setVisibility(View.VISIBLE);
            statusPagerView.setLoadingState(View.GONE);
            statusPagerView.setStateImage(StatusPagerView.DETAIL_NONE);
            statusPagerView.setHintStateVisibility(View.VISIBLE);
        }else{
            videoContentDetail.setVisibility(View.VISIBLE);
            statusPagerView.setVisibility(View.GONE);
            statusPagerView.setLoadingState(View.GONE);
            statusPagerView.setHintStateVisibility(View.GONE);
        }
    }
}
