package xiaoe.com.shop.business.audio.presenter;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import xiaoe.com.common.app.XiaoeApplication;
import xiaoe.com.common.entitys.AudioPlayEntity;
import xiaoe.com.common.entitys.AudioPlayTable;
import xiaoe.com.common.utils.DateFormat;
import xiaoe.com.common.utils.SQLiteUtil;
import xiaoe.com.shop.events.AudioPlayEvent;

public class AudioMediaPlayer extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {
    private static final String TAG = "AudioMediaPlayer";
    private static final int MSG_PLAY_PROGRESS = 80001;
    private static MediaPlayer mediaPlayer;
    private static AudioPlayEvent event;
    private static AudioPlayEntity audio = null;
    private static boolean isStop = true;//是否是停止（已经释放资源），
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
    @Override
    public void onCreate() {
        super.onCreate();
        init();
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
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //设置准备播放资源监听
        mediaPlayer.setOnPreparedListener(this);
        //设置定位播放监听
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnCompletionListener(this);
        //设置
        mediaPlayer.setOnErrorListener(this);

         event = new AudioPlayEvent();
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        event.setState(AudioPlayEvent.PLAY);
        EventBus.getDefault().post(event);
        mHandler.sendEmptyMessageDelayed(MSG_PLAY_PROGRESS, 100);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if(isPlaying()){
            event.setState(AudioPlayEvent.PLAY);
        }else{
            event.setState(AudioPlayEvent.PAUSE);
        }
        EventBus.getDefault().post(event);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        event.setState(AudioPlayEvent.STOP);
        EventBus.getDefault().post(event);
        isStop = true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        event.setState(AudioPlayEvent.STOP);
        EventBus.getDefault().post(event);
        return false;
    }


    /**
     * 开始播放
     */
    public static void start(){
        if(audio == null || mediaPlayer == null || TextUtils.isEmpty(audio.getPlayUrl())){
            return;
        }
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
    }
    public static boolean isPlaying(){
        if(mediaPlayer == null){
            return  false;
        }
        return mediaPlayer.isPlaying();
    }
    //播放，如果是播放中就执行暂停，否则播放
    public static void play() {
        if(mediaPlayer == null){
            return;
        }
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            event.setState(AudioPlayEvent.PAUSE);
        } else {
            mediaPlayer.start();
            event.setState(AudioPlayEvent.PLAY);
            mHandler.sendEmptyMessageDelayed(MSG_PLAY_PROGRESS, 100);
        }
        EventBus.getDefault().post(event);
    }
    public static void stop() {
        if(audio == null){
            return;
        }
        if(mediaPlayer != null && !isStop) {
            mediaPlayer.stop();
        }
        audio.setCurrentPlayState(0);

        SQLiteUtil.init(XiaoeApplication.getmContext(), new AudioSQLiteUtil());
        boolean tableExist = SQLiteUtil.tabIsExist(AudioPlayTable.TABLE_NAME);
        if(!tableExist){
            SQLiteUtil.execSQL(AudioPlayTable.CREATE_TABLE_SQL);
        }
        String sqlWhereClause = AudioPlayTable.getAppId()+"=? and "+AudioPlayTable.getResourceId()+"=?";
        SQLiteUtil.update(AudioPlayTable.TABLE_NAME, audio, sqlWhereClause,
                new String[]{audio.getAppId(),audio.getResourceId()});

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
        }
        event.setState(AudioPlayEvent.STOP);
        EventBus.getDefault().post(event);
        isStop = true;
    }
    public static int getDuration(){
        if(mediaPlayer != null){
            return  mediaPlayer.getDuration();
        }
        return  -1;
    }

    public static int getCurrentPosition(){
        if(mediaPlayer != null){
            return mediaPlayer.getCurrentPosition();
        }
        return  -1;
    }


    public static void playNext() {
        List<AudioPlayEntity> playList = AudioPlayUtil.getInstance().getAudioList();
        int indexNext = audio.getIndex() + 1;
        if(indexNext >= playList.size()){
            indexNext = 0;
        }
        stop();
        audio.setPlay(false);
        AudioPlayEntity nextAudio = playList.get(indexNext);
        nextAudio.setPlay(true);
        if(audio.getResourceId().equals(nextAudio.getResourceId())){
            if(isStop){
                start();
            }else{
                play();
            }
        }else{
            audio = nextAudio;
            new AudioPresenter(null).requestDetail(nextAudio.getResourceId());
        }
    }

    public static void playLast() {
        List<AudioPlayEntity> playList = AudioPlayUtil.getInstance().getAudioList();
        int indexLast = audio.getIndex() - 1;
        if(indexLast < 0){
            indexLast = playList.size() - 1;
        }
        stop();
        audio.setPlay(false);
        AudioPlayEntity lastAudio = playList.get(indexLast);
        lastAudio.setPlay(true);
        if(audio.getResourceId().equals(lastAudio.getResourceId())){
            if(isStop){
                start();
            }else{
                play();
            }
        }else{
            audio = lastAudio;
            new AudioPresenter(null).requestDetail(lastAudio.getResourceId());
        }
    }

    private static void playProgress() {
        if(!isPlaying()){
            //已经停止就不需要在轮循获取当前进度
            return;
        }
        event.setState(AudioPlayEvent.PROGRESS);
        event.setProgress(getCurrentPosition());
        EventBus.getDefault().post(event);
        mHandler.sendEmptyMessageDelayed(MSG_PLAY_PROGRESS,500);
    }

    public static AudioPlayEntity getAudio() {
        return audio;
    }

    public static void setAudio(AudioPlayEntity audio, boolean autoPlay) {
        AudioMediaPlayer.audio = audio;


        SQLiteUtil.init(XiaoeApplication.getmContext(), new AudioSQLiteUtil());
        boolean tableExist = SQLiteUtil.tabIsExist(AudioPlayTable.TABLE_NAME);
        if(!tableExist){
            SQLiteUtil.execSQL(AudioPlayTable.CREATE_TABLE_SQL);
        }
        AudioPlayEntity audioPlayEntity = new AudioPlayEntity();
        audioPlayEntity.setAppId(audio.getAppId());
        audioPlayEntity.setTitle(audio.getTitle());
        audioPlayEntity.setResourceId(audio.getResourceId());
        audioPlayEntity.setCurrentPlayState(1);
        audioPlayEntity.setState(0);
        audioPlayEntity.setCreateAt(DateFormat.currentTime());
        audioPlayEntity.setUpdateAt(DateFormat.currentTime());
        audioPlayEntity.setIndex(audio.getIndex());
        List<AudioPlayEntity> dbAudioEntitys = SQLiteUtil.query(AudioPlayTable.TABLE_NAME,
                "select * from "+AudioPlayTable.TABLE_NAME+" where "+AudioPlayTable.getResourceId()+"=?", new String[]{audio.getResourceId()});
        if(dbAudioEntitys.size() > 0){
            String sqlWhereClause = AudioPlayTable.getAppId()+"=? and "+AudioPlayTable.getResourceId()+"=?";
            SQLiteUtil.update(AudioPlayTable.TABLE_NAME, audioPlayEntity, sqlWhereClause,
                    new String[]{audioPlayEntity.getAppId(),audioPlayEntity.getResourceId()});
        }else{
            SQLiteUtil.insert(AudioPlayTable.TABLE_NAME, audioPlayEntity);
        }
    }

    public static boolean isStop() {
        return isStop;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
