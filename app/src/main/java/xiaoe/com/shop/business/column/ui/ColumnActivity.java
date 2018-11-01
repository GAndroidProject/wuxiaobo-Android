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

import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.common.utils.SharedPreferencesUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.ColumnListRequst;
import xiaoe.com.network.requests.DetailRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.PayOrderRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.column.ColumnFragmentStatePagerAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.column.presenter.ColumnPresenter;
import xiaoe.com.shop.interfaces.OnCustomScrollChangedListener;
import xiaoe.com.shop.utils.NumberFormat;
import xiaoe.com.shop.widget.CommonBuyView;
import xiaoe.com.shop.widget.CustomScrollView;
import xiaoe.com.shop.widget.ListBottomLoadMoreView;
import xiaoe.com.shop.widget.ScrollViewPager;

public class ColumnActivity extends XiaoeActivity implements View.OnClickListener, OnCustomScrollChangedListener {
    private static final String TAG = "ColumnActivity";
    private SimpleDraweeView columnImage;
    private TextView columnTitle;
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
    private CustomScrollView columnScrollView;
    private String resourceId;
    private Intent mIntent;
    private ColumnPresenter columnPresenter;
    private boolean isBigColumn;
    private int pageIndex = 1;
    private int pageSize = 20;
    private boolean isHasBuy = false;
    private boolean pay = false;
    private boolean refreshData = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column);
        mIntent = getIntent();
        isBigColumn = mIntent.getBooleanExtra("isBigColumn", false);
        initView();
        initData();
    }

    private void initData() {
        resourceId = mIntent.getStringExtra("resource_id");
        columnPresenter = new ColumnPresenter(this);
        columnPresenter.requestDetail(resourceId, isBigColumn ? "8" : "6");
    }

    private void initView() {
        columnMenuWarp = (RelativeLayout) findViewById(R.id.column_menu_warp);
        columnToolBar = (RelativeLayout) findViewById(R.id.column_tool_bar);
        columnToolBar.setBackgroundColor(Color.argb(0,255,255,255));
        toolBarheight = Dp2Px2SpUtil.dp2px(this,280);
        columnScrollView = (CustomScrollView) findViewById(R.id.column_scroll_view);
        columnScrollView.setScrollChanged(this);
        columnScrollView.setLoadHeigth(Dp2Px2SpUtil.dp2px(this, 40));

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
        buyCount = (TextView) findViewById(R.id.buy_num);
        columnViewPager = (ScrollViewPager) findViewById(R.id.column_view_pager);
        columnViewPager.setNeedMeasure(true);
        columnViewPagerAdapter = new ColumnFragmentStatePagerAdapter(getSupportFragmentManager(),isBigColumn);
        columnViewPager.setScroll(false);
        columnViewPager.setAdapter(columnViewPagerAdapter);
        columnViewPager.setOffscreenPageLimit(2);
        //加载更多
        loadMoreView = (ListBottomLoadMoreView) findViewById(R.id.btn_bottom_load_more);
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

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pay){
            int code = getWXPayCode(true);
            if(code == 0){
                columnPresenter.requestDetail(resourceId, isBigColumn ? "8" : "6");
            }
            SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        if(activityDestroy){
            return;
        }
        JSONObject jsonObject = (JSONObject) entity;
        if(entity == null || !success){
            if(iRequest instanceof PayOrderRequest){
                getDialog().dismissDialog();
                getDialog().setHintMessage(getResources().getString(R.string.pay_info_error));
                getDialog().showDialog(-1);
            }
            return;
        }
        Object dataObject = jsonObject.get("data");
        if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED || dataObject == null ){
            if(iRequest instanceof ColumnListRequst){
                setLoadState(ListBottomLoadMoreView.STATE_LOAD_FAILED);
            }else if(iRequest instanceof PayOrderRequest){
                getDialog().dismissDialog();
                getDialog().setHintMessage(getResources().getString(R.string.pay_info_error));
                getDialog().showDialog(-1);
            }
            return;
        }
        if(iRequest instanceof DetailRequest){
            JSONObject data = (JSONObject) dataObject;
            detailRequest(data.getJSONObject("resource_info"));
        }else if(iRequest instanceof ColumnListRequst){
            JSONArray data = (JSONArray) dataObject;
            columnListRequest(iRequest, data);
        }else if(iRequest instanceof PayOrderRequest){
            JSONObject data = (JSONObject) dataObject;
            payOrderRequest(data);
        }
    }

    private void payOrderRequest(JSONObject dataObject) {
        JSONObject payConfig = dataObject.getJSONObject("payConfig");
        pullWXPay(payConfig.getString("appid"), payConfig.getString("partnerid"), payConfig.getString("prepayid"),
                payConfig.getString("noncestr"), payConfig.getString("timestamp"), payConfig.getString("package"), payConfig.getString("sign"));
//        payPresenter.pullWXPay(payConfig.getString("appid"), payConfig.getString("partnerid"), payConfig.getString("prepayid"),
//                                payConfig.getString("noncestr"), payConfig.getString("timestamp"), payConfig.getString("package"), payConfig.getString("sign"));
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

    private void detailRequest(JSONObject data) {
        if(pay){
            refreshData = true;
            pay = false;
            getDialog().dismissDialog();
            getDialog().setHintMessage(getResources().getString(R.string.pay_succeed));
            getDialog().showDialog(-1);
            if(isBigColumn){
                ColumnDirectoryFragment fragment = (ColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
                fragment.clearData();
            }else{
                LittleColumnDirectoryFragment fragment = (LittleColumnDirectoryFragment) columnViewPagerAdapter.getItem(1);
                fragment.clearData();
            }
            setLoadState(ListBottomLoadMoreView.STATE_NOT_LOAD);
        }
        if(data.getIntValue("has_buy") == 0){
            buyView.setVisibility(View.VISIBLE);
            buyView.setBuyPrice(data.getIntValue("price"));
            isHasBuy = false;
        }else{
            buyView.setVisibility(View.GONE);
            isHasBuy = true;
        }
        ColumnDetailFragment detailFragment = (ColumnDetailFragment) columnViewPagerAdapter.getItem(0);
        detailFragment.setContentDetail(data.getString("content"));
        columnTitle.setText(data.getString("title"));
        columnImage.setImageURI(data.getString("img_url_compressed"));
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
                break;
            case R.id.btn_content_directory:
                setColumnViewPager(1);
                loadMoreView.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.buy_vip:
                toastCustom("购买超级会员");
                break;
            case R.id.buy_course:
                buyResource();
                break;
            default:
                break;
        }
    }

    private void buyResource() {
        pay = true;
        getDialog().showLoadDialog(false);
        int resourceType = isBigColumn ? 8 : 6;
        payOrder(resourceId, resourceType, 3);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
