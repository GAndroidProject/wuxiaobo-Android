package com.xiaoe.network.requests

import com.xiaoe.network.NetworkEngine
import com.xiaoe.network.network_interface.IBizCallback

class BoughtRecordRequest(iBizCallback: IBizCallback) :
        IRequest(NetworkEngine.API_THIRD_BASE_URL
                + "ceopenclose/getAllData",
                null, iBizCallback)
