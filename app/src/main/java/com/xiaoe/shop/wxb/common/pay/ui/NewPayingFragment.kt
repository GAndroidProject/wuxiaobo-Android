package com.xiaoe.shop.wxb.common.pay.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.xiaoe.common.entitys.BalanceEntity
import com.xiaoe.network.requests.EarningRequest
import com.xiaoe.network.requests.IRequest
import com.xiaoe.shop.wxb.R
import com.xiaoe.shop.wxb.base.BaseFragment
import com.xiaoe.shop.wxb.business.earning.presenter.EarningPresenter
import com.xiaoe.shop.wxb.common.JumpDetail
import kotlinx.android.synthetic.main.fragment_paying_new.*
import kotlinx.android.synthetic.main.paying_coupon_item.*
import kotlinx.android.synthetic.main.paying_way_bo_bi_item.*
import kotlinx.android.synthetic.main.paying_we_chat_item.*
import kotlinx.android.synthetic.main.paying_ali_item.*
import java.util.*

/**
 * @author: zak
 * @date: 2019/1/7
 */
class NewPayingFragment : BaseFragment() {

    private lateinit var rootView: View
    private lateinit var intent: Intent
    private var resPrice: Int = 0
    private var couponCount: Int = 0
    private var onClickListener: View.OnClickListener? = null
    private lateinit var earningPresenter: EarningPresenter
    private var isVipBuy: Boolean = false
    var isBoBiPayWay: Boolean = true
    var isWeChatPayWay: Boolean = false
    var isAliPayWay: Boolean = false
    private val moneyStart = "<font color='#BCA16B'><big>"
    private val moneyEnd = "</big></font>"
    private var balance: Int = 0 // 服务器返回波豆的价格
    private var needRefreshAccount: Boolean = false // 是否需要刷新波豆余额

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (inflater != null) {
            rootView = inflater.inflate(R.layout.fragment_paying_new, container, false)
            rootView
        } else {
            null
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intent = activity.intent

        earningPresenter = EarningPresenter(this)
        earningPresenter.requestAccountBalance()

        initView()
        initListener()
    }

    private fun initView() {
        val imgUrl = intent.getStringExtra("image_url")
        itemImage.setImageURI(imgUrl)
        itemTitle.text = intent.getStringExtra("title")
        resPrice = intent.getIntExtra("price", 0)
        val itemCountStr = "¥" + String.format("%1.2f", resPrice.toFloat() / 100)
        itemCount.text = itemCountStr
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            payingMoney.text = Html.fromHtml(String.format(getString(R.string.paying_account), moneyStart, itemCountStr, moneyEnd), Html.FROM_HTML_MODE_COMPACT)
        } else {
            payingMoney.text = Html.fromHtml(String.format(getString(R.string.paying_account), moneyStart, itemCountStr, moneyEnd))
        }
        setCanUseCouponCount(-1)
        val vipCarlUrl = "res:///" + R.mipmap.vip_card
        isVipBuy = vipCarlUrl == imgUrl
        val expireTime = intent.getStringExtra("expireTime")
        if (!TextUtils.isEmpty(expireTime)) {
            itemDesc.visibility = View.VISIBLE
            itemDesc.text = expireTime
        }
        if (isVipBuy) {
            payingBoBiSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.vip_buy_confirm))
            confirmBuy.background = ContextCompat.getDrawable(context, R.drawable.vip_high_button)
        } else {
            payingBoBiSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_checking))
            confirmBuy.background = ContextCompat.getDrawable(context, R.drawable.high_button)
        }
    }

    private fun initListener() {

        // 付费方式点击处理事件
        payingBoBiWrap.setOnClickListener {
            isBoBiPayWay = true
            isAliPayWay = false
            isWeChatPayWay = false
            if (payingBoBiSelect.visibility == View.VISIBLE) {
                if (payingBoBiSelect.drawable.constantState == ContextCompat.getDrawable(context, R.mipmap.download_tocheck).constantState) {
                    if (isVipBuy) {
                        payingBoBiSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.vip_buy_confirm))
                    } else {
                        payingBoBiSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_checking))
                    }
                }
            }
            payingAliSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_tocheck))
            payingWeChatSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_tocheck))
        }

        payingWeChatWrap.setOnClickListener {
            isBoBiPayWay = false
            isAliPayWay = false
            isWeChatPayWay = true
            if (payingWeChatSelect.drawable.constantState == ContextCompat.getDrawable(context, R.mipmap.download_tocheck).constantState) {
                if (isVipBuy) {
                    payingWeChatSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.vip_buy_confirm))
                } else {
                    payingWeChatSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_checking))
                }
            }
            payingBoBiSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_tocheck))
            payingAliSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_tocheck))
        }

        payingAliWrap.setOnClickListener {
            isBoBiPayWay = false
            isAliPayWay = true
            isWeChatPayWay = false
            if (payingAliSelect.drawable.constantState == ContextCompat.getDrawable(context, R.mipmap.download_tocheck).constantState) {
                if (isVipBuy) {
                    payingAliSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.vip_buy_confirm))
                } else {
                    payingAliSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_checking))
                }
            }
            payingBoBiSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_tocheck))
            payingWeChatSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_tocheck))
        }

        payingBoBiTopUp.setOnClickListener {
            needRefreshAccount = true
            JumpDetail.jumpBobi(context)
        }

        if (onClickListener == null) {
            return
        }
        confirmBuy.setOnClickListener(onClickListener)
        payingCouponWrap.setOnClickListener(onClickListener)
    }

    fun setCanUseCouponCount(count: Int) {
        couponCount = count
        if (useCouponTitleNew == null || count <= 0) {
            return
        }
        useCouponTitleNew.text = String.format(getString(R.string.coupons_available), count)
        useCouponTitleNew.setTextColor(ContextCompat.getColor(context, R.color.high_title_color))
    }

    fun setBtnSelectCouponClickListener(listener: View.OnClickListener) {
        if (onClickListener == null) {
            onClickListener = listener
        }
        if (payingCouponWrap == null) {
            return
        }
        payingCouponWrap.setOnClickListener(listener)
    }

    fun setBtnSucceedPayClickListener(listener: View.OnClickListener) {
        if (onClickListener == null) {
            onClickListener = listener
        }
        if (confirmBuy == null) {
            return
        }
        confirmBuy.setOnClickListener(listener)
    }

    fun setUseConponPrice(price: Int) {
        var price = price
        if (price <= 0) {
            price = 0
            useCouponTitleNew.text = resources.getString(R.string.not_can_use_or_no_sue_coupon)
            useCouponTitleNew.setTextColor(ContextCompat.getColor(context, R.color.secondary_title_color))
        } else {
            val titleText = "-¥" + price / 100f
            useCouponTitleNew.text = titleText
            useCouponTitleNew.setTextColor(ContextCompat.getColor(context, R.color.high_title_color))
        }
        var showPrice = resPrice - price
        if (showPrice < 0) {
            showPrice = 0
        }
        // 波豆比优惠后的价格多，默认选中波豆支付
        if (balance >= showPrice) {
            isBoBiPayWay = true
            isAliPayWay = false
            isWeChatPayWay = false
            if (payingBoBiTopUp.visibility == View.VISIBLE) {
                payingBoBiTopUp.visibility = View.GONE
            }
            payingBoBiSelect.visibility = View.VISIBLE
            if (isVipBuy) {
                payingBoBiSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.vip_buy_confirm))
            } else {
                payingBoBiSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_checking))
            }
            payingAliSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_tocheck))
            payingWeChatSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_tocheck))
        } else { // 否则默认微信支付
            isBoBiPayWay = false
            isAliPayWay = false
            isWeChatPayWay = true
            payingBoBiSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_tocheck))
            payingAliSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_tocheck))
            if (isVipBuy) {
                payingWeChatSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.vip_buy_confirm))
            } else {
                payingWeChatSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_checking))
            }
        }
        val moneyText = "¥" + showPrice / 100f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            payingMoney.text = Html.fromHtml(String.format(getString(R.string.paying_account), moneyStart, moneyText, moneyEnd), Html.FROM_HTML_MODE_COMPACT)
        } else {
            payingMoney.text = Html.fromHtml(String.format(getString(R.string.paying_account), moneyStart, moneyText, moneyEnd))
        }
    }

    override fun onResume() {
        super.onResume()
        if (needRefreshAccount) {
            needRefreshAccount = false
            earningPresenter.requestAccountBalance()
        }
    }

    override fun onMainThreadResponse(iRequest: IRequest?, success: Boolean, entity: Any?) {
        super.onMainThreadResponse(iRequest, success, entity)
        when (iRequest) {
            is EarningRequest -> try {
                val result = Gson().fromJson(entity.toString(), BalanceEntity::class.java)
                balance = result.data.balance.getInteger("2")
                val balanceStr = String.format("%1.2f", balance.toFloat() / 100)
                if (balance >= resPrice) { // 波豆余额大等于资源价格，可以购买，所以显示选中 icon
                    payingBoBiSelect.visibility = View.VISIBLE
                    if (isVipBuy) {
                        payingBoBiSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.vip_buy_confirm))
                    } else {
                        payingBoBiSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_checking))
                    }
                    payingWeChatSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_tocheck))
                    payingBoBiTopUp.visibility = View.GONE
                    isBoBiPayWay = true
                    isAliPayWay = false
                    isWeChatPayWay = false
                } else {
                    payingBoBiSelect.visibility = View.GONE
                    payingBoBiTopUp.visibility = View.VISIBLE
                    payingBoBiSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_tocheck))
                    if (isVipBuy) {
                        payingWeChatSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.vip_buy_confirm))
                    } else {
                        payingWeChatSelect.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.download_checking))
                    }
                    isBoBiPayWay = false
                    isAliPayWay = false
                    isWeChatPayWay = true
                }
                payingBoBiBalance.text = String.format(getString(R.string.paying_bo_bi_balance), balanceStr)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
