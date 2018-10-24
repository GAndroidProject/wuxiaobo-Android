package xiaoe.com.shop.business.course.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.smtt.sdk.WebView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.course.presenter.CourseItemPresenter;
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
    WebView itemOrgContent;

    String imgUrl;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 发送购买前网络请求判断是否已经购买
        CourseItemPresenter courseItemPresenter = new CourseItemPresenter(this);
        courseItemPresenter.requestBeforeBuy();
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

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        int code = result.getInteger("code");
        if (success) {
//            if () {
//
//            }
        }
    }
}
