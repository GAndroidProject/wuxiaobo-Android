package com.xiaoe.shop.wxb.business.mine_learning.ui

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.xiaoe.common.entitys.ComponentInfo
import com.xiaoe.common.entitys.ItemType.ITEM_TYPE_AUDIO
import com.xiaoe.common.entitys.ItemType.ITEM_TYPE_DEFAULT
import com.xiaoe.common.entitys.RecentlyLearning
import com.xiaoe.common.utils.Dp2Px2SpUtil
import com.xiaoe.network.requests.IRequest
import com.xiaoe.shop.wxb.R
import com.xiaoe.shop.wxb.base.BaseFragment
import com.xiaoe.shop.wxb.business.mine_learning.presenter.MineLearningPresenter
import com.xiaoe.shop.wxb.business.search.presenter.SpacesItemDecoration
import com.xiaoe.shop.wxb.utils.SetImageUriUtil
import kotlinx.android.synthetic.main.fragment_recently_learning.*
import java.util.ArrayList

/**
 * Date: 2018/12/26 10:14
 * Author: hans yang
 * Description:
 */
class RecentlyLearningFragment : BaseFragment(), OnRefreshListener, OnLoadMoreListener {
    private val mTag = "RecentlyLearning"
    // 我正在学请求
    private val mineLearningPresenter : MineLearningPresenter by lazy {
        MineLearningPresenter(this)
    }

    private lateinit var pageList: MutableList<ComponentInfo>
    var pageIndex = 1
    val pageSize = 10
    var hasDecorate: Boolean = false
    var isRefresh: Boolean = false
    var isLoadMore: Boolean = false

    private val mAdapter : MyAdapter by lazy {
        MyAdapter(activity!!)
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

        mSpacesItemDecoration.setMargin(0,Dp2Px2SpUtil.dp2px(activity,16f),
                0,0)
        var linearLayoutManager= LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,
                false)
        with(recentlyLearningList){
            addItemDecoration(mSpacesItemDecoration)
            layoutManager = linearLayoutManager
            adapter = mAdapter
        }
        val list = listOf<RecentlyLearning>(
                RecentlyLearning("11"),
                RecentlyLearning("1"),
                RecentlyLearning("111"),
                RecentlyLearning("111"),
                RecentlyLearning("111"),
                RecentlyLearning("11"),
                RecentlyLearning("111"),
                RecentlyLearning("111111"))
        mAdapter.setNewData(list)
        learningLoading.visibility = GONE
//        initPage()
        initListener()
    }

    // 初始化页面数据
    private fun initPage() {
        pageList = ArrayList<ComponentInfo>()
        with(learningLoading){
            visibility = View.VISIBLE
            setLoadingState(View.VISIBLE)
        }

        mineLearningPresenter.requestLearningData(1, 10)
    }

    private fun initListener() {
        learningRefresh.setOnRefreshListener(this)
        learningRefresh.setOnLoadMoreListener(this)
    }

    override fun onMainThreadResponse(iRequest: IRequest?, success: Boolean, entity: Any?) {
        super.onMainThreadResponse(iRequest, success, entity)
        if (success){

        }
    }

    class MyAdapter(context: Context): BaseMultiItemQuickAdapter<RecentlyLearning,
            BaseViewHolder>(null){

        init {
            addItemType(ITEM_TYPE_DEFAULT,R.layout.recently_learning_list_item)
            addItemType(ITEM_TYPE_AUDIO,R.layout.audio_learning_list_item)
        }

        override fun convert(helper: BaseViewHolder?, item: RecentlyLearning?) {
            helper?.apply {
                item?.apply {
                    when(itemViewType){
                        ITEM_TYPE_DEFAULT ->{
                            setText(R.id.itemTitle,name)
                            setText(R.id.itemContent,name)
                            setText(R.id.itemDesc,name) }
                        ITEM_TYPE_AUDIO ->{
                            SetImageUriUtil.setImgURI(helper!!.getView(R.id.itemIcoBbg),
                                    "res:///" + R.mipmap.audio_list_bg, Dp2Px2SpUtil.dp2px(mContext, 160f),
                                    Dp2Px2SpUtil.dp2px(mContext, 120f))
                            setText(R.id.itemTitle,name)
                            setText(R.id.itemContent,name)
                            setText(R.id.itemDesc,name)}
                    }

                }
            }
        }

    }

//    override fun onMainThreadResponse(iRequest: IRequest, success: Boolean, entity: Any) {
//        super.onMainThreadResponse(iRequest, success, entity)
//        val result = entity as JSONObject
//        if (success) {
//            if (iRequest is CollectionListRequest) {
//                val code = result.getInteger("code")!!
//                val data = result["data"] as JSONObject
//                if (code == NetworkCodes.CODE_SUCCEED) {
//                    initPageData(data)
//                } else if (code == NetworkCodes.CODE_COLLECT_LIST_FAILED) {
//                    Log.d(mTag, "onMainThreadResponse: 获取收藏列表失败")
//                    learningLoading.setPagerState(StatusPagerView.FAIL,
//                            getString(R.string.no_collection_content),
//                            R.mipmap.collection_none)
//                }
//            } else if (iRequest is MineLearningRequest) {
//                val code = result.getInteger("code")!!
//                val data: JSONObject
//                try {
//                    data = result["data"] as JSONObject
//                } catch (e: Exception) {
//                    if (pageList.size > 0) {
//                        learningRefresh.finishLoadMoreWithNoMoreData()
//                        learningRefresh.setEnableLoadMore(false)
//                    } else {
//                        learningLoading.setPagerState(StatusPagerView.FAIL,
//                                getString(R.string.no_learning_content),
//                                R.mipmap.collection_none)
//                    }
//                    e.printStackTrace()
//                    return
//                }
//
//                if (code == NetworkCodes.CODE_SUCCEED) {
//                    initPageData(data)
//                } else if (code == NetworkCodes.CODE_OBTAIN_LEARNING_FAIL) {
//                    learningLoading.setPagerState(StatusPagerView.FAIL,
//                            getString(R.string.no_learning_content),
//                            R.mipmap.collection_none)
//                    Log.d(mTag, "onMainThreadResponse: 获取学习记录失败...")
//                }
//            }
//        } else {
//            Log.d(mTag, "onMainThreadResponse: 请求失败")
//            with(learningRefresh){
//                finishRefresh()
//                setNoMoreData(true)
//                setEnableLoadMore(false)
//            }
//
//            isRefresh = false
//            if (result != null) {
//                val code = result.getInteger("code")!!
//                if (NetworkStateResult.ERROR_NETWORK == code) {
//                    if (pageList.size <= 0) {
//                        learningLoading.setPagerState(StatusPagerView.FAIL,
//                                StatusPagerView.FAIL_CONTENT,
//                                R.mipmap.error_page)
//                    } else {
//                        learningLoading.setLoadingFinish()
//                        activity?.showShortToast(getString(R.string.network_error_text))
//                    }
//                }
//            }
//        }
//    }
//
//    // 初始化收藏页和我正在学数据
//    private fun initPageData(data: JSONObject) {
//        val goodsList = data["goods_list"] as JSONArray
//        val itemList = ArrayList<KnowledgeCommodityItem>()
//        if (goodsList == null) {
//            // 收藏列表为空，显示为空的页面
//            learningRefresh.finishRefresh()
//            if (pageList.size == 0) {
//                learningLoading.setPagerState(StatusPagerView.FAIL,
//                        getString(R.string.no_collection_content),
//                        R.mipmap.collection_none)
//            } else {
//                learningRefresh.finishLoadMoreWithNoMoreData()
//                learningRefresh.setEnableLoadMore(false)
//            }
//            return
//        }
//        val knowledgeList = ComponentInfo()
//        knowledgeList.title = ""
//        knowledgeList.type = DecorateEntityType.KNOWLEDGE_COMMODITY_STR
//        knowledgeList.subType = DecorateEntityType.KNOWLEDGE_LIST_STR
//        // 隐藏 title
//        knowledgeList.isHideTitle = true
//        // 因为这个接口拿到的 resourceType 是 int 类型，转成字符串存起来
//        for (goodItem in goodsList) {
//            val item = KnowledgeCommodityItem()
//            val infoItem = goodItem as JSONObject
//            val learningInfo = infoItem["info"] as JSONObject
//            val learningId = learningInfo.getString("goods_id")
//            if ("" == learningId) {
//                continue
//            }
//            val learningType = convertInt2Str(learningInfo.getInteger("goods_type")!!)
//            val learningTitle = learningInfo.getString("title")
//            val learningImg = learningInfo.getString("img_url_compressed_larger")
//            val learningOrg = learningInfo.getString("org_summary")
//            val updateCount = if (learningInfo.getInteger("periodical_count") == null) 0
//            else learningInfo.getInteger("periodical_count")
//            var updateStr = ""
//            if (updateCount > 0) {
//                updateStr = String.format(getString(R.string.updated_to_issue), updateCount)
//            }
//
//            // 我正在学的数据
//            with(item){
//                itemImg = learningImg
//                itemTitle = learningTitle
//                resourceId = learningId
//                srcType = learningType
//                itemTitleColumn = learningOrg
//                itemPrice = updateStr
//                isCollectionList = false
//            }
//            itemList.add(item)
//        }
//        knowledgeList.knowledgeCommodityItemList = itemList
//        if (pageList.size > 0 && isRefresh) { // 有数据并且在刷新
//            isRefresh = false
//            pageList.clear()
////            recentlyLearningList.removeItemDecoration(spacesItemDecoration)
//        }
//        pageList.add(knowledgeList)
//        if (!hasDecorate) {
//            var layoutManager = LinearLayoutManager(activity)
//            layoutManager.orientation = LinearLayoutManager.VERTICAL
//            recentlyLearningList.layoutManager = layoutManager
//            decorateRecyclerAdapter = DecorateRecyclerAdapter(activity, pageList)
//            recentlyLearningList.adapter = decorateRecyclerAdapter
//            spacesItemDecoration = SpacesItemDecoration()
//            spacesItemDecoration.setMargin(
//                    Dp2Px2SpUtil.dp2px(activity, 0f),
//                    Dp2Px2SpUtil.dp2px(activity, -16f),
//                    Dp2Px2SpUtil.dp2px(activity, 0f),
//                    Dp2Px2SpUtil.dp2px(activity, 0f))
//            recentlyLearningList.addItemDecoration(spacesItemDecoration)
//            hasDecorate = true
//        } else {
//            if (pageList.size == 2) { // 第二个开始添加 decoration，只添加一次
////                recentlyLearningList.addItemDecoration(spacesItemDecoration)
//            }
//            if (pageList.size >= 2) { // 刷新新增数据的那一个 item
//                decorateRecyclerAdapter.notifyItemChanged(pageList.size - 1)
//            } else { // 第一个直接通知更新
//                decorateRecyclerAdapter.notifyDataSetChanged()
//            }
//            recentlyLearningList.isFocusableInTouchMode = false
//        }
//        learningLoading.setLoadingFinish()
//        learningRefresh.finishRefresh()
//        learningRefresh.finishLoadMore()
//    }
//
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

    override fun onRefresh(refreshLayout: RefreshLayout) {
        if (!isRefresh) {
            learningRefresh.setEnableLoadMore(true)
            if (pageIndex != 1) {
                pageIndex = 1
            }
            mineLearningPresenter.requestLearningData(pageIndex, pageSize)
            isRefresh = true
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (!isLoadMore) {
            mineLearningPresenter.requestLearningData(++pageIndex, pageSize)
        }
    }
}