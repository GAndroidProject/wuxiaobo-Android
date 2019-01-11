package com.xiaoe.common.entitys


import com.google.gson.annotations.SerializedName

/**
 * 三方预支付下单实体类（跟 PrepaidEntity 的区别：PrepaidBuyEntity 是根据 PrepaidEntity 返回值请求得到的）
 */
data class PrepaidBuyAliEntity(@SerializedName("msg")
                               val msg: String = "",
                               @SerializedName("code")
                               val code: Int = 0,
                               @SerializedName("data")
                               val data: String = "")


