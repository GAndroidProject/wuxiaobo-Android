package xiaoe.com.shop.business.course.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Explode;
import android.util.Log;
import android.view.Window;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;

import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;

public class CourseDetailActivity extends XiaoeActivity {

    private static final String TAG = "CourseDetailActivity";

    String imgUrl;
    String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // SimpleDraweeView 转场显示图片的设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//            getWindow().setExitTransition(new Explode());//new Slide()  new Fade()
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }
        setContentView(R.layout.activity_course);
        SimpleDraweeView sdv = (SimpleDraweeView) findViewById(R.id.test_bg);

        Intent transitionIntent = getIntent();
        imgUrl = transitionIntent.getStringExtra("imgUrl");
        type = transitionIntent.getStringExtra("type");

        sdv.setImageURI(imgUrl);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
