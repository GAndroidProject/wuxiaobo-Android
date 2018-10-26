package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class ColumnListRequst extends IRequest {
    //拉取专栏的资源列表
    private static final String TAG = "ColumnListRequst";

    public ColumnListRequst(IBizCallback iBizCallback) {
        super(NetworkEngine.CLASS_DETAIL_BASE_URL+"xe.goods.relation.get/1.0.0", iBizCallback);
    }

    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}
