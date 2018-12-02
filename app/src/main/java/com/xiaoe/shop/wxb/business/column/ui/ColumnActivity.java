package com.xiaoe.shop.wxb.business.column.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.socialize.UMShareAPI;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.CacheData;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.CacheDataUtil;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.AddCollectionRequest;
import com.xiaoe.network.requests.ColumnListRequst;
import com.xiaoe.network.requests.DetailRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.RemoveCollectionListRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.column.ColumnFragmentStatePagerAdapter;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.ui.MiniAudioPlayControllerLayout;
import com.xiaoe.shop.wxb.business.column.presenter.ColumnPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;
import com.xiaoe.shop.wxb.events.MyCollectListRefreshEvent;
import com.xiaoe.shop.wxb.interfaces.OnCustomScrollChangedListener;
import com.xiaoe.shop.wxb.utils.CollectionUtils;
import com.xiaoe.shop.wxb.utils.NumberFormat;
import com.xiaoe.shop.wxb.utils.UpdateLearningUtils;
import com.xiaoe.shop.wxb.widget.CommonBuyView;
import com.xiaoe.shop.wxb.widget.CustomScrollView;
import com.xiaoe.shop.wxb.widget.ListBottomLoadMoreView;
import com.xiaoe.shop.wxb.widget.ScrollViewPager;
import com.xiaoe.shop.wxb.widget.StatusPagerView;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ColumnActivity extends XiaoeActivity implements View.OnClickListener, OnCustomScrollChangedListener {
    private static final String TAG = "ColumnActivity";
    public static int RESOURCE_TYPE_TOPIC = 8;//大专栏
    public static int RESOURCE_TYPE_COLUMN = 6;//小专栏
    public static int RESOURCE_TYPE_MEMBER = 5;//会员
    private SimpleDraweeView columnImage;
    private TextView columnTitle;
    private TextView barTitle;
    private TextView buyCount;
    private ScrollViewPager columnViewPager;
    private LinearLayout btnContentDetail;
    private LinearLayout btnContentDirectory;
    private ImageView btnContentDetailTag;
    private ImageView btnContentDirectorTag;
    private RelativeLayout columnMenuWarp;
    private RelativeLayout columnToolBar;
//    private TextView statusBar;
    private int toolBarheight;
    private ImageView btnBack;
    private CommonBuyView buyView;
    private ColumnFragmentStatePagerAdapter columnViewPagerAdapter;
    private ListBottomLoadMoreView loadMoreView;
    private View mBottomEndView;
    private CustomScrollView columnScrollView;
    private String resourceId;
    private Intent mIntent;
    private ColumnPresenter columnPresenter;
//    private boolean isBigColumn;
    private int pageIndex = 1;
    private int pageSize = 20;
    private boolean isHasBuy = false;
    private boolean refreshData = false;
    private ImageView btnCollect;
    private boolean hasCollect = false;//是否收藏
    private CollectionUtils collectionUtils;
    private String collectTitle = "";
    private String collectImgUrl;
    private String collectImgUrlCompressed;
    private String collectPrice = "";
    private String shareUrl = "";
    private String summary = "";
    private int price = 0;

    List<LoginUser> loginUserList;
    TouristDialog touristDialog;

    boolean hasBuy;
    String realSrcId;
    private MiniAudioPlayControllerLayout miniAudioPlayControllerLayout;//悬浮音频播放器
    private int resourceType;//8-大专栏，6-小专栏，5-会员
    private TextView memberExpireTime;
    private String expireTime;
    private StatusPagerView statusPagerView;
    private boolean showDataByDB = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_column);
        mIntent = getIntent();
        loginUserList = getLoginUserList();

        if (loginUserList.size() == 0) {
            touristDialog = new TouristDialog(this);
            touristDialog.setDialogCloseClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touristDialog.dismissDialog();
                }
            });
            touristDialog.setDialogConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touristDialog.dismissDialog();
                    JumpDetail.jumpLogin(ColumnActivity.this);
                }
            });
        }

//        isBigColumn = mIntent.getBooleanExtra("isBigColumn", false);
        resourceType = mIntent.getIntExtra("resource_type", 0);
        resourceId = mIntent.getStringExtra("resource_id");
        EventBus.getDefault().register(this);
        initView();
        initData();
        initTitle();
    }

    // 沉浸式初始化
    private void initTitle() {
//        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
//        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
    }

    private void initData() {
        columnPresenter = new ColumnPresenter(this);
        setDataByDB();
        columnPresenter.requestDetail(resourceId, resourceType+"");
        collectionUtils = new CollectionUtils(this);
    }

    private void initView() {
        columnMenuWarp = (RelativeLayout) findViewById(R.id.column_menu_warp);
        columnToolBar = (RelativeLayout) findViewById(R.id.column_tool_bar);
        columnToolBar.setBackgroundColor(Color.argb(0,255,255,255));
        toolBarheight = Dp2Px2SpUtil.dp2px(this,280);
        columnScrollView = (CustomScrollView) findViewById(R.id.column_scroll_view);
        columnScrollView.setScrollChanged(this);
        columnScrollView.setLoadHeight(Dp2Px2SpUtil.dp2px(this, 40));

        //购买按钮
        buyView = (CommonBuyView) findViewById(R.id.common_buy_layout);
        buyView.setVisibility(View.GONE);
        buyView.setOnBuyBtnClickListener(this);
        buyView.setOnVipBtnClickListener(this);

        String imageUrl = mIntent.getStringExtra("column_image_url");
        columnImage = (SimpleDraweeView) findViewById(R.id.column_image);
        if(!"".equals(imageUrl)){
            columnImage.setImageURI(imageUrl);
        }
        columnTitle = (TextView) findViewById(R.id.column_title);
        barTitle = (TextView) findViewById(R.id.tv_title);
        buyCount = (TextView) findViewById(R.id.buy_num);

        memberExpireTime = (TextView) findViewById(R.id.member_expire_time);

        columnViewPager = (ScrollViewPager) findViewById(R.id.column_view_pager);
        columnViewPager.setNeedMeasure(true);
        columnViewPagerAdapter = new ColumnFragmentStatePagerAdapter(getSupportFragmentManager(),resourceType, resourceId);
        columnViewPager.setScroll(false);
        columnViewPager.setAdapter(columnViewPagerAdapter);
        columnViewPager.setOffscreenPageLimit(2);
        //加载更多
        loadMoreView = (ListBottomLoadMoreView) findViewById(R.id.btn_bottom_load_more);
        mBottomEndView = findViewById(R.id.bottom_end);
        loadMoreView.setVisibility(View.GONE);
        setLoadState(ListBottomLoadMoreView.STATE_LOADING);
        //课程简介按钮
        btnContentDetail = (LinearLayout) findViewById(R.id.btn_content_detail);
        btnContentDetail.setOnClickListener(this);
        btnContentDetail.setEnabled(false);
        btnContentDetailTag = (ImageView) findViewById(R.id.btn_content_detail_tag);

        //课程大纲按钮
        btnContentDirectory = (LinearLayout) findViewById(R.id.btn_content_directory);
        btnContentDirectory.setOnClickListener(this);
        btnContentDirectory.setEnabled(true);
        btnContentDirectorTag = (ImageView) findViewById(R.id.btn_content_director_tag);
        //返回按钮
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        //收藏按钮
        btnCollect = (ImageView) findViewById(R.id.btn_collect);
        btnCollect.setOnClickListener(this);
        //分享按钮
        ImageView btnToolBarShare = (ImageView) findViewById(R.id.btn_tool_bar_share);
        btnToolBarShare.setOnClickListener(this);
        ImageView btnShare = (ImageView) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        //悬浮音频播放器
        miniAudioPlayControllerLayout = (MiniAudioPlayControllerLayout) findViewById(R.id.mini_audio_play_controller);
        setMiniAudioPlayController(miniAudioPlayControllerLayout);
        setMiniPlayerAnimHeight(Dp2Px2SpUtil.dp2px(this, 76));
        setMiniPlayerPosition(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        statusPagerView = (StatusPagerView) findViewById(R.id.state_pager_view);
        setPagerState(-1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int code = getWXPayCode(true);
        if(code == 0){
            refreshData = true;
            getDialog().showLoadDialog(false);
            columnPresenter.requestDetail(resourceId, resourceType+"");
        }
        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
    }

    @Override
    public void onBackPressed() {
        if (hasBuy) {
            UpdateLearningUtils updateLearningUtils = new UpdateLearningUtils(this);
//            updateLearningUtils.updateLearningProgress(realSrcId, isBigColumn ? 8 : 6, 10);
            updateLearningUtils.updateLearningProgress(realSrcId, resourceType, 10);
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }
    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        if(activityDestroy){
            return;
        }
        JSONObject jsonObject = (JSONObject) entity;
        if(entity == null || !success){
            return;
        }
        int code = jsonObject.getIntValue("code");
        if(code != NetworkCodes.CODE_SUCCEED ){
            if(iRequest instanceof ColumnListRequst || iRequest instanceof DetailRequest){
                setLoadState(ListBottomLoadMoreView.STATE_LOAD_FAILED);
            }
            if(iRequest instanceof DetailRequest){
                if(code == NetworkCodes.CODE_GOODS_DELETE){
                    setPagerState(NetworkCodes.CODE_GOODS_DELETE);
                }else{
                    setPagerState(1);
                }
            }
            return;
        }
        Object dataObject = jsonObject.get("data");
        if(iRequest instanceof DetailRequest){
            JSONObject data = (JSONObject) dataObject;
            int hasFavorite = ((JSONObject) data.get("favorites_info")).getInteger("is_favorite");
            setCollectState(hasFavorite == 1);
            detailRequest(data.getJSONObject("resource_info"), data.getJSONObject("product_info"), data.getBoolean("available"), false);
            JSONObject shareInfo = data.getJSONObject("share_info");
            if(shareInfo != null && shareInfo.getJSONObject("wx") != null){
                shareUrl = shareInfo.getJSONObject("wx").getString("share_url");
            }
        }else if(iRequest instanceof ColumnListRequst){
            JSONArray data = (JSONArray) dataObject;
            columnListRequest(data);
        }else if(iRequest instanceof AddCollectionRequest){
            addCollectionRequest(jsonObject);
        }else if(iRequest instanceof RemoveCollectionListRequest){
            removeCollectionRequest(jsonObject);
        }
    }

    @Subscribe
    public void onEventMainThread(AudioPlayEvent event) {
        AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        if(playEntity == null){
            return;
        }
        switch (event.getState()){
            case AudioPlayEvent.LOADING:
                miniAudioPlayControllerLayout.setVisibility(View.VISIBLE);
                miniAudioPlayControllerLayout.setIsClose(false);
                miniAudioPlayControllerLayout.setAudioTitle(playEntity.getTitle());
                miniAudioPlayControllerLayout.setAudioImage(playEntity.getImgUrlCompressed());
                miniAudioPlayControllerLayout.setColumnTitle(playEntity.getProductsTitle());
                miniAudioPlayControllerLayout.setPlayButtonEnabled(false);
                miniAudioPlayControllerLayout.setPlayState(AudioPlayEvent.PAUSE);
                break;
            case AudioPlayEvent.PLAY:
                miniAudioPlayControllerLayout.setVisibility(View.VISIBLE);
                miniAudioPlayControllerLayout.setIsClose(false);
                miniAudioPlayControllerLayout.setPlayButtonEnabled(true);
                miniAudioPlayControllerLayout.setAudioTitle(playEntity.getTitle());
                miniAudioPlayControllerLayout.setColumnTitle(playEntity.getProductsTitle());
                miniAudioPlayControllerLayout.setPlayState(AudioPlayEvent.PLAY);
                miniAudioPlayControllerLayout.setMaxProgress(AudioMediaPlayer.getDuration());
                break;
            case AudioPlayEvent.PAUSE:
                miniAudioPlayControllerLayout.setPlayState(AudioPlayEvent.PAUSE);
                break;
            case AudioPlayEvent.STOP:
                miniAudioPlayControllerLayout.setPlayState(AudioPlayEvent.PAUSE);
                break;
            case AudioPlayEvent.PROGRESS:
                miniAudioPlayControllerLayout.setProgress(event.getProgress());
                break;
            default:
                break;
        }
    }

    /**
     * 取消收藏
     * @param jsonObject
     */
    private void removeCollectionRequest(JSONObject jsonObject) {
        if(jsonObject.getIntValue("code") == NetworkCodes.CODE_SUCCEED ){
            toastCustom(getString(R.string.cancel_collect_succeed));
            setCollectState(false);
        }else{
            toastCustom(getResources().getString(R.string.cancel_collect_fail));
        }
    }

    /**
     * 添加收藏
     * @param jsonObject
     */
    private void addCollectionRequest(JSONObject jsonObject) {
        if(jsonObject.getIntValue("code") == NetworkCodes.CODE_SUCCEED ){
            toastCustom(getString(R.string.collect_succeed));
            setCollectState(true);
        }else{
            toastCustom(getResources().getString(R.string.collect_fail));
        }
    }

    private void columnListRequest(JSONArray data) {
        if(resourceType == RESOURCE_TYPE_TOPIC){
            ColumnDirectoryFragment fragment = (ColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
            fragment.setHasBuy(isHasBuy);
            if(refreshData || showDataByDB){
                refreshData = false;
                fragment.refreshData(columnPresenter.formatColumnEntity(data, resourceId, hasBuy ? 1 : 0));
            }else{
                fragment.addData(columnPresenter.formatColumnEntity(data, resourceId, hasBuy ? 1 : 0));
            }
        }else if(resourceType == RESOURCE_TYPE_MEMBER){
            MemberFragment fragment = (MemberFragment) columnViewPagerAdapter.getItem(1);
            fragment.setHasBuy(isHasBuy);
            if(refreshData || showDataByDB){
                refreshData = false;
                fragment.refreshData(columnPresenter.formatSingleResourceEntity(data, collectTitle, resourceId, "", hasBuy ? 1 : 0));
            }else{
                fragment.addData(columnPresenter.formatSingleResourceEntity(data, collectTitle, resourceId, "", hasBuy ? 1 : 0));
            }
        }else{
            LittleColumnDirectoryFragment fragment = (LittleColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
            fragment.setHasBuy(isHasBuy);
            if(refreshData || showDataByDB){
                refreshData = false;
                fragment.refreshData(columnPresenter.formatSingleResourceEntity(data, collectTitle, resourceId, "", hasBuy ? 1 : 0));
            }else{
                fragment.addData(columnPresenter.formatSingleResourceEntity(data, collectTitle, resourceId, "", hasBuy ? 1 : 0));
            }
        }
        if(data.size() < pageSize){
            setLoadState(ListBottomLoadMoreView.STATE_ALL_FINISH);
        }else{
            setLoadState(ListBottomLoadMoreView.STATE_NOT_LOAD);
        }
    }

    private void detailRequest(JSONObject data, JSONObject productInfo, boolean available, boolean cache) {
        int isRelated  = data.getIntValue("is_related");
        if(isRelated == 1 && !available && productInfo != null){
            //是否只关联售卖 0-不是, 1-仅关联
            //非单卖需要跳转到所属专栏，如果所属专栏多个，只跳转第一个
            JSONArray productList = productInfo.getJSONArray("product_list");
            JSONObject product = productList.getJSONObject(0);
            int productType = product.getIntValue("product_type");
            String productId = product.getString("id");
            String productImgUrl = product.getString("img_url");
            //1-专栏, 2-会员, 3-大专栏
            if(productType == 3){
                JumpDetail.jumpColumn(this, productId, productImgUrl, 8);
            }else if(productType == 2){
                JumpDetail.jumpColumn(this, productId, productImgUrl, 5);
            }else{
                JumpDetail.jumpColumn(this, productId, productImgUrl, 6);
            }
            finish();
            return;
        }
        summary = data.getString("summary");
        hasBuy = available;
        getDialog().dismissDialog();
        if(refreshData){
            if(resourceType == RESOURCE_TYPE_TOPIC){
                ColumnDirectoryFragment fragment = (ColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
                fragment.clearData();
            }else if(resourceType == RESOURCE_TYPE_MEMBER){
                MemberFragment fragment = (MemberFragment) columnViewPagerAdapter.getItem(1);
                fragment.clearData();
            }else{
                LittleColumnDirectoryFragment fragment = (LittleColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
                fragment.clearData();
            }
            setLoadState(ListBottomLoadMoreView.STATE_NOT_LOAD);
        }
        if(available){
            buyView.setVisibility(View.GONE);
            setMiniPlayerPosition(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            isHasBuy = true;
            collectPrice = "";
            realSrcId = data.getString("resource_id");
        }else{
            price = data.getIntValue("price");
            buyView.setVisibility(cache ? View.GONE : View.VISIBLE);
            if(cache){
                //显示缓存数据，会先隐藏购买按钮，所以间悬浮播放器置底
                setMiniPlayerPosition(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            }else{
                setMiniPlayerPosition(RelativeLayout.ABOVE, R.id.common_buy_layout);
            }
            // 未购买
            if (CommonUserInfo.isIsSuperVipAvailable()) { // 超级会员判断
                if (CommonUserInfo.getSuperVipEffective() == 1) { // 全店免费
                    buyView.setVipBtnVisibility(View.VISIBLE);
                } else {
                    buyView.setVipBtnVisibility(View.GONE);
                }
            } else {
                buyView.setVipBtnVisibility(View.GONE);
            }
            buyView.setBuyPrice(price);
            isHasBuy = false;
            collectPrice = ""+price;
        }
        String title = data.getString("title");
        //收藏内容
        collectTitle = title;
        collectImgUrl = data.getString("img_url");
        collectImgUrlCompressed = data.getString("img_url_compressed");

        ColumnDetailFragment detailFragment = (ColumnDetailFragment) columnViewPagerAdapter.getItem(0);
        detailFragment.setContentDetail(data.getString("content"));
        columnTitle.setText(title);
        barTitle.setText(title);
        columnImage.setImageURI(collectImgUrl);
        int purchaseCount = data.getIntValue("purchase_count");
        if(purchaseCount > 0){
            buyCount.setVisibility(View.VISIBLE);
            buyCount.setText(NumberFormat.viewCountToString(purchaseCount)+"人学习");
        }else {
            buyCount.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(data.getString("expire_time"))){
            memberExpireTime.setVisibility(View.VISIBLE);
            expireTime = data.getString("expire_time");
            memberExpireTime.setText("有效期至："+expireTime);
        }
        resourceState(data, available);
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
        int timeLeft = data.getInteger("time_left");
        if(available && detailState != 2){
            //删除状态优秀级最高，available=true是除了删除状态显示删除页面外，其他的均可查看详情
            setPagerState(0);
            columnPresenter.requestColumnList(data.getString("resource_id"), "0", pageIndex, pageSize);
            return;
        }
        if(saleStatus == 1 || detailState == 1){
            setPagerState(2);
        }else if(isStopSell == 1){
            setPagerState(3);
        }else if(timeLeft > 0){
            setPagerState(4);
        }else if(detailState == 2){
            setPagerState(NetworkCodes.CODE_GOODS_DELETE);
        }else {
            setPagerState(0);
            columnPresenter.requestColumnList(data.getString("resource_id"), "0", pageIndex, pageSize);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_content_detail:
                setColumnViewPager(0);
                loadMoreView.setVisibility(View.GONE);
                mBottomEndView.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_content_directory:
                setColumnViewPager(1);
                loadMoreView.setVisibility(View.VISIBLE);
                mBottomEndView.setVisibility(View.GONE);
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.buy_vip:
                if (loginUserList.size() == 1) {
                    JumpDetail.jumpSuperVip(this);
                } else {
                    touristDialog.showDialog();
                }
                break;
            case R.id.buy_course:
                if (loginUserList.size() == 1) {
                    int resourceType = this.resourceType;
                    JumpDetail.jumpPay(this,resourceId,resourceType, collectImgUrl, collectTitle, price, null);
                } else {
                    touristDialog.showDialog();
                }
                break;
            case R.id.btn_collect:
                if (loginUserList.size() == 1) {
                    collect();
                } else {
                    touristDialog.showDialog();
                }
                break;
            case R.id.btn_share:
            case R.id.btn_tool_bar_share:
                umShare(collectTitle, TextUtils.isEmpty(collectImgUrlCompressed) ? collectImgUrl : collectImgUrlCompressed, shareUrl, summary );
                break;
            default:
                break;
        }
    }
    //点击收藏按钮
    private void collect() {
        hasCollect = !hasCollect;
        if(hasCollect){
            //添加收藏
            JSONObject collectionContent = new JSONObject();
            collectionContent.put("title",collectTitle);
            collectionContent.put("author","");
            collectionContent.put("img_url",collectImgUrl);
            collectionContent.put("img_url_compressed",collectImgUrlCompressed);
            collectionContent.put("price",collectPrice);
            collectionUtils.requestAddCollection(resourceId, resourceType+"", collectionContent);
        }else {
            //取消收藏
            collectionUtils.requestRemoveCollection(resourceId, resourceType+"");
        }
    }

    private void setColumnViewPager(int index){
        if(index == 0){
            columnViewPager.setCurrentItem(0);
            btnContentDetail.setEnabled(false);
            btnContentDirectory.setEnabled(true);
            btnContentDirectorTag.setImageResource(R.mipmap.detail_classtree_invalid);
            btnContentDetailTag.setImageResource(R.mipmap.detail_cls);

        }else{
            columnViewPager.setCurrentItem(1);
            btnContentDirectory.setEnabled(false);
            btnContentDetail.setEnabled(true);
            btnContentDirectorTag.setImageResource(R.mipmap.detail_classtree);
            btnContentDetailTag.setImageResource(R.mipmap.detail_classintroduce_invalid);
        }
        columnViewPager.requestLayout();
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        int[] location = new int[2];
        columnMenuWarp.getLocationOnScreen(location);
        int y = location[1];
        float alpha = (1 - y / (toolBarheight * 1.0f)) * 255;
        if(alpha > 255){
            alpha = 255;
        }else if(alpha < 0){
            alpha = 0;
        }
        barTitle.setVisibility(alpha > 10 ? View.VISIBLE : View.GONE);
        barTitle.setTextColor(Color.argb((int) alpha,0,0,0));
        btnBack.setImageResource(alpha > 150 ? R.mipmap.download_back :R.mipmap.detail_white_back);
        columnToolBar.setBackgroundColor(Color.argb((int) alpha,255,255,255));
    }
    @Override
    public void onLoadState(int state) {
        if(columnViewPager.getCurrentItem() == 1 && state == ListBottomLoadMoreView.STATE_NOT_LOAD){
            setLoadState(ListBottomLoadMoreView.STATE_LOADING);
            pageIndex++;
            columnPresenter.requestColumnList(resourceId, "0", pageIndex, pageSize);
        }
    }
    public void setLoadState(int state){
        loadMoreView.setLoadState(state);
        columnScrollView.setLoadState(state);
    }

    /**
     * 设置收藏状态
     * @param collect 0-未收藏，1-已收藏
     */
    private void setCollectState(boolean collect){
        hasCollect = collect;
        if(collect){
            btnCollect.setImageResource(R.mipmap.audio_collect);
        }else{
            btnCollect.setImageResource(R.mipmap.video_collect);
        }
    }

    public String getColumnTitle() {
        return collectTitle;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        UMShareAPI.get(this).release();
        if (!hasCollect) {
            EventBus.getDefault().post(new MyCollectListRefreshEvent(true, resourceId));
        }
    }

    /**
     *
     * @param code 0-正常的,1-请求失败,2-课程下架,-1： 加载，3-停售， 4-待上架，3004：商品已删除
     */
    private void setPagerState(int code) {
        if(code == 0){
            statusPagerView.setVisibility(View.GONE);
            statusPagerView.setLoadingState(View.GONE);
            statusPagerView.setHintStateVisibility(View.GONE);
        }else if(code == 1){
            statusPagerView.setPagerState(StatusPagerView.FAIL, getString(R.string.request_fail), StatusPagerView.DETAIL_NONE);
        }else if(code == 2){
            btnBack.setVisibility(View.VISIBLE);
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_sold_out), R.mipmap.course_off);
        }else if(code == 3){
            btnBack.setVisibility(View.VISIBLE);
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_sale_stop), R.mipmap.course_off);
        }else if(code == 4){
            btnBack.setVisibility(View.VISIBLE);
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_stay_putaway), R.mipmap.course_off);
        }else if(code == 3004){
            btnBack.setVisibility(View.VISIBLE);
            statusPagerView.setPagerState(StatusPagerView.SOLD, getString(R.string.resource_delete), R.mipmap.course_off);
        }
        else if(code == -1){
            statusPagerView.setVisibility(View.VISIBLE);
            statusPagerView.setLoadingState(View.VISIBLE);
            statusPagerView.setHintStateVisibility(View.GONE);
        }
    }

    public void setDataByDB(){
        SQLiteUtil sqLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new CacheDataUtil());
        String sql = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()+"' and resource_id='"+resourceId+"'";
        List<CacheData> cacheDataList = sqLiteUtil.query(CacheDataUtil.TABLE_NAME, sql, null );
        if(cacheDataList != null && cacheDataList.size() > 0){
            JSONObject data = JSONObject.parseObject(cacheDataList.get(0).getContent()).getJSONObject("data");
            int hasFavorite = ((JSONObject) data.get("favorites_info")).getInteger("is_favorite");
            setCollectState(hasFavorite == 1);
            detailRequest(data.getJSONObject("resource_info"), data.getJSONObject("product_info"), data.getBoolean("available"), true);
            JSONObject shareInfo = data.getJSONObject("share_info");
            if(shareInfo != null && shareInfo.getJSONObject("wx") != null){
                shareUrl = shareInfo.getJSONObject("wx").getString("share_url");
            }
        }
        String sqlList = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()+"' and resource_id='"+resourceId+"_list'";
        List<CacheData> cacheDataResourceList = sqLiteUtil.query(CacheDataUtil.TABLE_NAME, sqlList, null );
        if(cacheDataResourceList != null && cacheDataResourceList.size() > 0){
            JSONArray data = JSONObject.parseObject(cacheDataResourceList.get(0).getResourceList()).getJSONArray("data");
            columnListRequest( data);
        }
        if(cacheDataList != null && cacheDataList.size() > 0 &&
                cacheDataResourceList != null && cacheDataResourceList.size() > 0){
            showDataByDB = true;
        }
    }
    private void setMiniPlayerPosition(int verb, int subject){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) miniAudioPlayControllerLayout.getLayoutParams();
        if(verb == RelativeLayout.ALIGN_PARENT_BOTTOM){
            layoutParams.removeRule(RelativeLayout.ABOVE);
        }else{
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        layoutParams.addRule(verb, subject);
        miniAudioPlayControllerLayout.setLayoutParams(layoutParams);
    }
}
