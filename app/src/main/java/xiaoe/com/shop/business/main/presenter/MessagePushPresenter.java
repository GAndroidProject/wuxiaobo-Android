package xiaoe.com.shop.business.main.presenter;

import android.util.Log;

import cn.jpush.android.api.JPushInterface;
import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.common.app.XiaoeApplication;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.BindJgPushRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

/**
 * @author flynnWang
 * @date 2018/11/12
 * <p>
 * 描述：消息推送
 */
public class MessagePushPresenter implements IBizCallback {

    private static final String TAG = "MessagePushPresenter";

    private INetworkResponse inr;

    public MessagePushPresenter(INetworkResponse inr) {
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

    public void requestBindJgPush(boolean isFormalUser) {
        BindJgPushRequest bindJgPushRequest = new BindJgPushRequest(this);

        // 店铺 ID
        bindJgPushRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        // 用户 ID
        bindJgPushRequest.addRequestParam("user_id", isFormalUser ? CommonUserInfo.getUserId() : "");
        // 极光推送的 注册 ID
        bindJgPushRequest.addRequestParam("device_token", JPushInterface.getRegistrationID(XiaoeApplication.getmContext()));
        // 客户端类型 - 1：Android    2：iOS
        bindJgPushRequest.addRequestParam("type", 1);

        NetworkEngine.getInstance().sendRequest(bindJgPushRequest);
    }
}
