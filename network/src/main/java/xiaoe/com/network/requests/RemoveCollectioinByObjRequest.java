package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 删除指定收藏接口
public class RemoveCollectioinByObjRequest extends IRequest {

    public RemoveCollectioinByObjRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    public RemoveCollectioinByObjRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}
