package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class CommentListRequest extends IRequest {
    public CommentListRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.COMMENT_BASE_URL+"xe.goods.comments.get/1.0.0", iBizCallback);
    }

    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}
