package com.xiaoe.shop.wxb.business.course.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.socialize.UMShareAPI;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.CacheData;
import com.xiaoe.common.entitys.ChangeLoginIdentityEvent;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.Base64Util;
import com.xiaoe.common.utils.CacheDataUtil;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.NetworkState;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.AddCollectionRequest;
import com.xiaoe.network.requests.CourseITDetailRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.RemoveCollectionListRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.course.presenter.CourseImageTextPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.MyCollectListRefreshEvent;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.utils.CollectionUtils;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.utils.UpdateLearningUtils;
import com.xiaoe.shop.wxb.widget.CommonBuyView;
import com.xiaoe.shop.wxb.widget.CommonTitleView;
import com.xiaoe.shop.wxb.widget.PushScrollView;
import com.xiaoe.shop.wxb.widget.StatusPagerView;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CourseImageTextActivity extends XiaoeActivity implements PushScrollView.ScrollViewListener {

    private static final String TAG = "CourseImageTextActivity";

    private Unbinder unbinder;

    @BindView(R.id.it_wrap)
    FrameLayout itWrap;

    @BindView(R.id.it_push_scrollview)
    PushScrollView itPushScrollView;
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

//    @BindView(R.id.image_text_advertise_img)
//    SimpleDraweeView itDescImg;
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

    @BindView(R.id.image_text_loading)
    StatusPagerView itLoading;

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
    private String columnId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_course_image_text);

        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);

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
        columnId = transitionIntent.getStringExtra("columnId");
        resourceType = "1"; // 图文的资源类型为 1
        loginList = getLoginUserList();

        if (loginList.size() == 0) {
            touristDialog = new TouristDialog(this);
            touristDialog.setDialogCloseClickListener(v -> touristDialog.dismissDialog());
            touristDialog.setDialogConfirmClickListener(v -> {
                touristDialog.dismissDialog();
                JumpDetail.jumpLogin(mContext, true);
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
        setDataByDB();
        courseImageTextPresenter.requestITDetail(resourceId, Integer.parseInt(resourceType));
        // 请求检查该商品是否已经被收藏
        // collectionUtils.requestCheckCollection(resourceId, resourceType);
        // 显示 loading
        itLoading.setLoadingState(View.VISIBLE);
        itLoading.setVisibility(View.VISIBLE);
        // itDescImg.setClickable(false);
    }

    private void initListener() {
//        itPushScrollView.setScrollChanged(this);
        itPushScrollView.setScrollViewListener(this);
        itBack.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                onBackPressed();
            }
        });
        itCollection.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (loginList.size() == 1) {
                    if (isCollected) { // 收藏了，点击之后取消收藏
                        itCollection.setImageDrawable(getResources().getDrawable(R.mipmap.video_collect));
                        collectionUtils.requestRemoveCollection(resourceId, resourceType);
                    } else { // 没有收藏，点击之后收藏
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
        itShare.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (loginList.size() == 1) {
                    umShare(collectionTitle, collectionImgUrlCompressed == null ? collectionImgUrl : collectionImgUrlCompressed, shareUrl, "");
                } else {
                    touristDialog.showDialog();
                }
            }
        });
        itBuy.setOnVipBtnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (loginList.size() == 1) {
                    JumpDetail.jumpSuperVip(mContext);
                } else {
                    touristDialog.showDialog();
                }
            }
        });
        itBuy.setOnBuyBtnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (loginList.size() == 1) {
                    JumpDetail.jumpPay(mContext, resourceId, 1, collectionImgUrl, collectionTitle, resPrice, null);
                } else {
                    touristDialog.showDialog();
                }
            }
        });
        itToolbar.setTitleBackClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                onBackPressed();
            }
        });
//        itDescImg.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
//            @Override
//            public void singleClick(View v) {
//                if (loginList.size() == 1) {
//                    JumpDetail.jumpMainTab(mContext, true, true, 2);
//                } else {
//                    JumpDetail.jumpMainTab(mContext, false, true, 2);
//                }
//            }
//        });
        itLoading.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (courseImageTextPresenter == null) {
                    courseImageTextPresenter = new CourseImageTextPresenter(CourseImageTextActivity.this);
                }
                if (itLoading.getCurrentLoadingStatus() == StatusPagerView.FAIL) { // 网络错误
                    courseImageTextPresenter.requestITDetail(resourceId, Integer.parseInt(resourceType));
                }
            }
        });
    }

    private void initViews() {
        if (imgUrl != null) {
            itBg.setImageURI("");
        }
        // String descImgUrl = "res:///" + R.mipmap.img_text_bg;
        // SetImageUriUtil.setImgURI(itDescImg, descImgUrl, Dp2Px2SpUtil.dp2px(this, 375), Dp2Px2SpUtil.dp2px(this, 250));
        if (CommonUserInfo.isIsSuperVipAvailable() && !CommonUserInfo.isIsSuperVip()) {
            if (CommonUserInfo.getSuperVipEffective() == 1) { // 全店免费
                itBuy.setVipBtnVisibility(View.VISIBLE);
            } else {
                itBuy.setVipBtnVisibility(View.GONE);
            }
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
            courseImageTextPresenter.requestITDetail(resourceId, Integer.parseInt(resourceType));
        }
        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
    }

    @Override
    public void onBackPressed() {
        // 获取学习进度
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
        UMShareAPI.get(this).release();
        if (!isCollected)
            EventBus.getDefault().post(new MyCollectListRefreshEvent(true,resourceId));
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventArrived(ChangeLoginIdentityEvent changeLoginIdentityEvent) {
        if (changeLoginIdentityEvent != null && changeLoginIdentityEvent.isChangeSuccess()) {
            if (courseImageTextPresenter == null) {
                courseImageTextPresenter = new CourseImageTextPresenter(this);
            }
            courseImageTextPresenter.requestITDetail(resourceId, Integer.parseInt(resourceType));
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if(activityDestroy){
            return;
        }
        try{
            handleData(iRequest, success, (JSONObject) entity);
        }catch (Exception e){
            e.printStackTrace();
            Toast(getString(R.string.no_network_at_present));
        }
    }

    private void handleData(IRequest iRequest, boolean success, JSONObject entity) {
        JSONObject result = entity;
        if (success) {
            if (iRequest instanceof AddCollectionRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    Toast(getString(R.string.collect_succeed));
                    isCollected = !isCollected;
                } else if (code == NetworkCodes.CODE_COLLECT_FAILED) {
                    Toast(getString(R.string.collect_fail));
                }
            } else if (iRequest instanceof RemoveCollectionListRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    Toast(getString(R.string.cancel_collect_succeed));
                    isCollected = !isCollected;
                } else if (code == NetworkCodes.CODE_DELETE_COLLECT_FAILED) {
                    Toast(getString(R.string.cancel_collect_fail));
                }
            } else if (iRequest instanceof CourseITDetailRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    initPageData(data, false);
                } else if (code == NetworkCodes.CODE_GOODS_GROUPS_DELETE || code == NetworkCodes.CODE_GOODS_DELETE) {
                    Log.d(TAG, "onMainThreadResponse: 商品分组已被删除");
                    setPagerState(NetworkCodes.CODE_GOODS_DELETE);
                } else if (code == NetworkCodes.CODE_GOODS_NOT_FIND) {
                    Log.d(TAG, "onMainThreadResponse: 商品不存在");
                    setPagerState(1);
                }else{
                    setPagerState(1);
                }
            }
        } else {
            onBackPressed();
        }
    }

    // 初始化页面信息
    private void initPageData(JSONObject data, boolean cache) {
        // 获取接口返回的资源信息 resource_info
        getDialog().dismissDialog();
        JSONObject resourceInfo = (JSONObject) data.get("resource_info");
        hasBuy = data.getBoolean("available"); // false 没买，true 买了
        if (hasBuy) { // 已购
            imgUrl = data.getString("img_url") == null ? "" : data.getString("img_url");
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 0);
            itPushScrollView.setLayoutParams(layoutParams);
        } else { // 未购
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, Dp2Px2SpUtil.dp2px(this, 44));
            itPushScrollView.setLayoutParams(layoutParams);
            imgUrl = resourceInfo.getString("img_url") == null ? resourceInfo.getString("img_url_compressed") : resourceInfo.getString("img_url");
            // 未购数据格式，share_info 跟 resource_info 同级，所以在这里取到
            JSONObject shareInfo = (JSONObject) data.get("share_info");
            JSONObject wx = (JSONObject) shareInfo.get("wx");
            shareUrl = wx.getString("share_url");
            if(!TextUtils.isEmpty(columnId)){
                JSONObject shareJSON = Base64Util.base64ToJSON(shareUrl.substring(shareUrl.lastIndexOf("/")+1));
                shareJSON.put("product_id", columnId);
                shareUrl = shareUrl.substring(0, shareUrl.lastIndexOf("/") + 1) + Base64Util.jsonToBase64(shareJSON);
            }
            int hasFavorite = ((JSONObject) data.get("favorites_info")).getInteger("is_favorite");
            if (hasFavorite != -1) {
                isCollected = hasFavorite == 1;
                if (isCollected) { // 收藏了
                    itCollection.setImageDrawable(getResources().getDrawable(R.mipmap.audio_collect));
                } else {
                    itCollection.setImageDrawable(getResources().getDrawable(R.mipmap.video_collect));
                }
            }
        }
        if (resourceInfo == null) { // 已购
            initData(data, cache);
        } else { // 未购
            //1-免费,2-单卖，3-非单卖
            if(resourceInfo.getIntValue("is_related") == 1){
                //如果是仅关联售卖，则把缓存中的数据清除
                SQLiteUtil sqLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new CacheDataUtil());
                sqLiteUtil.delete(CacheDataUtil.TABLE_NAME, "app_id=? and resource_id=? and user_id=?", new String[]{Constants.getAppId(), resourceId, CommonUserInfo.getLoginUserIdOrAnonymousUserId()});
                //非单卖需要跳转到所属专栏，如果所属专栏多个，只跳转第一个
                JSONArray productList = data.getJSONObject("product_info").getJSONArray("product_list");
                if (productList.size() == 0) {
                    setPagerState(3004);
                    return;
                }
                JSONObject product = productList.getJSONObject(0);
                int productType = product.getIntValue("product_type");
                String productId = product.getString("id");
                String productImgUrl = product.getString("img_url");
                if (cache) { // 再缓存取的就不跳了
                    return;
                }
                //1-专栏, 2-会员, 3-大专栏
                if(productType == 3){
                    JumpDetail.jumpColumn(this, productId, productImgUrl, 8);
                }else if(productType == 2){
                    JumpDetail.jumpColumn(this, productId, productImgUrl, 5);
                }else{
                    JumpDetail.jumpColumn(this, productId, productImgUrl, 6);
                }
                finish();
            }else{
                initData(resourceInfo, cache);
            }
        }
    }

    private void initData(JSONObject data, boolean cache) {
        realSrcId = data.getString("resource_id");
        if (SetImageUriUtil.isGif(imgUrl)) {
            SetImageUriUtil.setGifURI(itBg, imgUrl, Dp2Px2SpUtil.dp2px(this, 375), Dp2Px2SpUtil.dp2px(this, 250));
        } else {
            SetImageUriUtil.setImgURI(itBg, imgUrl, Dp2Px2SpUtil.dp2px(this, 375), Dp2Px2SpUtil.dp2px(this, 250));
        }
        // 标题
        String title = data.getString("title");
        // 订阅量
        int purchaseCount = data.getInteger("view_count") == null ? 0 : data.getInteger("view_count");
        if (purchaseCount > 0 && purchaseCount < 10000) {
            String purchaseStr = String.format(getString(R.string.learn_count), purchaseCount);
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
            // 没有就那预览的内容
            String orgContent = data.getString("preview_content");
            setOrgContent(orgContent);
            itBuy.setVisibility(cache ? View.GONE : View.VISIBLE);
            itBuy.setBuyBtnText(getString(R.string.purchase_text) + price);
        } else {
            itBuy.setVisibility(View.GONE);
            String orgContent = data.getString("content");
            setOrgContent(orgContent);
            // 获取已购的分享信息
            shareUrl = ((JSONObject) ((JSONObject) data.get("share_info")).get("wx")).getString("share_url");
            if(!TextUtils.isEmpty(columnId)){
                JSONObject shareJSON = Base64Util.base64ToJSON(shareUrl.substring(shareUrl.lastIndexOf("/")+1));
                shareJSON.put("product_id", columnId);
                shareUrl = shareUrl.substring(0, shareUrl.lastIndexOf("/") + 1) + Base64Util.jsonToBase64(shareJSON);
            }
            // 已购才从 resource_info 中取
            int hasFavorite = ((JSONObject) data.get("favorites_info")).getInteger("is_favorite");
            if (hasFavorite != -1) {
                isCollected = hasFavorite == 1;
                if (isCollected) { // 收藏了
                    itCollection.setImageDrawable(getResources().getDrawable(R.mipmap.audio_collect));
                } else {
                    itCollection.setImageDrawable(getResources().getDrawable(R.mipmap.video_collect));
                }
            }
            // 超级会员的按钮显示

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
//        itDescImg.setClickable(true);

        resourceState(data, hasBuy);
    }

    /**
     * 课程状态
     * @param data
     */
    private void resourceState(JSONObject data, boolean available){
        //是否免费0：否，1：是
        int isFree = data.getIntValue("is_free");
        //0-正常, 1-隐藏, 2-删除
        int detailState = data.getIntValue("state");
        //0-上架,1-下架
        int saleStatus = data.getIntValue("sale_status");
        //是否停售 0:否，1：是
        int isStopSell = data.getIntValue("is_stop_sell");
        //离待上线时间，如有则是待上架
        int timeLeft = data.getIntValue("time_left");

        if(available && detailState != 2){
            //删除状态优秀级最高，available=true是除了删除状态显示删除页面外，其他的均可查看详情
            setPagerState(0);
            return;
        }

        if(saleStatus == 1 || detailState == 1){
            setPagerState(2);
        }else if(isStopSell == 1){
            setPagerState(3);
        }else if(timeLeft > 0){
            setPagerState(4);
        }else if(detailState == 2 ){
            setPagerState(NetworkCodes.CODE_GOODS_DELETE);
        }else {
            setPagerState(0);
        }
    }

    // 初始化富文本数据
    private void setOrgContent(String orgContent) {
        if ("".equals(orgContent)) {
            itOrgContent.setVisibility(View.GONE);
            return;
        } else {
            itOrgContent.setVisibility(View.VISIBLE);
        }
        itOrgContent.loadDataWithBaseURL(null, NetworkState.getNewContent(orgContent), "text/html", "UFT-8", null);
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
    public void onScrollChanged(PushScrollView scrollView, int x, int y, int oldX, int oldY) {
        float alpha = (y / (toolbarHeight * 1.0f)) * 255;
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

    private void setDataByDB(){
        SQLiteUtil sqLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new CacheDataUtil());
        String sql = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()
                +"' and resource_id='"+resourceId+"' and user_id='"+CommonUserInfo.getLoginUserIdOrAnonymousUserId()+"'";
        List<CacheData> cacheDataList = sqLiteUtil.query(CacheDataUtil.TABLE_NAME, sql, null );
        if(cacheDataList != null && cacheDataList.size() > 0){
            JSONObject data = JSONObject.parseObject(cacheDataList.get(0).getContent()).getJSONObject("data");
            try {
                initPageData(data, true);
            } catch (Exception e) {
                Toast(getString(R.string.cache_error_msg));
            }
        }
    }

    /**
     *
     * @param code 0-正常的,1-请求失败,2-课程下架,-1： 加载，3-停售，4-待上架，3004：商品已删除
     */
    private void setPagerState(int code) {
        if(code == 0){
            itLoading.setLoadingFinish();
        }else if(code == 1){
            itLoading.setPagerState(StatusPagerView.FAIL, getString(R.string.request_fail), StatusPagerView.DETAIL_NONE);
        }else if(code == 2){
            itLoading.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_sold_out), R.mipmap.course_off);
        }else if(code == 3){
            itLoading.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_sale_stop), R.mipmap.course_off);
        }else if(code == 4){
            itLoading.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_stay_putaway), R.mipmap.course_off);
        }else if(code == 3004){
            itLoading.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_delete), R.mipmap.course_off);
        }
    }
}
