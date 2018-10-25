package xiaoe.com.shop.business.audio.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import xiaoe.com.common.entitys.AudioPlayEntity;
import xiaoe.com.common.entitys.AudioPlayTable;
import xiaoe.com.common.utils.DateFormat;
import xiaoe.com.common.utils.NetworkState;
import xiaoe.com.common.utils.SQLiteUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.ContentRequest;
import xiaoe.com.network.requests.DetailRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.anim.ViewAnim;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;
import xiaoe.com.shop.business.audio.presenter.AudioPlayUtil;
import xiaoe.com.shop.business.audio.presenter.AudioPresenter;
import xiaoe.com.shop.business.audio.presenter.AudioSQLiteUtil;
import xiaoe.com.shop.business.comment.ui.CommentActivity;
import xiaoe.com.shop.events.AudioPlayEvent;
import xiaoe.com.shop.interfaces.OnClickMoreMenuListener;
import xiaoe.com.shop.widget.CommonBuyView;
import xiaoe.com.shop.widget.ContentMenuLayout;
import xiaoe.com.shop.widget.StatusPagerView;

public class AudioActivity extends XiaoeActivity implements View.OnClickListener, OnClickMoreMenuListener {
    private static final String TAG = "AudioActivity";
    private final int MSG_VIEW_STATE = 10001;
    private SimpleDraweeView audioBG;
    private SimpleDraweeView audioRing;
    private ViewAnim mViewAnim;
    private String appId = "";
    private String resourceId = "";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_VIEW_STATE:
                    initViewState(View.VISIBLE);
                    break;
                default:break;
            }
        }
    };


    private RelativeLayout btnPageClose;
    private TextView audioTitle;
    private TextView playNum;
    private TextView btnSpeedPlay;
    private AudioPlayControllerView audioPlayController;
    private ObjectAnimator diskRotate;
    private AudioHoverControllerLayout audioHoverPlayController;
    private ContentMenuLayout contentMenuLayout;
    private AudioPresenter audioPresenter;
    private WebView detailContent;
    private CommonBuyView commonBuyView;
    private StatusPagerView statusPagerView;
    private AudioDetailsSwitchLayout pagerContentDetailLayout;
    private boolean singleAudio = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }
        EventBus.getDefault().register(this);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_audio);
        initViews();
        SQLiteUtil.init(this, new AudioSQLiteUtil());
        boolean tableExist = SQLiteUtil.tabIsExist(AudioPlayTable.TABLE_NAME);
        if(!tableExist){
            SQLiteUtil.execSQL(AudioPlayTable.CREATE_TABLE_SQL);
        }
        audioPresenter = new AudioPresenter(this);
        initDatas();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(AudioMediaPlayer.isPlaying()){
            setDiskRotateAnimator(true);
        }
    }

    private void initViews() {
        pagerContentDetailLayout = (AudioDetailsSwitchLayout) findViewById(R.id.pager_content_detail);

        mViewAnim = new ViewAnim();
        audioBG = (SimpleDraweeView) findViewById(R.id.audio_bg);
        audioBG.setImageURI("res:///"+R.mipmap.detail_bg_wave);
        audioRing = (SimpleDraweeView) findViewById(R.id.audio_ring);
        audioRing.setImageURI("res:///"+R.mipmap.detail_disk);

        //页面关闭按钮
        btnPageClose = (RelativeLayout) findViewById(R.id.audio_page_close_btn);
        btnPageClose.setOnClickListener(this);
        //标题
        audioTitle = (TextView) findViewById(R.id.audio_title);
        //播放次数
        playNum = (TextView) findViewById(R.id.play_num);

        //倍速按钮
        btnSpeedPlay = (TextView) findViewById(R.id.audio_speed_play);
        //音频播放控制器
        audioPlayController = (AudioPlayControllerView) findViewById(R.id.audio_play_controller);
        //悬浮播放控制器
        audioHoverPlayController = (AudioHoverControllerLayout) findViewById(R.id.audio_hover_controller);
        audioHoverPlayController.setOnMenuListener(this);
        //菜单栏
        contentMenuLayout = (ContentMenuLayout) findViewById(R.id.content_menu_layout);
        contentMenuLayout.setButtonClickListener(this);

        ImageView btnAudioComment = (ImageView) findViewById(R.id.btn_audio_comment);
        btnAudioComment.setOnClickListener(this);
        //图文内容详细显示
        detailContent = (WebView) findViewById(R.id.audio_detail_content);
        //底部购买按钮
        commonBuyView = (CommonBuyView) findViewById(R.id.common_buy_view);
        //状态页面
        statusPagerView = (StatusPagerView) findViewById(R.id.state_pager_view);
        statusPagerView.setVisibility(View.GONE);
        setButtonEnabled(false);
    }
    private void initDatas() {
        audioPresenter.requestDetail();
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
        super.onBackPressed();
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
        JSONObject jsonObject = (JSONObject) entity;
        if(entity == null || !success){
            setPagerState(true);
            return;
        }
        if(iRequest instanceof ContentRequest){
            audioContentRequest(jsonObject);
        }else if(iRequest instanceof DetailRequest){
            audioDetailRequest(jsonObject);
        }
    }

    /**
     * 购买前的详情
     * @param jsonObject
     */
    private void audioDetailRequest(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED || data == null ){
            return;
        }
        JSONObject resourceInfo = data.getJSONObject("resource_info");
        if(resourceInfo.getIntValue("has_buy") == 1){
            commonBuyView.setVisibility(View.GONE);
            audioPresenter.requestContent();
        }else{
            commonBuyView.setVisibility(View.VISIBLE);
            setButtonEnabled(false);
            String detail = resourceInfo.getString("content");
            setContentDetail(detail);
        }
    }

    /**
     * 购买后的内容
     * @param jsonObject
     */
    private void audioContentRequest(JSONObject jsonObject) {
        if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED){
            setPagerState(true);
            return;
        }
        setButtonEnabled(true);
        JSONObject data = jsonObject.getJSONObject("data");
        String detail = data.getString("content");
        setContentDetail(detail);
        String title = data.getString("title");
        audioTitle.setText(title);
        if(AudioMediaPlayer.getAudio() != null){
            AudioPlayEntity oldAudio = AudioMediaPlayer.getAudio();
            oldAudio.setCurrentPlayState(0);
            String sqlWhereClause = AudioPlayTable.getAppId()+"=? and "+AudioPlayTable.getResourceId()+"=?";
            SQLiteUtil.update(AudioPlayTable.TABLE_NAME, oldAudio, sqlWhereClause,
                    new String[]{oldAudio.getAppId(),oldAudio.getResourceId()});
        }
        AudioPlayEntity audioPlayEntity = new AudioPlayEntity();
        audioPlayEntity.setAppId("123456");
        audioPlayEntity.setResourceId("123456");
        audioPlayEntity.setColumnId("123456");
        audioPlayEntity.setContent(detail);
        audioPlayEntity.setCurrentPlayState(1);
        audioPlayEntity.setPlayUrl(data.getString("audio_url"));
        audioPlayEntity.setState(0);
        audioPlayEntity.setTitle(title);
        audioPlayEntity.setCreateAt(DateFormat.currentTime());
        audioPlayEntity.setUpdateAt(DateFormat.currentTime());
        audioPlayEntity.setIndex(0);
        List<AudioPlayEntity> dbAudioEntitys = SQLiteUtil.query(AudioPlayTable.TABLE_NAME,
                "select * from "+AudioPlayTable.TABLE_NAME+" where "+AudioPlayTable.getResourceId()+"=?", new String[]{"123456"});
        if(dbAudioEntitys.size() > 0){
            String sqlWhereClause = AudioPlayTable.getAppId()+"=? and "+AudioPlayTable.getResourceId()+"=?";
            SQLiteUtil.update(AudioPlayTable.TABLE_NAME, audioPlayEntity, sqlWhereClause,
                    new String[]{audioPlayEntity.getAppId(),audioPlayEntity.getResourceId()});
        }else{
            SQLiteUtil.insert(AudioPlayTable.TABLE_NAME, audioPlayEntity);
        }
        if(singleAudio){
            AudioPlayUtil.getInstance().addAudio(audioPlayEntity);
        }
        AudioMediaPlayer.setAudio(audioPlayEntity,true);
    }
    private void setContentDetail(String detail){
        detailContent.loadDataWithBaseURL(null, NetworkState.getNewContent(detail), "text/html", "UFT-8", null);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }
    public void setPagerState(boolean error){
        if(error){
            pagerContentDetailLayout.setVisibility(View.GONE);
            statusPagerView.setVisibility(View.VISIBLE);
            statusPagerView.setLoadingState(View.GONE);
            statusPagerView.setStateImage(StatusPagerView.DETAIL_NONE);
            statusPagerView.setHintStateVisibility(View.VISIBLE);
        }else{
            pagerContentDetailLayout.setVisibility(View.VISIBLE);
            statusPagerView.setVisibility(View.GONE);
            statusPagerView.setLoadingState(View.GONE);
            statusPagerView.setHintStateVisibility(View.GONE);
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
                Intent intent = new Intent(this, CommentActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(AudioPlayEvent event) {
        switch (event.getState()){
            case AudioPlayEvent.LOADING:
                break;
            case AudioPlayEvent.PLAY:
                audioPlayController.setPlayState(true);
                audioHoverPlayController.setPlayState(true);
                audioPlayController.setTotalDuration(AudioMediaPlayer.getDuration());
                setDiskRotateAnimator(true);
                break;
            case AudioPlayEvent.PAUSE:
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
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(detailContent != null){
            detailContent.destroy();
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
}
