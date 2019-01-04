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
import android.widget.Toast
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.Gson
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.xiaoe.common.app.Constants
import com.xiaoe.common.entitys.BoughtRecord
import com.xiaoe.common.entitys.DataItem
import com.xiaoe.common.entitys.ItemType.ITEM_TYPE_AUDIO
import com.xiaoe.common.entitys.ItemType.ITEM_TYPE_DEFAULT
import com.xiaoe.common.utils.Dp2Px2SpUtil
import com.xiaoe.network.requests.EarningRequest
import com.xiaoe.network.requests.IRequest
import com.xiaoe.shop.wxb.R
import com.xiaoe.shop.wxb.base.BaseFragment
import com.xiaoe.shop.wxb.business.earning.presenter.EarningPresenter
import com.xiaoe.shop.wxb.business.search.presenter.SpacesItemDecoration
import com.xiaoe.shop.wxb.utils.LogUtils
import com.xiaoe.shop.wxb.utils.SetImageUriUtil
import com.xiaoe.shop.wxb.utils.jumpKnowledgeDetail
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

    private val mEarningPresenter : EarningPresenter by lazy {
        EarningPresenter(this)
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

        mEarningPresenter.requestLaundryList(Constants.BOUGHT_ASSET_TYPE, Constants.NEED_FLOW,
                Constants.EARNING_FLOW_TYPE,pageIndex, pageSize)
    }

    private fun initListener() {
        with(learningRefresh){
            setOnRefreshListener(this@BoughtRecordFragment)
            setOnLoadMoreListener(this@BoughtRecordFragment)
            setEnableLoadMoreWhenContentNotFull(false)
        }
        mAdapter.setOnItemClickListener { adapter, _, position ->
            val data = adapter.getItem(position) as DataItem
            jumpKnowledgeDetail(activity,data.resourceType,data.resourceId,data.imgUrl)
        }
    }

    override fun onMainThreadResponse(iRequest: IRequest?, success: Boolean, entity: Any?) {
        super.onMainThreadResponse(iRequest, success, entity)
        if (isFragmentDestroy)   return

        handleData(success, entity, iRequest)
    }

    private fun handleData(success: Boolean, entity: Any?, iRequest: IRequest?) {
        if (success && entity != null) {
            if (iRequest is EarningRequest) {
                try {
                    mStatusPagerView.setPagerState(StatusPagerView.FAIL,
                            getString(R.string.no_learning_content),R.mipmap.collection_none)
                    val result = Gson().fromJson<BoughtRecord>(entity!!.toString(), BoughtRecord::class.java)
                    if (result?.data == null)   return
                    result.data!!
                            .filter { 4 == it.resourceType }
                            .forEach {
                                result.data!!.remove(it)
                                LogUtils.d("filter 去掉直播的数据")
                            }

                    learningRefresh.finishRefresh()
                    when{
                        0 == result.code && 1 == pageIndex -> {
                            mAdapter.setNewData(result.data)
                            learningRefresh.setEnableLoadMore(result.data?.size!! >= pageSize) }
                        0 == result.code ->{
                            mAdapter.addData(result.data!!)
                            if (result.data!!.size!! >= pageSize) {
                                learningRefresh.finishLoadMore()
                            }else{
                                learningRefresh.finishLoadMoreWithNoMoreData()
                            } }
                        4001 == result.code|| 4002 == result.code ->{
                            Toast.makeText(activity,result!!.msg,Toast.LENGTH_SHORT).show()
                        }
                    }

                    //与上面when语句等效
//                    when(result.code){
//                        0 ->{
//                            when(pageIndex){
//                                1 ->{
//                                    mAdapter.setNewData(result.data)
//                                    learningRefresh.setEnableLoadMore(result.data?.size!! >= pageSize)
//                                }else ->{
//                                mAdapter.addData(result.data!!)
//                                if (result.data!!.size!! >= pageSize) {
//                                    learningRefresh.finishLoadMore()
//                                }else{
//                                    learningRefresh.finishLoadMoreWithNoMoreData()
//                                }
//                            }
//                            }
//                        }
//                        4001,4002 ->{
//                            Toast.makeText(activity,result!!.msg,Toast.LENGTH_SHORT).show()
//                        }
//                    }
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
        mEarningPresenter.requestLaundryList(Constants.BOUGHT_ASSET_TYPE, Constants.NEED_FLOW,
                Constants.EARNING_FLOW_TYPE,pageIndex, pageSize)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mEarningPresenter.requestLaundryList(Constants.BOUGHT_ASSET_TYPE, Constants.NEED_FLOW,
                Constants.EARNING_FLOW_TYPE,++pageIndex, pageSize)
    }

    class MyAdapter(val context: Context): BaseMultiItemQuickAdapter<DataItem,
            BaseViewHolder>(null){

        init {
            addItemType(ITEM_TYPE_DEFAULT,R.layout.recently_learning_list_item)
            addItemType(ITEM_TYPE_AUDIO,R.layout.audio_learning_list_item)
        }

        override fun convert(helper: BaseViewHolder?, item: DataItem?) {
            helper?.apply {
                item?.apply {
                    when(itemViewType){
                        ITEM_TYPE_DEFAULT ->{
                            setGone(R.id.learningProgress,false)
                            getView<SimpleDraweeView>(R.id.itemIcon).setImageURI(item!!.imgUrl)
                            setText(R.id.itemTitle,purchaseName)
                            val desc = getView<TextView>(R.id.itemDesc)

                            when(purchasedGoodsType){
                                3 -> desc.text = String.format(context.getString(R.string.valid_until2),
                                        expireAt?.split(" ")[0])
                                1,2 -> desc.text = String.format(context.getString(R.string.stages_text_str),
                                        purchasedGoodsDisplay)
                            }
                        }
                        ITEM_TYPE_AUDIO ->{
                            SetImageUriUtil.setImgURI(helper!!.getView(R.id.itemIcoBbg),
                                    "res:///" + R.mipmap.audio_list_bg, Dp2Px2SpUtil.dp2px(mContext,
                                    160f),Dp2Px2SpUtil.dp2px(mContext, 120f))
                            val url = if (TextUtils.isEmpty(imgUrl)) "res:///" + R.mipmap.detail_disk
                                      else imgUrl
                            val imageWidthDp = 84f
                            val itemIcon = helper!!.getView<SimpleDraweeView>(R.id.itemIcon)
                            if (url.contains("res:///") || !SetImageUriUtil.isGif(url)) {// 本地图片
                                SetImageUriUtil.setImgURI(itemIcon, url, Dp2Px2SpUtil.dp2px(mContext,
                                        imageWidthDp),Dp2Px2SpUtil.dp2px(mContext, imageWidthDp))
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
}