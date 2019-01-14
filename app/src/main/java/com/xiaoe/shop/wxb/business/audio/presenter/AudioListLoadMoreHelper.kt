package com.xiaoe.shop.wxb.business.audio.presenter

import android.text.TextUtils
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.xiaoe.common.app.Global
import com.xiaoe.common.entitys.AudioListLoadMoreEvent
import com.xiaoe.common.entitys.AudioPlayEntity
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity
import com.xiaoe.network.network_interface.INetworkResponse
import com.xiaoe.network.requests.DownloadListRequest
import com.xiaoe.network.requests.IRequest
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer.lastId
import com.xiaoe.shop.wxb.business.audio.ui.AudioPlayListDialog.DEFAULT_LAST_ID
import com.xiaoe.shop.wxb.business.column.presenter.ColumnPresenter
import com.xiaoe.shop.wxb.utils.LogUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Exception
import java.util.ArrayList

/**
 * Date: 2019/1/12 16:51
 * Author: hans yang
 * Description:
 */
object AudioListLoadMoreHelper : INetworkResponse {
    private const val mTag = "mAudioListLoadMore"

    var index = 0
    var mColumnId = ""
    var mGoodsType = 6
    var mPageSize = 10
//    var isAtAudioActivity = false
    var mDownloadType : IntArray ?= null

    var loadMordSuccessListener: ((JSONArray?)->Unit)? = null

    private val mColumnPresenter: ColumnPresenter by lazy {
        ColumnPresenter(this)
    }

    fun registerEventBus() {
//        val nullString : String ?= null
//        LogUtils.d("$mTag---addPlayListData----nullString = ${nullString.isNullOrEmpty()}")
//        val emptyString = ""
//        LogUtils.d("$mTag---addPlayListData----emptyString = ${emptyString.isNullOrEmpty()}")
//        val blankString = " "
//        LogUtils.d("$mTag---addPlayListData----blankString = ${blankString.isNullOrEmpty()}" +
//                "---blankString.isBlank = ${blankString.isNotEmpty()}")
//
//        LogUtils.d("$mTag---addPlayListData----lastId = ${lastId.isNullOrEmpty()}")
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    fun unregisterEventBus() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onEventMainThread(event: AudioListLoadMoreEvent){
        event?.apply {
            if (isLoadMore){
                requestAudioListLoadMoreData()
            }
        }
    }

    fun initConfigData(columnId: String,goodsType: Int,pageSize: Int,downloadType: IntArray){
        mColumnId = columnId
        mGoodsType = goodsType
        mPageSize = pageSize
        mDownloadType = downloadType
    }

    private fun requestAudioListLoadMoreData(){
        if (!TextUtils.isEmpty(mColumnId) && !TextUtils.isEmpty(lastId) && lastId != DEFAULT_LAST_ID) {
            LogUtils.d("$mTag--requestAudioListLoadMoreData--mColumnId = $mColumnId---lastId = $lastId")
            mColumnPresenter.requestDownloadList(mColumnId, mGoodsType, mPageSize, mDownloadType,
                    lastId, mColumnId)
        }else    AudioMediaPlayer.isHasMoreData = false
    }

    override fun onMainThreadResponse(iRequest: IRequest?, success: Boolean, entity: Any?) {
        LogUtils.d("$mTag---onMainThreadResponse")
        try {
            if (success && entity != null && iRequest != null && iRequest is DownloadListRequest){
                when (iRequest.requestTag) {
                    mColumnId-> {
                        val jsonObject = entity as JSONObject
                        val dataObject = jsonObject.getJSONObject("data")
                        var list: JSONArray? = null
                        if (dataObject != null) {
                            list = dataObject!!.getJSONArray("list")
                        }
                        LogUtils.d("$mTag---onMainThreadResponse-- loadMordSuccessListener = " +
                                "$loadMordSuccessListener")
                        if (loadMordSuccessListener != null){
                            loadMordSuccessListener!!.invoke(list)
                        }else{
                            val audio = AudioMediaPlayer.getAudio()
                            setAudioPlayList(mColumnPresenter.formatSingleResourceEntity2(list,
                                    if (audio == null) "" else audio.title, if (audio == null) ""
                            else audio.columnId, "", audio?.hasBuy ?: 0),
                                    audio?.hasBuy ?: 0)
                        }
                        AudioMediaPlayer.playNext()
                    }
                }
            }else   AudioMediaPlayer.isHasMoreData = false
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onResponse(iRequest: IRequest?, success: Boolean, entity: Any?) {
        LogUtils.d("$mTag---onResponse")
        Global.g().runOnUiThread {
            onMainThreadResponse(iRequest,success,entity)
        }
    }

    private fun setAudioPlayList(list: List<ColumnSecondDirectoryEntity>, isHasBuy: Int) {
        val audioPlayEntities = ArrayList<AudioPlayEntity>()
//        var page = 0
//        if (mAudioPlayListNewAdapter != null && mAudioPlayListNewAdapter.getData().size > 0)
//            page = mAudioPlayListNewAdapter.getData().size / PAGE_SIZE
//        var index = 10 * (page - 1)
        for (entity in list) {
            if (entity.resource_type != 2) {
                continue
            }
            val playEntity = AudioPlayEntity().apply {
                appId = entity.app_id
                resourceId = entity.resource_id
                index = AudioListLoadMoreHelper.index
                currentPlayState = 0
                title = entity.title
                state = 0
                isPlay = false
                playUrl = entity.audio_url
                code = -1
                hasBuy = isHasBuy
                columnId = entity.columnId
                bigColumnId = entity.bigColumnId
                totalDuration = entity.audio_length
                productsTitle = entity.columnTitle
                isTry = entity.isTry
            }
            index++
            audioPlayEntities.add(playEntity)
        }
        addPlayListData(audioPlayEntities)
    }

    private fun addPlayListData(list: ArrayList<AudioPlayEntity>) {
        if (!TextUtils.isEmpty(lastId)){
            AudioMediaPlayer.isHasMoreData = list.size >= mPageSize
            if (list.size > 0)      lastId = list[list.size - 1].resourceId
            list.filter {
                it !in AudioPlayUtil.getInstance().audioList
            }.forEach {
                AudioPlayUtil.getInstance().audioList.add(it)
            }
            AudioPlayUtil.getInstance().setAudioList2(AudioPlayUtil.getInstance().audioList)
            index = AudioPlayUtil.getInstance().audioListNew.size
        }
    }

}