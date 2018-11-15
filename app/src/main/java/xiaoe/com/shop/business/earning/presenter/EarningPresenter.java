package xiaoe.com.shop.business.earning.presenter;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.EarningRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.WithDrawalRequest;

public class EarningPresenter implements IBizCallback {

    private INetworkResponse inr = null;

    public EarningPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    /**
     * 获取流水
     * @param assetType 账户类型 0 -- 奖学金收益 1 -- 积分
     * @param needFlow  是否需要返回流水 0 -- 不需要 1 -- 需要
     * @param flowType  流水类型 0 -- 提现记录  1 -- 入账记录
     * @param pageIndex 页面下标
     * @param pageSize  页面大小
     */
    public void requestLaundryList(String assetType, int needFlow, int flowType, int pageIndex, int pageSize) {
//        EarningRequest earningRequest = new EarningRequest(NetworkEngine.EARNING_BASE_URL + "xe.user.asset.get/1.0.0", this);
        EarningRequest earningRequest = new EarningRequest(this);

        earningRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        earningRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        earningRequest.addRequestParam("asset_type", assetType);
        earningRequest.addRequestParam("is_flow", needFlow);
        earningRequest.addRequestParam("flow_type", flowType);
        earningRequest.addRequestParam("page_index", pageIndex);
        earningRequest.addRequestParam("page_size", pageSize);

        NetworkEngine.getInstance().sendRequest(earningRequest);
    }

    /**
     * 请求提现
     * @param price    提现价格
     * @param ip       手机 ip
     */
    public void requestWithdrawal(double price, String ip) {
//        WithDrawalRequest withDrawalRequest = new WithDrawalRequest(NetworkEngine.EARNING_BASE_URL + "done_c_withdraw", this);
        WithDrawalRequest withDrawalRequest = new WithDrawalRequest(this);

        withDrawalRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        withDrawalRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        withDrawalRequest.addRequestParam("amount", price);
        withDrawalRequest.addRequestParam("re_user_name", "");
        withDrawalRequest.addRequestParam("assert_type", "profit");
        withDrawalRequest.addRequestParam("account_type", 2);
        withDrawalRequest.addRequestParam("desc", "提现");
        withDrawalRequest.addRequestParam("client_ip", ip);

        NetworkEngine.getInstance().sendRequest(withDrawalRequest);
    }
}
