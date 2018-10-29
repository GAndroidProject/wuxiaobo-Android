package xiaoe.com.shop.business.audio.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import xiaoe.com.common.entitys.AudioPlayEntity;
import xiaoe.com.common.utils.NetworkState;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.anim.ViewAnim;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;
import xiaoe.com.shop.business.audio.presenter.AudioPlayUtil;
import xiaoe.com.shop.business.comment.ui.CommentActivity;
import xiaoe.com.shop.events.AudioPlayEvent;
import xiaoe.com.shop.interfaces.OnClickMoreMenuListener;
import xiaoe.com.shop.utils.NumberFormat;
import xiaoe.com.shop.widget.CommonBuyView;
import xiaoe.com.shop.widget.ContentMenuLayout;
import xiaoe.com.shop.widget.StatusPagerView;

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
    private WebView detailContent;
    private CommonBuyView commonBuyView;
    private StatusPagerView statusPagerView;
    private AudioDetailsSwitchLayout pagerContentDetailLayout;
    private Intent mIntent;
    private AudioPlayListLayout audioPlayList;

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
        mIntent = getIntent();
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
        //状态页面
        statusPagerView = (StatusPagerView) findViewById(R.id.state_pager_view);
        statusPagerView.setVisibility(View.GONE);
    }
    private void initDatas() {
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
            case R.id.btn_play_list:
                if(audioPlayList.getVisibility() == View.VISIBLE){
                    audioPlayList.setVisibility(View.GONE);
                }else{
                    audioPlayList.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
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

    private void refreshPager(){
        AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        int code = playEntity.getCode();
        if(playEntity == null || code == -2){
            setButtonEnabled(false);
            return;
        }else if (code == 1){
            setPagerState(true);
            return;
        }
        if(playEntity.getHasBuy() == 0){
            commonBuyView.setVisibility(View.VISIBLE);
            setButtonEnabled(false);
        }else{
            commonBuyView.setVisibility(View.GONE);
            setButtonEnabled(true);
        }
        if(code == 0){
            setContentDetail(playEntity.getContent());
        }
        if(AudioMediaPlayer.isStop()){
            AudioMediaPlayer.setAudio(playEntity, true);
        }
        audioTitle.setText(playEntity.getTitle());
        playNum.setText(NumberFormat.viewCountToString(playEntity.getPlayCount())+"次播放");
    }
}
