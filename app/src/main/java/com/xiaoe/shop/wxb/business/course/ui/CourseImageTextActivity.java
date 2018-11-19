package com.xiaoe.shop.wxb.business.course.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.umeng.socialize.UMShareAPI;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.NetworkState;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.AddCollectionRequest;
import com.xiaoe.network.requests.CheckCollectionRequest;
import com.xiaoe.network.requests.CourseITDetailRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.RemoveCollectionListRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.course.presenter.CourseImageTextPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.interfaces.OnCustomScrollChangedListener;
import com.xiaoe.shop.wxb.utils.ActivityCollector;
import com.xiaoe.shop.wxb.utils.CollectionUtils;
import com.xiaoe.shop.wxb.utils.NumberFormat;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.utils.UpdateLearningUtils;
import com.xiaoe.shop.wxb.widget.CommonBuyView;
import com.xiaoe.shop.wxb.widget.CommonTitleView;
import com.xiaoe.shop.wxb.widget.CustomScrollView;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CourseImageTextActivity extends XiaoeActivity implements OnCustomScrollChangedListener, OnRefreshListener {

    private static final String TAG = "CourseImageTextActivity";

    private Unbinder unbinder;

    @BindView(R.id.it_wrap)
    FrameLayout itWrap;

    @BindView(R.id.it_refresh)
    SmartRefreshLayout itRefresh;
    @BindView(R.id.it_custom_scrollview)
    CustomScrollView itCustomScrollView;
    @BindView(R.id.it_title_bg)
    SimpleDraweeView itBg;
    @BindView(R.id.it_title_back)
    ImageView itBack;
    @BindView(R.id.it_common_title_view)
    CommonTitleView itToolbar;
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
//    @BindView(R.id.image_text_advertise_title)
//    TextView itDescTitle;
//    @BindView(R.id.image_text_advertise_desc)
//    TextView itDescDesc;
//    @BindView(R.id.image_text_advertise_btn)
//    TextView itDescBtn;

    @BindView(R.id.image_text_org_content)
    WebView itOrgContent;

    @BindView(R.id.image_text_buy)
    CommonBuyView itBuy;

//    @BindView(R.id.image_text_loading)
//    StatusPagerView itLoading;

    // 图文图片的链接
    String imgUrl;

    // 图文处理工具类
    CourseImageTextPresenter courseImageTextPresenter = null;
    CollectionUtils collectionUtils = null;

    String resourceId; // 资源 id
    String resourceType; // 资源类型

    boolean isCollected; // 是否收藏

    // 需要收藏的字段
    String collectionTitle;
    String collectionAuthor;
    String collectionImgUrl;
    String collectionImgUrlCompressed;
    String collectionPrice;
    private int resPrice = 0;
    boolean hasBuy;

    List<LoginUser> loginList;
    int toolbarHeight;

    TouristDialog touristDialog;

    String shareUrl;
    String realSrcId; // 因为信息流的 id 的和实际的 id 不一样，而上报需要真实 id 所以需要一个真实 id

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_course_image_text);

        unbinder = ButterKnife.bind(this);

        itWrap.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);

        toolbarHeight = Dp2Px2SpUtil.dp2px(this,100);

        // SimpleDraweeView 转场显示图片的设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }

        // 从上一个 activity 中获取图文的图片链接
        Intent transitionIntent = getIntent();
        imgUrl = transitionIntent.getStringExtra("imgUrl");
        resourceId = transitionIntent.getStringExtra("resourceId");
        resourceType = "1"; // 图文的资源类型为 1
        loginList = getLoginUserList();

        if (loginList.size() == 0) {
            touristDialog = new TouristDialog(this);
            touristDialog.setDialogCloseClickListener(v -> touristDialog.dismissDialog());
            touristDialog.setDialogConfirmClickListener(v -> {
                touristDialog.dismissDialog();
                JumpDetail.jumpLogin(CourseImageTextActivity.this, true);
            });
        }

        initTitle();
        initData();
        initViews();
        initListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }

    private void initTitle() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        itToolbar.setVisibility(View.VISIBLE);
        itToolbar.setTitleEndVisibility(View.GONE);
        itToolbar.setTitleBackVisibility(View.GONE);
        itToolbar.setTitleContentTextVisibility(View.GONE);
        itToolbar.setBackgroundColor(Color.argb(0,255,255,255));
    }

    private void initData() {
        // 发送购买前网络请求判断是否已经购买
        courseImageTextPresenter = new CourseImageTextPresenter(this);
        collectionUtils = new CollectionUtils(this);
//        courseImageTextPresenter.requestBeforeBuy(resourceId, resourceType);
        courseImageTextPresenter.requestITDetail(resourceId, Integer.parseInt(resourceType));
        // 请求检查该商品是否已经被收藏
        collectionUtils.requestCheckCollection(resourceId, resourceType);
        // 显示 loading
//        itLoading.setHintStateVisibility(View.GONE);
//        itLoading.setLoadingState(View.VISIBLE);
        itDescImg.setClickable(false);
    }

    private void initListener() {
        itRefresh.setOnRefreshListener(this);
        itCustomScrollView.setScrollChanged(this);
        itBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        itCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginList.size() == 1) {
                    if (isCollected) { // 收藏了，点击之后取消收藏
                        itCollection.setImageDrawable(getResources().getDrawable(R.mipmap.video_collect));
                        collectionUtils.requestRemoveCollection(resourceId, resourceType);
                    } else { // 没有收藏，点击之后收藏\
                        // 改变图标
                        itCollection.setImageDrawable(getResources().getDrawable(R.mipmap.audio_collect));
                        JSONObject collectionContent = new JSONObject();
                        collectionContent.put("title", collectionTitle);
                        collectionContent.put("author", collectionAuthor);
                        collectionContent.put("img_url", collectionImgUrl);
                        collectionContent.put("img_url_compressed", collectionImgUrlCompressed);
                        collectionContent.put("price", collectionPrice);
                        collectionUtils.requestAddCollection(resourceId, resourceType, collectionContent);
                    }
                } else {
                    touristDialog.showDialog();
                }
            }
        });
        itShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginList.size() == 1) {
                    umShare(collectionTitle, collectionImgUrlCompressed == null ? collectionImgUrl : collectionImgUrlCompressed, shareUrl, "");
                } else {
                    touristDialog.showDialog();
                }
            }
        });
        itBuy.setOnVipBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginList.size() == 1) {
                    JumpDetail.jumpSuperVip(CourseImageTextActivity.this);
                } else {
                    touristDialog.showDialog();
                }
            }
        });
        itBuy.setOnBuyBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginList.size() == 1) {
                    JumpDetail.jumpPay(CourseImageTextActivity.this, resourceId, 1, collectionImgUrl, collectionTitle, resPrice);
                } else {
                    touristDialog.showDialog();
                }
            }
        });
        itToolbar.setTitleBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        itDescImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.finishAll();
                if (loginList.size() == 1) {
                    JumpDetail.jumpMainScholarship(CourseImageTextActivity.this, true, true);
                } else {
                    JumpDetail.jumpMainScholarship(CourseImageTextActivity.this, false, true);
                }
            }
        });
    }

    private void initViews() {
        if (imgUrl != null) {
            itBg.setImageURI("");
        }
        String descImgUrl = "res:///" + R.mipmap.img_text_bg;
        SetImageUriUtil.setImgURI(itDescImg, descImgUrl, Dp2Px2SpUtil.dp2px(this, 375), Dp2Px2SpUtil.dp2px(this, 100));
        if (CommonUserInfo.isIsSuperVipAvailable() && !CommonUserInfo.isIsSuperVip()) {
            itBuy.setVipBtnVisibility(View.VISIBLE);
        } else {
            itBuy.setVipBtnVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int code = getWXPayCode(true);
        if(code == 0){
            getDialog().showLoadDialog(false);
            courseImageTextPresenter.requestBeforeBuy(resourceId, resourceType);
        }
        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
    }

    @Override
    public void onBackPressed() {
        // TODO: 获取学习进度
        if (hasBuy) { // 买了才需要上报
            UpdateLearningUtils updateLearningUtils = new UpdateLearningUtils(this);
            updateLearningUtils.updateLearningProgress(realSrcId, Integer.parseInt(resourceType), 10);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        itOrgContent.destroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if(activityDestroy){
            return;
        }
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof CheckCollectionRequest) {
                int code = result.getInteger("code");
                try {
                    JSONObject data = (JSONObject) result.get("data");
                    if (code == NetworkCodes.CODE_SUCCEED) {
                        initCollectionData(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (iRequest instanceof AddCollectionRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    Toast("收藏成功");
                    isCollected = !isCollected;
                } else if (code == NetworkCodes.CODE_COLLECT_FAILED) {
                    Toast("收藏失败");
                }
            } else if (iRequest instanceof RemoveCollectionListRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    Toast("取消收藏成功");
                    isCollected = !isCollected;
                } else if (code == NetworkCodes.CODE_DELETE_COLLECT_FAILED) {
                    Toast("取消收藏失败");
                }
            } else if (iRequest instanceof CourseITDetailRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    initPageData(data);
                    itRefresh.finishRefresh();
                } else if (code == NetworkCodes.CODE_GOODS_GROUPS_DELETE) {
                    Log.d(TAG, "onMainThreadResponse: 商品分组已被删除");
                    itRefresh.finishRefresh();
                } else if (code == NetworkCodes.CODE_GOODS_NOT_FIND) {
                    Log.d(TAG, "onMainThreadResponse: 商品不存在");
                    itRefresh.finishRefresh();
                }
            }
        } else {
            onBackPressed();
        }
    }

    // 初始化页面信息
    private void initPageData(JSONObject data) {
        // 获取接口返回的资源信息 resource_info

        JSONObject resourceInfo = (JSONObject) data.get("resource_info");
        hasBuy = data.getBoolean("available"); // false 没买，true 买了
        if (hasBuy) {
            imgUrl = data.getString("img_url") == null ? "" : data.getString("img_url");
        } else { // 已购的话，都用未压缩图片
            imgUrl = resourceInfo.getString("img_url") == null ? resourceInfo.getString("img_url_compressed") : resourceInfo.getString("img_url");
            // 未购数据格式，share_info 跟 resource_info 同级，所以在这里取到
            JSONObject shareInfo = (JSONObject) data.get("share_info");
            JSONObject wx = (JSONObject) shareInfo.get("wx");
            shareUrl = wx.getString("share_url");
        }
        if (resourceInfo == null) { // 已购
            initData(data);
        } else { // 未购
            initData(resourceInfo);
        }
    }

    private void initData(JSONObject data) {
        realSrcId = data.getString("resource_id");
        SetImageUriUtil.setImgURI(itBg, imgUrl, Dp2Px2SpUtil.dp2px(this, 375), Dp2Px2SpUtil.dp2px(this, 250));
        // 没有就那预览的内容
        String orgContent = data.getString("content") == null ? data.getString("preview_content") : data.getString("content");
        setOrgContent(orgContent);
        // 标题
        String title = data.getString("title");
        // 订阅量
        int purchaseCount = data.getInteger("view_count") == null ? 0 : data.getInteger("view_count");
        if (purchaseCount > 0 && purchaseCount < 10000) {
            String purchaseStr = NumberFormat.viewCountToString(purchaseCount) + "次阅读";
            itDesc.setText(purchaseStr);
        } else {
            // 如果没有阅读量的话就将文本设置为空，并且将前面的点页隐藏
            itDesc.setText("");
            itDesc.setCompoundDrawables(null, null, null, null);
        }
        // 是否可以购买
        float price = data.getInteger("price") == null ? 0 : data.getInteger("price"); // 拿到分，需要除 100 拿到元
        resPrice = (int) price;
        if (price != 0) {
            price = price / 100;
            Log.d(TAG, "initBeforeBuyData: price --- " + price);
        }
        itTitle.setText(title);
        if (!hasBuy) { // 没买
            itBuy.setVisibility(View.VISIBLE);
            itBuy.setBuyBtnText("购买￥" + price);
        } else {
            itBuy.setVisibility(View.GONE);
        }

        itToolbar.setTitleContentText(title);
        // 将需要收藏的字段赋值（未购）
        collectionTitle = title;
        collectionAuthor = data.getString("author");
        // 未购的图片链接都是压缩的图片链接
        collectionImgUrl = imgUrl;
        collectionImgUrlCompressed = imgUrl;
        collectionPrice = price + "";
        // 购买前初始化完成，去掉 loading
//      itLoading.setVisibility(View.GONE);
        itDescImg.setClickable(true);
    }

    // 初始化富文本数据
    private void setOrgContent(String orgContent) {
        if ("".equals(orgContent)) {
            itOrgContent.setVisibility(View.GONE);
            return;
        }
        itOrgContent.loadDataWithBaseURL(null, NetworkState.getNewContent(orgContent), "text/html", "UFT-8", null);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    // 初始化收藏信息
    private void initCollectionData(JSONObject data) {
        int isFavorite = data.getInteger("is_favorite") == null ? -1 : data.getInteger("is_favorite");
        if (isFavorite != -1) {
            isCollected = isFavorite == 1;
            if (isCollected) { // 收藏了
                itCollection.setImageDrawable(getResources().getDrawable(R.mipmap.audio_collect));
            } else {
                itCollection.setImageDrawable(getResources().getDrawable(R.mipmap.video_collect));
            }
        }
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        float alpha = (t / (toolbarHeight * 1.0f)) * 255;
        if(alpha > 255){
            alpha = 255;
        }else if(alpha < 0){
            alpha = 0;
        }
        itToolbar.setBackgroundColor(Color.argb((int) alpha,255,255,255));
        if (alpha == 255) {
            itToolbar.setTitleBackVisibility(View.VISIBLE);
            itToolbar.setTitleContentTextVisibility(View.VISIBLE);
            itBack.setVisibility(View.GONE);
        } else {
            itToolbar.setTitleBackVisibility(View.GONE);
            itToolbar.setTitleContentTextVisibility(View.GONE);
            itBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoadState(int state) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (courseImageTextPresenter == null) {
            courseImageTextPresenter = new CourseImageTextPresenter(this);
        }
        courseImageTextPresenter.requestITDetail(resourceId, Integer.parseInt(resourceType));
    }
}
