package xiaoe.com.shop.business.video.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.events.VideoPlayEvent;
import xiaoe.com.shop.interfaces.OnClickVideoBackListener;

public class VideoActivity extends XiaoeActivity implements View.OnClickListener, OnClickVideoBackListener {
    private static final String TAG = "VideoActivity";
    private TextView playCount;
    private VideoPlayControllerView playControllerView;
    private RelativeLayout videoContentDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }
        setContentView(R.layout.activity_video);
        initViews();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        playControllerView.setPlayUrl("http://1252524126.vod2.myqcloud.com/2919df88vodtranscq1252524126/b57d493a5285890781745904784/v.f30.mp4");
    }

    private void initViews() {
        Intent intent = getIntent();
        String videoImageUrl = intent.getStringExtra("videoImageUrl");

        playControllerView = (VideoPlayControllerView) findViewById(R.id.video_play_controller);
        playControllerView.setOnClickVideoBackListener(this);
        playControllerView.setPreviewImage(videoImageUrl);

        videoContentDetail = (RelativeLayout) findViewById(R.id.video_content_detail);

        playCount = (TextView) findViewById(R.id.play_num);
        playCount.setText("100 次播放");
    }

    @Override
    public void onBackPressed() {
        if(playControllerView.isFullScreen()){
            playControllerView.setFullScreen(false);
            setPlayScreen(VideoPlayConstant.VIDEO_LITTLE_SCREEN);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(VideoPlayEvent event) {
        int state = event.getState();
        switch (state){
            case VideoPlayConstant.VIDEO_FULL_SCREEN:
                setPlayScreen(VideoPlayConstant.VIDEO_FULL_SCREEN);
                break;
            case VideoPlayConstant.VIDEO_LITTLE_SCREEN:
                setPlayScreen(VideoPlayConstant.VIDEO_LITTLE_SCREEN);
                break;
            default:
                break;
        }
    }

    private void setPlayScreen(int videoLittleScreen) {
        if(videoLittleScreen == VideoPlayConstant.VIDEO_FULL_SCREEN){
            videoContentDetail.setVisibility(View.GONE);

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


            RelativeLayout.LayoutParams playLayoutParam = (RelativeLayout.LayoutParams) playControllerView.getLayoutParams();
            playLayoutParam.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            playLayoutParam.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            playControllerView.setLayoutParams(playLayoutParam);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            videoContentDetail.setVisibility(View.VISIBLE);

            getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams playLayoutParam = (RelativeLayout.LayoutParams) playControllerView.getLayoutParams();
            playLayoutParam.height = Dp2Px2SpUtil.dp2px(this, 210);
            playControllerView.setLayoutParams(playLayoutParam);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        playControllerView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playControllerView.release();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBack(View view, int type) {
        if(type == VideoPlayConstant.VIDEO_FULL_SCREEN){

        }else{
            onBackPressed();
        }
    }
}
