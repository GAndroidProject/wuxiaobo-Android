package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class CommentLikeRequest extends IRequest {
    public CommentLikeRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.COMMENT_BASE_URL+"xe.goods.comments.like.add/1.0.0", iBizCallback);
    }
    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}
