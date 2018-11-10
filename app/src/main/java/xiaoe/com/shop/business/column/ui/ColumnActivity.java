package xiaoe.com.shop.business.column.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.socialize.UMShareAPI;

import java.util.List;

import xiaoe.com.common.entitys.LoginUser;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.common.utils.SharedPreferencesUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.AddCollectionRequest;
import xiaoe.com.network.requests.ColumnListRequst;
import xiaoe.com.network.requests.DetailRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.RemoveCollectionListRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.column.ColumnFragmentStatePagerAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.column.presenter.ColumnPresenter;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.interfaces.OnCustomScrollChangedListener;
import xiaoe.com.shop.utils.CollectionUtils;
import xiaoe.com.shop.utils.NumberFormat;
import xiaoe.com.shop.widget.CommonBuyView;
import xiaoe.com.shop.widget.CustomScrollView;
import xiaoe.com.shop.widget.ListBottomLoadMoreView;
import xiaoe.com.shop.widget.ScrollViewPager;

public class ColumnActivity extends XiaoeActivity implements View.OnClickListener, OnCustomScrollChangedListener {
    private static final String TAG = "ColumnActivity";
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
    private boolean isBigColumn;
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
    private int price = 0;

    List<LoginUser> loginUserList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column);
        mIntent = getIntent();
        loginUserList = getLoginUserList();
        isBigColumn = mIntent.getBooleanExtra("isBigColumn", false);
        resourceId = mIntent.getStringExtra("resource_id");
        initView();
        initData();
    }

    private void initData() {
        columnPresenter = new ColumnPresenter(this);
        columnPresenter.requestDetail(resourceId, isBigColumn ? "8" : "6");
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
        columnViewPager = (ScrollViewPager) findViewById(R.id.column_view_pager);
        columnViewPager.setNeedMeasure(true);
        columnViewPagerAdapter = new ColumnFragmentStatePagerAdapter(getSupportFragmentManager(),isBigColumn, resourceId);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        int code = getWXPayCode(true);
        if(code == 0){
            refreshData = true;
            getDialog().showLoadDialog(false);
            columnPresenter.requestDetail(resourceId, isBigColumn ? "8" : "6");
        }
        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
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
        Object dataObject = jsonObject.get("data");
        if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED || dataObject == null ){
            if(iRequest instanceof ColumnListRequst){
                setLoadState(ListBottomLoadMoreView.STATE_LOAD_FAILED);
            }
            return;
        }
        if(iRequest instanceof DetailRequest){
            JSONObject data = (JSONObject) dataObject;
            detailRequest(data.getJSONObject("resource_info"), data.getBoolean("available"));
        }else if(iRequest instanceof ColumnListRequst){
            JSONArray data = (JSONArray) dataObject;
            columnListRequest(iRequest, data);
        }else if(iRequest instanceof AddCollectionRequest){
            addCollectionRequest(jsonObject);
        }else if(iRequest instanceof RemoveCollectionListRequest){
            removeCollectionRequest(jsonObject);
        }
    }

    /**
     * 取消收藏
     * @param jsonObject
     */
    private void removeCollectionRequest(JSONObject jsonObject) {
        if(jsonObject.getIntValue("code") == NetworkCodes.CODE_SUCCEED ){
//            toastCustom(getResources().getString(R.string.cancel_collect_succeed));
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
//            toastCustom(getResources().getString(R.string.collect_succeed));
            setCollectState(true);
        }else{
            toastCustom(getResources().getString(R.string.collect_fail));
        }
    }

    private void columnListRequest(IRequest iRequest, JSONArray data) {
        if(isBigColumn){
            ColumnDirectoryFragment fragment = (ColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
            fragment.setHasBuy(isHasBuy);
            if(refreshData){
                refreshData = false;
                fragment.refreshData(columnPresenter.formatColumnEntity(data, resourceId));
            }else{
                fragment.addData(columnPresenter.formatColumnEntity(data, resourceId));
            }
        }else{
            LittleColumnDirectoryFragment fragment = (LittleColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
            fragment.setHasBuy(isHasBuy);
            if(refreshData){
                refreshData = false;
                fragment.refreshData(columnPresenter.formatSingleResouceEntity(data, resourceId, ""));
            }else{
                fragment.addData(columnPresenter.formatSingleResouceEntity(data, resourceId, ""));
            }
        }
        if(data.size() < pageSize){
            setLoadState(ListBottomLoadMoreView.STATE_ALL_FINISH);
        }else{
            setLoadState(ListBottomLoadMoreView.STATE_NOT_LOAD);
        }
    }

    private void detailRequest(JSONObject data, boolean available) {
        getDialog().dismissDialog();
        if(refreshData){
            if(isBigColumn){
                ColumnDirectoryFragment fragment = (ColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
                fragment.clearData();
            }else{
                LittleColumnDirectoryFragment fragment = (LittleColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
                fragment.clearData();
            }
            setLoadState(ListBottomLoadMoreView.STATE_NOT_LOAD);
        }
        if(available){
            buyView.setVisibility(View.GONE);
            isHasBuy = true;
            collectPrice = "";
        }else{
            price = data.getIntValue("price");
            buyView.setVisibility(View.VISIBLE);
            buyView.setBuyPrice(price);
            isHasBuy = false;
            collectPrice = ""+price;
        }
        String title = data.getString("title");
        //收藏内容
        collectTitle = title;
        collectImgUrl = data.getString("img_url");
        collectImgUrlCompressed = data.getString("img_url_compressed");
        setCollectState(data.getIntValue("has_favorite") == 1);

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
        columnPresenter.requestColumnList(resourceId, "0", pageIndex, pageSize);
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
                    toastCustom("请先登录呦");
                }
                break;
            case R.id.buy_course:
                if (loginUserList.size() == 1) {
                    int resourceType = isBigColumn ? 8 : 6;
                    JumpDetail.jumpPay(this,resourceId,resourceType, collectImgUrl, collectTitle, price);
                } else {
                    toastCustom("请先登录呦");
                }
                break;
            case R.id.btn_collect:
                if (loginUserList.size() == 1) {
                    collect();
                } else {
                    toastCustom("请先登录呦");
                }
                break;
            case R.id.btn_share:
            case R.id.btn_tool_bar_share:
                umShare("hello");
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
            collectionUtils.requestAddCollection(resourceId, isBigColumn ? "8" : "6", collectionContent);
        }else {
            //取消收藏
            collectionUtils.requestRemoveCollection(resourceId, isBigColumn ? "8" : "6");
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
        barTitle.setVisibility(alpha == 255 ? View.VISIBLE : View.GONE);
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
    }
}
