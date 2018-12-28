package com.xiaoe.shop.wxb.business.video.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.umeng.socialize.UMShareAPI;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.CacheData;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.DownloadResourceTableInfo;
import com.xiaoe.common.entitys.LearningRecord;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.Base64Util;
import com.xiaoe.common.utils.CacheDataUtil;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.NetUtils;
import com.xiaoe.common.utils.NetworkState;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.requests.AddCollectionRequest;
import com.xiaoe.network.requests.ColumnListRequst;
import com.xiaoe.network.requests.DetailRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.RemoveCollectionListRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper;
import com.xiaoe.shop.wxb.business.column.presenter.ColumnPresenter;
import com.xiaoe.shop.wxb.business.video.presenter.VideoPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.web.BrowserActivity;
import com.xiaoe.shop.wxb.events.VideoPlayEvent;
import com.xiaoe.shop.wxb.interfaces.OnClickVideoButtonListener;
import com.xiaoe.shop.wxb.utils.CollectionUtils;
import com.xiaoe.shop.wxb.utils.LogUtils;
import com.xiaoe.shop.wxb.utils.UpdateLearningUtils;
import com.xiaoe.shop.wxb.widget.CommonBuyView;
import com.xiaoe.shop.wxb.widget.CustomDialog;
import com.xiaoe.shop.wxb.widget.StatusPagerView;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

import static com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper.COUNT_DOWN_STATE_CURRENT;

public class VideoActivity extends XiaoeActivity implements View.OnClickListener, OnClickVideoButtonListener,
        VideoPlayControllerView.IPlayNext {
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
//    private SimpleDraweeView videoAdvertise;
    private boolean showCacheData = false;

    List<LoginUser> loginUserList;
    TouristDialog touristDialog;

    boolean hasBuy;
    String realSrcId;
    private String shareUrl = "";
    private ImageView btnBack;
    private boolean mIsDownload;
    private String columnId;
    private TextView bottomTip;
    private String playNumStr;
    private boolean isEnableNext = true;
    private boolean isAutoPlayNext = false;
    private int playNextIndex = -1;
    private String requestNextVideoResId;
    private ColumnPresenter mColumnPresenter;
    final String REQUEST_TAG = "VideoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
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
        mResourceId = mIntent.getStringExtra("resourceId");
        columnId = mIntent.getStringExtra("columnId");
        playNextIndex = mIntent.getIntExtra("videoIndex",-1);
        requestNextVideoResId = mIntent.getStringExtra("requestNextVideoResId");
        initViews();
        initDatas();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (statusPagerView != null)
            statusPagerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initStatusBar();
                }
            },200);
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
//        String videoImageUrl = mIntent.getStringExtra("videoImageUrl");

        playControllerView = (VideoPlayControllerView) findViewById(R.id.video_play_controller);
        playControllerView.setIPlayNext(this);
        playControllerView.setPlayProgressWidgetVisibility(View.GONE);
        playControllerView.setOnClickVideoBackListener(this);
        PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        playControllerView.setWakeLock(wakeLock);
//        playControllerView.setPreviewImage(videoImageUrl);

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
        initWebView(videoContentWebView);
        videoContentWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                BrowserActivity.openUrl(mContext, s, "");
                return true;
            }
        });
        //收藏按钮
        btnCollect = (ImageView) findViewById(R.id.btn_collect);
        btnCollect.setOnClickListener(this);
        //分享按钮
        ImageView btnShare = (ImageView) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        // 广告图片
//        videoAdvertise = (SimpleDraweeView) findViewById(R.id.video_advertise_img);
//        String descImgUrl = "res:///" + R.mipmap.img_text_bg;
//        SetImageUriUtil.setImgURI(videoAdvertise, descImgUrl, Dp2Px2SpUtil.dp2px(this, 375), Dp2Px2SpUtil.dp2px(this, 100));
//        videoAdvertise.setOnClickListener(this);

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.GONE);
        btnBack.setOnClickListener(this);

        mIsDownload = DownloadManager.getInstance().isDownload(CommonUserInfo.getShopId(), mResourceId);
        if(mIsDownload){
            playControllerView.setDownloadState(1);
        }else{
            playControllerView.setDownloadState(0);
        }

        bottomTip = (TextView) findViewById(R.id.bottom_end);
    }

    private void initDatas() {
        String tempResourceId = mResourceId;
        localResource = mIntent.getBooleanExtra("local_resource", false);
        if(!TextUtils.isEmpty(mResourceId)){
            //先查询数据库中是否存在，如果存在则先显示
            String sql = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()
                    +"' and resource_id='"+mResourceId+"' and user_id='"+CommonUserInfo.getLoginUserIdOrAnonymousUserId()+"'";
            List<CacheData> cacheDataList = SQLiteUtil.init(this, new CacheDataUtil())
                    .query(CacheDataUtil.TABLE_NAME, sql, null);
            if(cacheDataList != null && cacheDataList.size() > 0){
                detailRequest(JSONObject.parseObject(cacheDataList.get(0).getContent()), true);
                showCacheData = true;
            }
        }
        videoPresenter.requestDetail(tempResourceId);
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
                LearningRecord lr = new LearningRecord();
                lr.setLrId(realSrcId);
                lr.setLrType(DecorateEntityType.VIDEO);
                lr.setLrTitle(collectTitle);
                lr.setLrImg(collectImgUrl);
                lr.setLrDesc(playNumStr);
                UpdateLearningUtils.saveLr2Local(this, lr);
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
            case R.id.btn_back:
                finish();
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
        MediaPlayerCountDownHelper.INSTANCE.onViewDestroy();
        if (COUNT_DOWN_STATE_CURRENT == MediaPlayerCountDownHelper.INSTANCE.getMCurrentState()){
            MediaPlayerCountDownHelper.INSTANCE.closeCountDownTimer();
        }
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
                if(mIsDownload){
                    return;
                }
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
                    downloadVideo();
                }
            } else {
                touristDialog.showDialog();
            }
        }else{
            onBackPressed();
        }
    }

    @Override
    public void onClickConfirm(View view, int tag) {
        super.onClickConfirm(view, tag);
        if(CustomDialog.NOT_WIFI_NET_DOWNLOAD_TAG == tag){
            downloadVideo();
        }
    }

    /**
     * 下载视频
     */
    private void downloadVideo(){
        boolean isDownload = DownloadManager.getInstance().isDownload(CommonUserInfo.getShopId(), mResourceId);
        mIsDownload = isDownload;
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
        playControllerView.setDownloadState(1);
        toastCustom(getString(R.string.add_download_list));
    }
    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if(activityDestroy){
            return;
        }

        if(entity == null && iRequest instanceof DetailRequest && !showCacheData){
            setPagerState(1);
            return;
        }
        JSONObject jsonObject = (JSONObject) entity;
        if(iRequest instanceof DetailRequest){
            detailRequest(jsonObject, false);
        }else if(iRequest instanceof AddCollectionRequest){
            addCollectionRequest(jsonObject);
        }else if(iRequest instanceof RemoveCollectionListRequest){
            removeCollectionRequest(jsonObject);
        }else if (iRequest instanceof ColumnListRequst
                && REQUEST_TAG.equals(iRequest.getRequestTag())){
            isEnableNext = true;
            if (!success || jsonObject == null){
                toastCustom(getString(R.string.network_error_text));
                return;
            }
            JSONArray data = jsonObject.getJSONArray("data");
            if (data == null || data.size() == 0){
                playNextIndex = -1;
                if (!isAutoPlayNext)
                    toastCustom(getString(R.string.already_was_last_video));
                return;
            }
            if (playControllerView != null) {
                playControllerView.retSet();
            }
            mResourceId = ((JSONObject)data.get(0)).getString("resource_id");
            initDatas();
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

    private void detailRequest(JSONObject jsonObject, boolean cache) {
        getDialog().dismissDialog();
        try{
            JSONObject data = jsonObject.getJSONObject("data");
            int code = jsonObject.getIntValue("code");
            if(code != NetworkCodes.CODE_SUCCEED || data == null ){
                if(code == NetworkCodes.CODE_GOODS_DELETE){
                    playControllerView.setVisibility(View.GONE);
                    setPagerState(NetworkCodes.CODE_GOODS_DELETE);
                }else{
                    setPagerState(1);
                }
                return;
            }
            //已购或者免费
            if(data.getBoolean("available")){
                setContent(data, true, cache);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, Dp2Px2SpUtil.dp2px(this, 20));
                bottomTip.setLayoutParams(layoutParams);
            }else{
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, Dp2Px2SpUtil.dp2px(this, 64));
                bottomTip.setLayoutParams(layoutParams);
                //未购
                JSONObject resourceInfo = data.getJSONObject("resource_info");
                int hasFavorite = ((JSONObject) data.get("favorites_info")).getInteger("is_favorite");
                setCollectState(hasFavorite == 1);
                if(resourceInfo.getIntValue("is_related") == 1){
                    //1-免费,2-单卖，3-非单卖
                    //非单卖需要跳转到所属专栏，如果所属专栏多个，只跳转第一个
                    //如果是仅关联售卖，则把缓存中的数据清除
                    SQLiteUtil sqLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new CacheDataUtil());
                    sqLiteUtil.delete(CacheDataUtil.TABLE_NAME, "app_id=? and resource_id=? and user_id=?", new String[]{Constants.getAppId(), mResourceId, CommonUserInfo.getLoginUserIdOrAnonymousUserId()});
                    JSONArray productList = data.getJSONObject("product_info").getJSONArray("product_list");
                    if (productList.size() == 0) {
                        setPagerState(3004);
                        return;
                    }
                    JSONObject product = productList.getJSONObject(0);
                    int productType = product.getIntValue("product_type");
                    String productId = product.getString("id");
                    String productImgUrl = product.getString("img_url");
                    if (cache) {
                        return;
                    }
                    //1-专栏, 2-会员, 3-大专栏
                    if(productType == 3){
                        JumpDetail.jumpColumn(this, productId, productImgUrl, 8);
                    }else if(productType == 2){
                        JumpDetail.jumpColumn(this, productId, productImgUrl, 5);
                    }else{
                        JumpDetail.jumpColumn(this, productId, productImgUrl, 6);
                    }
                    finish();
                    return;
                }else{
                    if(resourceInfo.getIntValue("sale_status") == 1){
                        playControllerView.setVisibility(View.GONE);
                        setPagerState(2);
                    }else{
                        setContent(resourceInfo, false, cache);
                    }
                }
            }
            JSONObject shareInfo = data.getJSONObject("share_info");
            if(shareInfo != null && shareInfo.getJSONObject("wx") != null){
                shareUrl = shareInfo.getJSONObject("wx").getString("share_url");
            }
            if(!TextUtils.isEmpty(columnId) && !TextUtils.isEmpty(shareUrl)){
                JSONObject shareJSON = Base64Util.base64ToJSON(shareUrl.substring(shareUrl.lastIndexOf("/")+1));
                shareJSON.put("product_id", columnId);
                shareUrl = shareUrl.substring(0, shareUrl.lastIndexOf("/") + 1) + Base64Util.jsonToBase64(shareJSON);
            }
        }catch (Exception e){
//            e.printStackTrace();
        }
    }

    private void setContent(JSONObject data, boolean available, boolean cache) {
        hasBuy = available;
        mResourceId = data.getString("resource_id");
        String title = data.getString("title");
        videoTitle.setText(title);
        int count = data.getIntValue("view_count");
        if(count > 0){
            playCount.setVisibility(View.VISIBLE);
            playNumStr = String.format(getString(R.string.learn_count), count);
            playCount.setText(playNumStr);
        }else{
            playNumStr = "";
            playCount.setVisibility(View.GONE);
        }
        collectTitle = title;
        collectImgUrl = data.getString("img_url");
        collectImgUrlCompressed = data.getString("img_url_compressed");
        if (hasBuy) { // 已购可以这么取
            int hasFavorite = ((JSONObject) data.get("favorites_info")).getInteger("is_favorite");
            setCollectState(hasFavorite == 1);
        }
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
        boolean valid = resourceState(data, available);
        if(available && valid){
            collectPrice = "";
            buyView.setVisibility(View.GONE);
            if(!showCacheData){
                String detail = data.getString("content");
                setContentDetail(detail);
            }
            mVideoUrl = data.getString("video_mp4");
            if(!cache){
                if(TextUtils.isEmpty(mLocalVideoUrl)){
                    playControllerView.setPlayUrl(mVideoUrl);
                }else{
                    playControllerView.setPlayUrl(mLocalVideoUrl);
                }
            }

            collectImgUrl = data.getString("img_url");
            realSrcId = data.getString("resource_id");
        }else{
            if(!TextUtils.isEmpty(mLocalVideoUrl)){
                playControllerView.setPlayUrl(mLocalVideoUrl);
            }
            buyView.setVisibility(cache ? View.GONE : View.VISIBLE);
            // 未购
            if (CommonUserInfo.isIsSuperVipAvailable()) { // 超级会员的判断
                if (CommonUserInfo.getSuperVipEffective() == 1) { // 全店免费
                    buyView.setVipBtnVisibility(View.VISIBLE);
                } else {
                    buyView.setVipBtnVisibility(View.GONE);
                }
            } else {
                buyView.setVipBtnVisibility(View.GONE);
            }
            int price = data.getIntValue("price");
            resPrice = price;
            collectPrice = ""+price;
            buyView.setBuyPrice(price);
            if(!showCacheData){
                String detail = data.getString("preview_content");
                setContentDetail(detail);
            }
        }
        if(TextUtils.isEmpty(data.getString("video_slice_img"))){
            playControllerView.setPreviewImage(data.getString("img_url"));
        }else {
            playControllerView.setPreviewImage(data.getString("video_slice_img"));
        }

    }

    /**
     * 课程状态
     * @param data
     */
    private boolean resourceState(JSONObject data, boolean available){
        //是否免费0：否，1：是
        int isFree = data.getIntValue("is_free");
        //0-正常, 1-隐藏, 2-删除
        int detailState = data.getIntValue("state");
        //0-上架,1-下架
        int saleStatus = data.getIntValue("sale_status");
        //是否停售 0:否，1：是
        int isStopSell = data.getIntValue("is_stop_sell");
        //离待上线时间，如有则是待上架
        int timeLeft = data.getIntValue("time_left");
        if(available && detailState != 2){
            //删除状态优秀级最高，available=true是除了删除状态显示删除页面外，其他的均可查看详情
            setPagerState(0);
            return true;
        }

        if(saleStatus == 1 || detailState == 1){
            setPagerState(2);
            return false;
        }else if(isStopSell == 1){
            setPagerState(3);
            return false;
        }else if(timeLeft > 0){
            setPagerState(4);
            return false;
        }else if(detailState == 2){
            setPagerState(NetworkCodes.CODE_GOODS_DELETE);
            return false;
        }else {
            setPagerState(0);
            return true;
        }
    }

    private void setContentDetail(String detail){
        videoContentWebView.loadDataWithBaseURL(null, NetworkState.getNewContent(detail), "text/html", "UFT-8", null);
    }

    /**
     *
     * @param code 0-正常的,1-请求失败,2-课程下架,3-停售，4-待上架，3004-课程删除
     */
    private void setPagerState(int code) {
        if(code == 0){
            videoContentDetail.setVisibility(View.VISIBLE);
            statusPagerView.setVisibility(View.GONE);
            statusPagerView.setLoadingState(View.GONE);
            statusPagerView.setHintStateVisibility(View.GONE);
        }else if(code == 1){
            videoContentDetail.setVisibility(View.GONE);
            statusPagerView.setPagerState(StatusPagerView.FAIL, getString(R.string.request_fail), StatusPagerView.DETAIL_NONE);
        }else if(code == 2){
            btnBack.setVisibility(View.VISIBLE);
            videoContentDetail.setVisibility(View.GONE);
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_sold_out), R.mipmap.course_off);
        }else if(code == 3){
            btnBack.setVisibility(View.VISIBLE);
            videoContentDetail.setVisibility(View.GONE);
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_sale_stop), R.mipmap.course_off);
        }else if(code == 4){
            btnBack.setVisibility(View.VISIBLE);
            videoContentDetail.setVisibility(View.GONE);
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_stay_putaway), R.mipmap.course_off);
        }else if(code == NetworkCodes.CODE_GOODS_DELETE){
            btnBack.setVisibility(View.VISIBLE);
            videoContentDetail.setVisibility(View.GONE);
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_delete), R.mipmap.course_off);
        }
        if(code != 0 && localResource){
            //如果存在本地视频则播放本地是否
            DownloadResourceTableInfo download = DownloadManager.getInstance().getDownloadFinish(CommonUserInfo.getShopId(), mResourceId);
            if(download != null){
                File file = new File(download.getLocalFilePath());
                if(file.exists()){
                    mLocalVideoUrl = download.getLocalFilePath();
                    playControllerView.setPlayUrl(mLocalVideoUrl);
                }
            }
        }
    }

    @Override
    public void onNext(boolean isAuto) {
        if (isEnableNext){
            setHasToast(true);
            isAutoPlayNext = isAuto;
            LogUtils.d("onNext = " + playNextIndex);
            if (playNextIndex < 1 || TextUtils.isEmpty(requestNextVideoResId)){
                toastCustom(getString(R.string.already_was_last_video));
                return;
            }

            if (mColumnPresenter == null)
                mColumnPresenter = new ColumnPresenter(this);
            mColumnPresenter.requestColumnList(requestNextVideoResId,"3",++playNextIndex,
                    1,false,REQUEST_TAG);
            isEnableNext = false;
        }
    }
}
