package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class ScholarshipBoughtListRequest extends IRequest {

    public ScholarshipBoughtListRequest(IBizCallback iBizCallback) {
//        super(NetworkEngine.API_THIRD_BASE_URL + "get_share_list",  iBizCallback);
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.get.share.list/1.0.0",  iBizCallback);
    }
}
