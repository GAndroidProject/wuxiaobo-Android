package com.xiaoe.shop.wxb.common.pay.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.xiaoe.common.entitys.AmountItemEntity
import com.xiaoe.common.interfaces.OnItemClickWithAmountListener
import com.xiaoe.common.utils.Dp2Px2SpUtil
import com.xiaoe.shop.wxb.R

import com.xiaoe.shop.wxb.base.XiaoeActivity
import com.xiaoe.shop.wxb.business.search.presenter.SpacesItemDecoration
import com.xiaoe.shop.wxb.common.JumpDetail
import com.xiaoe.shop.wxb.common.pay.presenter.AmountAdapter
import kotlinx.android.synthetic.main.activity_bo_bi.*
import kotlinx.android.synthetic.main.bo_bi_top_up_view.*
import kotlinx.android.synthetic.main.bo_bi_amount_item.*
import kotlinx.android.synthetic.main.common_title_view.*
import kotlinx.android.synthetic.main.pay_ali_item.*
import kotlinx.android.synthetic.main.pay_selector_view.*
import kotlinx.android.synthetic.main.pay_title_view.*
import kotlinx.android.synthetic.main.pay_we_chat_item.*
import org.jetbrains.anko.find


/**
 * @author: zak
 * @date: 2018/12/28
 */
class BoBiActivity : XiaoeActivity(), OnItemClickWithAmountListener {

    private lateinit var layoutManger: GridLayoutManager
    private lateinit var amountAdapter: AmountAdapter
    private lateinit var decorate: SpacesItemDecoration
    private val tempData:MutableList<AmountItemEntity> = mutableListOf()
    private var selectMoney: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        setContentView(R.layout.activity_bo_bi)

        tempData.add(AmountItemEntity(6, 6))
        tempData.add(AmountItemEntity(68, 68))
        tempData.add(AmountItemEntity(198, 198))
        tempData.add(AmountItemEntity(298, 298))
        tempData.add(AmountItemEntity(698, 698))
        tempData.add(AmountItemEntity(998, 998))

        title_back.setImageDrawable(resources.getDrawable(R.mipmap.detail_white_back, null))
        title_back.setOnClickListener { onBackPressed() }
        title_end.text = getString(R.string.bo_bi_content_desc)
        title_end.setTextColor(ContextCompat.getColor(this, R.color.white))
        title_end.setOnClickListener { JumpDetail.jumpAccountDetail(this) }
        layoutManger = GridLayoutManager(this, 3)
        amountAdapter = AmountAdapter(this, tempData)
        decorate = SpacesItemDecoration(Dp2Px2SpUtil.dp2px(this, 12f))
        boBiTopUpRecycler.layoutManager = layoutManger as RecyclerView.LayoutManager?
        boBiTopUpRecycler.addItemDecoration(decorate)
        boBiTopUpRecycler.adapter = amountAdapter

        amountAdapter.setOnItemClickWithAmountListener(this)

        // 初始化第一个加个
        selectMoney = 6
        boBiTopUpSubmit.setOnClickListener {
            confirmMoney.text = String.format(getString(R.string.confirm_money), selectMoney)
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
            if (weChatItemIcon.drawable.constantState == ContextCompat.getDrawable(this, R.mipmap.download_checking).constantState) { // 调起微信支付
                Toast(selectMoney.toString() + "元可微信支付")
            } else {
                Toast(selectMoney.toString() + "元可支付宝支付")
            }
        }
    }

    companion object {
        private val TAG = "BoBiActivity"
    }

    override fun onAmountItemClick(view: View?, amountItemEntity: AmountItemEntity?, position: Int) {
        selectMoney = amountItemEntity?.money ?: 6
        amountAdapter.refreshItemStyle(position)
    }

    override fun onBackPressed() {
        if (paySelectorView.visibility == View.VISIBLE) {
            paySelectorView.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }
}
