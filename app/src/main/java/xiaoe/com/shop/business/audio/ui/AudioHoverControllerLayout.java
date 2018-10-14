package xiaoe.com.shop.business.audio.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import xiaoe.com.shop.R;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;

public class AudioHoverControllerLayout extends FrameLayout implements View.OnClickListener {

    private View rootView;
    private ImageView audioDisk;
    private ImageView btnLast;
    private ImageView btnPlay;
    private ImageView btnNext;
    private ImageView btnMore;

    public AudioHoverControllerLayout(Context context) {
        this(context,null);
    }

    public AudioHoverControllerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        rootView = View.inflate(context,R.layout.layout_audio_hover_controller, this);
        initView();
    }

    private void initView() {
        audioDisk = (ImageView) rootView.findViewById(R.id.hover_audio_disk);
        btnLast = (ImageView) rootView.findViewById(R.id.hover_audio_last);
        btnLast.setOnClickListener(this);
        btnPlay = (ImageView) rootView.findViewById(R.id.hover_audio_play);
        btnPlay.setOnClickListener(this);
        btnNext = (ImageView) rootView.findViewById(R.id.hover_audio_next);
        btnNext.setOnClickListener(this);
        btnMore = (ImageView) rootView.findViewById(R.id.hover_audio_more);
        btnMore.setOnClickListener(this);
    }


    public void play(){
        AudioMediaPlayer.play();
    }

    public void setPlayState(boolean play){
        if(play){
            btnPlay.setImageResource(R.mipmap.audio_stop);
        }else{
            btnPlay.setImageResource(R.mipmap.audio_play);
        }
    }

    public void last(){
        AudioMediaPlayer.playLast();
    }
    public void next(){
        AudioMediaPlayer.playNext();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hover_audio_play:
                play();
                break;
            case R.id.hover_audio_last:
                last();
                break;
            case R.id.hover_audio_next:
                next();
                break;
            default:
                break;
        }
    }
}
