package com.xiaoe.shop.wxb.business.video.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.common.entitys.ResourceType;
import com.xiaoe.common.utils.DateFormat;
import com.xiaoe.network.utils.ThreadPoolUtils;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.business.audio.presenter.CountDownTimerTool;
import com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.video.presenter.VideoPlayer;
import com.xiaoe.shop.wxb.events.VideoPlayEvent;
import com.xiaoe.shop.wxb.interfaces.OnClickVideoButtonListener;
import com.xiaoe.shop.wxb.utils.LearnRecordPageProgressManager;
import com.xiaoe.shop.wxb.utils.UploadLearnProgressManager;

import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import static com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper.COUNT_DOWN_DURATION_30;
import static com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper.COUNT_DOWN_DURATION_60;
import static com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper.COUNT_DOWN_STATE_CLOSE;
import static com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper.COUNT_DOWN_STATE_CURRENT;
import static com.xiaoe.shop.wxb.business.audio.presenter.MediaPlayerCountDownHelper.COUNT_DOWN_STATE_TIME;

public class VideoPlayControllerView extends FrameLayout implements View.OnClickListener,SeekBar.OnSeekBarChangeListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = "VideoPlayControllerView";
    private View rootView;
    private Context mContext;
    private SurfaceView videoSurface;
    private RelativeLayout playControllerPage;
    private SimpleDraweeView previewImage;
    private ImageView btnPlay;
    private ImageView btnBack;
    private OnClickVideoButtonListener clickVideoBackListener;
    private VideoPlayer mVideoPlayer;
    private boolean isShowControl = false;
    private boolean isPrepareMedia = false;
    private RelativeLayout videoLayout;
    private ImageView btnProgressPlay;
    private ImageView btnFullScreen;
    private TextView currentPlayTime;
    private SeekBar playSeekBar;
    private TextView totalPlayTime;
    private boolean isFullScreen = false;
    private VideoPlayEvent videoPlayEvent;
    private RelativeLayout playProgressWidget;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean isTouchSeekBar;
    private ImageView btnDownload;
    private PowerManager.WakeLock mWakeLock;
    private boolean hasBuy;
    private String columnId;
    private String realSrcId;

    public void setRealSrcId(String realSrcId) {
        this.realSrcId = realSrcId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public void setHasBuy(boolean hasBuy) {
        this.hasBuy = hasBuy;
    }

    private View mCountDownView,mSpeedPlayView;
    private List<TextView> mCountDownTexts,mSpeedPlayTexts;
    private float mSpeedPlay = 1.0f;
    private int mSpeedPlayPosition = 1;
    private IPlayNext mIPlayNext;

    public void retSet() {
        if (COUNT_DOWN_STATE_CURRENT == MediaPlayerCountDownHelper.INSTANCE.getMCurrentState()){
            MediaPlayerCountDownHelper.INSTANCE.closeCountDownTimer();
        }
        mSpeedPlay = 1.0f;
        mSpeedPlayPosition = 1;
        updateSpeedPlayTexts();
        updateCountDownText();
        pause();
    }

    public void setIPlayNext(IPlayNext IPlayNext) {
        mIPlayNext = IPlayNext;
    }

    public interface IPlayNext{
        void onNext(boolean isAuto);
    }

    public void setWakeLock(PowerManager.WakeLock wakeLock) {
        mWakeLock = wakeLock;
    }

    /**
     * 请求WakeLock
     */
    private void acquireWakeLock(){
        if (mWakeLock != null && !mWakeLock.isHeld())
            mWakeLock.acquire();
    }

    /**
     *禁止/释放WakeLock
     */
    private void releaseWakeLock(){
        if (mWakeLock != null && mWakeLock.isHeld())
            mWakeLock.release();
    }

    public VideoPlayControllerView(@NonNull Context context) {
        this(context,null);
    }

    public VideoPlayControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        rootView = View.inflate(context, R.layout.view_video_play_controller, this);
        initView(context);
    }

    private void initView(Context context) {

        videoSurface = (SurfaceView) rootView.findViewById(R.id.id_video_surface_view);
        videoLayout = (RelativeLayout) rootView.findViewById(R.id.video_layout);
        videoLayout.setOnClickListener(this);
        playControllerPage = (RelativeLayout) rootView.findViewById(R.id.play_controller_page);
        playControllerPage.setVisibility(View.VISIBLE);
        previewImage = (SimpleDraweeView) rootView.findViewById(R.id.id_preview_img);
        btnPlay = (ImageView) rootView.findViewById(R.id.id_btn_playVideo);
        btnPlay.setOnClickListener(this);
        btnBack = (ImageView) rootView.findViewById(R.id.id_btn_come_back);
        btnBack.setOnClickListener(this);

        btnProgressPlay = (ImageView) findViewById(R.id.id_video_progress_play);
        btnProgressPlay.setOnClickListener(this);
        findViewById(R.id.id_video_play_next).setOnClickListener(this);
        btnFullScreen = (ImageView) findViewById(R.id.id_full_screen_play);
        btnFullScreen.setOnClickListener(this);
        currentPlayTime = (TextView) findViewById(R.id.id_current_play_time);
        playSeekBar = (SeekBar) findViewById(R.id.id_play_seekbar);
        playSeekBar.setOnSeekBarChangeListener(this);
        totalPlayTime = (TextView) findViewById(R.id.id_total_play_time);

        playProgressWidget = (RelativeLayout) findViewById(R.id.id_play_progress_widget);
        playProgressWidget.setVisibility(View.GONE);

        mVideoPlayer = new VideoPlayer();
        mVideoPlayer.setOnPreparedListener(this);
        mVideoPlayer.setSurfaceHolder(videoSurface.getHolder(), true);

        videoPlayEvent = new VideoPlayEvent();

        btnDownload = (ImageView) findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(this);
        findViewById(R.id.btn_countdown).setOnClickListener(this);
        findViewById(R.id.btn_speed).setOnClickListener(this);

        mSpeedPlayView = rootView.findViewById(R.id.speed_play_view);
        mSpeedPlayView.setOnClickListener(this);
        if (mSpeedPlayTexts == null){
            mSpeedPlayTexts = new ArrayList<>();
            mSpeedPlayTexts.add((TextView) rootView.findViewById(R.id.btn_speed_play_0));
            mSpeedPlayTexts.add((TextView) rootView.findViewById(R.id.btn_speed_play_1));
            mSpeedPlayTexts.add((TextView) rootView.findViewById(R.id.btn_speed_play_2));
            mSpeedPlayTexts.add((TextView) rootView.findViewById(R.id.btn_speed_play_3));
            mSpeedPlayTexts.add((TextView) rootView.findViewById(R.id.btn_speed_play_4));
        }
        for (int i = 0; i < mSpeedPlayTexts.size(); i++) {
            mSpeedPlayTexts.get(i).setOnClickListener(this);
        }
        updateSpeedPlayTexts();

        mCountDownView = rootView.findViewById(R.id.count_down_view);
        mCountDownView.setOnClickListener(this);
        if (mCountDownTexts == null) {
            if (COUNT_DOWN_STATE_CURRENT == MediaPlayerCountDownHelper.INSTANCE.getMCurrentState()){
                MediaPlayerCountDownHelper.INSTANCE.closeCountDownTimer();
            }
            mCountDownTexts = new ArrayList<>();
            mCountDownTexts.add((TextView) rootView.findViewById(R.id.video_countdown_text_1));
            mCountDownTexts.add((TextView) rootView.findViewById(R.id.video_countdown_text_2));
            mCountDownTexts.add((TextView) rootView.findViewById(R.id.video_countdown_text_3));
            mCountDownTexts.add((TextView) rootView.findViewById(R.id.video_countdown_text_4));
        }

        for (int i = 0; i < mCountDownTexts.size(); i++) {
            mCountDownTexts.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.video_countdown_text_1:
                            countDown(COUNT_DOWN_STATE_CLOSE, 0);
                            break;
                        case R.id.video_countdown_text_2:
                            countDown(COUNT_DOWN_STATE_CURRENT, 0);
                            break;
                        case R.id.video_countdown_text_3:
                            countDown(COUNT_DOWN_STATE_TIME, COUNT_DOWN_DURATION_30);
                            break;
                        case R.id.video_countdown_text_4:
                            countDown(COUNT_DOWN_STATE_TIME, COUNT_DOWN_DURATION_60);
                            break;
                    }
                }
            });
        }

    }

    private void countDown(int state, int duration) {
        if (mCountDownView != null && VISIBLE == mCountDownView.getVisibility())
            mCountDownView.setVisibility(GONE);
        switch (state){
            case COUNT_DOWN_STATE_CURRENT:
                MediaPlayerCountDownHelper.INSTANCE.choiceCurrentPlayFinished();
                break;
            case COUNT_DOWN_STATE_TIME:
                MediaPlayerCountDownHelper.INSTANCE.startCountDown(duration,null);
                break;
            case COUNT_DOWN_STATE_CLOSE:
            default:
                MediaPlayerCountDownHelper.INSTANCE.closeCountDownTimer();
                break;
        }
    }

    CountDownTimerTool.CountDownCallBack mCountDownCallBack = new CountDownTimerTool.CountDownCallBack() {
        @Override
        public void onTick(long millisUntilFinished) {
            if (mCountDownView != null && VISIBLE == mCountDownView.getVisibility()){
                updateCountDownText();
            }
        }

        @Override
        public void onFinish() {
            pause();
            updateCountDownText();
        }
    };

    public void setPreviewImage(String imageUrl){
        previewImage.setImageURI(Uri.parse(imageUrl));
    }

    public void setPlayUrl(String url){
        setPlayState(VideoPlayConstant.VIDEO_STATE_LOADING);
        mVideoPlayer.setSourcePath(url);
        mVideoPlayer.prepareMediaPlayer();
    }

    public void setOnClickVideoBackListener(OnClickVideoButtonListener clickVideoBackListener) {
        this.clickVideoBackListener = clickVideoBackListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_btn_come_back:
                if(clickVideoBackListener != null){
                    clickVideoBackListener.onVideoButton(v, VideoPlayConstant.VIDEO_LITTLE_SCREEN);
                }
                break;
            case R.id.video_layout:
                touchVideoLayout();
                break;
            case R.id.id_btn_playVideo:
            case R.id.id_video_progress_play:
                clickPlayView();
                break;
            case R.id.id_video_play_next:
                if (mIPlayNext != null)   mIPlayNext.onNext(false);
                break;
            case R.id.id_full_screen_play:
                clickFullScreen();
                break;
            case R.id.btn_download:
                if(clickVideoBackListener != null){
                    clickVideoBackListener.onVideoButton(v, VideoPlayConstant.VIDEO_STATE_DOWNLOAD);
                }
                break;
            case R.id.btn_speed:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mSpeedPlayView.setVisibility(VISIBLE);
                    updateSpeedPlayTexts();
                }else  Toast.makeText(mContext,mContext.getString(R.string.speed_play_not_support),
                        Toast.LENGTH_SHORT).show();

                break;
            case R.id.btn_countdown:
                mCountDownView.setVisibility(VISIBLE);
                updateCountDownText();

                MediaPlayerCountDownHelper.INSTANCE.setMCountDownCallBack(mCountDownCallBack);
                break;
            case R.id.count_down_view:
                mCountDownView.setVisibility(GONE);
                break;
            case R.id.speed_play_view:
                mSpeedPlayView.setVisibility(GONE);
                break;
            case R.id.btn_speed_play_0:
                mSpeedPlay = 0.75f;
                mSpeedPlayPosition = 0;
                changeSpeedPlay();
                break;
            case R.id.btn_speed_play_1:
                mSpeedPlay = 1.0f;
                mSpeedPlayPosition = 1;
                changeSpeedPlay();
                break;
            case R.id.btn_speed_play_2:
                mSpeedPlay = 1.25f;
                mSpeedPlayPosition = 2;
                changeSpeedPlay();
                break;
            case R.id.btn_speed_play_3:
                mSpeedPlay = 1.5f;
                mSpeedPlayPosition = 3;
                changeSpeedPlay();
                break;
            case R.id.btn_speed_play_4:
                mSpeedPlay = 2.0f;
                mSpeedPlayPosition = 4;
                changeSpeedPlay();
                break;
            default:
                break;
        }
    }

    private void changeSpeedPlay() {
        if (mSpeedPlayView != null && VISIBLE == mSpeedPlayView.getVisibility())
            mSpeedPlayView.setVisibility(GONE);
        if (mVideoPlayer == null || !isPrepareMedia)   return;
        boolean isChange = mVideoPlayer.changeSpeedPlay(mSpeedPlay);
        if (!isChange) {
            Toast.makeText(mContext,mContext.getString(R.string.speed_play_fail),Toast.LENGTH_SHORT).show();
            return;
        }else {
            if (!mVideoPlayer.isPlaying()){
                clickPlayView();
            }
            setPlayState(VideoPlayConstant.VIDEO_STATE_PLAY);
        }
        updateSpeedPlayTexts();
    }

    private void updateSpeedPlayTexts() {
        if (mSpeedPlayTexts == null)    return;
        for (int i = 0; i < mSpeedPlayTexts.size(); i++) {
            int color = i == mSpeedPlayPosition ? ContextCompat.getColor(mContext,R.color.scholarship_btn_press) :
                    ContextCompat.getColor(mContext,R.color.white);
            mSpeedPlayTexts.get(i).setTextColor(color);
        }
    }

    private void updateCountDownText() {
        if (mCountDownTexts == null || mCountDownTexts.isEmpty())   return;
        int position = MediaPlayerCountDownHelper.INSTANCE.getMVideoSelectedPosition();
        for (int i = 0; i < mCountDownTexts.size(); i++) {
            TextView textView = mCountDownTexts.get(i);
            int color = i == position ? ContextCompat.getColor(mContext,R.color.scholarship_btn_press) :
                    ContextCompat.getColor(mContext,R.color.white);
            textView.setTextColor(color);
            String text = "";
            if (2 == i){
                text = 2 == position ? MediaPlayerCountDownHelper.INSTANCE.getCountText() :
                        mContext.getString(R.string.video_count_down_item_3);
            }else if (3 == i){
                text = 3 == position ? MediaPlayerCountDownHelper.INSTANCE.getCountText() :
                        mContext.getString(R.string.video_count_down_item_4);
            }
            if (!TextUtils.isEmpty(text))   textView.setText(text);
        }
    }

    public void setDownloadState(int state){
        if(state == 1){
            //已经下载
            btnDownload.setImageResource(R.mipmap.video_alreadydownload);
        }else{
            btnDownload.setImageResource(R.mipmap.video_download);
        }
    }

    private void clickFullScreen() {
        isFullScreen = !isFullScreen;
        if(isFullScreen){
            videoPlayEvent.setState(VideoPlayConstant.VIDEO_FULL_SCREEN);
        }else{
            videoPlayEvent.setState(VideoPlayConstant.VIDEO_LITTLE_SCREEN);
        }
        EventBus.getDefault().post(videoPlayEvent);
    }

    private void clickPlayView() {
        if(mVideoPlayer == null){
            return;
        }
        if(isPrepareMedia){
            mVideoPlayer.play();
            setPlayState(mVideoPlayer.isPlaying() ? VideoPlayConstant.VIDEO_STATE_PLAY : VideoPlayConstant.VIDEO_STATE_PAUSE);
        }else{
            mVideoPlayer.prepareMediaPlayer();
        }
    }

    private void touchVideoLayout() {
        if(isShowControl){
            playControllerPage.setVisibility(View.GONE);
            if(mVideoPlayer.isPlaying()){
                btnPlay.setVisibility(View.GONE);
            }
            isShowControl = false;
        }else{
            playControllerPage.setVisibility(View.VISIBLE);
//            btnPlay.setVisibility(View.VISIBLE);
            isShowControl = true;
            controlDelayAutoDismiss(2);
        }
    }

    public void pause(){
        if(mVideoPlayer != null && mVideoPlayer.isPlaying()){
            mVideoPlayer.play();
            setPlayState(VideoPlayConstant.VIDEO_STATE_PAUSE);
        }
    }

    public void release(){
        releaseWakeLock();
        if(mVideoPlayer != null){
            mVideoPlayer.release();
        }
        if (mTimer != null)
            mTimer.cancel();
        if (mTimerTask != null)
            mTimerTask.cancel();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideoPlayer.play();
        isPrepareMedia = true;
//        btnPlay.setImageResource(R.mipmap.audiolist_stop);
        btnPlay.setVisibility(View.GONE);
        playSeekBar.setMax(mVideoPlayer.getDuration());
        totalPlayTime.setText(DateFormat.longToString(mVideoPlayer.getDuration()));
        previewImage.setVisibility(View.GONE);
        setPlayState(VideoPlayConstant.VIDEO_STATE_PLAY);

        videoPlayEvent.setState(VideoPlayConstant.VIDEO_STATE_PLAY);
        EventBus.getDefault().post(videoPlayEvent);

        if (LearnRecordPageProgressManager.INSTANCE.getVideoProgress() > 0){
            mVideoPlayer.seekTo(LearnRecordPageProgressManager.INSTANCE.getVideoProgress());
            LearnRecordPageProgressManager.INSTANCE.setVideoProgress(0);
        }else {
            uploadVideoProgress();
        }

        initPlayListener();

        mVideoPlayer.changeSpeedPlay(mSpeedPlay);
    }

    private void uploadVideoProgress(){
        try {
            if (playSeekBar == null || mVideoPlayer == null || "-1".equals(UploadLearnProgressManager.
                    INSTANCE.getMCurrentColumnId()))
                return;
            int progress = playSeekBar.getProgress();
            int totalTime = mVideoPlayer.getDuration() / 1000;
            if (totalTime < 1) return;
            progress = progress / 10 /totalTime;
            if (UploadLearnProgressManager.INSTANCE.isSingleBuy()) {
                UploadLearnProgressManager.INSTANCE.addSingleItemData(realSrcId, ResourceType.TYPE_VIDEO,
                        progress, totalTime, true);
            } else if (!TextUtils.isEmpty(UploadLearnProgressManager.INSTANCE.getMCurrentColumnId())){
                UploadLearnProgressManager.INSTANCE.addColumnSingleItemData(UploadLearnProgressManager.INSTANCE.getMCurrentColumnId(), realSrcId,
                        ResourceType.TYPE_VIDEO, progress, totalTime);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void uploadSingleBuyVideoProgress(){
        try {
            if (playSeekBar == null || mVideoPlayer == null || !TextUtils.isEmpty(UploadLearnProgressManager.INSTANCE.getMCurrentColumnId()))
                return;
            int progress = playSeekBar.getProgress();
            int totalTime = mVideoPlayer.getDuration() / 1000;
            if (totalTime < 1)   return;
            progress = progress / 10 /totalTime;
            UploadLearnProgressManager.INSTANCE.addSingleItemData(realSrcId,ResourceType.TYPE_VIDEO,
                    progress,totalTime,true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initPlayListener() {
        mVideoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setPlayState(VideoPlayConstant.VIDEO_STATE_PAUSE);
                if (COUNT_DOWN_STATE_CURRENT == MediaPlayerCountDownHelper.INSTANCE.getMCurrentState()){
                    MediaPlayerCountDownHelper.INSTANCE.closeCountDownTimer();
                    updateCountDownText();
                }else {
                    if (mIPlayNext != null)   mIPlayNext.onNext(true);
                }

            }
        });
        if (mTimer == null){
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (mVideoPlayer != null && mVideoPlayer.isPlaying()) {
                            ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    int currentPosition = mVideoPlayer.getCurrentPosition();
                                    if (!isTouchSeekBar && currentPosition > -1) {
                                        setProgress(currentPosition);
                                    }
                                }
                            });
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            mTimer.schedule(mTimerTask,0,500);
        }
    }

    private void setProgress(int currentPosition) {
        Log.d(TAG,"currentPosition = " + currentPosition);
        playSeekBar.setProgress(currentPosition);
        currentPlayTime.setText(DateFormat.longToString(currentPosition));
    }

    private void setPlayState(int state){
        if(state == VideoPlayConstant.VIDEO_STATE_LOADING){
            //加载
            acquireWakeLock();
            btnPlay.setImageResource(R.mipmap.icon_play_video_loading);
            btnPlay.setVisibility(VISIBLE);
        }else if(state == VideoPlayConstant.VIDEO_STATE_PLAY){
            //播放
            acquireWakeLock();
//            btnPlay.setImageResource(R.mipmap.audiolist_stop);
            btnPlay.setVisibility(GONE);
            btnProgressPlay.setImageResource(R.mipmap.icon_video_stop);
            controlDelayAutoDismiss(1);
        }else if(state == VideoPlayConstant.VIDEO_STATE_PAUSE){
            releaseWakeLock();
            //停止
            btnPlay.setImageResource(R.mipmap.icon_video_play_big);
            btnPlay.setVisibility(VISIBLE);
            btnProgressPlay.setImageResource(R.mipmap.icon_video_play);
            controlDelayAutoDismiss(1);
            uploadSingleBuyVideoProgress();
        }
    }

    /**
     * 视频控制自动延迟消失
     */
    private void controlDelayAutoDismiss(int state) {
        Log.d(TAG,"state = " + state + "--- mVideoPlayer.isPlaying() = " + mVideoPlayer.isPlaying());
        btnPlay.removeCallbacks(mControlDelayDismissRunnable);
        if(!mVideoPlayer.isPlaying())  return;
        btnPlay.postDelayed(mControlDelayDismissRunnable,3000);
    }

    Runnable mControlDelayDismissRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isTouchSeekBar){
                if (btnPlay != null && VISIBLE == btnPlay.getVisibility()){
                    btnPlay.setVisibility(GONE);
                }
                if (playControllerPage != null && VISIBLE == playControllerPage.getVisibility()){
                    playControllerPage.setVisibility(View.GONE);
                    isShowControl = false;
                }
            }
        }
    };

    public void setPlayProgressWidgetVisibility(int v){
        playProgressWidget.setVisibility(v);
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (isTouchSeekBar) {
            currentPlayTime.setText(DateFormat.longToString(progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTouchSeekBar = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mVideoPlayer.seekTo(seekBar.getProgress());
        isTouchSeekBar = false;
        controlDelayAutoDismiss(3);
    }
}
