package xiaoe.com.shop.business.audio.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;

import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;

public class AudioActivity extends XiaoeActivity {
    private static final String TAG = "AudioActivity";
    private SimpleDraweeView audioBG;
    private SimpleDraweeView audioRing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//            getWindow().setExitTransition(new Explode());//new Slide()  new Fade()
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }
        setContentView(R.layout.activity_audio);
        initView();
    }

    private void initView() {
        audioBG = (SimpleDraweeView) findViewById(R.id.audio_bg);
        audioBG.setImageURI("res:///"+R.mipmap.audio_bg);
        audioRing = (SimpleDraweeView) findViewById(R.id.audio_ring);
        audioRing.setImageURI("res:///"+R.mipmap.audio_ring);

    }
}
