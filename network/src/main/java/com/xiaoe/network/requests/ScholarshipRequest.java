package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class ScholarshipRequest extends IRequest {

    public ScholarshipRequest(IBizCallback iBizCallback) {
//        super(NetworkEngine.API_THIRD_BASE_URL + "get_reward_ranking_list",iBizCallback);
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.get.reward.ranking.list/1.0.0",iBizCallback);
    }
}
