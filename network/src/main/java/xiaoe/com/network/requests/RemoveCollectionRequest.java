package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 移除收藏
public class RemoveCollectionRequest extends IRequest {
    public RemoveCollectionRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    public RemoveCollectionRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}
