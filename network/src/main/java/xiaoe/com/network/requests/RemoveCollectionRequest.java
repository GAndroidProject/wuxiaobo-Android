package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 移除收藏
public class RemoveCollectionRequest extends IRequest {
    RemoveCollectionRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    RemoveCollectionRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}
