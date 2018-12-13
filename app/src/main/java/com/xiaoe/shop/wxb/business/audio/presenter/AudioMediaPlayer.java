package com.xiaoe.shop.wxb.business.audio.presenter;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.AudioPlayTable;
import com.xiaoe.common.utils.DateFormat;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

public class AudioMediaPlayer extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {
    private static final String TAG = "AudioMediaPlayer";
    private static final int MSG_PLAY_PROGRESS = 80001;
    public static MediaPlayer mediaPlayer;
    private static AudioPlayEvent event;
    private static AudioPlayEntity audio = null;
    private static boolean isStop = true;//是否是停止（已经释放资源），
    public static boolean prepared = false;
    public static float mPlaySpeed = 1f;//播放倍数
    private static AudioFocusManager audioFocusManager;
    private static boolean isSaveProgress = true;

    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_PLAY_PROGRESS:
                    playProgress();
                    break;
                default:
                    break;
            }
        }
    };
    private static boolean isThirdPause = false;//非主动暂停，如电话呼入
    private static SQLiteUtil audioSQLiteUtil;


    @Override
    public void onCreate() {
        super.onCreate();
        init();
        AudioNotifier.get().init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void init() {
        audioSQLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new AudioSQLiteUtil());
        boolean tableExist = audioSQLiteUtil.tabIsExist(AudioPlayTable.TABLE_NAME);
        if(!tableExist){
            audioSQLiteUtil.execSQL(AudioPlayTable.CREATE_TABLE_SQL);
        }
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        audioFocusManager = new AudioFocusManager(this);
        //设置准备播放资源监听
        mediaPlayer.setOnPreparedListener(this);
        //设置定位播放监听
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnCompletionListener(this);
        //设置
        mediaPlayer.setOnErrorListener(this);

        event = new AudioPlayEvent();
        isStop = true;
        prepared = false;
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared: ");
        prepared = true;
        mediaPlayer.start();
        audioFocusManager.requestAudioFocus();
        event.setState(AudioPlayEvent.PLAY);
        changePlayerSpeed(mPlaySpeed);
        EventBus.getDefault().post(event);
        mHandler.sendEmptyMessageDelayed(MSG_PLAY_PROGRESS, 100);

        if (audio != null && audio.getProgress() > 0)
            mediaPlayer.seekTo(audio.getProgress());
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "onSeekComplete: ");
        if(isPlaying()){
            event.setState(AudioPlayEvent.PLAY);
        }else{
            event.setState(AudioPlayEvent.PAUSE);
        }
        EventBus.getDefault().post(event);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion: ");
        event.setState(AudioPlayEvent.STOP);
        EventBus.getDefault().post(event);
        isStop = true;
        if(audio.getIsTry() == 1){
            //如果是试听，播放
            Toast.makeText(XiaoeApplication.getmContext(),R.string.play_has_last_sing,Toast.LENGTH_SHORT).show();
            return;
        }
        audio.setProgress(0);
        playNext(false);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: ");
        event.setState(AudioPlayEvent.STOP);
        EventBus.getDefault().post(event);
        return false;
    }


    /**
     * 开始播放
     */
    public static void start(){
        if(audio == null || mediaPlayer == null || (TextUtils.isEmpty(audio.getPlayUrl()) && isStop)){
            return;
        }
        if (0 == audio.getHasBuy() && 0 == audio.getIsTry()){
            //如果未购买时并且也没有试听
            Context context = XiaoeApplication.getmContext();
            Toast.makeText(context,context.getString(R.string.listen_after_purchase),Toast.LENGTH_SHORT).show();
            return;
        }
        audio.setPlay(true);
        prepared = false;
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(audio.getPlayUrl());
        } catch (IOException e) {
//            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        event.setState(AudioPlayEvent.LOADING);
        EventBus.getDefault().post(event);
        isStop = false;
        saveAudioDB();
        AudioNotifier.get().showPlay(audio);
    }
    public static boolean isPlaying(){
        if(mediaPlayer == null || !prepared || isStop){
            return  false;
        }
        return mediaPlayer.isPlaying();
    }
    //播放，如果是播放中就执行暂停，否则播放
    public static void play() {
        if(mediaPlayer == null || !prepared){
            return;
        }
        isThirdPause = false;
        if (getAudio() != null && mediaPlayer.isPlaying())
            getAudio().setPlaying(false);
        if(mediaPlayer.isPlaying()){
            audioFocusManager.abandonAudioFocus();
            mediaPlayer.pause();
            event.setState(AudioPlayEvent.PAUSE);
            AudioNotifier.get().showPause(audio);
        } else {
            audioFocusManager.requestAudioFocus();
            mediaPlayer.start();
            event.setState(AudioPlayEvent.PLAY);
            mHandler.sendEmptyMessageDelayed(MSG_PLAY_PROGRESS, 100);
            AudioNotifier.get().showPlay(audio);
            changePlayerSpeed(mPlaySpeed);
        }
        EventBus.getDefault().post(event);
    }
    public static void stop() {
        if(audio == null || mediaPlayer == null || isStop){
            return;
        }
        audioFocusManager.abandonAudioFocus();
        if(prepared){
            mediaPlayer.stop();
        }
        audio.setCurrentPlayState(0);
        audio.setUpdateAt(DateFormat.currentTime());

        String sqlWhereClause = AudioPlayTable.getAppId()+"=? and "+AudioPlayTable.getResourceId()+"=? and user_id=?";
        audioSQLiteUtil.update(AudioPlayTable.TABLE_NAME, audio, sqlWhereClause,
                new String[]{audio.getAppId(),audio.getResourceId(), CommonUserInfo.getLoginUserIdOrAnonymousUserId()});

        event.setState(AudioPlayEvent.STOP);
        EventBus.getDefault().post(event);
        isStop = true;
    }

    public static void seekTo(int msec){
        if(mediaPlayer != null){
            mediaPlayer.seekTo(msec);
        }
        event.setState(AudioPlayEvent.LOADING);
        EventBus.getDefault().post(event);
    }
    public static void release(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mPlaySpeed = 1.0f;
            mediaPlayer = null;
        }
        prepared = false;
        event.setState(AudioPlayEvent.STOP);
        EventBus.getDefault().post(event);
        isStop = true;
    }
    public static int getDuration(){
        if(mediaPlayer != null && prepared){
            int duration = mediaPlayer.getDuration();
            audio.setMaxProgress(duration);
            return duration;
        }
        return  -1;
    }

    public static int getCurrentPosition(){
        if(mediaPlayer != null){
//            boolean tableExist = audioSQLiteUtil.tabIsExist(AudioPlayTable.TABLE_NAME);
//            if(!tableExist){
//                audioSQLiteUtil.execSQL(AudioPlayTable.CREATE_TABLE_SQL);
//            }else{
//                audioSQLiteUtil.query(AudioPlayTable.TABLE_NAME, "select * from "+AudioPlayTable.TABLE_NAME, null);
//            }
            return mediaPlayer.getCurrentPosition();
        }
        return  -1;
    }


    public static void thirdPause(){
        isThirdPause = true;
        if(mediaPlayer == null || !prepared){
            return;
        }
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            event.setState(AudioPlayEvent.PAUSE);
            AudioNotifier.get().showPause(audio);
        }
        EventBus.getDefault().post(event);
    }

    public static boolean isThirdPause() {
        return isThirdPause;
    }

    public static void playAppoint(int position){
        audio.setPlay(false);
        stop();

        List<AudioPlayEntity> playList = AudioPlayUtil.getInstance().getAudioList();
        AudioPlayEntity nextAudio = playList.get(position);
        setAudio(nextAudio, true);
        if(nextAudio.getCode() != 0){
            new AudioPresenter(null).requestDetail(nextAudio.getResourceId());
        }
        event.setState(AudioPlayEvent.NEXT);
        EventBus.getDefault().post(event);
    }

    public static void playNext() {
        playNext(true);
    }

    public static void playNext(boolean isShowLastOneToast) {
        List<AudioPlayEntity> playList = AudioPlayUtil.getInstance().getAudioList();
        int indexNext = audio.getIndex() + 1;
        if(indexNext >= playList.size()){
            if (isShowLastOneToast)
                Toast.makeText(XiaoeApplication.getmContext(),R.string.play_has_last_sing,Toast.LENGTH_SHORT).show();
            return;
        }
        audio.setPlay(false);
        stop();
        AudioPlayEntity nextAudio = playList.get(indexNext);
        setAudio(nextAudio, true);
        if(nextAudio.getCode() != 0){
            new AudioPresenter(null).requestDetail(nextAudio.getResourceId());
        }
        event.setState(AudioPlayEvent.NEXT);
        EventBus.getDefault().post(event);
    }

    public static void playLast() {
        List<AudioPlayEntity> playList = AudioPlayUtil.getInstance().getAudioList();
        int indexLast = audio.getIndex() - 1;
        if(indexLast < 0){
            Toast.makeText(XiaoeApplication.getmContext(),R.string.play_has_first_sing,Toast.LENGTH_SHORT).show();
            return;
        }
        audio.setPlay(false);
        stop();
        AudioPlayEntity lastAudio = playList.get(indexLast);
        setAudio(lastAudio, true);
        if(lastAudio.getCode() != 0){
            new AudioPresenter(null).requestDetail(lastAudio.getResourceId());
        }
        event.setState(AudioPlayEvent.LAST);
        EventBus.getDefault().post(event);

    }

    public static boolean changePlayerSpeed(float speed) {
        // this checks on API 23 and up
        if (!prepared)    return false;
        if (mediaPlayer != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (speed != mediaPlayer.getPlaybackParams().getSpeed()) {
                try {
                    PlaybackParams playbackParams = mediaPlayer.getPlaybackParams();
                    playbackParams.setSpeed(speed);
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.setPlaybackParams(playbackParams);
                    } else {
                        mediaPlayer.setPlaybackParams(playbackParams);
                        mediaPlayer.pause();
                    }
                    mPlaySpeed = speed;
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static void playProgress() {
        Log.d(TAG, "playProgress: ");
        if(!isPlaying()){
            //已经停止就不需要在轮循获取当前进度
            return;
        }
        int currentPosition = getCurrentPosition();
        if (isSaveProgress){
            savePlayProgress(currentPosition);
        }
        isSaveProgress = !isSaveProgress;
        event.setState(AudioPlayEvent.PROGRESS);
        event.setProgress(currentPosition);
        EventBus.getDefault().post(event);
        mHandler.sendEmptyMessageDelayed(MSG_PLAY_PROGRESS,500);
    }

    public static AudioPlayEntity getAudio() {
        return audio;
    }

    public static void setAudio(AudioPlayEntity audio, boolean autoPlay) {
        AudioMediaPlayer.audio = audio;
        if(!autoPlay){
            return;
        }
        start();
    }
    private static void saveAudioDB(){
        audio.setState(0);
        audio.setCurrentPlayState(1);
        audio.setUpdateAt(DateFormat.currentTime());
        List<AudioPlayEntity> dbAudioEntitys = audioSQLiteUtil.query(AudioPlayTable.TABLE_NAME,
                "select * from "+AudioPlayTable.TABLE_NAME+" where app_id=? and user_id =? and "+AudioPlayTable.getResourceId()+"=?", new String[]{Constants.getAppId(), CommonUserInfo.getLoginUserIdOrAnonymousUserId(), audio.getResourceId()});
        if(dbAudioEntitys.size() > 0){
            String sqlWhereClause = AudioPlayTable.getAppId()+"=? and user_id=? and "+AudioPlayTable.getResourceId()+"=?";
            audioSQLiteUtil.update(AudioPlayTable.TABLE_NAME, audio, sqlWhereClause,
                    new String[]{audio.getAppId(), CommonUserInfo.getLoginUserIdOrAnonymousUserId(), audio.getResourceId()});
        }else{
            audio.setCreateAt(DateFormat.currentTime());
            audioSQLiteUtil.insert(AudioPlayTable.TABLE_NAME, audio);
        }
    }

    public static MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }

    public static boolean isStop() {
        return isStop;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        AudioNotifier.get().cancelAll();
    }

    private static void savePlayProgress(int progress) {
        progress = progress - 5 * 1000;//保存播放进度向后退五秒
        progress = progress < 0 ? 0 : progress;
        audio.setProgress(progress);
        saveAudioDB();
    }
}
