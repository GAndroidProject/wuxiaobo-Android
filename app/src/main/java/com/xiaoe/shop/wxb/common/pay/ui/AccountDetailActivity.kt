package com.xiaoe.shop.wxb.common.pay.ui

import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.xiaoe.common.entitys.AccountEntity
import com.xiaoe.common.entitys.AeDataItem
import com.xiaoe.network.requests.AccountDetailRequest
import com.xiaoe.network.requests.IRequest
import com.xiaoe.shop.wxb.R
import com.xiaoe.shop.wxb.base.XiaoeActivity
import com.xiaoe.shop.wxb.business.earning.presenter.EarningPresenter
import com.xiaoe.shop.wxb.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_account_detail.*
import kotlinx.android.synthetic.main.common_title_view.*

class AccountDetailActivity : XiaoeActivity() {

    private lateinit var earningPresenter: EarningPresenter
    private var pageIndex: Int = 1
    private var pageSize: Int = 2
    private var totalCount: Int = 0
    private var lastPage: Int = 0
    private val aeList: MutableList<AeDataItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        setContentView(R.layout.activity_account_detail)

        recordWrap.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0)
        recordLoading.visibility = View.VISIBLE
        recordLoading.setLoadingState(View.VISIBLE)

        earningPresenter = EarningPresenter(this)
        earningPresenter.requestAccountDetail(pageIndex, pageSize)

        title_content.text = getString(R.string.bo_bi_content_desc)
        title_end.visibility = View.GONE
        title_back.setOnClickListener{ onBackPressed() }
    }

    override fun onMainThreadResponse(iRequest: IRequest?, success: Boolean, entity: Any?) {
        super.onMainThreadResponse(iRequest, success, entity)
        if (iRequest is AccountDetailRequest) {
            try {
                val result = Gson().fromJson(entity.toString(), AccountEntity::class.java)
                totalCount = result.data.total
                lastPage = result.data.lastPage
                if (result.data.data!!.isEmpty()) {
                    // TODO: 没有账户明细情况
                } else {
                    for (item in result.data.data!!) {
                        aeList.add(item)
                    }
                    if (aeList.size > 0) {

                    } else {
                        // 没有账户明细情况
                    }
                }
            } catch (e: Exception) {

            }
        }
    }
}
