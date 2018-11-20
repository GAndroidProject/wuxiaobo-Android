package com.xiaoe.shop.wxb.business.super_vip.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.SuperVipBuyInfoRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.super_vip.presenter.SuperVipPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;

public class SuperVipActivity extends XiaoeActivity {

    private static final String TAG = "SuperVipActivity";

    @BindView(R.id.super_vip_back)
    ImageView superVipBack;
    @BindView(R.id.super_vip_bg)
    SimpleDraweeView superVipBg;
    @BindView(R.id.super_vip_submit)
    TextView superVipSubmit;

    Unbinder unbinder;

    String productId; // 购买所需要的 produceId
    String resourceId; // 购买所需要的 资源 Id
    String price; // 购买价格

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_vip);
        unbinder = ButterKnife.bind(this);

        SuperVipPresenter superVipPresenter = new SuperVipPresenter(this);
        superVipPresenter.requestSuperVipBuyInfo();
        superVipBg.setImageURI("res:///" + R.mipmap.super_vip_new);

        initListener();
    }

    private void initListener() {
        superVipSubmit.setClickable(false);
        superVipSubmit.setOnClickListener(v -> {
            // 默认超级会员的 resourceType 为 23，resourceId 为 resourceId
            if (price != null) {
                double priceNum = Double.parseDouble(price) * 100;
                JumpDetail.jumpPay(SuperVipActivity.this, resourceId, productId, 23, "res:///" + R.mipmap.pay_vip_bg, "超级会员", (int) priceNum);
            }
        });
        superVipBack.setOnClickListener(v -> onBackPressed());
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
                    int priceNum = data.getInteger("price");
                    BigDecimal top = new BigDecimal(priceNum);
                    BigDecimal bottom = new BigDecimal(100);
                    BigDecimal res = top.divide(bottom, 2, BigDecimal.ROUND_HALF_UP);
                    price = res.toPlainString();
                    superVipSubmit.setClickable(true);
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
        superVipSubmit.setText(String.format(getString(R.string.vip_btn_money), price));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
