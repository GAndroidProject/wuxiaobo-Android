package com.xiaoe.network.requests

import com.xiaoe.network.NetworkEngine
import com.xiaoe.network.network_interface.IBizCallback

/**
 * Date: 2019/1/8 13:04
 * Author: hans yang
 * Description:
 */
class GetSingleRecordRequest(iBizCallback: IBizCallback) :
        IRequest(NetworkEngine.API_THIRD_BASE_URL + "xe.user.learning.records.pull/1.0.1",
                null, iBizCallback)