package xiaoe.com.shop.business.video.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import xiaoe.com.common.utils.DateFormat;
import xiaoe.com.network.utils.ThreadPoolUtils;
import xiaoe.com.shop.R;
import xiaoe.com.shop.business.video.presenter.VideoPlayer;
import xiaoe.com.shop.events.VideoPlayEvent;
import xiaoe.com.shop.interfaces.OnClickVideoButtonListener;

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
    }

    public void setPreviewImage(String imageUrl){
        previewImage.setImageURI(imageUrl);
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
            case R.id.id_full_screen_play:
                clickFullScreen();
                break;
            case R.id.btn_download:
                if(clickVideoBackListener != null){
                    clickVideoBackListener.onVideoButton(v, VideoPlayConstant.VIDEO_STATE_DOWNLOAD);
                }
                break;
            default:
                break;
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
        }
    }

    public void release(){
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

        initPlayListener();
    }

    private void initPlayListener() {
        mVideoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setPlayState(VideoPlayConstant.VIDEO_STATE_PAUSE);
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
                                    if (!isTouchSeekBar)
                                        setProgress(currentPosition);
                                }
                            });
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            mTimer.schedule(mTimerTask,0,1000);
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
            btnPlay.setImageResource(R.mipmap.icon_play_video_loading);
            btnPlay.setVisibility(VISIBLE);
        }else if(state == VideoPlayConstant.VIDEO_STATE_PLAY){
            //播放
//            btnPlay.setImageResource(R.mipmap.audiolist_stop);
            btnPlay.setVisibility(GONE);
            btnProgressPlay.setImageResource(R.mipmap.icon_video_stop);
            controlDelayAutoDismiss(1);
        }else if(state == VideoPlayConstant.VIDEO_STATE_PAUSE){
            //停止
            btnPlay.setImageResource(R.mipmap.icon_video_play_big);
            btnPlay.setVisibility(VISIBLE);
            btnProgressPlay.setImageResource(R.mipmap.icon_video_play);
            controlDelayAutoDismiss(1);
        }
    }

    /**
     * 视频控制自动延迟消失
     */
    private void controlDelayAutoDismiss(int state) {
        Log.d(TAG,"state = " + state + "--- mVideoPlayer.isPlaying() = " + mVideoPlayer.isPlaying());
        btnPlay.removeCallbacks(mControlDelayDismissRunnable);
        if(!mVideoPlayer.isPlaying())  return;
        btnPlay.postDelayed(mControlDelayDismissRunnable,2000);
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
