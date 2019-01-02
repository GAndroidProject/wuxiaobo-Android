package com.xiaoe.shop.wxb.business.mine_learning.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facebook.drawee.view.SimpleDraweeView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.xiaoe.common.entitys.AllDataItem
import com.xiaoe.common.entitys.BoughtRecord
import com.xiaoe.common.entitys.ItemType.Companion.type_audio
import com.xiaoe.common.entitys.ItemType.Companion.type_default
import com.xiaoe.common.utils.Dp2Px2SpUtil
import com.xiaoe.network.requests.BoughtRecordRequest
import com.xiaoe.network.requests.IRequest
import com.xiaoe.shop.wxb.R
import com.xiaoe.shop.wxb.base.BaseFragment
import com.xiaoe.shop.wxb.business.mine_learning.presenter.MyBoughtPresenter
import com.xiaoe.shop.wxb.business.search.presenter.SpacesItemDecoration
import com.xiaoe.shop.wxb.utils.SetImageUriUtil
import com.xiaoe.shop.wxb.widget.StatusPagerView
import kotlinx.android.synthetic.main.fragment_recently_learning.*
import java.lang.Exception

/**
 * Date: 2018/12/26 10:14
 * Author: hans yang
 * Description:
 */
class BoughtRecordFragment : BaseFragment(), OnRefreshListener, OnLoadMoreListener {
    private val mTag = "RecentlyLearning"
    var pageIndex = 1
    val pageSize = 10

    private val mAdapter : MyAdapter by lazy {
        MyAdapter(activity)
    }

    private val mStatusPagerView : StatusPagerView by lazy {
        StatusPagerView(activity)
    }

    private val mBoughtPresenter : MyBoughtPresenter by lazy {
        MyBoughtPresenter(this)
    }

    private val mSpacesItemDecoration: SpacesItemDecoration by lazy {
        SpacesItemDecoration()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_recently_learning,container,false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    // 初始化页面数据
    private fun initView() {
        mSpacesItemDecoration.setMargin(0,Dp2Px2SpUtil.dp2px(activity,16f),
                0,0)
        var linearLayoutManager= LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,
                false)

        with(recentlyLearningList){
            addItemDecoration(mSpacesItemDecoration)
            layoutManager = linearLayoutManager
            adapter = mAdapter
        }
        mStatusPagerView.setLoadingState(View.VISIBLE)
        mAdapter.emptyView = mStatusPagerView

        mBoughtPresenter.requestRecordData(pageIndex, pageSize)
    }

    private fun initListener() {
        with(learningRefresh){
            setOnRefreshListener(this@BoughtRecordFragment)
            setOnLoadMoreListener(this@BoughtRecordFragment)
            setEnableLoadMoreWhenContentNotFull(false)
        }
    }

    override fun onMainThreadResponse(iRequest: IRequest?, success: Boolean, entity: Any?) {
        super.onMainThreadResponse(iRequest, success, entity)
        if (isFragmentDestroy)   return

        handleData(success, entity, iRequest)
    }

    private fun handleData(success: Boolean, entity: Any?, iRequest: IRequest?) {
        if (success && entity != null) {
            if (iRequest is BoughtRecordRequest) {
                try {
                    val result = JSON.parseObject(entity!!.toString(), BoughtRecord::class.java)
                    if (result?.data == null || result.data!!.allData == null)   result

                    mStatusPagerView.setPagerState(StatusPagerView.FAIL,
                            getString(R.string.no_learning_content),R.mipmap.collection_none)
                    learningRefresh.finishRefresh()
                    when(pageIndex){
                        1 ->{
                            mAdapter.setNewData(result.data?.allData)
                            learningRefresh.setEnableLoadMore(result.data?.allData?.size!! >= pageSize)
                        }else ->{
                            mAdapter.addData(result.data?.allData!!)
                            if (result.data?.allData?.size!! >= pageSize) {
                                learningRefresh.finishLoadMore()
                            }else{
                                learningRefresh.finishLoadMoreWithNoMoreData()
                            }
                        }
                    }
                }catch (e : Exception){
                    e.printStackTrace()
                    doRequestFail()
                }
            }
        }else{
            doRequestFail()
        }
    }

    private fun doRequestFail() {
        learningRefresh.finishRefresh()
        learningRefresh.finishLoadMore()
        mStatusPagerView.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT,
                R.mipmap.error_page)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageIndex = 1
        mBoughtPresenter.requestRecordData(pageIndex, pageSize)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mBoughtPresenter.requestRecordData(++pageIndex, pageSize)
    }

    class MyAdapter(context: Context): BaseMultiItemQuickAdapter<AllDataItem,
            BaseViewHolder>(null){

        init {
            addItemType(type_default,R.layout.recently_learning_list_item)
            addItemType(type_audio,R.layout.audio_learning_list_item)
        }

        override fun convert(helper: BaseViewHolder?, item: AllDataItem?) {
            helper?.apply {
                item?.apply {
                    when(itemViewType){
                        type_default ->{
                            setGone(R.id.learningProgress,true)
                            getView<SimpleDraweeView>(R.id.itemIcon).setImageURI(item!!.imgUrl)
                            setText(R.id.itemTitle,purchaseName)
                            val desc = getView<TextView>(R.id.itemDesc)
                            when(resourceType){
                                5 -> desc.text = "有效期至${expireAt.split(" ")[0]}"
                                8 -> desc.text = "已更新${resourceType}期"
                            }
                        }
                        type_audio ->{
                            SetImageUriUtil.setImgURI(helper!!.getView(R.id.itemIcoBbg),
                                    "res:///" + R.mipmap.audio_list_bg, Dp2Px2SpUtil.dp2px(mContext, 160f),
                                    Dp2Px2SpUtil.dp2px(mContext, 120f))
                            val url = if (TextUtils.isEmpty(imgUrl)) "res:///" + R.mipmap.detail_disk
                                      else imgUrl
                            val imageWidthDp = 84f
                            val itemIcon = helper!!.getView<SimpleDraweeView>(R.id.itemIcon)
                            if (url.contains("res:///") || !SetImageUriUtil.isGif(url)) {// 本地图片
                                SetImageUriUtil.setImgURI(itemIcon, url, Dp2Px2SpUtil.dp2px(mContext, imageWidthDp),
                                        Dp2Px2SpUtil.dp2px(mContext, imageWidthDp))
                            } else {// 网络图片
                                SetImageUriUtil.setRoundAsCircle(itemIcon, Uri.parse(url))
                            }
                            setText(R.id.itemTitle,purchaseName)
                        }
                    }
                }
            }
        }

    }

//    /**
//     * 资源类型转换 int - str
//     * @param resourceType 资源类型
//     * @return 资源类型的字符串形式
//     */
//    private fun convertInt2Str(resourceType: Int): String? {
//        return when (resourceType) {
//            1 // 图文
//            -> DecorateEntityType.IMAGE_TEXT
//            2 // 音频
//            -> DecorateEntityType.AUDIO
//            3 // 视频
//            -> DecorateEntityType.VIDEO
//            6 // 专栏
//            -> DecorateEntityType.COLUMN
//            8 // 大专栏
//            -> DecorateEntityType.TOPIC
//            5 // 会员
//            -> DecorateEntityType.MEMBER
//            else -> null
//        }
//    }
}