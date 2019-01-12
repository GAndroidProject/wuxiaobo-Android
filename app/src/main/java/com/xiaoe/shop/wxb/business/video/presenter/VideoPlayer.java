package com.xiaoe.shop.wxb.business.video.presenter;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import com.xiaoe.shop.wxb.business.audio.presenter.CountDownTimerTool;
import com.xiaoe.shop.wxb.utils.LogUtils;

/**
 * Created by Administrator on 2017/7/20.
 */

public class VideoPlayer{
    private static final String TAG = "VideoPlayer";
    private MediaPlayer mediaPlayer;
    public static float mPlaySpeed = 1f;//播放倍数
    private String mSourcePath;
    private SurfaceHolder mSurfaceHolder;
    private boolean mSurfaceViewCreated = false;

    public VideoPlayer(){
        mediaPlayer = new MediaPlayer();
        //设置媒体准备监听
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder, boolean isLocal){
        if(!isLocal){
            mSurfaceViewCreated = true;
        }
        mSurfaceHolder = surfaceHolder;
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated: prepareMediaPlayer");
                if(!mSurfaceViewCreated){
                    mSurfaceViewCreated = true;
                    prepareMediaPlayer();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged: prepareMediaPlayer");
                if(mediaPlayer != null){
                    mediaPlayer.setDisplay(mSurfaceHolder);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    //设置媒体源路径
    public void setSourcePath(String sourcePath){
        mSourcePath = sourcePath;
    }
    //准备播放
    public void prepareMediaPlayer() {
        Log.d(TAG, "prepareMediaPlayer: "+mSourcePath+" ; "+mSurfaceViewCreated);
        if(TextUtils.isEmpty(mSourcePath) || !mSurfaceViewCreated){
            return;
        }
        mSurfaceViewCreated = true;
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(mSourcePath);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDisplay(mSurfaceHolder);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "prepareMediaPlayer: "+e);
//            e.printStackTrace();
        }
    }
    //指定播放位置（时间） 单位毫秒
    public void seekTo(int msec){
        if(mediaPlayer != null){
            mediaPlayer.seekTo(msec);
        }
    }

    public boolean changeSpeedPlay(float speed){
        // this checks on API 23 and up
        if (mediaPlayer != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (speed != mediaPlayer.getPlaybackParams().getSpeed()) {
                    PlaybackParams playbackParams = mediaPlayer.getPlaybackParams();
                    playbackParams.setSpeed(speed);
                    if (isPlaying()) {
                        mediaPlayer.setPlaybackParams(playbackParams);
                    } else {
                        mediaPlayer.setPlaybackParams(playbackParams);
                    }
                    mPlaySpeed = speed;
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    //播放，如果是播放中就执行暂停，否则播放
    public void play() {
        if(mediaPlayer == null){
            return;
        }
        if(isPlaying()){
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }
    public void start(){
//        prepareMediaPlayer();
    }
    //停止播放
    public void stop() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
    //获取总时长
    public int getDuration(){
        if(mediaPlayer != null){
            return mediaPlayer.getDuration();
        }
        return -1;
    }
    //获取当前位置(播放时间)
    public int getCurrentPosition(){
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.getCurrentPosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    //释放资源
    public void release(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
    //设置媒体准备监听
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener completionListener){
        if(completionListener != null){
            mediaPlayer.setOnPreparedListener(completionListener);
        }
    }
    //设置播放结束监听
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener completionListener){
        if(completionListener != null){
            mediaPlayer.setOnCompletionListener(completionListener);
        }
    }
    //设置seekTo监听
    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener seekCompleteListener){
        if(seekCompleteListener != null){
            mediaPlayer.setOnSeekCompleteListener(seekCompleteListener);
        }
    }
    public void setOnErrorListener(MediaPlayer.OnErrorListener errorListener){
        mediaPlayer.setOnErrorListener(errorListener);
    }
}
