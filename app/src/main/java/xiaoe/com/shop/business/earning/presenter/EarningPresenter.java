package xiaoe.com.shop.business.earning.presenter;

import java.math.BigDecimal;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.EarningRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.IntegralRequest;
import xiaoe.com.network.requests.WithDrawalRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;
import xiaoe.com.shop.utils.OSUtils;

public class EarningPresenter implements IBizCallback {

    private INetworkResponse inr = null;

    public EarningPresenter(INetworkResponse inr) {
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

    /**
     * 请求奖学金详情页数据
     * @param flowType   流水类型 1 -- 获取 2 -- 提现
     * @param pageIndex  页面下标
     * @param pageSize   页面大小
     */
    public void requestDetailData(int flowType, int pageIndex, int pageSize) {
        EarningRequest earningRequest = new EarningRequest(NetworkEngine.EARNING_BASE_URL + "get_c_balance_flow", this);

        earningRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        earningRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        earningRequest.addRequestParam("account_type", 2);
        earningRequest.addRequestParam("flow_type", flowType);
        earningRequest.addRequestParam("page_index", pageIndex);
        earningRequest.addRequestParam("page_size", pageSize);

        NetworkEngine.getInstance().sendRequest(earningRequest);
    }

    /**
     * 请求积分详情数据
     * @param flowType  流水类型
     * @param pageIndex 页面下标
     * @param pageSize  页面大小
     */
    public void requestIntegralData(int flowType, int pageIndex, int pageSize) {
        IntegralRequest integralRequest = new IntegralRequest(NetworkEngine.EARNING_BASE_URL + "get_credit_balance_flow", this);

        integralRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        integralRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        integralRequest.addRequestParam("account_type", 0);
        integralRequest.addRequestParam("flow_type", flowType);
        integralRequest.addRequestParam("page_index", pageIndex);
        integralRequest.addRequestParam("page_size", pageSize);

        NetworkEngine.getInstance().sendRequest(integralRequest);
    }

    /**
     * 请求提现
     * @param price    提现价格
     * @param realName 真实姓名（金额大于 20000 的时候需要）
     * @param ip       手机 ip
     */
    public void requestWithdrawal(double price, String realName, String ip) {
        WithDrawalRequest withDrawalRequest = new WithDrawalRequest(NetworkEngine.EARNING_BASE_URL + "done_c_withdraw", this);

        withDrawalRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        withDrawalRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        withDrawalRequest.addRequestParam("amount", price);
        withDrawalRequest.addRequestParam("re_user_name", realName);
        withDrawalRequest.addRequestParam("account_type", 2);
        withDrawalRequest.addRequestParam("desc", "提现");
        withDrawalRequest.addRequestParam("client_ip", ip);

        NetworkEngine.getInstance().sendRequest(withDrawalRequest);
    }
}
