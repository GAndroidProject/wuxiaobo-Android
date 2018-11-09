package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

// 移除收藏
public class RemoveCollectionListRequest extends IRequest {

    public RemoveCollectionListRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.COLLECTION_BASE_URL+"xe.user.favorites.del/1.0.0", iBizCallback);
    }
    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}
