package xiaoe.com.network.network_interface;


import xiaoe.com.network.requests.IRequest;

/**
 * Created by Administrator on 2017/5/25.
 */

public interface INetworkResponse {
    void onResponse(IRequest iRequest, boolean success, Object entity);
    void onMainThreadResponse(IRequest iRequest, boolean success, Object entity);
}
