package xiaoe.com.shop.business.super_vip.presenter;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.IsSuperVipRequest;
import xiaoe.com.network.requests.SuperVipBuyInfoRequest;

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

        isSuperVipRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
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
