package xiaoe.com.shop.business.super_vip.presenter;

import android.net.Network;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.IsSuperVipRequest;
import xiaoe.com.network.requests.SuperVipBuyInfoRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

public class SuperVipPresenter implements IBizCallback {

    private INetworkResponse inr = null;

    public SuperVipPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (success && entity != null) {
                    inr.onMainThreadResponse(iRequest, true, entity);
                } else {
                    inr.onMainThreadResponse(iRequest, false, entity);
                }
            }
        });
    }

    // 请求超级会员信息
    public void requestSuperVip() {
        IsSuperVipRequest isSuperVipRequest = new IsSuperVipRequest(NetworkEngine.CLASS_DETAIL_BASE_URL + "xe.user.svip.info.get/1.0.0", this);

        isSuperVipRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        isSuperVipRequest.addRequestParam("user_id", CommonUserInfo.getUserId());

        NetworkEngine.getInstance().sendRequest(isSuperVipRequest);
    }

    // 请求超级会员购买信息
    public void requestSuperVipBuyInfo() {
        SuperVipBuyInfoRequest superVipBuyInfoRequest = new SuperVipBuyInfoRequest(NetworkEngine.CLASS_DETAIL_BASE_URL + "xe.user.svip.pay.info.get/1.0.0", this);

        superVipBuyInfoRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        superVipBuyInfoRequest.addRequestParam("user_id", CommonUserInfo.getUserId());

        NetworkEngine.getInstance().sendRequest(superVipBuyInfoRequest);
    }
}
