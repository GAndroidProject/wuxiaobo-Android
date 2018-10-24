package xiaoe.com.shop.business.audio.presenter;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import xiaoe.com.common.entitys.AudioPlayEntity;
import xiaoe.com.shop.events.AudioPlayEvent;

public class AudioMediaPlayer extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {
    private static final String TAG = "AudioMediaPlayer";
    private static MediaPlayer mediaPlayer;
    private static AudioPlayEvent event;
    private static AudioPlayEntity audio = null;
    private static boolean isStop = true;//是否是停止（已经释放资源），
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
        if(audio == null){
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
        }
        EventBus.getDefault().post(event);
    }
    public static void stop() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
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
        setAudio(playList.get(indexNext));
        stop();
        if(isStop){
            start();
        }else{
            play();
        }
    }

    public static void playLast() {
        List<AudioPlayEntity> playList = AudioPlayUtil.getInstance().getAudioList();
        int indexLast = audio.getIndex() - 1;
        if(indexLast < 0){
            indexLast = playList.size() - 1;
        }
        setAudio(playList.get(indexLast));
        stop();
        if(isStop){
            start();
        }else{
            play();
        }
    }

    public static AudioPlayEntity getAudio() {
        return audio;
    }

    public static void setAudio(AudioPlayEntity audio) {
        AudioMediaPlayer.audio = audio;
    }

    public static boolean isStop() {
        return isStop;
    }

}
