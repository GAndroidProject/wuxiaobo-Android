package xiaoe.com.shop.business.audio.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import xiaoe.com.common.utils.DateFormat;
import xiaoe.com.shop.R;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;
import xiaoe.com.shop.business.audio.presenter.AudioPlayUtil;

public class AudioPlayControllerView extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "PlayControllerView";
    private Context mContent;
    private View mRootView;
    private TextView playDuration;
    private TextView totalDuration;
    private SeekBar playSeek;
    private ImageView btnPlayBack;
    private ImageView btnPlayLast;
    private ImageView btnPlay;
    private ImageView btnPlayNext;
    private ImageView btnPlayForward;
    private boolean touchSeekBar = false;

    public AudioPlayControllerView(@NonNull Context context) {
        super(context);
        initView(context);
        initData();
    }

    public AudioPlayControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initData();
    }

    private void initView(Context context) {
        mRootView = View.inflate(context, R.layout.view_audio_play_controller, this);
        playDuration = (TextView) mRootView.findViewById(R.id.id_current_play_time);
        totalDuration = (TextView) mRootView.findViewById(R.id.id_total_play_time);
        playSeek = (SeekBar) mRootView.findViewById(R.id.id_play_seek_bar);
        playSeek.setOnSeekBarChangeListener(this);
        btnPlayBack = (ImageView) mRootView.findViewById(R.id.audio_play_back);
        btnPlayBack.setOnClickListener(this);
        btnPlayLast = (ImageView) mRootView.findViewById(R.id.audio_play_last);
        btnPlayLast.setOnClickListener(this);
        btnPlay = (ImageView) mRootView.findViewById(R.id.audio_play);
        btnPlay.setOnClickListener(this);
        btnPlayNext = (ImageView) mRootView.findViewById(R.id.audio_play_next);
        btnPlayNext.setOnClickListener(this);
        btnPlayForward = (ImageView) mRootView.findViewById(R.id.audio_play_forward);
        btnPlayForward.setOnClickListener(this);
    }
    private void initData(){
        if(!AudioMediaPlayer.isStop()){
            playSeek.setMax(AudioMediaPlayer.getDuration());
            playSeek.setProgress(AudioMediaPlayer.getCurrentPosition());
            playDuration.setText(DateFormat.longToString(AudioMediaPlayer.getCurrentPosition()));
            totalDuration.setText(DateFormat.longToString(AudioMediaPlayer.getDuration()));
            setPlayState(AudioMediaPlayer.isPlaying());
        }
    }

    public void setPlayState(boolean isPlay){
        if(isPlay){
            btnPlay.setImageResource(R.mipmap.audio_stop);
        }else{
            btnPlay.setImageResource(R.mipmap.audio_play);
        }
        AudioMediaPlayer.getAudio().setPlay(isPlay);
    }

    public void setPlayDuration(int playProgress){
        if(!touchSeekBar){
            playSeek.setProgress(playProgress);
        }
        playDuration.setText(DateFormat.longToString(playProgress));
    }

    public void setTotalDuration(int duration){
        playSeek.setMax(duration);
        totalDuration.setText(DateFormat.longToString(duration));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.audio_play:
                audioPlay();
                break;
            case R.id.audio_play_back:
                audioSeekTo(-15000);
                break;
            case R.id.audio_play_forward:
                audioSeekTo(15000);
                break;
            case R.id.audio_play_last:
                playLast();
                break;
            case R.id.audio_play_next:
                playNext();
                break;
            default:
                break;
        }
    }

    private void playNext() {
        List<AudioPlayUtil.Audio> playList = AudioPlayUtil.getInstance().getAudioList();
        AudioPlayUtil.Audio playAudio = AudioMediaPlayer.getAudio();
        int indexNext = playAudio.getIndex() + 1;
        Log.d(TAG, "playNext: "+indexNext);
        Log.d(TAG, "size: "+playList.size());
        Log.d(TAG, "index: "+playAudio.getIndex());
        if(indexNext >= playList.size()){
            indexNext = 0;
        }
        AudioMediaPlayer.setAudio(playList.get(indexNext));
        AudioMediaPlayer.stop();
        audioPlay();
    }

    private void playLast() {
        List<AudioPlayUtil.Audio> playList = AudioPlayUtil.getInstance().getAudioList();
        AudioPlayUtil.Audio playAudio = AudioMediaPlayer.getAudio();
        int indexLast = playAudio.getIndex() - 1;
        if(indexLast < 0){
            indexLast = playList.size() - 1;
        }
        Log.d(TAG, "playLast: "+indexLast);
        AudioMediaPlayer.setAudio(playList.get(indexLast));
        AudioMediaPlayer.stop();
        audioPlay();
    }

    private void audioSeekTo(int i) {
        if(AudioMediaPlayer.isStop()){
            return;
        }
        int currentPosition = AudioMediaPlayer.getCurrentPosition();
        int duration = AudioMediaPlayer.getDuration();
        int seekTo = currentPosition + i;
        if(seekTo < 0){
            seekTo = 0;
        }else if(seekTo > duration){
            seekTo = duration;
        }
        setPlayDuration(seekTo);
        AudioMediaPlayer.seekTo(seekTo);
    }

    private void audioPlay() {
        if(AudioMediaPlayer.isStop()){
            AudioMediaPlayer.start();
        }else{
            AudioMediaPlayer.play();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "onProgressChanged: ");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStartTrackingTouch: ");
        touchSeekBar = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(AudioMediaPlayer.isStop()){
            seekBar.setProgress(0);
        }else{
            AudioMediaPlayer.seekTo(seekBar.getProgress());
        }
        touchSeekBar = false;
    }
}
