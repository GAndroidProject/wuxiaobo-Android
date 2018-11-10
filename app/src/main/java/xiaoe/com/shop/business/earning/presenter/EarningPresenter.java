package xiaoe.com.shop.business.earning.presenter;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.EarningRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.IntegralRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

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
     * @param flowType   流水类型 0 -- 详情页，1 -- 提现页
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
     * @param pageIndex 页面下标
     * @param pageSize  页面大小
     */
    public void requestIntegralData(int pageIndex, int pageSize) {
        IntegralRequest integralRequest = new IntegralRequest(NetworkEngine.EARNING_BASE_URL + "get_credit_balance_flow", this);

        integralRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        integralRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        integralRequest.addRequestParam("account_type", 2);
        integralRequest.addRequestParam("page_index", pageIndex);
        integralRequest.addRequestParam("page_size", pageSize);

        NetworkEngine.getInstance().sendRequest(integralRequest);
    }
}
