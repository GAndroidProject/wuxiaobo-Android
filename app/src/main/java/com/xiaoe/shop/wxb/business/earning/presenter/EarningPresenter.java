package com.xiaoe.shop.wxb.business.earning.presenter;

import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.AccountDetailRequest;
import com.xiaoe.network.requests.EarningRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.WithdrawRequest;

public class EarningPresenter implements IBizCallback {

    private INetworkResponse inr;
    public static final String BALANCE_TAG = "balance_tag";

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
        EarningRequest earningRequest = new EarningRequest(this);

        earningRequest.addRequestParam("asset_type", assetType);
        earningRequest.addRequestParam("is_flow", needFlow);
        earningRequest.addRequestParam("flow_type", flowType);
        earningRequest.addRequestParam("page_index", pageIndex);
        earningRequest.addRequestParam("page_size", pageSize);

        earningRequest.setNeedCache(true);
        earningRequest.setCacheKey(assetType);

        earningRequest.sendRequest();
    }

    /**
     * 获取账户余额
     */
    public void requestAccountBalance() {
        EarningRequest earningRequest = new EarningRequest(this);

        earningRequest.addRequestParam("asset_type", "balance");
        earningRequest.addRequestParam("wallect_type", 2);
        earningRequest.setRequestTag(BALANCE_TAG);

        earningRequest.sendRequest();
    }

    /**
     * 请求提现
     * @param price    提现价格
     * @param ip       手机 ip
     */
    public void requestWithdrawal(double price, String ip) {
        WithdrawRequest withdrawRequest = new WithdrawRequest(this);

        withdrawRequest.addRequestParam("amount", price);
        withdrawRequest.addRequestParam("re_user_name", "");
        withdrawRequest.addRequestParam("assert_type", "profit");
        withdrawRequest.addRequestParam("account_type", 2);
        withdrawRequest.addRequestParam("desc", "提现");
        withdrawRequest.addRequestParam("client_ip", ip);

        withdrawRequest.sendRequest();
    }

    /**
     * 请求账户明细
     * @param pageIndex 页码
     * @param pageSize  每页大小
     */
    public void requestAccountDetail(int pageIndex, int pageSize) {
        AccountDetailRequest accountDetailRequest = new AccountDetailRequest(this);

        accountDetailRequest.addRequestParam("wallet_type", 2);
        accountDetailRequest.addRequestParam("page_index", pageIndex);
        accountDetailRequest.addRequestParam("page_size", pageSize);

        accountDetailRequest.sendRequest();
    }
}
