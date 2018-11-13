package xiaoe.com.shop.business.historymessage.presenter;

import cn.jpush.android.api.JPushInterface;
import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.common.app.XiaoeApplication;
import xiaoe.com.network.NetworkEngine;
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

    public void requestHistoryMessage(int pageSize, int messageLastTime) {
        GetHistoryMessageRequest getHistoryMessageRequest = new GetHistoryMessageRequest(this);

        // 店铺 ID
        getHistoryMessageRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        // 用户 ID
        getHistoryMessageRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        // 页码大小(默认20)
        getHistoryMessageRequest.addRequestParam("page_size", 20);
        // 最后一条消息的发送时间send_at（默认-1表示当前时间 ）
        getHistoryMessageRequest.addRequestParam("message_last_id", -1);

        getHistoryMessageRequest.sendRequest();
    }

}
