package xiaoe.com.shop.business.audio.ui;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;

import xiaoe.com.shop.R;
import xiaoe.com.shop.anim.ViewAnim;
import xiaoe.com.shop.base.XiaoeActivity;

public class AudioActivity extends XiaoeActivity implements View.OnClickListener {
    private static final String TAG = "AudioActivity";
    private static final int MSG_VIEW_STATE = 10001;
    private SimpleDraweeView audioBG;
    private SimpleDraweeView audioRing;
    private ViewAnim mViewAnim;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_VIEW_STATE:
                    initViewState(View.VISIBLE);
                    break;
            }
        }
    };
    private RelativeLayout btnPageClose;
    private TextView audioTitle;
    private TextView playNum;
    private TextView btnSpeedPlay;
    private AudioPlayControllerView audioPlayController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }
        setContentView(R.layout.activity_audio);
        initView();
    }

    private void initView() {
        mViewAnim = new ViewAnim();
        audioBG = (SimpleDraweeView) findViewById(R.id.audio_bg);
        audioBG.setImageURI("res:///"+R.mipmap.audio_bg);
        audioRing = (SimpleDraweeView) findViewById(R.id.audio_ring);
        audioRing.setImageURI("res:///"+R.mipmap.audio_ring);

        //页面关闭按钮
        btnPageClose = (RelativeLayout) findViewById(R.id.audio_page_close_btn);
        btnPageClose.setOnClickListener(this);
        //标题
        audioTitle = (TextView) findViewById(R.id.audio_title);
        audioTitle.setText("我的财富计划 新中产必修财富课程");
        //播放次数
        playNum = (TextView) findViewById(R.id.play_num);
        playNum.setText("1000次播放");

        //倍速按钮
        btnSpeedPlay = (TextView) findViewById(R.id.audio_speed_play);
        //音频播放控制器
        audioPlayController = (AudioPlayControllerView) findViewById(R.id.audio_play_controller);

        initViewState(View.GONE);
        mHandler.sendEmptyMessageDelayed(MSG_VIEW_STATE, 300);
    }

    @Override
    public void onBackPressed() {
        setViewAnim(audioRing, 1, 0, 1, 0, 1, 0);
        setViewAnim(btnPageClose, 1, 0, 1, 0, 1, 0);
        super.onBackPressed();
    }

    private void setViewAnim(final View fromView, float startScaleX, float finaScaleX,
                             float startScaleY, float finaScaleY,
                             float startAlpha, float finalAlpha){
        mViewAnim.startViewSimpleAnim(fromView, startScaleX, finaScaleX, startScaleY, finaScaleY, startAlpha, finalAlpha);
    }

    private void initViewState(int visible) {
        audioRing.setVisibility(visible);
        btnPageClose.setVisibility(visible);
        playNum.setVisibility(visible);
        btnSpeedPlay.setVisibility(visible);
        audioPlayController.setVisibility(visible);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.audio_page_close_btn:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
