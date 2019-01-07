package com.xiaoe.shop.wxb.business.download.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.xiaoe.common.app.Constants
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity
import com.xiaoe.common.entitys.CommonDownloadBean
import com.xiaoe.common.entitys.NewDownloadBean
import com.xiaoe.common.interfaces.OnItemClickWithCdbItemListener
import com.xiaoe.common.utils.Dp2Px2SpUtil
import com.xiaoe.common.utils.NetUtils
import com.xiaoe.network.NetworkCodes
import com.xiaoe.network.downloadUtil.DownloadManager
import com.xiaoe.network.requests.DownloadListRequest
import com.xiaoe.network.requests.IRequest
import com.xiaoe.shop.wxb.R
import com.xiaoe.shop.wxb.adapter.download.DownLoadListAdapter

import com.xiaoe.shop.wxb.base.BaseFragment
import com.xiaoe.shop.wxb.business.column.presenter.ColumnPresenter
import com.xiaoe.shop.wxb.widget.CustomDialog
import kotlinx.android.synthetic.main.download_list_bottom.*
import kotlinx.android.synthetic.main.download_list_content.*
import kotlinx.android.synthetic.main.download_list_header.*
import kotlinx.android.synthetic.main.download_list_second_content.*
import kotlinx.android.synthetic.main.fragment_download_list.*
import java.util.*

/**
 * 下载列表页面
 *
 * @author: zak
 * @date: 2019/1/2
 */
class DownloadListFragment : BaseFragment(), OnLoadMoreListener, OnItemClickWithCdbItemListener {

    private val TAG = "DownloadListFragment"

    private val downloadAll = "0"     // 拉取全部类型
    private val downloadMember = "5"  // 拉取会员
    private val downloadColumn = "6"  // 拉取专栏
    private val downloadTopic = "8"   // 拉取大专栏
    private val downloadAudio = 2   // 下载音频
    private val downloadVideo = 3   // 下载视频
    private val downloadGroup = 6   // 下载专栏
    private val downloadTypeSingle: IntArray = intArrayOf(downloadAudio, downloadVideo) // 下载的类型：2 - 音频，3 - 视频
    private val downloadTypeGroup: IntArray = intArrayOf(downloadGroup) // 下载的专栏
    private val requestSingleTag = "single" // 请求单品的 tag
    private val requestGroupTag = "group"   // 请求专栏的 tag

    private var mRootView: View? = null
    private lateinit var columnPresenter: ColumnPresenter
    private lateinit var mainLayoutManager: LinearLayoutManager
    private lateinit var secondLayoutManager: LinearLayoutManager
    private lateinit var resourceId: String
    private lateinit var resourceType: String
    private lateinit var downTitle: String
    private var pageIndex: Int = 1
    private var pageSize: Int = 5
    private var isMainContent: Boolean = true
    private var refreshData: Boolean = false
    private var showDataByDB: Boolean = false
    private lateinit var downloadSingleAdapter: DownLoadListAdapter
    private lateinit var downloadGroupAdapter: DownLoadListAdapter
    private lateinit var downloadSingleList: MutableList<CommonDownloadBean>
    private lateinit var downloadGroupList: MutableList<CommonDownloadBean>
    private var totalSelectedCount: Int = 0 // 全部选择的 item 数
    private var totalCount: Int = 0 // 某一个专栏下的全部课程数
    private var allSelectEnable: Boolean = true // 全选按钮可用
    private var lastSingleId: String = "" // 最后一条单品 id
    private var lastGroupId: String = "" // 最后一条专科 id
    private var canMainLoadMore: Boolean = true // 主页能否加载更多
    private var canSecondLoadMore: Boolean = true // 副页能够加载更多

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater?.inflate(R.layout.fragment_download_list, container, false)
        columnPresenter = ColumnPresenter(this)
        return mRootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
    }

    /**
     * 初始化数据
     */
    fun initData() {
        val intent = activity.intent
        val intentRt = intent.getStringExtra("resourceType")
        resourceId = intent.getStringExtra("resourceId")
        downTitle = intent.getStringExtra("down_title")
        if (intentRt.isNotEmpty()) {
            resourceType = when (intentRt) {
                downloadMember -> downloadMember
                downloadColumn -> downloadColumn
                downloadTopic -> downloadTopic
                else -> downloadAll
            }
        }
        // 大专栏和会员需要请求专栏列表和全部数据
        when (resourceType) {
            downloadTopic -> {
//                columnPresenter.requestColumnList(resourceId, downloadColumn, pageIndex, pageSize, true, resourceType)
                columnPresenter.requestDownloadList(resourceId, downloadMember.toInt(), pageSize, downloadTypeGroup, lastGroupId, requestGroupTag)
                columnPresenter.requestDownloadList(resourceId, downloadTopic.toInt(), pageSize, downloadTypeSingle, lastSingleId, requestSingleTag)
            }
            downloadMember -> {
//                columnPresenter.requestColumnList(resourceId, downloadAll, pageIndex, pageSize, true, resourceType)
                columnPresenter.requestDownloadList(resourceId, downloadMember.toInt(), pageSize, downloadTypeGroup, lastGroupId, requestGroupTag)
                columnPresenter.requestDownloadList(resourceId, downloadMember.toInt(), pageSize, downloadTypeSingle, lastSingleId, requestSingleTag)
            }
            downloadColumn -> {
//                columnPresenter.requestColumnList(resourceId, downloadAll, pageIndex, pageSize, true, resourceType)
                columnPresenter.requestDownloadList(resourceId, downloadColumn.toInt(), pageSize, downloadTypeSingle, lastSingleId, requestSingleTag)
            }
            else -> toastCustom("资源类型有误..")
        }
        downloadSingleList = mutableListOf()
        downloadSingleAdapter = DownLoadListAdapter(context)
        downloadGroupList = mutableListOf()
        downloadGroupAdapter = DownLoadListAdapter(context)
    }

    /**
     * 初始化 View
     */
    fun initView() {

        if (resourceType == downloadColumn) { // 资源类型为专栏，则不需要展开全部的按钮
            downloadTitleName.text = downTitle
            downloadTitleName.setPadding(0, 0, 0, 0)
            downloadTitleName.background = null
            downloadTitleName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else { // 大专栏、会员有全部的选择
            downloadTitleName.text = getString(R.string.all_text)
            downloadTitleName.setPadding(
                    Dp2Px2SpUtil.dp2px(context, 16f),
                    Dp2Px2SpUtil.dp2px(context, 8f),
                    Dp2Px2SpUtil.dp2px(context, 16f),
                    Dp2Px2SpUtil.dp2px(context, 8f))
            downloadTitleName.background = ContextCompat.getDrawable(context, R.drawable.download_title_bg)
            downloadTitleName.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.mipmap.title_detail), null)
        }

        // 先将总的数量置为空字符串
        downloadTitleCount.text = ""

        mainLayoutManager = LinearLayoutManager(context)
        secondLayoutManager = LinearLayoutManager(context)
        mainLayoutManager.isAutoMeasureEnabled = true
        secondLayoutManager.isAutoMeasureEnabled = true

        downloadContent.layoutManager = mainLayoutManager
        downloadSecondContent.layoutManager = secondLayoutManager

        initListener()
    }

    private fun initListener() {
        downloadTitleName.setOnClickListener {
            // 不是专栏的话可展开，进行展开操作
            if (resourceType != downloadColumn) {
                if (downloadSecondWrap.visibility == View.VISIBLE) {
                    downloadSecondWrap.visibility = View.GONE
                } else {
                    downloadSecondWrap.visibility = View.VISIBLE
                }
            }
        }

        downloadRefresh.setOnLoadMoreListener(this)
        downloadSecondRefresh.setOnLoadMoreListener(this)

        downloadColumnEmptyView.setOnClickListener {
            if (downloadSecondWrap.visibility == View.VISIBLE) {
                downloadSecondWrap.visibility = View.GONE
            }
        }

        allSelectBtn.setOnClickListener {
            if (Objects.requireNonNull<Drawable.ConstantState>(allSelectBtn.compoundDrawables[0].constantState) == Objects.requireNonNull<Drawable>(context.getDrawable(R.mipmap.download_tocheck)).constantState) {
                allSelectBtn.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.mipmap.download_checking), null, null, null)
                for (item in downloadSingleList) {
                    if (!item.isSelected) {
                        item.isSelected = true
                        totalSelectedCount++
                    }
                }
                downloadSingleAdapter.notifyDataSetChanged()
            } else {
                allSelectBtn.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.mipmap.download_tocheck), null, null, null)
                for (item in downloadSingleList) {
                    if (item.isSelected) {
                        item.isSelected = false
                        totalSelectedCount--
                    }
                }
                downloadSingleAdapter.notifyDataSetChanged()
            }
            if (totalSelectedCount == 0) {
                selectCountDesc.text = ""
            } else {
                selectCountDesc.text = String.format(context.getString(R.string.the_selected_count), totalSelectedCount)
            }
        }

        downloadSubmit.setOnClickListener {
            clickDownload()
        }
    }

    /**
     * 上拉加载回调
     */
    override fun onLoadMore(refreshLayout: RefreshLayout) {
        when (refreshLayout) {
            downloadRefresh -> {
                toastCustom("上拉主内容")
                downloadRefresh.finishLoadMore()
                columnPresenter.requestDownloadList(resourceId, resourceType.toInt(), pageSize, downloadTypeSingle, "", requestSingleTag)
            }
            downloadSecondRefresh -> {
                toastCustom("上拉副内容")
                downloadSecondRefresh.finishLoadMore()
            }
            else -> toastCustom("上拉未知错误")
        }
    }

    override fun onMainThreadResponse(iRequest: IRequest?, success: Boolean, entity: Any?) {
        super.onMainThreadResponse(iRequest, success, entity)
        val result = entity as JSONObject
        if (!success) {
            return
        }
        val code = result.getInteger("code")
        allSelectBtn.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.mipmap.download_tocheck), null, null, null)
        /*if (iRequest is ColumnListRequst) {
            if (code != NetworkCodes.CODE_SUCCEED) {
                // TODO: 列表加载失败
                Log.d(TAG, "onMainThreadResponse: 列表加载失败了..")
                return
            }
            val data = result["data"]
            val dataArr = data as JSONArray
            val resultArr = JSONArray()
            if (resourceType == iRequest.requestTag) {
                filterData(dataArr, resultArr)
                if (resourceType == downloadColumn) {
                    initSingle(resultArr)
                } else {
                    initGroup(resultArr)
                }
            }
        } else*/ if (iRequest is DownloadListRequest) {
            if (code != NetworkCodes.CODE_SUCCEED) {
                Log.d(TAG, "onMainThreadResponse: 下载列表加载失败了..")
                return
            }
            if (iRequest.requestTag == requestSingleTag) { // 请求单品的数据
                initDownloadSingleData(entity)
            } else if (iRequest.requestTag == requestGroupTag) { // 请求专栏的数据
                initDownloadGroupData(entity)
            }
        }
    }

    /**
     * 过滤无用数据
     * @param dataArr   源数据
     * @param resultArr 结果数据
     */
//    private fun filterData(dataArr: JSONArray, resultArr: JSONArray) {
//        for (item in dataArr) {
//            val itemObj = item as JSONObject
//            val tempResourceType = if (itemObj.getInteger("resource_type") == null) -1 else itemObj.getInteger("resource_type")
//            if (tempResourceType != -1 && tempResourceType != 1 && tempResourceType != 4 && tempResourceType != 20) { // 非音、视频单品
//                resultArr.add(item)
//            }
//        }
//    }

    /**
     * 初始化单品
     */
//    private fun initSingle(resultArr: JSONArray) {
//        for (result in resultArr) {
//            val commonDownloadBean = Gson().fromJson(result.toString(), CommonDownloadBean:: class.java)
//            downloadSingleList.add(commonDownloadBean)
//        }
//        downloadSingleAdapter = DownLoadListAdapter(context, DownLoadListAdapter.SINGLE_TYPE, downloadSingleList)
//        downloadSingleAdapter.setOnItemClickWithCdbItemListener(this)
//        downloadContent.adapter = downloadSingleAdapter
//    }

    /**
     * 初始化非单品
     */
//    private fun initGroup(resultArr: JSONArray) {
//        for (result in resultArr) {
//            val commonDownloadBean = Gson().fromJson(result.toString(), CommonDownloadBean:: class.java)
//            downloadSingleList.add(commonDownloadBean)
//        }
//        downloadGroupAdapter = DownLoadListAdapter(context, DownLoadListAdapter.GROUP_TYPE, downloadSingleList)
//        downloadSecondContent.adapter = downloadGroupAdapter
//    }

    /**
     * 初始化专栏数据
     *
     * @param entity 返回的数据对象
     */
    private fun initDownloadGroupData(entity: Any) {
        val result = Gson().fromJson(entity.toString(), NewDownloadBean::class.java)
        val firstDownloadBean = CommonDownloadBean()
        firstDownloadBean.title = getString(R.string.all_text)
        firstDownloadBean.periodicalCount = 0
        firstDownloadBean.resourceId = resourceId
        firstDownloadBean.resourceType = resourceType.toInt()
        firstDownloadBean.isSelected = true
        downloadGroupList.add(firstDownloadBean)
        for (item in result.data.list!!) {
            val commonDownloadBean = CommonDownloadBean()
            commonDownloadBean.title = item.title
            commonDownloadBean.periodicalCount = item.periodicalCount
            commonDownloadBean.resourceId = item.goodsId
            commonDownloadBean.resourceType = item.goodsType
            firstDownloadBean.isSelected = false
            downloadGroupList.add(commonDownloadBean)
        }
        lastGroupId = result.data.list!![result.data.list!!.size - 1].goodsId
        if (result.data.list!!.size < pageSize) {
            canSecondLoadMore = false
            downloadSecondRefresh.setEnableLoadMore(false)
        }
        downloadGroupAdapter.setInitType(DownLoadListAdapter.GROUP_TYPE)
        downloadGroupAdapter.setNewData(downloadGroupList)
        downloadGroupAdapter.setOnItemClickWithCdbItemListener(this)
        downloadSecondContent.adapter = downloadGroupAdapter
    }

    /**
     * 初始化下载数据
     *
     * @param entity 返回的数据对象
     */
    private fun initDownloadSingleData(entity: Any) {
        if (downloadSingleList.size > 0) {
            downloadSingleList.clear()
        }
        val result = Gson().fromJson(entity.toString(), NewDownloadBean::class.java)
        for (item in result.data.list!!) {
            val commonDownloadBean = CommonDownloadBean()
            commonDownloadBean.appId = Constants.getAppId()
            commonDownloadBean.resourceId = item.goodsId
            commonDownloadBean.resourceType = item.goodsType
            commonDownloadBean.title = item.title
            commonDownloadBean.isSelected = false
            commonDownloadBean.isEnable = true
            if (item.goodsType == downloadAudio) {
                commonDownloadBean.audioUrl = item.downloadUrl
                commonDownloadBean.audioLength = item.length
            } else if (item.goodsType == downloadVideo) {
                commonDownloadBean.videoUrl = item.downloadUrl
                commonDownloadBean.videoLength = item.length
            }
            downloadSingleList.add(commonDownloadBean)
        }
        lastSingleId = result.data.list!![result.data.list!!.size - 1].goodsId
        if (result.data.list!!.size < pageSize) {
            canMainLoadMore = false
            downloadRefresh.setEnableLoadMore(false)
        }
        downloadSingleAdapter.setInitType(DownLoadListAdapter.SINGLE_TYPE)
        downloadSingleAdapter.setNewData(downloadSingleList)
        downloadSingleAdapter.setOnItemClickWithCdbItemListener(this)
        downloadContent.adapter = downloadSingleAdapter
    }

    override fun onCommonDownloadBeanItemClick(view: View?, initType: Int, commonDownloadBean: CommonDownloadBean) {
        val textView = view?.findViewById(R.id.singleItemContent) as TextView
        if (initType == DownLoadListAdapter.SINGLE_TYPE) {
            if (commonDownloadBean.isEnable) {
                if (Objects.requireNonNull<Drawable.ConstantState>(textView.compoundDrawables[0].constantState) == Objects.requireNonNull<Drawable>(context.getDrawable(R.mipmap.download_tocheck)).constantState) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.mipmap.download_checking), null, null, null)
                    commonDownloadBean.isSelected = true
                    totalSelectedCount++
                } else {
                    textView.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.mipmap.download_tocheck), null, null, null)
                    commonDownloadBean.isSelected = false
                    totalSelectedCount--
                }
                if (totalSelectedCount == 0) {
                    selectCountDesc.text = ""
                } else {
                    selectCountDesc.text = String.format(context.getString(R.string.the_selected_count), totalSelectedCount)
                }
            }
        } else if (initType == DownLoadListAdapter.GROUP_TYPE) {
            // TODO: 专栏的 item 点击回调
        }
    }

    private fun clickDownload() {
        var download = false
        for (item in downloadSingleList) {
            if (item.isSelected) {
                download = true
            }
        }
        if (download) {
            if (NetUtils.NETWORK_TYPE_WIFI != NetUtils.getNetworkType(context)) {
                dialog.setMessageVisibility(View.GONE)
                dialog.titleView.gravity = Gravity.START
                dialog.titleView.setPadding(Dp2Px2SpUtil.dp2px(context, 22f), 0, Dp2Px2SpUtil.dp2px(context, 22f), 0)
                dialog.titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                dialog.setCancelable(false)
                dialog.setHideCancelButton(false)
                dialog.setTitle(getString(R.string.not_wifi_net_download_hint))
                dialog.setConfirmText(getString(R.string.confirm_title))
                dialog.setCancelText(getString(R.string.cancel_title))
                dialog.showDialog(CustomDialog.NOT_WIFI_NET_DOWNLOAD_TAG)
            } else {
                downloadResource()
            }
        } else {
            toastCustom(getString(R.string.please_select_download))
        }
    }

    private fun downloadResource() {
        //全选是否可选
        allSelectEnable = totalSelectedCount != downloadSingleList.size
        if (!allSelectEnable) {
            allSelectBtn.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.mipmap.download_alreadychecked), null, null, null)
            allSelectBtn.isEnabled = false
        } else {
            allSelectBtn.isEnabled = true
        }
        selectCountDesc.text = String.format(getString(R.string.the_selected_count), 0)
        allSelectBtn.isEnabled = allSelectEnable
        downloadSubmit.isEnabled = allSelectEnable
        for (item in downloadSingleList) {
            if (item.isSelected) {
                item.isEnable = false
                formatDownloadBean(item)
            }
        }
        toastCustom(getString(R.string.add_download_list))
        downloadSingleAdapter.notifyDataSetChanged()
    }

    private fun formatDownloadBean(item: CommonDownloadBean) {
        val downloadItem = ColumnSecondDirectoryEntity()
        downloadItem.app_id = item.appId
        downloadItem.resource_id = item.resourceId
        downloadItem.resource_type = item.resourceType
        downloadItem.title = item.title
        downloadItem.img_url = item.imgUrl
        downloadItem.img_url_compress = item.imgUrlCompress
        downloadItem.start_at = item.startAt
        downloadItem.try_m3u8_url = item.tryM3U8Url
        downloadItem.try_audio_url = item.tryAudioUrl
        downloadItem.audio_length = item.audioLength
        downloadItem.video_length = item.videoLength
        downloadItem.audio_url = item.audioUrl
        downloadItem.audio_compress_url = item.audioCompressUrl
        downloadItem.m3u8_url = item.m3U8Url
        downloadItem.video_url = item.videoUrl
        downloadItem.isTry = 0
        downloadItem.isHasBuy = 1
        downloadItem.columnId = resourceId
        downloadItem.columnTitle = downTitle
        downloadItem.bigColumnId = ""

        DownloadManager.getInstance().addDownload(null, null, downloadItem)
    }
}
