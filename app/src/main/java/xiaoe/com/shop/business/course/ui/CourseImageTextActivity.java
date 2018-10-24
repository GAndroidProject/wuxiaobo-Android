package xiaoe.com.shop.business.course.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.utils.NetworkState;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.CourseITAfterBuyRequest;
import xiaoe.com.network.requests.CourseITBeforeBuyRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.course.presenter.CourseImageTextPresenter;
import xiaoe.com.shop.widget.CommonBuyView;
import xiaoe.com.shop.widget.PushScrollView;
import xiaoe.com.shop.widget.StatusPagerView;

public class CourseImageTextActivity extends XiaoeActivity {

    private static final String TAG = "CourseImageTextActivity";

    private Unbinder unbinder;

    @BindView(R.id.image_text_wrap)
    PushScrollView itWrap;

    @BindView(R.id.image_text_bg)
    SimpleDraweeView itBg;
    @BindView(R.id.image_text_back)
    ImageView itBack;

    @BindView(R.id.image_text_title)
    TextView itTitle;
    @BindView(R.id.image_text_collection)
    ImageView itCollection;
    @BindView(R.id.image_text_share)
    ImageView itShare;
    @BindView(R.id.image_text_desc)
    TextView itDesc;

    @BindView(R.id.image_text_advertise_img)
    SimpleDraweeView itDescImg;
    @BindView(R.id.image_text_advertise_title)
    TextView itDescTitle;
    @BindView(R.id.image_text_advertise_desc)
    TextView itDescDesc;
    @BindView(R.id.image_text_advertise_btn)
    TextView itBtn;

    @BindView(R.id.image_text_org_content)
    WebView itOrgContent;

    @BindView(R.id.image_text_buy)
    CommonBuyView itBuy;

    @BindView(R.id.image_text_loading)
    StatusPagerView itLoading;

    // 图文图片的链接
    String imgUrl;

    // 图文处理工具类
    CourseImageTextPresenter courseImageTextPresenter = null;

    String resourceId; // 资源 id
    String resourceType; // 资源类型

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_image_text);

        // SimpleDraweeView 转场显示图片的设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }

        unbinder = ButterKnife.bind(this);

        // 从上一个 activity 中获取图文的图片链接
        Intent transitionIntent = getIntent();
        imgUrl = transitionIntent.getStringExtra("imgUrl");
        resourceId = transitionIntent.getStringExtra("resourceId");
        resourceType = "1"; // 图文的资源类型为 1

        initData();
        initViews();
        initListener();
    }

    private void initData() {
        // 发送购买前网络请求判断是否已经购买
        courseImageTextPresenter = new CourseImageTextPresenter(this);
        courseImageTextPresenter.requestBeforeBuy(resourceId, resourceType);
        // 显示 loading
        itLoading.setHintStateVisibility(View.GONE);
        itLoading.setLoadingState(View.VISIBLE);
    }

    private void initListener() {
        itBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        itBg.setImageURI(imgUrl);
        itDescImg.setImageURI("res:///" + R.mipmap.img_text_bg);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        JSONObject data = (JSONObject) result.get("data");
        if (success) {
            if (iRequest instanceof CourseITBeforeBuyRequest) {
                if (code == NetworkCodes.CODE_SUCCEED) {
                    initBeforeBuyData(data);
                    itLoading.setVisibility(View.GONE);
                } else if (code == NetworkCodes.CODE_GOODS_GROUPS_DELETE) {
                    Log.d(TAG, "onMainThreadResponse: 商品分组已被删除");
                } else if (code == NetworkCodes.CODE_GOODS_NOT_FIND) {
                    Log.d(TAG, "onMainThreadResponse: 商品不存在");
                }
            } else if (iRequest instanceof CourseITAfterBuyRequest) {
                Log.d(TAG, "onMainThreadResponse: 成功请求购买后的接口");
            }
        }
    }

    // 初始化购买前的数据
    private void initBeforeBuyData(JSONObject data) {
        // 获取接口返回的资源信息 resource_info
        JSONObject resourceInfo = (JSONObject) data.get("resource_info");
        boolean hasBuy = resourceInfo.getInteger("has_buy") == 1; // 0 没买，1 买了
        if (hasBuy) { // 已经买了，请求购买后接口
            courseImageTextPresenter.requestAfterBuy();
        } else { // 没买，显示没买的数据
            // 富文本内容
            String orgContent = resourceInfo.getString("content");
            if (orgContent.equals("")) { // 表示没有买，不能看图文的内容，此时需要拿 preview_content 的内容
                orgContent = resourceInfo.getString("preview_content");
            }
            setOrgContent(orgContent);
            // 标题
            String title = resourceInfo.getString("title");
            // 订阅量
            int purchaseCount = resourceInfo.getInteger("purchase_count");
            String purchaseStr;
            if (purchaseCount > 0 && purchaseCount < 10000) {
                purchaseStr = purchaseCount + "次阅读";
                itDesc.setText(purchaseStr);
            } else if (purchaseCount > 10000) {
                purchaseStr = (purchaseCount / 10000) + "w次阅读";
                itDesc.setText(purchaseStr);
            } else {
                // 如果没有阅读量的话就将文本设置为空，并且将前面的点页隐藏
                itDesc.setText("");
                itDesc.setCompoundDrawables(null, null, null, null);
            }
            // 是否可以购买
            boolean canBuy = resourceInfo.getInteger("is_can_buy") == 1;
            float price = resourceInfo.getInteger("price") == null ? 0 : resourceInfo.getInteger("price"); // 拿到分，需要除 100 拿到元
            if (price != 0) {
                price = price / 100;
                Log.d(TAG, "initBeforeBuyData: price --- " + price);
            }
            itTitle.setText(title);
            if (canBuy) {
                itBuy.setVisibility(View.VISIBLE);
                itBuy.setBuyBtnText("购买￥" + price);
            } else {
                itBuy.setVisibility(View.GONE);
            }
        }
    }

    // 初始化富文本数据
    private void setOrgContent(String orgContent) {
        if (orgContent.equals("")) {
            itOrgContent.setVisibility(View.GONE);
            return;
        }
        itOrgContent.loadDataWithBaseURL(null, NetworkState.getNewContent(orgContent), "text/html", "UFT-8", null);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }
}
