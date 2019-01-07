package com.xiaoe.shop.wxb.common.pay.ui

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.xiaoe.common.entitys.AmountItemEntity
import com.xiaoe.common.entitys.AmountDataItem
import com.xiaoe.common.entitys.PrepaidBuyEntity
import com.xiaoe.common.entitys.PrepaidEntity
import com.xiaoe.common.interfaces.OnItemClickWithAmountListener
import com.xiaoe.common.utils.Dp2Px2SpUtil
import com.xiaoe.common.utils.SharedPreferencesUtil
import com.xiaoe.network.NetworkCodes
import com.xiaoe.network.requests.IRequest
import com.xiaoe.network.requests.PrepaidBuyRequest
import com.xiaoe.network.requests.PrepaidRequest
import com.xiaoe.network.requests.TopUpListRequest
import com.xiaoe.shop.wxb.R

import com.xiaoe.shop.wxb.base.XiaoeActivity
import com.xiaoe.shop.wxb.business.search.presenter.SpacesItemDecoration
import com.xiaoe.shop.wxb.common.JumpDetail
import com.xiaoe.shop.wxb.common.pay.presenter.AmountAdapter
import com.xiaoe.shop.wxb.common.pay.presenter.PayPresenter
import kotlinx.android.synthetic.main.activity_bo_bi.*
import kotlinx.android.synthetic.main.bo_bi_tip_view.*
import kotlinx.android.synthetic.main.bo_bi_top_up_view.*
import kotlinx.android.synthetic.main.common_title_view.*
import kotlinx.android.synthetic.main.pay_ali_item.*
import kotlinx.android.synthetic.main.pay_selector_view.*
import kotlinx.android.synthetic.main.pay_title_view.*
import kotlinx.android.synthetic.main.pay_we_chat_item.*
import java.lang.Exception


/**
 * @author: zak
 * @date: 2018/12/28
 */
class BoBiActivity : XiaoeActivity(), OnItemClickWithAmountListener {

    private val payByWeChat: Int = 0
    private val payByAliPay: Int = 1

    private lateinit var layoutManger: GridLayoutManager
    private lateinit var amountAdapter: AmountAdapter
    private lateinit var decorate: SpacesItemDecoration
    private lateinit var payPresenter: PayPresenter
    private val topUpList:MutableList<AmountDataItem> = mutableListOf()
    private var selectMoney: Int = 0
    private var productId: String = ""
    private var preOrderId: String = ""
    private var isPaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        setContentView(R.layout.activity_bo_bi)

        payPresenter = PayPresenter(this, this)
        payPresenter.getTopUpList()

        initData()
        initView()
    }

    private fun initData() {
        title_back.setImageDrawable(resources.getDrawable(R.mipmap.detail_white_back, null))
        title_end.text = getString(R.string.bo_bi_content_desc)
        title_end.setTextColor(ContextCompat.getColor(this, R.color.white))
        boBiTopUpSubmit.isEnabled = false
        layoutManger = GridLayoutManager(this, 3)
    }

    private fun initView() {
        title_back.setOnClickListener { onBackPressed() }

        title_end.setOnClickListener { JumpDetail.jumpAccountDetail(this) }

        boBiTopUpSubmit.setOnClickListener {
            confirmMoney.text = String.format(getString(R.string.confirm_money), selectMoney)
            payPresenter.prepaidMsg(productId)
            paySelectorView.visibility = View.VISIBLE
        }

        payBg.setOnClickListener { paySelectorView.visibility = View.GONE }

        payAliItem.setOnClickListener {
            if (aliItemIcon.drawable.constantState == ContextCompat.getDrawable(this, R.mipmap.download_tocheck).constantState) {
                aliItemIcon.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.download_checking))
                weChatItemIcon.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.download_tocheck))
            } else {
                aliItemIcon.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.download_tocheck))
                weChatItemIcon.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.download_checking))
            }
        }

        payWeChatItem.setOnClickListener {
            if (weChatItemIcon.drawable.constantState == ContextCompat.getDrawable(this, R.mipmap.download_tocheck).constantState) {
                weChatItemIcon.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.download_checking))
                aliItemIcon.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.download_tocheck))
            } else {
                weChatItemIcon.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.download_tocheck))
                aliItemIcon.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.download_checking))
            }
        }

        confirmPay.setOnClickListener {
            val payWay: Int = if (weChatItemIcon.drawable.constantState == ContextCompat.getDrawable(this, R.mipmap.download_checking).constantState) {
                payByWeChat
            } else {
                payByAliPay
            }
            if (payWay != -1) {
                payPresenter.prepaidBuyMsg(preOrderId, payWay)
            }
        }
    }

    companion object {
        private val TAG = "BoBiActivity"
    }

    override fun onResume() {
        super.onResume()
        if (isPaying) {
            isPaying = false
            val code = getWXPayCode(true)
            if (code == 0) { // 支付成功回调

            } else {
                SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100)
            }
            dialog.dismissDialog()
        }
    }

    override fun onAmountItemClick(view: View?, amountDataItem: AmountDataItem?, position: Int) {
        selectMoney = amountDataItem?.price ?: 6
        productId = amountDataItem?.productId!!
        amountAdapter.refreshItemStyle(position)
    }

    override fun onBackPressed() {
        if (paySelectorView.visibility == View.VISIBLE) {
            paySelectorView.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    override fun onMainThreadResponse(iRequest: IRequest?, success: Boolean, entity: Any?) {
        super.onMainThreadResponse(iRequest, success, entity)
        when (iRequest) {
            // 支付价格列表回调
            is TopUpListRequest -> try {
                val result = Gson().fromJson(entity.toString(), AmountItemEntity::class.java)
                if (result.code == NetworkCodes.CODE_SUCCEED) {
                    for (item in result.data!!) {
                        item.balance = item.balance / 100
                        item.price = item.price / 100
                        topUpList.add(item)
                    }
                    initPageRecycler()
                } else {
                    // TODO: 获取充值列表失败
                }
            } catch (e: Exception) {
                // TODO: 服务器返回数据类型与接口文档不一致处理
                e.printStackTrace()
            }
            // 拉起预支付回调
            is PrepaidRequest -> try {
                val result = Gson().fromJson(entity.toString(), PrepaidEntity::class.java)
                if (result.code == NetworkCodes.CODE_SUCCEED) {
                    preOrderId = result.data.orderId
                } else {
                    // TODO: 获取预支付订单失败
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // 拉起三方预支付回调
            is PrepaidBuyRequest -> try {
                val result = Gson().fromJson(entity.toString(), PrepaidBuyEntity::class.java)
                if (result.code == NetworkCodes.CODE_SUCCEED) {
                    val payConfig = result.data.payConfig
                    isPaying = true
                    pullWXPay(payConfig.appid,
                            payConfig.partnerid,
                            payConfig.prepayid,
                            payConfig.noncestr,
                            payConfig.timestamp.toString(),
                            payConfig.packageName,
                            payConfig.sign)
                } else {
                    // TODO: 获取三方支付订单失败
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val jsonObject = entity as JSONObject
                Toast(jsonObject["msg"].toString())
            }
        }
    }

    private fun initPageRecycler() {
        amountAdapter = AmountAdapter(this, topUpList)
        decorate = SpacesItemDecoration(Dp2Px2SpUtil.dp2px(this, 12f))
        boBiTopUpRecycler.layoutManager = layoutManger
        boBiTopUpRecycler.addItemDecoration(decorate)
        boBiTopUpRecycler.adapter = amountAdapter

        amountAdapter.setOnItemClickWithAmountListener(this)
        boBiTopUpSubmit.isEnabled = true

        // 初始化第一个加个
        selectMoney = topUpList[0].balance
        productId = topUpList[0].productId
        var totalMoney = ""
        for (item in topUpList) {
            totalMoney += item.balance.toString() + ", "
        }
        totalMoney = totalMoney.substring(0, totalMoney.length - 2)
        boBiTopUpAmount.text = String.format(getString(R.string.bo_bi_top_up_amount), totalMoney)
    }
}
