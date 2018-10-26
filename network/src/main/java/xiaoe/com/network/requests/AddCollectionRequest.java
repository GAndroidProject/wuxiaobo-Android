package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 添加收藏请求
public class AddCollectionRequest extends IRequest {
    public AddCollectionRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    public AddCollectionRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}
