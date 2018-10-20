package xiaoe.com.shop.business.audio.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
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

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import xiaoe.com.shop.R;
import xiaoe.com.shop.anim.ViewAnim;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;
import xiaoe.com.shop.business.audio.presenter.AudioPlayUtil;
import xiaoe.com.shop.business.comment.ui.CommentActivity;
import xiaoe.com.shop.events.AudioPlayEvent;
import xiaoe.com.shop.interfaces.OnClickMoreMenuListener;
import xiaoe.com.shop.widget.ContentMenuLayout;

public class AudioActivity extends XiaoeActivity implements View.OnClickListener, OnClickMoreMenuListener {
    private static final String TAG = "AudioActivity";
    private final int MSG_VIEW_STATE = 10001;
    private final int MSG_PLAY_PROGRESS = 10002;
    private SimpleDraweeView audioBG;
    private SimpleDraweeView audioRing;
    private ViewAnim mViewAnim;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_VIEW_STATE:
                    initViewState(View.VISIBLE);
                    break;
                case MSG_PLAY_PROGRESS:
                    playProgress();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }
        setContentView(R.layout.activity_audio);
        initViews();
        initDatas();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(AudioMediaPlayer.isPlaying()){
            setDiskRotateAnimator(true);
            mHandler.sendEmptyMessageDelayed(MSG_PLAY_PROGRESS,100);
        }
    }

    private void initViews() {
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
        audioTitle.setText("我的财富计划 新中产必修财富课程");
        //播放次数
        playNum = (TextView) findViewById(R.id.play_num);
        playNum.setText("1000次播放");

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
    }
    private void initDatas() {
        AudioPlayUtil audioPlayUtil = AudioPlayUtil.getInstance();
        String[] paths = {getResources().getString(R.string.test_audio_url1),
                getResources().getString(R.string.test_audio_url2),
                getResources().getString(R.string.test_audio_url3),
                getResources().getString(R.string.test_audio_url4),
                getResources().getString(R.string.test_audio_url5)};
        for (int i = 0; i < 5; i++){
            AudioPlayUtil.Audio audio = new AudioPlayUtil.Audio();
            audio.setUrl(paths[i]);
            audio.setPlay(false);
            audio.setIndex(i);
            audioPlayUtil.addAudio(audio);
        }
        AudioMediaPlayer.setAudio(audioPlayUtil.getAudioList().get(0));
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
                mHandler.sendEmptyMessageDelayed(MSG_PLAY_PROGRESS,100);
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
            default:
                break;
        }
    }

    private void playProgress() {
        if(!AudioMediaPlayer.isPlaying()){
            //已经停机就不需要在轮循获取当前进度
            return;
        }
        audioPlayController.setPlayDuration(AudioMediaPlayer.getCurrentPosition());
        mHandler.sendEmptyMessageDelayed(MSG_PLAY_PROGRESS,500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClickMoreMenu(View view) {
        if(view.getId() == R.id.hover_audio_more){
            contentMenuLayout.setVisibility(View.VISIBLE);
        }
    }
}
