package xiaoe.com.shop.business.audio.presenter;

import android.media.MediaPlayer;

public class AudioMediaPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener {
    private static final String TAG = "AudioMediaPlayer";
    private MediaPlayer mediaPlayer;
    private static AudioMediaPlayer mInstance;
    private AudioMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        //设置准备播放资源监听
        mediaPlayer.setOnPreparedListener(this);
        //设置定位播放监听
        mediaPlayer.setOnSeekCompleteListener(this);
        //设置
        mediaPlayer.setOnErrorListener(this);
    }
    public static AudioMediaPlayer getInstance(){
        if(mInstance == null){
            //加上锁
            synchronized (AudioMediaPlayer.class){
                if(mInstance == null){
                    mInstance = new AudioMediaPlayer();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
}
