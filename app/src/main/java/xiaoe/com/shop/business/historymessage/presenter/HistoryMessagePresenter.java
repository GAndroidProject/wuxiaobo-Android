package xiaoe.com.shop.business.historymessage.presenter;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.common.entitys.HistoryMessageReq;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.GetHistoryMessageRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

/**
 * @author flynnWang
 * @date 2018/11/13
 * <p>
 * 描述：
 */
public class HistoryMessagePresenter implements IBizCallback {

    private INetworkResponse inr;

    public HistoryMessagePresenter(INetworkResponse inr) {
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

    public void requestHistoryMessage(HistoryMessageReq historyMessageReq) {
        GetHistoryMessageRequest getHistoryMessageRequest = new GetHistoryMessageRequest(this);

        // 店铺 ID
        getHistoryMessageRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        // 用户 ID
        getHistoryMessageRequest.addRequestParam("user_id", "i_5be03f4a9b9b2_W08enCASgk");
//        getHistoryMessageRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        // 其他参数
        getHistoryMessageRequest.addRequestParam("buz_data", historyMessageReq.getBuz_data());

        getHistoryMessageRequest.sendRequest();
    }

}
