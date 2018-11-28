package com.xiaoe.shop.wxb.business.coupon.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.entitys.CouponInfo;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.MineCouponRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.coupon.presenter.CouponPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.interfaces.OnSelectCouponListener;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.widget.StatusPagerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zakli
 * @date 2018/10/20
 * <p>
 * 描述：优惠券列表
 */
public class CouponActivity extends XiaoeActivity implements View.OnClickListener, OnSelectCouponListener {

    private static final String TAG = "CouponActivity";

    protected static final String EMPTY_CONTENT = "empty_content";

    private Toolbar toolbar;
    private ImageView couponBack;

    private CouponPresenter mCouponPresenter;
    private StatusPagerView statusPagerView;
    private CouponFragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_coupon);

        initTitle();
        initView();
        initListener();
        statusPagerView.setLoadingState(View.VISIBLE);
        mCouponPresenter = new CouponPresenter(this);
        mCouponPresenter.requestMineCoupon("all");
    }

    // 沉浸式初始化
    private void initTitle() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        toolbar = (Toolbar) findViewById(R.id.coupon_bar);
        toolbar.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initView() {
        couponBack = (ImageView) findViewById(R.id.coupon_back);

        statusPagerView = (StatusPagerView) findViewById(R.id.state_pager_view);

        currentFragment = CouponFragment.newInstance(R.layout.fragment_coupone);
        currentFragment.setMarginTop(Dp2Px2SpUtil.dp2px(this, 20));
        getSupportFragmentManager().beginTransaction().add(R.id.coupon_content_wrap, currentFragment, EMPTY_CONTENT).commit();
    }

    private void initListener() {
        couponBack.setOnClickListener(this);
        currentFragment.setOnSelectCouponListener(this);
        statusPagerView.setOnClickListener(this);
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if(activityDestroy){
            return;
        }
        if(success){
            if(iRequest instanceof MineCouponRequest){
                JSONObject jsonObject = (JSONObject) entity;
                mineCouponRequest(jsonObject);
            }
        }else{
            statusPagerView.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
        }
    }

    private void mineCouponRequest(JSONObject jsonObject) {
        if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED){
            statusPagerView.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
            return;
        }
        JSONObject data = jsonObject.getJSONObject("data");

        //可用优惠券
        List<CouponInfo> validCoupon = data.getJSONArray("valid").toJavaList(CouponInfo.class);
        //可用的优惠券
        List<CouponInfo> invalidCoupon = data.getJSONArray("invalid").toJavaList(CouponInfo.class);
        if(validCoupon.size() <= 0 && invalidCoupon.size() <= 0){
            statusPagerView.setPagerState(StatusPagerView.EMPTY, getString(R.string.coupon_empty_desc), R.mipmap.cash_none);
            return;
        }
        statusPagerView.setLoadingFinish();
        List<CouponInfo> couponList = new ArrayList<CouponInfo>();
        for (CouponInfo couponInfo : validCoupon) {
            couponInfo.setValid(true);
            couponList.add(couponInfo);
        }
        for (CouponInfo couponInfo : invalidCoupon) {
            couponInfo.setValid(false);
            couponList.add(couponInfo);
        }

        currentFragment.addData(couponList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.coupon_back:
                finish();
                break;
            case R.id.state_pager_view:
                if (statusPagerView.getCurrentLoadingStatus() == StatusPagerView.FAIL) {
                    statusPagerView.setPagerState(StatusPagerView.LOADING, "", 0);
                    mCouponPresenter.requestMineCoupon("all");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onSelect(CouponInfo couponInfo, int position) {
        switch (couponInfo.getType()) {
            case 0:
                JumpDetail.jumpCouponCanRerource(this, couponInfo);
                break;
            case 1:
                JumpDetail.jumpMainScholarship(this, true, true, 1);
                break;
            default:
                break;
        }
    }
}
