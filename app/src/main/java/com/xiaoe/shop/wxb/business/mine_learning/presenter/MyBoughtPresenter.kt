package com.xiaoe.shop.wxb.business.mine_learning.presenter

import com.xiaoe.network.network_interface.IBizCallback
import com.xiaoe.network.network_interface.INetworkResponse
import com.xiaoe.network.requests.IRequest
import com.xiaoe.network.requests.RecentlyLearningRequest

class MyBoughtPresenter(private val inr: INetworkResponse) : IBizCallback {

    override fun onResponse(iRequest: IRequest, success: Boolean, entity: Any?) {
        inr.onResponse(iRequest, success, entity)
    }

    // 获取正在学习列表
    fun requestLearningData(pageIndex: Int, pageSize: Int) {
        val recentlyLearningRequest = RecentlyLearningRequest(this)

        recentlyLearningRequest.addDataParam("page_size", pageSize)
        recentlyLearningRequest.addDataParam("page", pageIndex)
        recentlyLearningRequest.addDataParam("agent_type", 2)
        recentlyLearningRequest.isNeedCache = true
        recentlyLearningRequest.cacheKey = "learning"
        recentlyLearningRequest.sendRequest()
    }
}
