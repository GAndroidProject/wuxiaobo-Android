package com.xiaoe.shop.wxb.business.super_vip.presenter;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.IsSuperVipRequest;
import com.xiaoe.network.requests.SuperVipBuyInfoRequest;

public class SuperVipPresenter implements IBizCallback {

    private INetworkResponse inr = null;

    public SuperVipPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    // 请求超级会员信息
    public void requestSuperVip() {
        IsSuperVipRequest isSuperVipRequest = new IsSuperVipRequest(this);

        isSuperVipRequest.addRequestParam("shop_id", CommonUserInfo.getShopId());
        isSuperVipRequest.addRequestParam("user_id", CommonUserInfo.getUserId());

        NetworkEngine.getInstance().sendRequest(isSuperVipRequest);
    }

    // 请求超级会员购买信息
    public void requestSuperVipBuyInfo() {
        SuperVipBuyInfoRequest superVipBuyInfoRequest = new SuperVipBuyInfoRequest(this);

        superVipBuyInfoRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        superVipBuyInfoRequest.addRequestParam("user_id", CommonUserInfo.getUserId());

        NetworkEngine.getInstance().sendRequest(superVipBuyInfoRequest);
    }
}
