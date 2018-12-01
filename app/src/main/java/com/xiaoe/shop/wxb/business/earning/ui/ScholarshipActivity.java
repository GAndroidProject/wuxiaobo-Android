package com.xiaoe.shop.wxb.business.earning.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.entitys.EarningItem;
import com.xiaoe.common.utils.MeasureUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.EarningRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.earning.presenter.EarningListAdapter;
import com.xiaoe.shop.wxb.business.earning.presenter.EarningPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.widget.StatusPagerView;

public class ScholarshipActivity extends XiaoeActivity {

    private static final String TAG = "ScholarshipActivity";

    @BindView(R.id.scholarship_page_wrap)
    LinearLayout scholarWrap;

    @BindView(R.id.title_back)
    ImageView scholarshipBack;
    @BindView(R.id.title_content)
    TextView scholarshipTitle;
    @BindView(R.id.title_end)
    TextView scholarshipWithdrawal;

    @BindView(R.id.scholarship_refresh)
    SmartRefreshLayout scholarshipRefresh;

    @BindView(R.id.scholarship_page_content)
    TextView scholarshipMoney;
    @BindView(R.id.scholarship_page_submit)
    TextView scholarshipSubmit;
    @BindView(R.id.scholarship_page_be_super_vip)
    TextView scholarshipBeSuperVip;
    @BindView(R.id.scholarship_loading)
    StatusPagerView scholarshipLoading;

    @BindView(R.id.earning_list_title)
    TextView scholarshipListTitle;
    @BindView(R.id.earning_list)
    ListView scholarshipList;
    @BindView(R.id.earning_empty_tip)
    TextView scholarshipTip;

    Unbinder unbinder;

    EarningPresenter earningPresenter;

    List<EarningItem> dataList;

    String allMoney; // 可提现金额
    private int pageIndex = 1; // 默认第一页
    private int pageSize = 10; // 默认每页加载 10 条
    EarningListAdapter earningListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_scholarship);

        unbinder = ButterKnife.bind(this);

        scholarWrap.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);

        earningPresenter = new EarningPresenter(this);
        earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NEED_FLOW ,Constants.EARNING_FLOW_TYPE, pageIndex, pageSize);

        initData();
        initListener();
    }

    private void initData() {
        dataList = new ArrayList<>();
        scholarshipTitle.setText(getString(R.string.scholarship_title));
        scholarshipWithdrawal.setText(R.string.withdrawal_record_text);
        scholarshipListTitle.setText(getString(R.string.scholarship_page_list_title));
        scholarshipTip.setText(R.string.no_scholarship_record);
        scholarshipTip.setVisibility(View.VISIBLE);
        scholarshipLoading.setVisibility(View.GONE);
        if (CommonUserInfo.isIsSuperVipAvailable() && !CommonUserInfo.isIsSuperVip()) { // 不是超级会员
            scholarshipBeSuperVip.setVisibility(View.VISIBLE);
        } else {
            scholarshipBeSuperVip.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        scholarshipRefresh.setEnableRefresh(true);
        scholarshipRefresh.setEnableLoadMore(true);
        scholarshipRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (pageIndex != 1) {
                    pageIndex = 1;
                }
                earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NEED_FLOW ,Constants.EARNING_FLOW_TYPE, pageIndex, pageSize);
            }
        });
        scholarshipRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (earningPresenter == null) {
                    earningPresenter = new EarningPresenter(ScholarshipActivity.this);
                }
                earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NEED_FLOW ,Constants.EARNING_FLOW_TYPE, ++pageIndex, pageSize);
            }
        });
        scholarshipBack.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                onBackPressed();
            }
        });
        scholarshipWithdrawal.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                JumpDetail.jumpWrRecord(ScholarshipActivity.this);
            }
        });
        scholarshipSubmit.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                JumpDetail.jumpWr(ScholarshipActivity.this, allMoney);
            }
        });
        scholarshipBeSuperVip.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                JumpDetail.jumpSuperVip(ScholarshipActivity.this);
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
                    scholarshipLoading.setVisibility(View.VISIBLE);
                    scholarshipLoading.setPagerState(StatusPagerView.FAIL, getString(R.string.request_fail), R.mipmap.error_page);
                    Log.d(TAG, "onMainThreadResponse: 请求数据失败...");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail...");
            scholarshipRefresh.finishRefresh();
            if (TextUtils.isEmpty(scholarshipMoney.getText().toString())) {
                scholarshipLoading.setVisibility(View.VISIBLE);
                scholarshipLoading.setPagerState(StatusPagerView.FAIL, getString(R.string.request_fail), R.mipmap.error_page);
            } else {
                scholarshipLoading.setLoadingFinish();
                Toast.makeText(this, getString(R.string.network_error_text), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 初始化页面数据
    private void initPageData(JSONObject data) {
        int money = data.getInteger("balance");
        BigDecimal top = new BigDecimal(money);
        BigDecimal bottom = new BigDecimal(100);
        allMoney = String.valueOf(top.divide(bottom, 2, RoundingMode.HALF_UP));
        JSONArray flowList = (JSONArray) data.get("flow_list");
        if (flowList.size() == 0  && earningListAdapter != null) {
            scholarshipRefresh.setNoMoreData(true);
            scholarshipRefresh.setEnableLoadMore(false);
            scholarshipRefresh.finishLoadMore();
            return;
        }
        if (pageIndex == 1 && dataList.size() != 0) { // 第一页但是有数据，表示为下拉加载
            dataList.clear();
            // 放开加载更多限制
            scholarshipRefresh.setEnableLoadMore(true);
            scholarshipRefresh.setNoMoreData(false);
        }
        scholarshipRefresh.finishLoadMore();
        scholarshipRefresh.finishRefresh();
        for (Object item : flowList) {
            JSONObject jsonItem = (JSONObject) item;
            int flowType = jsonItem.getInteger("flow_type");
            String detailId = jsonItem.getString("detail_id");
            int price = jsonItem.getInteger("price");
            String createdAt = jsonItem.getString("created_at");
            String desc = jsonItem.getString("desc");

            EarningItem earningItem = new EarningItem();
            earningItem.setItemTitle(desc);
            earningItem.setItemContent(createdAt.split(" ")[0]);
            BigDecimal topPrice = new BigDecimal(price);
            String realPriceStr = topPrice.divide(bottom, 2, RoundingMode.HALF_UP) + "元";
            earningItem.setItemMoney(realPriceStr);

            dataList.add(earningItem);
        }

        scholarshipMoney.setText(allMoney);
        if (dataList.size() > 0) {
            if (earningListAdapter == null) {
                earningListAdapter = new EarningListAdapter(this, dataList);
                scholarshipList.setAdapter(earningListAdapter);
            } else {
                earningListAdapter.notifyDataSetChanged();
            }
            MeasureUtil.setListViewHeightBasedOnChildren(scholarshipList);
            scholarshipTip.setVisibility(View.GONE);
            scholarshipLoading.setVisibility(View.GONE);
        } else {
            scholarshipTip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
