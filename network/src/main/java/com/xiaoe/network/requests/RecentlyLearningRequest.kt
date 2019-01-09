package com.xiaoe.network.requests

import com.xiaoe.network.NetworkEngine
import com.xiaoe.network.network_interface.IBizCallback

class RecentlyLearningRequest(iBizCallback: IBizCallback) :
        IRequest(NetworkEngine.API_THIRD_BASE_URL + "xe.user.learning.records.get/1.0.1",
                null, iBizCallback)
