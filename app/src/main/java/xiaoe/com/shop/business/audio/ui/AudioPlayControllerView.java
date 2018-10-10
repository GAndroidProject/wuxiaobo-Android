package xiaoe.com.shop.business.audio.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import xiaoe.com.shop.R;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;

public class AudioPlayControllerView extends FrameLayout {
    private static final String TAG = "PlayControllerView";
    private View mRootView;
    private TextView playDuration;
    private TextView totalDuration;
    private SeekBar playSeek;
    private ImageView btnPlayBack;
    private ImageView btnPlayLast;
    private ImageView btnPlay;
    private ImageView btnPlayNext;
    private ImageView btnPlayForward;

    public AudioPlayControllerView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public AudioPlayControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mRootView = View.inflate(context, R.layout.view_audio_play_controller, this);
        playDuration = (TextView) mRootView.findViewById(R.id.id_current_play_time);
        totalDuration = (TextView) mRootView.findViewById(R.id.id_total_play_time);
        playSeek = (SeekBar) mRootView.findViewById(R.id.id_play_seek_bar);
        btnPlayBack = (ImageView) mRootView.findViewById(R.id.audio_play_back);
        btnPlayLast = (ImageView) mRootView.findViewById(R.id.audio_play_last);
        btnPlay = (ImageView) mRootView.findViewById(R.id.audio_play);
        btnPlayNext = (ImageView) mRootView.findViewById(R.id.audio_play_next);
        btnPlayForward = (ImageView) mRootView.findViewById(R.id.audio_play_forward);
    }

    private void setPlayState(boolean isPlay){
        if(isPlay){
            btnPlay.setImageResource(R.mipmap.audio_stop);
        }else{
            btnPlay.setImageResource(R.mipmap.audio_play);
        }
    }
}
