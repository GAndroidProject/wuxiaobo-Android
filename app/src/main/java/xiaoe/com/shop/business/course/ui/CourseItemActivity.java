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
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
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
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.widget.PushScrollView;

public class CourseItemActivity extends XiaoeActivity {

    private static final String TAG = "CourseItemActivity";

    private Unbinder unbinder;

    @BindView(R.id.item_detail_wrap)
    PushScrollView itemWrap;
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
    TextView itemOrgContent;

    private CourseItemHandler courseItemHandler;

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
        initListener();
    }

    private void initListener() {
        itemBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        itemBg.setImageURI(imgUrl);
        itemDescImg.setImageURI("http://pic6.nipic.com/20100417/4578581_140045259657_2.jpg");
        courseItemHandler = new CourseItemHandler(this);
        final String html = "<div>我&nbsp;是&nbsp;div</div><br/><span>我是 span</span><br/><h1>h1 标题</h1><br/><img src=\"http://pic6.nipic.com/20100417/4578581_140045259657_2.jpg\"/><img src=\"http://pic.58pic.com/58pic/15/62/48/98Z58PICyiN_1024.jpg\"/>";
        // 涉及到网络请求，所以放到子线程中处理
        new Thread(new Runnable() {
            @Override
            public void run() {
                CharSequence charSequence = Html.fromHtml(html, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        // source 是 img 标签的 src，多个会执行多次
                        return getImageFromNetwork(source);
                    }
                }, null);
                Message message = courseItemHandler.obtainMessage();
                message.what = 0;
                message.obj = charSequence;
                courseItemHandler.sendMessage(message);
            }
        }).start();
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

    /**
     * 获取网络图片
     * @param imgUrl 网络图片链接
     * @return 网络图片 drawable
     */
    private Drawable getImageFromNetwork(String imgUrl) {
        Drawable drawable;
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            drawable = Drawable.createFromStream(is, null);
            // 获取到 drawable 后需要给它设置边界，不然不会现出来
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            // textView 的宽度
            int width = itemOrgContent.getWidth();
            // 取图片的高度转换成 px
            int height = Dp2Px2SpUtil.dp2px(CourseItemActivity.this, drawable.getIntrinsicHeight());
            drawable.setBounds(0 , 0, width, height);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return drawable;
    }

    private static class CourseItemHandler extends Handler {
        WeakReference<Activity> weakReference;

        CourseItemHandler(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0 && msg.obj != null) {
                ((CourseItemActivity) weakReference.get()).itemOrgContent.setText((CharSequence) msg.obj);
                ((CourseItemActivity) weakReference.get()).itemOrgContent.setClickable(true);
                ((CourseItemActivity) weakReference.get()).itemOrgContent.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }
}
