package com.xiaoe.shop.wxb.business.mine_learning.presenter

import com.xiaoe.network.network_interface.IBizCallback
import com.xiaoe.network.network_interface.INetworkResponse
import com.xiaoe.network.requests.BoughtRecordRequest
import com.xiaoe.network.requests.IRequest

class MyBoughtPresenter(private val inr: INetworkResponse) : IBizCallback {

    override fun onResponse(iRequest: IRequest, success: Boolean, entity: Any) {
        inr.onResponse(iRequest, success, entity)
    }

    // 获取购买记录列表
    fun requestRecordData(page: Int, pageSize: Int) {
        val boughtRecordRequest = BoughtRecordRequest(this)

        boughtRecordRequest.addDataParam("page_size", pageSize)
        boughtRecordRequest.addDataParam("page", page)

        boughtRecordRequest.isNeedCache = true
        boughtRecordRequest.cacheKey = "bought_record"

        boughtRecordRequest.sendRequest()
    }
}
