package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 检查是否收藏请求
public class CheckCollectionRequest extends IRequest {

    public CheckCollectionRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    public CheckCollectionRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}
