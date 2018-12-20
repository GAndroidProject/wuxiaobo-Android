package com.xiaoe.shop.wxb.business.column.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.socialize.UMShareAPI;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.CacheData;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.ExpandableItem;
import com.xiaoe.common.entitys.ExpandableLevel;
import com.xiaoe.common.entitys.LearningRecord;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.CacheDataUtil;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.AddCollectionRequest;
import com.xiaoe.network.requests.ColumnListRequst;
import com.xiaoe.network.requests.DetailRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.QueryProductTypeRequest;
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
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
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
    public static final int RESOURCE_TYPE_TOPIC = 8;//大专栏
    public static final int RESOURCE_TYPE_COLUMN = 6;//小专栏
    public static final int RESOURCE_TYPE_MEMBER = 5;//会员
    private final String TOPIC_LITTLE_REQUEST_TAG = "1001";//大专栏中请求小专栏
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

//    boolean hasBuy;
    String realSrcId;
    private MiniAudioPlayControllerLayout miniAudioPlayControllerLayout;//悬浮音频播放器
    private int resourceType;//8-大专栏，6-小专栏，5-会员
    private TextView memberExpireTime;
    private String expireTime;
    private StatusPagerView statusPagerView;
    private boolean showDataByDB = false;
    //记录是否已经请求专栏列表，因为获取缓存中详情数据后，同时会亲情一遍详情页，但详情页中会拉取专栏列表
    private boolean requestColumnList = false;
    //记录大专栏下小专栏列表，（小专栏、会员页面无效）
    private List<MultiItemEntity> topicLittleColumnList;
    //请求大专栏下第一个小专栏下资源列表，（小专栏、会员页面无效）
    private boolean requestTopicFirstLittle = false;
    private String playNumStr = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
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
        resourceType = mIntent.getIntExtra("resource_type", 0);
        resourceId = mIntent.getStringExtra("resource_id");
        columnPresenter = new ColumnPresenter(this);
        EventBus.getDefault().register(this);

        initView();
        if(resourceType == 3){
            //resourceType=3可能是通过一些链接或消息类型进入，需要通过接口查询获取准确的resourceType
            //拿到准确的resourceTyp后才初始化数据
            columnPresenter.productTypeRequest(resourceId);

        }else{
            columnViewPager = (ScrollViewPager) findViewById(R.id.column_view_pager);
            columnViewPager.setNeedMeasure(true);
            columnViewPagerAdapter = new ColumnFragmentStatePagerAdapter(getSupportFragmentManager(),resourceType, resourceId);
            columnViewPager.setScroll(false);
            columnViewPager.setAdapter(columnViewPagerAdapter);
            columnViewPager.setOffscreenPageLimit(2);
            initData();
            initTitle();
        }
    }

    // 沉浸式初始化
    private void initTitle() {
//        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
//        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
    }

    private void initData() {
        setDataByDB();
        columnPresenter.requestDetail(resourceId, resourceType+"");
        collectionUtils = new CollectionUtils(this);
    }

    private void initView() {
        columnMenuWarp = (RelativeLayout) findViewById(R.id.column_menu_warp);
        columnToolBar = (RelativeLayout) findViewById(R.id.column_tool_bar);
        columnToolBar.setBackgroundColor(Color.argb(0,255,255,255));
        columnToolBar.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
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

//        columnViewPager = (ScrollViewPager) findViewById(R.id.column_view_pager);
//        columnViewPager.setNeedMeasure(true);
//        columnViewPagerAdapter = new ColumnFragmentStatePagerAdapter(getSupportFragmentManager(),resourceType, resourceId);
//        columnViewPager.setScroll(false);
//        columnViewPager.setAdapter(columnViewPagerAdapter);
//        columnViewPager.setOffscreenPageLimit(2);
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
        if (isHasBuy) {
            Log.d(TAG, "onBackPressed: hasBuy ------- ");
            UpdateLearningUtils updateLearningUtils = new UpdateLearningUtils(this);
            updateLearningUtils.updateLearningProgress(realSrcId, resourceType, 10);
            LearningRecord lr = new LearningRecord();
            //	1-会员，2-大专栏，3-专栏
            lr.setLrId(realSrcId);
            switch (resourceType) {
                case 5: // 会员
                    lr.setLrType(DecorateEntityType.MEMBER);
                    break;
                case 6: // 专栏
                    lr.setLrType(DecorateEntityType.COLUMN);
                    break;
                case 8: // 大专栏
                    lr.setLrType(DecorateEntityType.TOPIC);
                    break;
                default:
                    break;
            }
            lr.setLrTitle(collectTitle);
            lr.setLrImg(collectImgUrl);
            lr.setLrDesc(playNumStr);
            UpdateLearningUtils.saveLr2Local(this, lr);
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
        if(code != NetworkCodes.CODE_SUCCEED && !TOPIC_LITTLE_REQUEST_TAG.equals(iRequest.getRequestTag())){
            if(iRequest instanceof ColumnListRequst || iRequest instanceof DetailRequest){
                setLoadState(ListBottomLoadMoreView.STATE_LOAD_FAILED);
            }
            if(iRequest instanceof DetailRequest || iRequest instanceof QueryProductTypeRequest){
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

            if(TOPIC_LITTLE_REQUEST_TAG.equals(iRequest.getRequestTag())){
                //请求大专栏下第一个小专栏下的资源列表
                topicLittleColumnRequest(jsonObject);
            }else{
                JSONArray data = (JSONArray) dataObject;
                columnListRequest(data);
            }
        }else if(iRequest instanceof AddCollectionRequest){
            addCollectionRequest(jsonObject);
        }else if(iRequest instanceof RemoveCollectionListRequest){
            removeCollectionRequest(jsonObject);
        }else if(iRequest instanceof QueryProductTypeRequest){
            JSONObject data = (JSONObject) dataObject;
            int type = data.getIntValue("type");
            //	1-会员，2-大专栏，3-专栏
            if(type == 1){
                resourceType = 5;
            }else if(type == 2){
                resourceType = 8;
            }else if(type == 3){
                resourceType = 6;
            }else{
                setPagerState(1);
                return;
            }
            columnViewPager = (ScrollViewPager) findViewById(R.id.column_view_pager);
            columnViewPager.setNeedMeasure(true);
            columnViewPagerAdapter = new ColumnFragmentStatePagerAdapter(getSupportFragmentManager(),resourceType, resourceId);
            columnViewPager.setScroll(false);
            columnViewPager.setAdapter(columnViewPagerAdapter);
            columnViewPager.setOffscreenPageLimit(2);
            initData();
            initTitle();
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
            NewColumnDirectoryFragment fragment = (NewColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
            fragment.setHasBuy(isHasBuy);
            if(refreshData || showDataByDB || pageIndex == 1){
                refreshData = false;
                showDataByDB = false;
                topicLittleColumnList = columnPresenter.formatExpandableEntity(data, resourceId, isHasBuy ? 1 : 0);
                //请求第一个小专栏下资源
                if(topicLittleColumnList.size() > 0){
                    requestTopicFirstLittle = true;
                    ExpandableLevel level = (ExpandableLevel) topicLittleColumnList.get(0);

                    //比较播放中的专栏是否和点击的状态相同
                    AudioPlayEntity audioPlayEntity = AudioMediaPlayer.getAudio();
                    boolean resourceEquals = !TextUtils.isEmpty(level.getBigColumnId()) && audioPlayEntity != null
                            && level.getBigColumnId().equals(audioPlayEntity.getBigColumnId())
                            && level.getResource_id().equals(audioPlayEntity.getColumnId());
                    if(resourceEquals && level.getChildPage() == 1 && audioPlayEntity != null && audioPlayEntity.getPlayColumnPage() > 1){
                        level.setChildPageSize(level.getChildPageSize() * audioPlayEntity.getPlayColumnPage());
                    }

                    columnPresenter.requestColumnList(level.getResource_id(), "0", level.getChildPage(), level.getChildPageSize(), false, TOPIC_LITTLE_REQUEST_TAG);
                }
            }else{
                fragment.addData(columnPresenter.formatExpandableEntity(data, resourceId, isHasBuy ? 1 : 0));
            }
            if(data.size() < pageSize && !requestTopicFirstLittle){
                setLoadState(ListBottomLoadMoreView.STATE_ALL_FINISH);
            }else if(!requestTopicFirstLittle){
                setLoadState(ListBottomLoadMoreView.STATE_NOT_LOAD);
            }
        }else if(resourceType == RESOURCE_TYPE_MEMBER){
            MemberFragment fragment = (MemberFragment) columnViewPagerAdapter.getItem(1);
            fragment.setHasBuy(isHasBuy);
            if(refreshData || showDataByDB || pageIndex == 1){
                refreshData = false;
                showDataByDB = false;
                fragment.refreshData(columnPresenter.formatSingleResourceEntity(data, collectTitle, resourceId, "", isHasBuy ? 1 : 0));
            }else{
                fragment.addData(columnPresenter.formatSingleResourceEntity(data, collectTitle, resourceId, "", isHasBuy ? 1 : 0));
            }
            if(data.size() < pageSize){
                setLoadState(ListBottomLoadMoreView.STATE_ALL_FINISH);
            }else{
                setLoadState(ListBottomLoadMoreView.STATE_NOT_LOAD);
            }
        }else{
            LittleColumnDirectoryFragment fragment = (LittleColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
            fragment.setHasBuy(isHasBuy);
            if(refreshData || showDataByDB || pageIndex == 1){
                refreshData = false;
                showDataByDB = false;
                fragment.refreshData(columnPresenter.formatSingleResourceEntity(data, collectTitle, resourceId, "", isHasBuy ? 1 : 0));
            }else{
                fragment.addData(columnPresenter.formatSingleResourceEntity(data, collectTitle, resourceId, "", isHasBuy ? 1 : 0));
            }
            if(data.size() < pageSize){
                setLoadState(ListBottomLoadMoreView.STATE_ALL_FINISH);
            }else{
                setLoadState(ListBottomLoadMoreView.STATE_NOT_LOAD);
            }
        }
    }

    /**
     * 大专栏下第一个小专栏下的资源列表请求
     * @param data
     */
    private void topicLittleColumnRequest(JSONObject data){
        int code = data.getIntValue("code");

        requestTopicFirstLittle = false;
        if(topicLittleColumnList.size() < pageSize){
            setLoadState(ListBottomLoadMoreView.STATE_ALL_FINISH);
        }else{
            setLoadState(ListBottomLoadMoreView.STATE_NOT_LOAD);
        }
        ExpandableLevel level = (ExpandableLevel) topicLittleColumnList.get(0);
        NewColumnDirectoryFragment fragment = (NewColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
        if(code != NetworkCodes.CODE_SUCCEED){
            level.getSubItem(0).setLoadType(3);
        }else{
            List<ExpandableItem> expandableItems = columnPresenter.formatExpandableChildEntity(data.getJSONArray("data"), level.getTitle(), level.getResource_id(), level.getBigColumnId(), isHasBuy ? 1 : 0);
            if(expandableItems.size() < level.getChildPageSize()){
                //说明小专栏下列表资源已加载完
                if(expandableItems.size()  > 0){
                    expandableItems.get(expandableItems.size() - 1).setLastItem(true);
                }else {
                    ExpandableItem item = level.getSubItem(level.getSubItems().size() - 3);
                    if(item.getItemType() == 1){
                        item.setLastItem(true);
                    }
                }
                level.getSubItems().remove(0);
            }else{
                level.getSubItem(0).setLoadType(0);
            }
            level.getSubItems().addAll(0, expandableItems);
        }
        fragment.refreshData(topicLittleColumnList);
    }

    private void detailRequest(JSONObject data, JSONObject productInfo, boolean available, boolean cache) {
        int isRelated  = data.getIntValue("is_related");
        if(isRelated == 1 && !available && productInfo != null){
            //是否只关联售卖 0-不是, 1-仅关联
            //非单卖需要跳转到所属专栏，如果所属专栏多个，只跳转第一个
            //如果是仅关联售卖，则把缓存中的数据清除
            SQLiteUtil sqLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new CacheDataUtil());
            sqLiteUtil.delete(CacheDataUtil.TABLE_NAME, "app_id=? and resource_id=? and user_id=?", new String[]{Constants.getAppId(), resourceId, CommonUserInfo.getLoginUserIdOrAnonymousUserId()});
            JSONArray productList = productInfo.getJSONArray("product_list");
            if (productList.size() == 0) {
                setPagerState(3004);
                return;
            }
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
        getDialog().dismissDialog();
        if(refreshData){
            if(resourceType == RESOURCE_TYPE_TOPIC){
//                ColumnDirectoryFragment fragment = (ColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
                NewColumnDirectoryFragment fragment = (NewColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
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
        realSrcId = data.getString("resource_id");
        if(available){
            buyView.setVisibility(View.GONE);
            setMiniPlayerPosition(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            isHasBuy = true;
            collectPrice = "";
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
        int periodicalCount = data.getInteger("periodical_count") == null ? 0 : data.getInteger("periodical_count");
        if (periodicalCount > 0) {
            playNumStr = String.format(getString(R.string.stages_text), periodicalCount);
        } else {
            playNumStr = "";
        }
        if(purchaseCount > 0){
            buyCount.setVisibility(View.VISIBLE);
            String learnStr = String.format(getString(R.string.people_learn), NumberFormat.viewCountToString(mContext, purchaseCount));
            buyCount.setText(learnStr);
        }else {
            buyCount.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(data.getString("expire_time"))){
            memberExpireTime.setVisibility(View.VISIBLE);
            expireTime = data.getString("expire_time");
            memberExpireTime.setText(String.format(getString(R.string.valid_until), expireTime));
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
            if(!requestColumnList){
                requestColumnList = true;
                String requestType = resourceType == RESOURCE_TYPE_TOPIC ? "6" : "0";//如果是大专栏则只请求小专栏
                columnPresenter.requestColumnList(data.getString("resource_id"), requestType, pageIndex, pageSize, true, resourceType+"");
            }
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
            if(!requestColumnList){
                requestColumnList = true;
                String requestType = resourceType == RESOURCE_TYPE_TOPIC ? "6" : "0";//如果是大专栏则只请求小专栏
                columnPresenter.requestColumnList(data.getString("resource_id"), requestType, pageIndex, pageSize, true, resourceType+"");
            }
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
                if(loadMoreView.getLoadState() == ListBottomLoadMoreView.STATE_ALL_FINISH){
                    loadMoreView.setVisibility(View.GONE);
                }else{
                    loadMoreView.setVisibility(View.VISIBLE);
                }
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
                    JumpDetail.jumpPay(this,realSrcId,resourceType, collectImgUrl, collectTitle, price, null);
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
            String requestType = resourceType == RESOURCE_TYPE_TOPIC ? "6" : "0";//如果是大专栏则只请求小专栏
            columnPresenter.requestColumnList(resourceId, requestType, pageIndex, pageSize, false, resourceType+"");
        }
    }
    public void setLoadState(int state){
        loadMoreView.setLoadState(state);
        if(state == ListBottomLoadMoreView.STATE_ALL_FINISH){
            loadMoreView.setVisibility(View.GONE);
        }else{
            loadMoreView.setVisibility(View.VISIBLE);
            if (btnContentDetail != null && !btnContentDetail.isEnabled()) { // 选中状态
                loadMoreView.setVisibility(View.GONE);
            }
        }
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
        String sql = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()
                +"' and resource_id='"+resourceId+"' and user_id='"+CommonUserInfo.getLoginUserIdOrAnonymousUserId()+"'";
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
        String sqlList = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()
                +"' and resource_id='"+resourceId+"_list' and user_id='"+CommonUserInfo.getLoginUserIdOrAnonymousUserId()+"'";
        List<CacheData> cacheDataResourceList = sqLiteUtil.query(CacheDataUtil.TABLE_NAME, sqlList, null );
        if(cacheDataResourceList != null && cacheDataResourceList.size() > 0 && resourceType != RESOURCE_TYPE_TOPIC){
            JSONArray data = JSONObject.parseObject(cacheDataResourceList.get(0).getResourceList()).getJSONArray("data");
            columnListRequest( data);
        }
        if(cacheDataList != null && cacheDataList.size() > 0){
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
