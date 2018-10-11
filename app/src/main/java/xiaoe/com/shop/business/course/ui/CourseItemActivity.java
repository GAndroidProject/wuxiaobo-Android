package xiaoe.com.shop.business.course.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;

public class CourseItemActivity extends XiaoeActivity {

    private static final String TAG = "CourseItemActivity";

    private Unbinder unbinder;

    @BindView(R.id.item_detail_bg)
    SimpleDraweeView itemBg;
    @BindView(R.id.item_detail_back)
    ImageView itemBack;
    @BindView(R.id.item_detail_classify)
    TextView itemClassify;
    @BindView(R.id.item_detail_title)
    TextView itemTitle;
    @BindView(R.id.item_detail_collection)
    ImageView itemCollection;
    @BindView(R.id.item_detail_share)
    ImageView itemShare;
    @BindView(R.id.item_detail_desc)
    TextView itemDesc;
    @BindView(R.id.item_detail_desc_img)
    SimpleDraweeView itemDescImg;
    @BindView(R.id.item_detail_btn)
    TextView itemBtn;
    @BindView(R.id.item_detail_org_content)
    WebView itemOrgContent;

    String imgUrl;
    String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // SimpleDraweeView 转场显示图片的设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }
        setContentView(R.layout.activity_course_item);
        unbinder = ButterKnife.bind(this);

        Intent transitionIntent = getIntent();
        imgUrl = transitionIntent.getStringExtra("imgUrl");
        type = transitionIntent.getStringExtra("type");

        initViews();
    }

    private void initViews() {
        itemBg.setImageURI(imgUrl);
        itemDescImg.setImageURI("http://pic6.nipic.com/20100417/4578581_140045259657_2.jpg");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

//    private Drawable getImageFromNetwork(String imgUrl) {
//        Drawable drawable;
//        try {
//            URL url = new URL(imgUrl);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setDoInput(true);
//            conn.connect();
//            InputStream is = conn.getInputStream();
//            drawable = Drawable.createFromStream(is, null);
//            is.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return drawable;
//    }

//    private static class CourseItemHandler extends Handler {
//        WeakReference<Activity> weakReference;
//
//        CourseItemHandler(Activity activity) {
//            weakReference = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    }
}
