package xiaoe.com.shop.business.video.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.common.utils.NetworkState;
import xiaoe.com.common.utils.SharedPreferencesUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.ContentRequest;
import xiaoe.com.network.requests.DetailRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.PayOrderRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;
import xiaoe.com.shop.business.video.presenter.VideoPresenter;
import xiaoe.com.shop.events.VideoPlayEvent;
import xiaoe.com.shop.interfaces.OnClickVideoBackListener;
import xiaoe.com.shop.utils.NumberFormat;
import xiaoe.com.shop.widget.CommonBuyView;
import xiaoe.com.shop.widget.StatusPagerView;

public class VideoActivity extends XiaoeActivity implements View.OnClickListener, OnClickVideoBackListener {
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
    private boolean paying = false;

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
        videoPresenter = new VideoPresenter(this);
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
        if(paying){
            paying = false;
            int code = getWXPayCode(true);
            if(code == 0){
                videoPresenter.requestDetail(mResourceId);
            }
            SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
        }
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

        videoContentWebView = (WebView) findViewById(R.id.video_web_view);
    }

    private void initDatas() {
        mResourceId = mIntent.getStringExtra("resourceId");
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buy_course:
                buyResource();
                break;
            case R.id.buy_vip:
                toastCustom("购买超级会员");
            default:
                break;
        }
    }

    private void buyResource() {
        paying = true;
        getDialog().showLoadDialog(false);
        payOrder(mResourceId, 3, 2);
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
    public void onBack(View view, int type) {
        if(type == VideoPlayConstant.VIDEO_FULL_SCREEN){

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
            if(iRequest instanceof PayOrderRequest){
                getDialog().dismissDialog();
                getDialog().setHintMessage(getResources().getString(R.string.pay_info_error));
                getDialog().showDialog(-1);
            }
            return;
        }
        JSONObject jsonObject = (JSONObject) entity;
        if(iRequest instanceof ContentRequest){
            contentRequest(jsonObject);
        }else if(iRequest instanceof DetailRequest){
            detailRequest(jsonObject);
        }else if(iRequest instanceof PayOrderRequest){
            payOrderRequest(jsonObject);
        }

    }

    private void payOrderRequest(JSONObject jsonObject) {

        JSONObject data = jsonObject.getJSONObject("data");
        if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED || data == null ){
            getDialog().dismissDialog();
            getDialog().setHintMessage(getResources().getString(R.string.pay_info_error));
            getDialog().showDialog(-1);
            return;
        }


        JSONObject payConfig = data.getJSONObject("payConfig");
        pullWXPay(payConfig.getString("appid"), payConfig.getString("partnerid"), payConfig.getString("prepayid"),
                payConfig.getString("noncestr"), payConfig.getString("timestamp"), payConfig.getString("package"), payConfig.getString("sign"));
    }

    private void detailRequest(JSONObject jsonObject) {
        getDialog().dismissDialog();
        JSONObject data = jsonObject.getJSONObject("data");
        if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED || data == null ){
            setPagerState(true);
            return;
        }
        JSONObject resourceInfo = data.getJSONObject("resource_info");
        videoTitle.setText(resourceInfo.getString("title"));
        int count = resourceInfo.getIntValue("audio_play_count");
        if(count > 0){
            playCount.setVisibility(View.VISIBLE);
            playCount.setText(NumberFormat.viewCountToString(count)+"次播放");
        }else{
            playCount.setVisibility(View.GONE);
        }
        if(resourceInfo.getIntValue("has_buy") == 1){
            buyView.setVisibility(View.GONE);
            videoPresenter.requestContent(mResourceId);
        }else{
            buyView.setVisibility(View.VISIBLE);
            int price = resourceInfo.getIntValue("price");
            buyView.setBuyPrice(price);
            String detail = resourceInfo.getString("content");
            setContentDetail(detail);
            setPagerState(false);
        }
    }

    private void contentRequest(JSONObject jsonObject) {
        if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED){
            setPagerState(true);
            return;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        String detail = data.getString("content");
        setContentDetail(detail);
        playControllerView.setPlayUrl(data.getString("video_mp4"));
        setPagerState(false);
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
