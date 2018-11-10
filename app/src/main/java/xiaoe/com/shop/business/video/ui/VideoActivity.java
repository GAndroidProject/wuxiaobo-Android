package xiaoe.com.shop.business.video.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebView;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import java.util.List;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.common.entitys.ColumnSecondDirectoryEntity;
import xiaoe.com.common.entitys.LoginUser;
import xiaoe.com.common.entitys.DownloadResourceTableInfo;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.common.utils.NetworkState;
import xiaoe.com.common.utils.SharedPreferencesUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.downloadUtil.DownloadManager;
import xiaoe.com.network.requests.AddCollectionRequest;
import xiaoe.com.network.requests.DetailRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.RemoveCollectionListRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;
import xiaoe.com.shop.business.video.presenter.VideoPresenter;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.events.VideoPlayEvent;
import xiaoe.com.shop.interfaces.OnClickVideoButtonListener;
import xiaoe.com.shop.utils.CollectionUtils;
import xiaoe.com.shop.utils.NumberFormat;
import xiaoe.com.shop.widget.CommonBuyView;
import xiaoe.com.shop.widget.StatusPagerView;

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

    List<LoginUser> loginUserList;

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
                    JumpDetail.jumpPay(this, mResourceId, 3, collectImgUrl, collectTitle, resPrice);
                } else {
                    Toast("请先登录呦");
                }
                break;
            case R.id.buy_vip:
                if (loginUserList.size() == 1) {
                    JumpDetail.jumpSuperVip(this);
                } else {
                    Toast("请先登录呦");
                }
            case R.id.btn_collect:
                if (loginUserList.size() == 1) {
                    collect();
                } else {
                    Toast("请先登录呦");
                }
                break;
            case R.id.btn_share:
                umShare("hello");
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
                Toast("请先登录呦");
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
//            toastCustom(getResources().getString(R.string.cancel_collect_succeed));
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
//            toastCustom(getResources().getString(R.string.collect_succeed));
            setCollectState(true);
        }else{
            toastCustom(getResources().getString(R.string.collect_fail));
        }
    }

    private void detailRequest(JSONObject jsonObject) {
        getDialog().dismissDialog();
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
    }

    private void setContent(JSONObject data, boolean available) {
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
        }else{
            if(!TextUtils.isEmpty(mLocalVideoUrl)){
                playControllerView.setPlayUrl(mLocalVideoUrl);
            }
            buyView.setVisibility(View.VISIBLE);
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
