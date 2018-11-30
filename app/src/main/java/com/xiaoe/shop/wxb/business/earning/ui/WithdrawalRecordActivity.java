package com.xiaoe.shop.wxb.business.earning.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.entitys.WrItem;
import com.xiaoe.common.utils.MeasureUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.EarningRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.earning.presenter.EarningPresenter;
import com.xiaoe.shop.wxb.business.earning.presenter.WrListAdapter;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.widget.StatusPagerView;

// 提现记录页面
public class WithdrawalRecordActivity extends XiaoeActivity {

    private static final String TAG = "wrActivity";

    @BindView(R.id.withdrawal_record_wrap)
    LinearLayout wrWrap;

    @BindView(R.id.title_back)
    ImageView wrBack;
    @BindView(R.id.title_content)
    TextView wrTitle;
    @BindView(R.id.title_end)
    TextView wrEnd;

    @BindView(R.id.withdrawal_record_refresh)
    SmartRefreshLayout wrRefresh;

    @BindView(R.id.withdrawal_record_list)
    ListView wrList;

    @BindView(R.id.withdrawal_loading)
    StatusPagerView wrLoading;

    List<WrItem> dataList;

    Unbinder unbinder;
    EarningPresenter earningPresenter;
    private int pageIndex = 1; // 默认加载第一页
    private int pageSize = 10; // 默认每页加载 10 条
    WrListAdapter wrListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_withdrawal_record);

        unbinder = ButterKnife.bind(this);
        wrWrap.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        wrLoading.setVisibility(View.VISIBLE);
        wrLoading.setLoadingState(View.VISIBLE);

        earningPresenter = new EarningPresenter(this);
        earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NEED_FLOW, Constants.WITHDRAWAL_FLOW_TYPE, pageIndex, pageSize);

        initData();
        initListener();
    }

    private void initData() {
        wrTitle.setText("提现记录");
        wrEnd.setVisibility(View.GONE);
        dataList = new ArrayList<>();
    }

    private void initListener() {
        wrRefresh.setEnableRefresh(true);
        wrRefresh.setEnableLoadMore(true);
        wrBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        wrRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (pageIndex != 1) {
                    pageIndex = 1;
                }
                earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NEED_FLOW, Constants.WITHDRAWAL_FLOW_TYPE, pageIndex, pageSize);
            }
        });
        wrRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NEED_FLOW, Constants.WITHDRAWAL_FLOW_TYPE, ++pageIndex, pageSize);
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof EarningRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    initPageData(data);
                } else {
                    Log.d(TAG, "onMainThreadResponse: 请求失败...");
                    wrLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: ");
            wrRefresh.finishRefresh();
            if (dataList.size() == 0) {
                wrLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
            } else {
                wrLoading.setLoadingFinish();
                Toast.makeText(this, getString(R.string.network_error_text), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initPageData(JSONObject data) {
        int priceBottom = 100;
        JSONArray flowList = (JSONArray) data.get("flow_list");
        if (flowList.size() == 0 && wrListAdapter != null) { // 加载更多没有数据了
            wrRefresh.setNoMoreData(true);
            wrRefresh.setEnableLoadMore(false);
            wrRefresh.finishLoadMore();
            return;
        }
        if (pageIndex == 1 && dataList.size() > 0) { // 有数据时下拉刷新
            dataList.clear();
            // 放开上拉加载限制
            wrRefresh.setNoMoreData(false);
            wrRefresh.setEnableLoadMore(true);
        }
        wrRefresh.finishRefresh();
        wrRefresh.finishLoadMore();
        for (Object flowListItem : flowList) {
            JSONObject jsonItem = (JSONObject) flowListItem;
            int itemPriceTop = jsonItem.getInteger("price");
            String itemTitle = jsonItem.getString("description");
            String itemDesc = jsonItem.getString("desc");
            String itemTime = jsonItem.getString("created_at");

            BigDecimal top = new BigDecimal(itemPriceTop);
            BigDecimal bottom = new BigDecimal(priceBottom);

            // 提现记录假数据
            WrItem wrItem = new WrItem();
            wrItem.setWrTitle(itemTitle);
            wrItem.setWrMoney(top.divide(bottom, 2, RoundingMode.HALF_UP));
            wrItem.setWrTime(itemTime);
            wrItem.setWrState(itemDesc);

            dataList.add(wrItem);
        }

        if (dataList.size() > 0) {
            if (wrListAdapter == null) {
                wrListAdapter = new WrListAdapter(this, dataList);
                wrList.setAdapter(wrListAdapter);
            } else {
                wrListAdapter.notifyDataSetChanged();
            }
            MeasureUtil.setListViewHeightBasedOnChildren(wrList);
        } else {
            wrLoading.setPagerState(StatusPagerView.EMPTY, "无提现记录，快去做任务领取奖学金吧", R.mipmap.cash_none);
            return;
        }

        wrLoading.setLoadingFinish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
