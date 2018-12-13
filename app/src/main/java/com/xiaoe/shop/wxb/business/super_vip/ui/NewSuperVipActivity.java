package com.xiaoe.shop.wxb.business.super_vip.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.SuperVipBuyInfoRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.super_vip.presenter.SuperVipPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NewSuperVipActivity extends XiaoeActivity {

    private static final String TAG = "NewSuperVipActivity";

    Unbinder unbinder;

    @BindView(R.id.super_vip_bg_new)
    ImageView vipBg;
    @BindView(R.id.super_vip_buy_new)
    Button vipSubmit;
    @BindView(R.id.super_vip_has_buy)
    ImageView vipHasBuy;

    boolean hasBuy; // 是否购买
    String productId; // 购买所需要的 produceId
    String resourceId; // 购买所需要的 资源 Id
    String price; // 购买价格
    private String expireAtStart;
    private String expireAtEnd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_super_vip_new);
        unbinder = ButterKnife.bind(this);
        hasBuy = getIntent().getBooleanExtra("hasBuy", false);

        initView();
    }

    private void initView() {
        if (!hasBuy) { // 没买就请求购买信息
            SuperVipPresenter superVipPresenter = new SuperVipPresenter(this);
            superVipPresenter.requestSuperVipBuyInfo();
            vipHasBuy.setVisibility(View.GONE);
        } else { // 买了，文案改为已购文案
            vipSubmit.setVisibility(View.GONE);
            vipHasBuy.setVisibility(View.VISIBLE);
        }
        Glide.with(this).load(R.mipmap.vip_bg_new).into(vipBg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick(R.id.super_vip_buy_new)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.super_vip_buy_new:
                if (!hasBuy) { // 没买
                    // 默认超级会员的 resourceType 为 23，resourceId 为 resourceId
                    if (price != null) {
                        double priceNum = Double.parseDouble(price) * 100;
                        JumpDetail.jumpPay(this, resourceId, productId, 23, "res:///" + R.mipmap.vip_card,
                                getString(R.string.super_members), (int) priceNum, expireAtStart + "-" + expireAtEnd);
                        this.finish();
                    }
                }
                break;
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof SuperVipBuyInfoRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    productId = data.getString("svip_id");
                    resourceId = data.getString("resource_id");
                    expireAtStart = data.getString("expire_at_start");
                    expireAtEnd = data.getString("expire_at_end");
                    int priceNum = data.getInteger("price");
                    BigDecimal top = new BigDecimal(priceNum);
                    BigDecimal bottom = new BigDecimal(100);
                    BigDecimal res = top.divide(bottom, 2, BigDecimal.ROUND_HALF_UP);
                    price = res.toPlainString();
                    vipSubmit.setClickable(true);
                    initPageData();
                } else {
                    Log.d(TAG, "onMainThreadResponse: 请求超级会员购买信息失败...");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail...");
        }
    }

    private void initPageData() {
        vipSubmit.setText(String.format(getString(R.string.vip_btn_money), price));
        vipSubmit.setBackground(getResources().getDrawable(R.drawable.vip_buy));
        vipSubmit.setVisibility(View.VISIBLE);
    }
}
