package com.xiaoe.shop.wxb.common.pay.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.alipay.sdk.app.PayTask
import com.google.gson.Gson
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.xiaoe.common.entitys.*
import com.xiaoe.common.interfaces.OnItemClickWithAmountListener
import com.xiaoe.common.utils.Dp2Px2SpUtil
import com.xiaoe.common.utils.SharedPreferencesUtil
import com.xiaoe.network.NetworkCodes
import com.xiaoe.network.requests.*
import com.xiaoe.shop.wxb.R

import com.xiaoe.shop.wxb.base.XiaoeActivity
import com.xiaoe.shop.wxb.business.earning.presenter.EarningPresenter
import com.xiaoe.shop.wxb.business.search.presenter.SpacesItemDecoration
import com.xiaoe.shop.wxb.common.JumpDetail
import com.xiaoe.shop.wxb.common.alipay.PayResult
import com.xiaoe.shop.wxb.common.alipay.util.OrderInfoUtil2_0
import com.xiaoe.shop.wxb.common.pay.presenter.AmountAdapter
import com.xiaoe.shop.wxb.common.pay.presenter.PayPresenter
import kotlinx.android.synthetic.main.activity_bo_bi.*
import kotlinx.android.synthetic.main.bo_bi_balance_view.*
import kotlinx.android.synthetic.main.bo_bi_tip_view.*
import kotlinx.android.synthetic.main.bo_bi_top_up_view.*
import kotlinx.android.synthetic.main.common_title_view.*
import kotlinx.android.synthetic.main.pay_ali_item.*
import kotlinx.android.synthetic.main.pay_selector_view.*
import kotlinx.android.synthetic.main.pay_title_view.*
import kotlinx.android.synthetic.main.pay_we_chat_item.*
import org.greenrobot.eventbus.EventBus
import java.lang.Exception


/**
 * @author: zak
 * @date: 2018/12/28
 */
class BoBiActivity : XiaoeActivity(), OnItemClickWithAmountListener, OnRefreshListener {

    private val payByWeChat: Int = 0
    private val payByAliPay: Int = 1

    private lateinit var layoutManger: GridLayoutManager
    private lateinit var amountAdapter: AmountAdapter
    private lateinit var decorate: SpacesItemDecoration
    private lateinit var payPresenter: PayPresenter
    private lateinit var earningPresenter: EarningPresenter
    private val topUpList:MutableList<AmountDataItem> = mutableListOf()
    private var selectMoney: Int = 0
    private var productId: String = ""
    private var preOrderId: String = ""
    private var isPaying: Boolean = false
    private var payWay: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        setContentView(R.layout.activity_bo_bi)

        payPresenter = PayPresenter(this, this)
        earningPresenter = EarningPresenter(this)
        payPresenter.getTopUpList()
        earningPresenter.requestAccountBalance()

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
        boBiRefresh.setOnRefreshListener(this)
        boBiRefresh.setEnableLoadMore(false)

        title_back.setOnClickListener { onBackPressed() }

        title_end.setOnClickListener { JumpDetail.jumpAccountDetail(this) }

        val selectMoneyStart = "<font color='#BCA16B'><big>"
        val selectMoneyEnd = "</big></font>"

        boBiTopUpSubmit.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                confirmMoney.text = Html.fromHtml(String.format(getString(R.string.confirm_money), selectMoneyStart, selectMoney, selectMoneyEnd), Html.FROM_HTML_MODE_COMPACT)
            } else {
                confirmMoney.text = Html.fromHtml(String.format(getString(R.string.confirm_money), selectMoneyStart, selectMoney, selectMoneyEnd))
            }
            payPresenter.prepaidMsg(productId)
            paySelectorView.visibility = View.VISIBLE
            confirmPay.isClickable = false
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
            if (!isPaying) {
                payWay = if (weChatItemIcon.drawable.constantState == ContextCompat.getDrawable(this, R.mipmap.download_checking).constantState) {
                    payByWeChat
                } else {
                    payByAliPay
                }
                if (payWay != -1) {
                    payPresenter.prepaidBuyMsg(preOrderId, payWay)
                }
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
                if (paySelectorView.visibility == View.VISIBLE) {
                    paySelectorView.visibility = View.GONE
                }
                earningPresenter.requestAccountBalance()
                val changeLoginIdentityEvent = ChangeLoginIdentityEvent()
                changeLoginIdentityEvent.isHasBalanceChange = true
                EventBus.getDefault().post(changeLoginIdentityEvent)
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

    override fun onRefresh(refreshLayout: RefreshLayout) {
        earningPresenter.requestAccountBalance()
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
                    confirmPay.isClickable = true
                } else {
                    // TODO: 获取预支付订单失败
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // 拉起三方预支付回调
            is PrepaidBuyRequest -> try {
                if (payWay == payByWeChat) {
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
                } else if (payWay == payByAliPay) {
                    val result = Gson().fromJson(entity.toString(), PrepaidBuyAliEntity::class.java)
                    if (result.code == NetworkCodes.CODE_SUCCEED) {
                        showToast(mContext, "支付宝回来了")
//                        pullAliPay(payConfig.appid,
//                                payConfig.partnerid,
//                                payConfig.prepayid,
//                                payConfig.noncestr,
//                                payConfig.timestamp.toString(),
//                                payConfig.packageName,
//                                payConfig.sign)
                        payV2(result.data)
                    } else {
                        // TODO: 获取三方支付订单失败
                    }
                }
//                val result = Gson().fromJson(entity.toString(), PrepaidBuyEntity::class.java)
//                if (result.code == NetworkCodes.CODE_SUCCEED) {
//                    if (payWay == payByWeChat) {
//                        val payConfig = result.data.payConfig
//                        isPaying = true
//                        pullWXPay(payConfig.appid,
//                                payConfig.partnerid,
//                                payConfig.prepayid,
//                                payConfig.noncestr,
//                                payConfig.timestamp.toString(),
//                                payConfig.packageName,
//                                payConfig.sign)
//                    } else if (payWay == payByAliPay) {
//                        Toast("支付宝回来了")
////                        pullAliPay(payConfig.appid,
////                                payConfig.partnerid,
////                                payConfig.prepayid,
////                                payConfig.noncestr,
////                                payConfig.timestamp.toString(),
////                                payConfig.packageName,
////                                payConfig.sign)
//                    }
//                } else {
//                    // TODO: 获取三方支付订单失败
//                }
            } catch (e: Exception) {
                e.printStackTrace()
                val jsonObject = entity as JSONObject
                Toast(jsonObject["msg"].toString())
            }
            // 获取账户波豆余额
            is EarningRequest -> try {
                boBiRefresh.finishRefresh()
                val result = Gson().fromJson(entity.toString(), BalanceEntity::class.java)
                val balanceStart = "<font color='#FFFFFF'><big>"
                val balanceEnd = "</big></font>"
                val balance = String.format("%1.2f", result.data.balance.getInteger("2").toFloat() / 100)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    boBiBalanceContent.text = Html.fromHtml(String.format(getString(R.string.balance_str), balanceStart, balance, balanceEnd), Html.FROM_HTML_MODE_COMPACT)
                } else {
                    boBiBalanceContent.text = Html.fromHtml(String.format(getString(R.string.balance_str), balanceStart, balance, balanceEnd))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun payV2(orderInfo: String) {
        Log.e(TAG, "payV2: orderInfo-> $orderInfo")
        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */
//        val rsa2 = RSA2_PRIVATE.length > 0
//        val params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2)
//        val orderParam = OrderInfoUtil2_0.buildOrderParam(params)
//
//        val privateKey = if (rsa2) RSA2_PRIVATE else RSA_PRIVATE
//        val sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2)
//        val orderInfo = "$orderParam&$sign"

//        val params = OrderInfoUtil2_0.buildOrderParamMap("2019010862855483", true)
//        val orderParam = OrderInfoUtil2_0.buildOrderParam(params)
//
//        val privateKey = "MIIEowIBAAKCAQEAxBSqVgwHcascouoRvBiA3C6GOhMlTl95tb3U7MIKOYxrnJVJpM2qqTk+LVhoUZD8xdxJsxm89vKflGoiY3/DmfHspvwDpzAN1OkdlzgfnIinu62n/6EbVa7KNKwWPZbjkT1i8Fj+1AOxrFhU5ij4tD4P4aIhqR2JNf6fTtMOidUhcDe8rs+bRdTt4zah6dPzRSLU8yhsgk1hyjEzi9n/DRyzdeWq8A+QdmRF+Vw4sAPmEWXuFvlUwaYrRWvR4WYnDNHB9rzZJCLaaKDjWAvZJKgSruM0PmoWMDBHMcV669Gx+XjHU3uLTYDzDLXN5UZ6zMBNJ5mX20yy5WWG9gTlpQIDAQABAoIBAQC7VFL4Kj3iZuvQ6XdQEMjrkrdCPJBjs+t/qoEGQMur92/IBQh6ntLxIXM5t0DSzIXyMmdm6KwT8fBrxopcLPAe33aMgjCrRcmxI5XVpxTsY9J8B4h9PNn0ni0o7U/CP6niVJd1sFClFXJliW5zBam16aUal984xrMUFtL0Z8d3/a783426+diMdSEqRk7FC+FtXX4aFxI0dKRzOkVg+SBmPB6k849A8zGKQ48u4+5FnUGkIPe2BDZMMViFpRBxYAqAcG5+fTlppljvCWWYIiDf6xL+anrApqKWPk6qexfRflSu2NW5cYyxdziykzy9/GeMQ0sydQOOo2Xaj0eiE54BAoGBAOF18YLjb00usm5PtVxjc+dW2iKjUnqMr/waFiuUOShtyusprFYoobBifdvethouW9uknBqOe78QWjWCRcwZMa/DDbJDo0UJX3X0J7kZt/QRd1Z6LgHU3HDHvGuHknqCHEmE+iWOJRjradfds0J1EU0qwT4BzWm72Ah3PFsHSJHFAoGBAN6j8aDu4ktIx97P306J+o49aZJXE3hlndGYwZ4XEW9P9uEi2pqciYVaMCKwgrTdfWxmdv2NckQY6+fuK9YZIECH2kZ+A4Q2XvbKhax8aUhbHlcxdqsMTz1fSlA85MSJNx/ibLigGdw8GUGTvR8ufWkg4XnKGJCQLTGYBjJBm6JhAoGAaeSYa05hD1I4cgE0AFGhtW0ghRZKfc6oNXMXKJ60fd2bafonvEvXLV4FGQiwZPmTIUVeIEb9rg6DIBCwsZy/rnqhazHDdIBjeqYTJigDMzok39QA2dFdPAdD4wlI7gtAN/Oh/ZOWru6axs2VDUiouKK1Imd8UAXvAILoMSTpm7UCgYAqAKv5PJgcs+JhOr5aRtWqOBPOs/bMY/9eJ39/n8J+SYu99aFgQd//9H/YS4ydKpwa80nHlAPWrQz0eI50mdDn27lmSyfGratEy/hkDiSerfIPaRmGM5BPd8bBVHukSG7J6SGxwyKd1gi9wm2PkGnAh5+CJopz239rN88TdCU0gQKBgB4IGxyNuM97WKSTUCmykpQ6CZWIA09D7m+XyKytg8HTELKbWR2U2aBA0PW7pdFy9TpVBGlOdU5mR3WfozSiaMLuETElP36Zi7Tb4SOYw46+eIADDdFGcNqle5GbozftajJS5XLBD49Yw3HXm9viUIWKJvQwAtEJyF3r5/ubuDcy"
//        val sign = OrderInfoUtil2_0.getSign(params, privateKey, true)
//        val orderInfo = "$orderParam&$sign"
//        Log.e(TAG, "payV2: orderInfo1-> $orderInfo")

        val payRunnable = Runnable {
            val alipay = PayTask(this@BoBiActivity)
            val result = alipay.payV2(orderInfo, true)
            Log.i("msp", result.toString())

            val msg = Message()
            msg.what = SDK_PAY_FLAG
            msg.obj = result
            mHandler.sendMessage(msg)
        }

        // 必须异步调用
        val payThread = Thread(payRunnable)
        payThread.start()
    }

    private val SDK_PAY_FLAG = 1
    private val SDK_AUTH_FLAG = 2

    private val mHandler = Handler(Handler.Callback { msg ->
        when (msg.what) {
            SDK_PAY_FLAG -> {
                val payResult = PayResult(msg.obj as Map<String, String>)
                /*
                  对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                 */
                // 同步返回需要验证的信息
                val resultInfo = payResult.result
                val resultStatus = payResult.resultStatus
                // 判断resultStatus 为9000则代表支付成功
                if (TextUtils.equals(resultStatus, "9000")) {
                    // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                    showAlert(mContext, getString(R.string.pay_success) + payResult)
                } else {
                    // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                    showAlert(mContext, getString(R.string.pay_failed) + payResult)
                }
            }
//            SDK_AUTH_FLAG -> {
//                val authResult = AuthResult(msg.obj as Map<String, String>, true)
//                val resultStatus = authResult.resultStatus
//
//                // 判断resultStatus 为“9000”且result_code
//                // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
//                if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.resultCode, "200")) {
//                    // 获取alipay_open_id，调支付时作为参数extern_token 的value
//                    // 传入，则支付账户为该授权账户
//                    showAlert(this@PayDemoActivity, getString(R.string.auth_success) + authResult)
//                } else {
//                    // 其他状态值则为授权失败
//                    showAlert(this@PayDemoActivity, getString(R.string.auth_failed) + authResult)
//                }
//            }
            else -> {
            }
        }

        false
    })

    private fun showAlert(ctx: Context, info: String) {
        showAlert(ctx, info, null)
    }

    private fun showAlert(ctx: Context, info: String, onDismiss: DialogInterface.OnDismissListener?) {
        AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton(R.string.confirm, null)
                .setOnDismissListener(onDismiss)
                .show()
    }

    private fun showToast(ctx: Context, msg: String) {
        android.widget.Toast.makeText(ctx, msg, android.widget.Toast.LENGTH_LONG).show()
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
        selectMoney = topUpList[2].balance
        productId = topUpList[2].productId
        var totalMoney = ""
        for (item in topUpList) {
            totalMoney += item.balance.toString() + ", "
        }
        totalMoney = totalMoney.substring(0, totalMoney.length - 2)
        boBiTopUpAmount.text = String.format(getString(R.string.bo_bi_top_up_amount), totalMoney)
    }
}
