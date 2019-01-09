package com.xiaoe.shop.wxb.utils

import android.text.TextUtils
import com.google.gson.Gson
import com.xiaoe.common.app.Global
import com.xiaoe.common.entitys.*
import com.xiaoe.network.network_interface.IBizCallback
import com.xiaoe.network.requests.GetSingleRecordRequest
import com.xiaoe.network.requests.IRequest
import com.xiaoe.network.requests.UpdateMineLearningRequest
import java.lang.Exception

/**
 * Date: 2019/1/8 11:20
 * Author: hans yang
 * Description:
 */
object UploadLearnProgressManager : IBizCallback {

    var isSingleBuy = true

    private const val mTag = "UploadLearnProgress"

    private val mUploadLearnData by lazy {
        HashMap<String, UploadDataParam>()
    }

    private val mUploadLearnHistoryData by lazy {
        HashMap<String, HashMap<String,History>>()
    }

    /**
     * 添加非单品中的单品数据
     */
    fun addColumnSingleItemData(columnId: String,resourceId: String,resourceType: Int,
                                progress: Int = 10,playTime: Int = 0){
        if (TextUtils.isEmpty(columnId)){
            addSingleItemData(resourceId,resourceType,progress,playTime)
        }else if (!TextUtils.isEmpty(resourceId) && mUploadLearnData[columnId] != null){
            LogUtils.d("$mTag--addColumnSingleItemData--progress = $progress---playTime = $playTime")
            val data = History(resourceType, progress)
            var historyData = mUploadLearnHistoryData[columnId]
            if (historyData == null) {
                historyData = HashMap()
            }
            historyData!![resourceId] = data
            mUploadLearnHistoryData[columnId] = historyData!!
        }
//        if (!TextUtils.isEmpty(resourceId)) {
//            val data = History(resourceType, progress)
//            if (!TextUtils.isEmpty(columnId) && data != null) {
//                var historyData = mUploadLearnHistoryData[columnId]
//                if (historyData == null) {
//                    historyData = HashMap()
//                }
//                historyData!![resourceId] = data
//            }else{
//                addSingleItemData(resourceId,resourceType,progress,playTime)
//            }
//        }
    }

    /**
     * 添加非单品数据
     */
    fun addColumnData(resourceId: String,resourceType: Int){
        if (!TextUtils.isEmpty(resourceId)) {
            val data = UploadDataParam(false, resourceId, resourceType)
            mUploadLearnData[resourceId] = data
        }
    }

    /**
     * 添加单品数据
     */
    fun addSingleItemData(resourceId: String,resourceType: Int,progress: Int,playTime: Int = 0,
                          isUpload: Boolean = true){
        LogUtils.d("$mTag--addSingleItemData--progress = $progress---playTime = $playTime")
        if (!TextUtils.isEmpty(resourceId)) {
            val data = UploadDataParam(true, resourceId, resourceType)
            if (progress > -1) {
                val progressData = SingleItemLearnProgress(resourceType, playTime, progress)
                val proString = Gson().toJson(progressData)
                data.orgLearnProgress = proString
            }
            mUploadLearnData[resourceId] = data
            if (isUpload)
                uploadLearningProgress(resourceId)
        }
    }

    override fun onResponse(iRequest: IRequest?, success: Boolean, entity: Any?) {
        Global.g().runOnUiThread{
            try {
                if (success && entity != null && iRequest != null){
                    val tag = iRequest!!.requestTag
                    //没有数据的时候data返回数组类型，但是有数据又返回object类型
                    val obj = entity as com.alibaba.fastjson.JSONObject
                    val code = obj.getInteger("code") as Int
                    if (0 == code){
                        when(iRequest!!){
                            is UpdateMineLearningRequest ->{
                                mUploadLearnHistoryData.remove(tag)
                                val uploadData = mUploadLearnData.remove(tag)
                                LogUtils.d("$mTag--onResponse-success  tag = $tag----mUploadLearnData.size = " +
                                        "${mUploadLearnData.size}----mUploadLearnHistoryData.size = " +
                                        "${mUploadLearnHistoryData.size}---isSingleBuy = ${uploadData?.isSingleItem}")
                                uploadData?.isReTry = true
                            }
                            is GetSingleRecordRequest ->{
                                val data = obj.getJSONObject("data")
                                var progress = ""
                                if (data != null){
                                    progress = data.getString("OrgLearnProgress")
                                }
                                val uploadParam = mUploadLearnData[tag]
                                uploadParam?.run {
                                    if (!isSingleItem)
                                        orgLearnProgress = getUploadHistory(tag,progress)
                                    uploadLearningProgress(tag)
                                }
                            }
                        }
                    }else   doRequestFail(iRequest)
                }else   doRequestFail(iRequest)
            }catch (e : Exception){
                e.printStackTrace()
                LogUtils.d("$mTag--Exception UpdateMineLearningRequest")
            }
        }
    }

    private fun doRequestFail(iRequest: IRequest?) {
        val tag = iRequest?.requestTag
        if (iRequest is UpdateMineLearningRequest && !TextUtils.isEmpty(tag)) {
            val uploadParam = mUploadLearnData[tag]
            if (uploadParam != null && !uploadParam!!.isReTry) {
                uploadParam!!.isReTry = true
                uploadLearningProgress(tag!!)
                LogUtils.d("$mTag--doRequestFail  tag = $tag---isSingleBuy = $isSingleBuy")
            }
        }else if (iRequest is GetSingleRecordRequest){
            LogUtils.d("$mTag--doRequestFail GetSingleRecordRequest")
        }
    }

    // 上报学习记录
    fun uploadLearningData(resourceId: String) {
        val uploadParam = mUploadLearnData[resourceId]
        uploadParam?.run {
            val getSingleRecordRequest = GetSingleRecordRequest(
                    this@UploadLearnProgressManager)
            with(getSingleRecordRequest) {
                addDataParam("agent_type", agentType)
                addDataParam("resource_id", resourceId)
                requestTag = resourceId

                sendRequest()
            }
        }
    }

    // 上报学习记录
    private fun uploadLearningProgress(resourceId: String) {
        val uploadParam = mUploadLearnData[resourceId]
        uploadParam?.apply {
            val updateMineLearningRequest = UpdateMineLearningRequest(
                    this@UploadLearnProgressManager)

            with(updateMineLearningRequest) {
                addDataParam("agent_type", agentType)
                addDataParam("resource_id", resourceId)
                addDataParam("resource_type", resourceType)
                addDataParam("learn_progress", progress)
                // 原始学习进度，app 没有进度，所以传 0，从头开始
                addDataParam("org_learn_progress", orgLearnProgress)
                addDataParam("spend_time", spendTime)
                requestTag = resourceId

                sendRequest()
            }
        }
    }

    private fun getUploadHistory(resourceId: String,history : String): String{
        var historyString = ""
        val newData = mUploadLearnHistoryData[resourceId]
        val oldData = parseUploadHistory(history)
        newData?.forEach { (key, value) ->
            oldData[key] = value
        }
        val uploadParam = mUploadLearnData[resourceId]
        uploadParam?.run{
            var data = UploadHistory(resourceType,resourceId,oldData)
            historyString = Gson().toJson(data)
        }
        return historyString
    }

    fun parseUploadHistory(history : String): HashMap<String,History>{
        var map  = HashMap<String,History>()
        if (TextUtils.isEmpty(history) || "0" == history)    return map
        if (!TextUtils.isEmpty(history)){
            try {
                val data = Gson().fromJson(history,UploadHistory::class.java)
                data?.historyList?.run {
                    map = data!!.historyList!!
                }
            }catch (e : Exception){
                e.printStackTrace()
                return map
            }
        }
        return map
    }

    fun parseSingleItemLearnProgress(progress : String): SingleItemLearnProgress? {
        if (TextUtils.isEmpty(progress) || "0" == progress)    return null
        var data : SingleItemLearnProgress ?= null
        if (!TextUtils.isEmpty(progress)){
            try {
                data = Gson().fromJson(progress, SingleItemLearnProgress::class.java)
            }catch (e : Exception){
                e.printStackTrace()
                return data
            }
        }
        return data
    }

}

