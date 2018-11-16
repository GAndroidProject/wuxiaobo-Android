package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class ScholarshipTaskListRequest extends IRequest {

    public ScholarshipTaskListRequest(IBizCallback iBizCallback) {
//        super(NetworkEngine.API_THIRD_BASE_URL + "get_task_list", iBizCallback);
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.get.task.list/1.0.0", iBizCallback);
    }
}
