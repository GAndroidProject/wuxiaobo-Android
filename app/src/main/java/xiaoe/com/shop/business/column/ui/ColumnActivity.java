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

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;

import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.DetailRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.column.ColumnFragmentStatePagerAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.column.presenter.ColumnPresenter;
import xiaoe.com.shop.interfaces.OnCustomScrollChangedListener;
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
        columnPresenter.requestDetail(resourceId, isBigColumn ? "8" : "5");
    }

    private void initView() {
        columnMenuWarp = (RelativeLayout) findViewById(R.id.column_menu_warp);
        columnToolBar = (RelativeLayout) findViewById(R.id.column_tool_bar);
        columnToolBar.setBackgroundColor(Color.argb(0,255,255,255));
        toolBarheight = Dp2Px2SpUtil.dp2px(this,280);
        columnScrollView = (CustomScrollView) findViewById(R.id.column_scroll_view);
        columnScrollView.setScrollChanged(this);

        buyView = (CommonBuyView) findViewById(R.id.common_buy_layout);
        buyView.setVisibility(View.GONE);

        String imageUrl = mIntent.getStringExtra("column_image_url");
        columnImage = (SimpleDraweeView) findViewById(R.id.column_image);
        if("".equals(imageUrl)){
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
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        JSONObject jsonObject = (JSONObject) entity;
        if(entity == null || !success){
            return;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED || data == null ){
            return;
        }
        if(iRequest instanceof DetailRequest){
            detailRequest(data.getJSONObject("resource_info"));
        }
    }

    private void detailRequest(JSONObject data) {
        if(data.getIntValue("has_buy") == 0){
            buyView.setVisibility(View.VISIBLE);
        }else{
            buyView.setVisibility(View.GONE);
        }
        ColumnDetailFragment detailFragment = (ColumnDetailFragment) columnViewPagerAdapter.getItem(0);
        detailFragment.setContentDetail(data.getString("content"));
        columnTitle.setText(data.getString("title"));
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
            default:
                break;
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
        columnToolBar.setBackgroundColor(Color.argb((int) alpha,255,255,255));
    }
    @Override
    public void onLoadState(int state) {
        if(columnViewPager.getCurrentItem() == 1){
            loadMoreView.setLoadState(ListBottomLoadMoreView.STATE_LOADING);
            columnScrollView.setLoadState(ListBottomLoadMoreView.STATE_LOADING);
        }
    }
}
