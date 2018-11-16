package xiaoe.com.shop.business.audio.presenter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import xiaoe.com.common.app.XiaoeApplication;
import xiaoe.com.common.entitys.AudioPlayEntity;
import xiaoe.com.common.entitys.AudioPlayTable;
import xiaoe.com.common.utils.DateFormat;
import xiaoe.com.common.db.SQLiteUtil;
import xiaoe.com.shop.R;
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
    public static boolean prepared = false;
    public static float mPlaySpeed = 1f;//播放倍数
    private static AudioFocusManager audioFocusManager;

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
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        audioFocusManager = new AudioFocusManager(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
        prepared = true;
        mediaPlayer.start();
        audioFocusManager.requestAudioFocus();
        event.setState(AudioPlayEvent.PLAY);
        changePlayerSpeed(mPlaySpeed);
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
        playNext();
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
        if (0 == audio.getHasBuy()){//如果未购买时
            Context context = XiaoeApplication.getmContext();
            Toast.makeText(context,context.getString(R.string.listen_after_purchase),Toast.LENGTH_SHORT).show();
            return;
        }
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
        if(mediaPlayer != null && prepared){
            return  mediaPlayer.getDuration();
        }
        return  -1;
    }

    public static int getCurrentPosition(){
        if(mediaPlayer != null){
            SQLiteUtil.init(XiaoeApplication.getmContext(), new AudioSQLiteUtil());
            boolean tableExist = SQLiteUtil.tabIsExist(AudioPlayTable.TABLE_NAME);
            if(!tableExist){
                SQLiteUtil.execSQL(AudioPlayTable.CREATE_TABLE_SQL);
            }else{
                SQLiteUtil.query(AudioPlayTable.TABLE_NAME, "select * from "+AudioPlayTable.TABLE_NAME, null);
            }
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
        List<AudioPlayEntity> playList = AudioPlayUtil.getInstance().getAudioList();
        int indexNext = audio.getIndex() + 1;
        if(indexNext >= playList.size()){
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
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                } else {
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                    mediaPlayer.pause();
                }
                mPlaySpeed = speed;
            }
            return true;
        }
        return false;
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
        if(!autoPlay || audio.getHasBuy() == 0){
            return;
        }
        audio.setPlay(true);
        start();
    }
    private static void saveAudioDB(){
        SQLiteUtil.init(XiaoeApplication.getmContext(), new AudioSQLiteUtil());
        boolean tableExist = SQLiteUtil.tabIsExist(AudioPlayTable.TABLE_NAME);
        if(!tableExist){
            SQLiteUtil.execSQL(AudioPlayTable.CREATE_TABLE_SQL);
        }

        audio.setState(0);
        audio.setCurrentPlayState(1);
        audio.setUpdateAt(DateFormat.currentTime());
        List<AudioPlayEntity> dbAudioEntitys = SQLiteUtil.query(AudioPlayTable.TABLE_NAME,
                "select * from "+AudioPlayTable.TABLE_NAME+" where "+AudioPlayTable.getResourceId()+"=?", new String[]{audio.getResourceId()});
        if(dbAudioEntitys.size() > 0){
            String sqlWhereClause = AudioPlayTable.getAppId()+"=? and "+AudioPlayTable.getResourceId()+"=?";
            SQLiteUtil.update(AudioPlayTable.TABLE_NAME, audio, sqlWhereClause,
                    new String[]{audio.getAppId(),audio.getResourceId()});
        }else{
            audio.setCreateAt(DateFormat.currentTime());
            SQLiteUtil.insert(AudioPlayTable.TABLE_NAME, audio);
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
}
