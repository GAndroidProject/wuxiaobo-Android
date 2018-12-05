package com.xiaoe.shop.wxb.business.audio.ui;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.interfaces.OnClickMoreMenuListener;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

public class AudioHoverControllerLayout extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "AudioHoverControllerLay";
    private View rootView;
    private SimpleDraweeView audioDisk;
    private ImageView btnLast;
    private ImageView btnPlay;
    private ImageView btnNext;
    private ImageView btnMore;
    private OnClickMoreMenuListener menuListener;

    public AudioHoverControllerLayout(Context context) {
        this(context,null);
    }

    public AudioHoverControllerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        rootView = View.inflate(context,R.layout.layout_audio_hover_controller, this);
        initView();
    }

    private void initView() {
        audioDisk = (SimpleDraweeView) rootView.findViewById(R.id.hover_audio_disk);
        btnLast = (ImageView) rootView.findViewById(R.id.hover_audio_last);
        btnLast.setOnClickListener(this);
        btnPlay = (ImageView) rootView.findViewById(R.id.hover_audio_play);
        btnPlay.setOnClickListener(this);
        btnNext = (ImageView) rootView.findViewById(R.id.hover_audio_next);
        btnNext.setOnClickListener(this);
        btnMore = (ImageView) rootView.findViewById(R.id.hover_audio_more);
        btnMore.setOnClickListener(this);
    }

    public void setAudioImage(String url){
        if("gif".equals(url.substring(url.lastIndexOf(".")+1)) || "GIF".equals(url.substring(url.lastIndexOf(".")+1))){
            SetImageUriUtil.setRoundAsCircle(audioDisk, Uri.parse(url));
        }else{
            audioDisk.setImageURI(Uri.parse(url));
        }
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

    public void setOnMenuListener(OnClickMoreMenuListener listener){
        this.menuListener = listener;
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
            case R.id.hover_audio_more:
                if(menuListener != null){
                    menuListener.onClickMoreMenu(v);
                }
                break;
            default:
                break;
        }
    }

    public void setButtonEnabled(boolean enabled){
        btnPlay.setEnabled(enabled);
        btnNext.setEnabled(enabled);
        btnLast.setEnabled(enabled);
    }
}
