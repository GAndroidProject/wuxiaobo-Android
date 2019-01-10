package com.xiaoe.common.entitys


import com.google.gson.annotations.SerializedName

/**
 * 三方预支付下单实体类（跟 PrepaidEntity 的区别：PrepaidBuyEntity 是根据 PrepaidEntity 返回值请求得到的）
 */
data class PayConfig(@SerializedName("package")
                     val packageName: String = "",
                     @SerializedName("appid")
                     val appid: String = "",
                     @SerializedName("sign")
                     val sign: String = "",
                     @SerializedName("partnerid")
                     val partnerid: String = "",
                     @SerializedName("prepayid")
                     val prepayid: String = "",
                     @SerializedName("noncestr")
                     val noncestr: String = "",
                     @SerializedName("timestamp")
                     val timestamp: Int = 0)


data class PrepaidBuyEntity(@SerializedName("msg")
                            val msg: String = "",
                            @SerializedName("code")
                            val code: Int = 0,
                            @SerializedName("data")
                            val data: PrepaidBuyEntityData)


data class PrepaidBuyEntityData(@SerializedName("pay_config")
                                val payConfig: PayConfig,
                                @SerializedName("prepay_id")
                                val prepayId: String = "")


